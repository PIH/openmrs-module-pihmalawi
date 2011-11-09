package org.openmrs.module.pihmalawi.scripting;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;

import au.com.bytecode.opencsv.CSVReader;

public class SyncFromExcelRegister {
	
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Run as tomcat user or set global property module_repository_folder to an absolute path");
			if (args.length != 4) {
				System.out.println("importer importFile, openmrsRuntimeProperties, openmrsUser, openmrsPw");
				System.exit(1);
			}
			
			// parameters
			String importFile = args[0];
			String openmrsRuntimeProperties = args[1];
			String openmrsUser = args[2];
			String openmrsPw = args[3];
			
			// properties
			Properties prop = new Properties();
			prop.load(new FileInputStream(openmrsRuntimeProperties));
			String connectionUser = prop.getProperty("connection.username");
			String conncetionPw = prop.getProperty("connection.password");
			String conncetionUrl = prop.getProperty("connection.url");
			
			// connection init
			Context.startup(conncetionUrl, connectionUser, conncetionPw, new Properties());
			Context.openSession();
			Context.authenticate(openmrsUser, openmrsPw);
			
			// import
			new SyncFromExcelRegister().run(importFile);
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			Context.closeSession();
		}
	}
	
	Session hibernateSession = null;
	
	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private void run(String concept_file) throws IOException {
		hibernateSession = sessionFactory().getCurrentSession();
		PatientIdentifierType arvPI = Context.getPatientService().getPatientIdentifierTypeByName("ARV Number");
		
		CSVReader reader = new CSVReader(new FileReader(concept_file), ';', '"', 1);
		String arvNo = null;
		Date regDate = null;
		Integer ageAtReg = null;
		Date arvStartDate = null;
		String arvStartReason = null;
		String outcome = null;
		Date outcomeDate = null;
		
		for (String[] entry : reader.readAll()) {
			try {
				System.out.println("Importing register line: " + Arrays.toString(entry));
				
				if (entry.length != 7) {
					throw new RuntimeException("Wrong syntax, ignoring line");
				}
				
				int i = 0;
				// get values, assume previous ones if empty
				arvNo = currentValue(entry[i++], arvNo);
				 regDate = currentValue(date(entry[i++]), regDate);
				 ageAtReg = currentValue(Integer.parseInt(entry[i++]), ageAtReg);
				 arvStartDate = currentValue(date(entry[i++]), arvStartDate);
				 arvStartReason = currentValue(entry[i++], arvStartReason);
				 outcome = currentValue(entry[i++], outcome);
				 outcomeDate = currentValue(date(entry[i++]), outcomeDate);
				 
				 // find patient
				 List<Patient> ps = Context.getPatientService().getPatients(null, arvNo, Arrays.asList(arvPI), true);
				 if (ps.size() != 1) {
					throw new RuntimeException("No unique patient found");
				 }
				 Patient p = ps.get(0);
				 
				 Date existingRegDate = regDate(p);
				 if (!same(regDate, existingRegDate, 1)) {
					 System.err.println("");
				 }
				 
				 Integer existingAgeAtReg = ageAtDate(p, existingRegDate);
				 if (!same(ageAtReg, existingAgeAtReg, 1)) {
					 System.err.println("");
				 }
				 
				 Date existingArvStartDate = arvStartDate(p);
				 if (!same(arvStartDate, existingArvStartDate, 1)) {
					 System.err.println("");
				 }
				 
				 String existingArvStartReason = arvStartReason(p);
				 if (!same(arvStartReason, existingArvStartReason)) {
					 System.err.println("");
				 }
				 
				 String existingOutcome = outcome(p);
				 if (!same(outcome, existingOutcome)) {
					 System.err.println("");
				 }
				 
				 Date existingOutcomeDate = outcomeDate(p);
				 if (!same(outcomeDate, existingOutcomeDate, 1)) {
					 System.err.println("");
				 }
				 
			}
			catch (RuntimeException e) {
				System.err.println("  Error importing: " + e.getMessage());
				
			}
		}
	}
	
	private Date outcomeDate(Patient p) {
		// TODO Auto-generated method stub
		return null;
	}

	private String outcome(Patient p) {
		// TODO Auto-generated method stub
		return null;
	}

	private String arvStartReason(Patient p) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date arvStartDate(Patient p) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date regDate(Patient p) {
		ProgramWorkflowState onArtState = Context.getProgramWorkflowService().getState(1);
		Location location = Context.getLocationService().getLocation("Neno District Hospital");
		
		// hiv program states at location
		List<PatientState> states = new Helper().getPatientStatesByWorkflowAtLocation(p, onArtState, location , hibernateSession);
		
		// check for the first On ART state ever
		PatientState earliestState = null;
		for (PatientState state : states) {
			if (state.getState().getId().equals(onArtState.getId())) {
				if (earliestState == null 
						|| state.getStartDate().getTime() < earliestState.getStartDate().getTime()) {
					earliestState = state;
				}
			}
		}
		
		return earliestState.getStartDate();
	}

	private Integer ageAtDate(Patient p, Date onDate) {
		return p.getAge(onDate);
	}

	private boolean same(String first, String second) {
		return first.equals(second);
	}

	private boolean same(Integer first, Integer second,
			int tolerance) {
		return (first == second);
	}

	private boolean same(Date firstDate, Date secondDate, int toleranceInMonths) {
		long maxDifference = 1000 * 60 * 60 * 24 * 30 * toleranceInMonths;
		long difference = firstDate.getTime() - secondDate.getTime();
		if (difference < 0) {
			difference = secondDate.getTime() - firstDate.getTime();
		}
		if (difference > maxDifference) {
			return false;
		}
		return true;
	}

	private String currentValue(String currentValue, String oldValue) {
		if (currentValue == null) {
			return oldValue;
		}
		return currentValue;
	}

	private Date currentValue(Date currentValue, Date oldValue) {
		if (currentValue == null) {
			return oldValue;
		}
		return currentValue;
	}

	private Integer currentValue(Integer currentValue, Integer oldValue) {
		if (currentValue == null) {
			return oldValue;
		}
		return currentValue;
	}

	private Date date(String trim) {
		if (trim != null) {
			try {
				return new SimpleDateFormat("dd MM yy").parse(trim.trim());
			} catch (ParseException e) {
				System.err.println("Error parsing date: " + e);
//				e.printStackTrace();
			}
		}
		return null;
	}
}
