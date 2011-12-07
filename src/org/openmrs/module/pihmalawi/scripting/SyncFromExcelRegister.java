package org.openmrs.module.pihmalawi.scripting;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;

import au.com.bytecode.opencsv.CSVReader;

public class SyncFromExcelRegister {

	class PatientRecord {
		Integer arvNo = null;
		Date regDate = null;
		boolean transferIn = false;
		boolean female = true;
		boolean pregnant = false;
		Integer ageAtReg = null;
		Date arvStartDate = null;
		String arvStartReason = null;
		Date deathDate = null;
		Date defDate = null;
		Date stoppedDate = null;
		Date toDate = null;

	}

	public static void main(String[] args) throws Exception {
		try {
			System.out
					.println("Run as tomcat user or set global property module_repository_folder to an absolute path");
			if (args.length != 4) {
				System.out
						.println("importer importFile, openmrsRuntimeProperties, openmrsUser, openmrsPw");
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
			Context.startup(conncetionUrl, connectionUser, conncetionPw, prop);
			Context.openSession();
			Context.authenticate(openmrsUser, openmrsPw);

			// import
			new SyncFromExcelRegister().run(importFile);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			Context.closeSession();
		}
	}

	Session hibernateSession = null;

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private void run(String concept_file) throws IOException {
		String siteCode = "NNO ";

		CSVReader reader = new CSVReader(new FileReader(concept_file), ';',
				'"', 1);

		PatientRecord previousPr = new PatientRecord();
		List prs = new ArrayList();

		for (String[] entry : reader.readAll()) {
			try {
				System.out.println("Importing register line: "
						+ Arrays.toString(entry));

				PatientRecord pr = createPatientRecord(entry, previousPr);
				prs.add(pr);
				previousPr = pr;
				/*
				 * // hibernateSession = sessionFactory().getCurrentSession();
				 * PatientIdentifierType arvPI = Context.getPatientService()
				 * .getPatientIdentifierTypeByName("ARV Number"); // find
				 * patient List<Patient> ps =
				 * Context.getPatientService().getPatients( null, siteCode +
				 * arvNo, Arrays.asList(arvPI), true); if (ps.size() != 1) {
				 * throw new RuntimeException("No unique patient found"); }
				 * Patient p = ps.get(0);
				 * 
				 * Date existingRegDate = regDate(p); if (!same(regDate,
				 * existingRegDate, 1)) { logMismatch(p, "regDate", regDate,
				 * existingRegDate); }
				 * 
				 * Integer existingAgeAtReg = ageAtDate(p, existingRegDate); if
				 * (!same(ageAtReg, existingAgeAtReg, 1)) { logMismatch(p,
				 * "ageAtReg", ageAtReg, existingAgeAtReg); }
				 * 
				 * Date existingArvStartDate = arvStartDate(p); if
				 * (!same(arvStartDate, existingArvStartDate, 1)) {
				 * logMismatch(p, "arvStartDate", arvStartDate,
				 * existingArvStartDate); }
				 * 
				 * String existingArvStartReason = arvStartReason(p); if
				 * (!same(arvStartReason, existingArvStartReason)) { //
				 * unimportant, take register }
				 * 
				 * if (deathDate != null) { Date existingDeathDate =
				 * outcomeDeath(p); if (!same(deathDate, existingDeathDate, 1))
				 * { logMismatch(p, "deathDate", deathDate, existingDeathDate);
				 * } } else if (toDate != null) { Date existingToDate =
				 * outcomeTo(p); if (!same(toDate, existingToDate, 1)) {
				 * logMismatch(p, "toDate", toDate, existingToDate); } } else if
				 * (stoppedDate != null) { Date existingStoppedDate =
				 * outcomeStopped(p); if (!same(stoppedDate,
				 * existingStoppedDate, 1)) { logMismatch(p, "stoppedDate",
				 * stoppedDate, existingStoppedDate); } } else if (defDate !=
				 * null) { Date existingDefDate = outcomeDef(p); if
				 * (!same(defDate, existingDefDate, 1)) { logMismatch(p,
				 * "defDate", defDate, existingDefDate); } }
				 */
			} catch (Throwable e) {
				System.err.println("  Error importing: " + e.getMessage());

			}

			writeRecord("NNO", prs);

		}
	}

	private PatientRecord createPatientRecord(String[] entry,
			PatientRecord previousPr) throws ParseException {
		if (entry.length != 10) {
			throw new RuntimeException("Wrong syntax, ignoring line for: "
					+ csv(entry));
		}

		PatientRecord pr = new PatientRecord();
		try {

			int i = 0;

			pr.arvNo = Integer.parseInt(entry[i++]);

			pr.regDate = date(entry[i++], previousPr.regDate);

			String e = entry[i++];
			if ("fp".equalsIgnoreCase(e) || "preg".equalsIgnoreCase(e)) {
				pr.pregnant = true;
			}
			if ("m".equalsIgnoreCase(e)) {
				pr.female = false;
			}

			pr.ageAtReg = Integer.parseInt(entry[i++]);

			pr.arvStartDate = date(entry[i++], previousPr.regDate);

			pr.transferIn = (pr.regDate.equals(pr.arvStartDate));

			e = entry[i++];
			if (isEmpty(e)) {
				pr.arvStartReason = "3";
			} else {
				pr.arvStartReason = e;
			}

			pr.deathDate = date(entry[i++], null);

			pr.toDate = date(entry[i++], null);

			pr.defDate = date(entry[i++], null);

			pr.stoppedDate = date(entry[i++], null);

		} catch (Throwable t) {
			System.err.println("  Error importing: " + t.getMessage());
		}
		return pr;
	}

	private void writeRecord(String siteCode, List<PatientRecord> prs) {
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(siteCode + "_converted.csv")));
			w.write("ARV, Registration Date, Gender (female), Pregnant, ARV Start date, Transfer in, Reason starting ARVs, Death date, Default date, Stopped date, Transfer out date"
					+ System.getProperty("line.separator"));
			for (PatientRecord pr : prs) {
				String line = siteCode
						+ " "
						+ pr.arvNo
						+ ","
						+ csv(pr.regDate, pr.female, pr.pregnant,
								pr.arvStartDate, !pr.transferIn, pr.arvStartReason,
								pr.deathDate, pr.defDate, pr.stoppedDate,
								pr.toDate)
						+ System.getProperty("line.separator");

				w.write(line);
			}
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String csv(Object... values) {
		String line = "";
		for (Object value : values) {
			if (value == null) {
				line += ",";
			} else if (value instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				line += sdf.format((Date) value) + ",";
			} else {
				line += value + ",";
			}
		}
		return line;
	}

	private Date outcomeDef(Patient p) {
		// TODO Auto-generated method stub
		return null;
	}

	private Date outcomeTo(Patient p) {
		Program PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		ProgramWorkflowState STATE = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Patient transferred out");
		ProgramWorkflowState STATE2 = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Transferred internally");

		int id = STATE.getId();

		PatientState state = mostRecentOutcome(p);
		if (state.getState().getId() == id
				|| state.getState().getId() == STATE2.getId()) {
			return state.getStartDate();
		}
		return null;
	}

	private Date outcomeStopped(Patient p) {
		Program PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		ProgramWorkflowState STATE = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Treatment stopped");
		int id = STATE.getId();

		PatientState state = mostRecentOutcome(p);
		if (state.getState().getId() == id) {
			return state.getStartDate();
		}
		return null;
	}

	private Date outcomeDeath(Patient p) {
		Program PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		ProgramWorkflowState STATE = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Patient died");
		int id = STATE.getId();

		PatientState state = mostRecentOutcome(p);
		if (state.getState().getId() == id) {
			return state.getStartDate();
		}
		return null;
	}

	private void logMismatch(Patient p, String string, Integer ageAtReg,
			Integer existingAgeAtReg) {
		System.err.print("mismatch," + string + "," + ageAtReg + ","
				+ existingAgeAtReg);
	}

	private void logMismatch(Patient p, String string, Date regDate,
			Date existingRegDate) {
		System.err.print("mismatch," + string + "," + regDate + ","
				+ existingRegDate);
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
		ProgramWorkflowState onArtState = Context.getProgramWorkflowService()
				.getState(1);
		Location location = Context.getLocationService().getLocation(
				"Neno District Hospital");

		// hiv program states at location
		List<PatientState> states = new Helper()
				.getPatientStatesByWorkflowAtLocation(p, onArtState, location,
						hibernateSession);

		// check for the first On ART state ever
		PatientState latestState = null;
		for (PatientState state : states) {
			if (state.getState().getId().equals(onArtState.getId())) {
				if (latestState == null
						|| state.getStartDate().getTime() > latestState
								.getStartDate().getTime()) {
					latestState = state;
				}
			}
		}

		return latestState.getStartDate();
	}

	private PatientState mostRecentOutcome(Patient p) {
		ProgramWorkflowState onArtState = Context.getProgramWorkflowService()
				.getState(1);
		Location location = Context.getLocationService().getLocation(
				"Neno District Hospital");

		// hiv program states at location
		PatientState state = new Helper().getMostRecentStateAtLocation(p,
				Arrays.asList(onArtState), location, hibernateSession);

		return state;
	}

	private Integer ageAtDate(Patient p, Date onDate) {
		return p.getAge(onDate);
	}

	private boolean same(String first, String second) {
		return first.equals(second);
	}

	private boolean same(Integer first, Integer second, int tolerance) {
		int delta = first - second;
		if (delta < 0) {
			delta = delta * -1;
		}
		return (delta <= tolerance);
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

	private Date date(String trim, Date previous) throws ParseException {
		if ("?".equals(trim)) {
			return null;
		}
		if (!isEmpty(trim)) {
			if (previous != null && trim.trim().lastIndexOf(" ") == 2) {
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
				return new SimpleDateFormat("dd MM yy").parse(trim.trim() + " "
						+ simpleDateformat.format(previous));
			}
			return new SimpleDateFormat("dd MM yy").parse(trim.trim());
		}
		return previous;
	}

	private boolean isEmpty(String trim) {
		return (trim == null || "".equals(trim));
	}

}
