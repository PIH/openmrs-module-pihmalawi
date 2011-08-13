package org.openmrs.module.pihmalawi.scripting;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;

import au.com.bytecode.opencsv.CSVReader;

public class ChwPatientRelationshipsImporter {

	protected static final Log log = LogFactory
			.getLog(ChwPatientRelationshipsImporter.class);

	private Patient unknownChwPatient = null;
	private RelationshipType vhwRelationshipType = null;
	
	class Vhw {
		public int serial;
		public String firstname;
		public String lastname;
		public String sex;
		public Date dob;
		public Date hired;
		public String village;
	}

	class VhwPatient {
		public String name;
		public String arv;
		public String comment;
	}

	public static void main(String[] args) throws Exception {
		// Process outline
		// 1. Export XLS list into CSV
		// 2. Paste them together into one file for Upper Neno
		// 3. Import it
		// 4. Check for unknown CHW and add proper patient record according to the CHW list
		// 5. Check for any unknown ARV number and change in XLS
		// 6. Check for inconsistencies where names differ too much and change in XLS
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
			Context.startup(conncetionUrl, connectionUser, conncetionPw,
					new Properties());
			Context.openSession();
			Context.authenticate(openmrsUser, openmrsPw);

			// import
			new ChwPatientRelationshipsImporter().run(importFile);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			Context.closeSession();
		}
	}

	private void run(String concept_file) throws IOException {
		unknownChwPatient = Context.getPatientService().getPatients("Unknown Community" /* Health Worker */, null, null, false).get(0);
		vhwRelationshipType = Context.getPersonService().getRelationshipTypeByName("Patient/Village Health Worker");

		log.info("log\tstatus\tmessages");
		CSVReader reader = new CSVReader(new FileReader(concept_file), ';',
				'"', 1);

		// clear out all chw relationships
		List<Relationship> relationships = Context.getPersonService()
				.getRelationships(
						null,
						null,
						Context.getPersonService().getRelationshipTypeByName(
								"VHW ID"));
		log.info("\tRemoving VHW relationships: " + relationships.size());
		for (Relationship r : relationships) {
			Context.getPersonService().purgeRelationship(r);
		}

		// recreate relationships
		List<String[]> entries = reader.readAll();
		for (int lineCount = 0; lineCount < entries.size(); lineCount++) {
			String[] line = entries.get(lineCount);
			// System.out.println("Importing line\t" + line + "-"
			// + Arrays.toString(line));

			if (!isVhwLine(line)) {
				continue;
			}
			Vhw vhw = parseVhw(line);
			Patient vhwPatient = vhw(vhw);
			if (vhwPatient == null) {
				log.error("\tCHW not found, using Unknown Community Health Worker as placeholder\t"
						+ string(vhw));
				vhwPatient = unknownChwPatient;
				// continue;
			} else {
//				log.info("\tCHW found\t" + string(vhw));
			}

			// process all patients for this vhw by messing around with outer
			// loop count
			for (; lineCount < entries.size(); lineCount++) {
				line = entries.get(lineCount);
				if (!isPatientLine(line)) {
					// done with all patients for this vhw
					break;
				}
				VhwPatient patient = parsePatient(line);
				Patient p = patient(patient);
				if (p == null) {
					continue;
				}
				ensureRelationship(p, vhwPatient);
			}
		}
	}

	private void ensureRelationship(Patient patient, Patient vhw) {
		if (patient == null && vhw == null) {
			log.error("\tCouldn't create VHW relationship\t");
			return;
		}
		try {
		Relationship rel = new Relationship();
		rel.setPersonA(patient);
		rel.setRelationshipType(vhwRelationshipType);
		rel.setPersonB(vhw);
		Context.getPersonService().saveRelationship(rel);
//		log.info("\tCreated VHW relationship\t" + vhw.getGivenName() + " "
//				+ vhw.getFamilyName() + " " + patient.getGivenName() + " "
//				+ patient.getFamilyName());
		} catch (Throwable t) {
			log.error("\tCouldn't create VHW relationship\t");
			return;
			
		}
	}

	private boolean patientExists(VhwPatient vhwPatient) {
		return (patient(vhwPatient) != null);
	}

	private VhwPatient parsePatient(String[] line) {
		VhwPatient p = new VhwPatient();
		p.name = line[4].trim();
		p.arv = line[5].trim();
		return p;
	}

	private boolean isPatientLine(String[] line) {
		if (!"".equals(line[4]) && !"".equals(line[5])) {
			return true;
		}
		return false;
	}

	private Patient patient(VhwPatient vhwPatient) {
		PatientIdentifierType arvNumberType = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");
		List<Patient> patients = Context.getPatientService().getPatients(null,
				"NNO " + vhwPatient.arv, Arrays.asList(arvNumberType), false);
		if (patients.size() > 1) {
			log.error("\tMultiple patients found for ARV number\t"
					+ string(vhwPatient));
			return null;
		}
		if (patients.size() < 1) {
			log.error("\tNo patients found for ARV number\t"
					+ string(vhwPatient));
			return null;
		}

		List<Patient> patientsByName = Context.getPatientService().getPatients(
				vhwPatient.name, "NNO " + vhwPatient.arv, null, true);
		if (patientsByName.size() == 1) {
//			log.info("\tPatient found\t" + vhwPatient.name + "\t"
//					+ collectNames(patientsByName));
		} else {
			log.warn("\tName mismatch for patient (but still importing)\t"
					+ vhwPatient.arv + "\t" + vhwPatient.name + "\t"
					+ collectNames(patients));
		}
		return patients.get(0);
	}

	private String collectNames(List<Patient> patients) {
		String string = "";
		for (Patient p : patients) {
			Set<PersonName> names = p.getNames();
			for (PersonName pn : names) {
				string += pn.getGivenName() + " " + pn.getFamilyName() + "\t";
			}
		}
		return string;
	}

	private boolean vhwExists(Vhw vhw) {
		return (vhw(vhw) != null);
	}

	private Patient vhw(Vhw vhw) {
		if (vhw == null || !notEmpty(vhw.firstname) || !notEmpty(vhw.lastname)) {
			log.error("\t\tProblem finding VHW\t" + string(vhw));
			return null;
		}
		List<Patient> patients = Context.getPatientService().getPatients(
				vhw.firstname + " " + vhw.lastname, null, null, true);
		if (patients.size() != 1) {
			return null;
		}
		Patient p = patients.get(0);
		// todo, ignore the numbers for now
		// List<PatientIdentifier> ids = p.getActiveIdentifiers();
		// for (PatientIdentifier id : ids) {
		// if ("VHW ID".equals(id.getIdentifierType().getName())) {
		// if (id.getIdentifier().equals(vhw.serial)) {
		// break;
		// }
		// }
		// return null;
		// }
		return p;
	}

	private boolean isVhwLine(String[] line) {
		try {
			return Integer.parseInt(line[0]) > 0;
		} catch (Exception e) {
		}
		return false;
	}

	private Vhw parseVhw(String[] line) {
		Vhw vhw = new Vhw();
		try {
			vhw.serial = Integer.parseInt(line[0]);
		} catch (NumberFormatException nfe) {
			vhw.serial = -1;
		}
		vhw.firstname = line[1].trim();
		vhw.lastname = line[2].trim();
		vhw.village = line[3].trim();
		return vhw;
	}

	private String string(VhwPatient patient) {
		return "\t\t\t\t" + patient.arv + "\t" + patient.name;
	}

	private String string(Vhw vhw) {
		if (vhw == null) {
			return "";
		}
		return "\t\t\t\t" + vhw.serial + "\t" + vhw.firstname + "\t"
				+ vhw.lastname + "\t" + vhw.sex + "\t" + vhw.dob + "\t"
				+ vhw.village;
	}

	private boolean notEmpty(String string) {
		if (string != null && !"".equals(string)) {
			return true;
		}
		return false;
	}
}
