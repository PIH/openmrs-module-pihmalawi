package org.openmrs.module.pihmalawi.scripting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.SessionFactory;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAddress;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.Metadata;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;

public class ArtExporter {

	private static final String NOT_AVAILABLE = "-";

	private static final String NEWLINE = "\n";

	private ProgramHelper h = new ProgramHelper();

	private Map<String, String> mapper = new HashMap<String, String>();

	public ArtExporter() {
	}

	public static void main(String[] args) throws Exception {
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
				prop);
		Context.openSession();
		Context.authenticate(openmrsUser, openmrsPw);

		new ArtExporter().run();
	}

	public void run() throws Exception {
		String[] mappings = { 
				"Neno District Hospital", "NNO",
				"Nsambe HC", "NSM", 
				"Magaleta HC", "MGT",
				
				"On antiretrovirals", "", 
				"ALIVE", "",
				"Patient transferred out", "TO",
				
				"ALIVE AND ON FIRST LINE ANTIRETROVIRAL REGIMEN", "1L",
				"SUBSTITUTE ANOTHER 1ST LINE ARV DRUG", "",
				
				"NONE", "No", 
				"PERIPHERAL NEUROPATHY", "PN",
				"OTHER NON-CODED", "Oth",

				"WHO STAGE I PEDS",  "1", 
				"WHO STAGE II PEDS", "2",
				"WHO STAGE III PEDS","3", 
				"WHO STAGE IV PEDS", "4",
				"WHO STAGE I ADULT",  "1", 
				"WHO STAGE II ADULT", "2",
				"WHO STAGE III ADULT","3", 
				"WHO STAGE IV ADULT", "4", 
				"", ""
		};
		for (int i = 0; i < mappings.length; i += 2) {
			mapper.put(mappings[i], mappings[i + 1]);
		}

		export();
	}

	public void export() throws Exception {
		EncounterService es = Context.getEncounterService();
		PatientService ps = Context.getPatientService();
		Location nno = null; // location("Neno District Hospital"); dont filter
								// as mobile clinics also count

		System.out.println("Start exporting");
		// List<Patient> patients = Arrays.asList(ps.getPatient(16466));
		List<Patient> patients = new ArrayList<Patient>();
		List<PatientIdentifierType> identifierTypes = Arrays.asList(ps
				.getPatientIdentifierTypeByName("ARV Number"));
		List<EncounterType> artInitials = Arrays.asList(es
				.getEncounterType("ART_INITIAL"));
		List<EncounterType> artFollowups = Arrays.asList(es
				.getEncounterType("ART_FOLLOWUP"));
		for (int i = 1795; i < 3000; i++) {
			patients.addAll(ps.getPatients(null, "NNO " + i, identifierTypes,
					true));
		}
		// patients.addAll(ps.getPatients(null, "NNO 1", identifierTypes,
		// true));
		// doesn't work ???
		// List<Patient> patients = ps.getPatients(null, "NNO%",
		// Arrays.asList(ps.getPatientIdentifierTypeByName("ARV Number"))),
		// false);
		(new File("export")).mkdir();
		for (Patient p : patients) {
			String nnoArv = p.getPatientId() + "";
			for (PatientIdentifier pi : p.getPatientIdentifiers(
							Context.getPatientService().getPatientIdentifierType(
									"ARV Number"))) {
				if (pi.getIdentifier().startsWith("NNO ")) {
					nnoArv = pi.getIdentifier();
				}
			}
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("export/" +  nnoArv + ".csv" )));
			Encounter initial = null;
			w.newLine();
			System.out.println("\tPatient " + p.getId());
			// create new file
			// BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new
			// FileOutputStream("export/" + p.getId() + ".csv")));
			List<Encounter> el = es.getEncounters(p, nno, null, null, null,
					artInitials, null, false);
			if (el.size() == 0) {
				// missing initial
				System.out.println("\t\tMissing initial, trying to use another encounter");
				List<Encounter> anyEncounter = es.getEncounters(p, nno, null, null, null,
						null, null, false);
				if (anyEncounter.size() > 0) {
					w.write(exportInitial(anyEncounter.get(0)));
					w.newLine();
				}
			} else {
				// take last initial (assuming list is ordered by date of entry)
				try {
					w.write(exportInitial(el.get(el.size() - 1)));
					initial = el.get(el.size() - 1);
					w.newLine();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			el = es.getEncounters(p, nno, null, null, null, artFollowups, null,
					false);
			// Visit Date Hgt Wt Adverse Outcome Outcome date 1st L Alt 1st Line
			// 2nd L NS Side effects TB status current Pill count Doses missed
			// ARVs given # To CPT # Comment Next appointment
			w.write(csv("Visit loc", "Vist Date", "Hgt", "Wt",
					"Outcome Enrollment", "Adverse Outcome", "Outcome date",
					"Regimen", "Side Effects", "TB status",
					"current Pill count", "Doses missed", "ARVs given #", "To",
					"CPT #", "Comment", "Next appointment", "Unknown Obs"));
			w.newLine();
			if (initial != null) {
				w.write(exportFollowup(initial));
			}
			w.newLine();
			for (Encounter e : el) {
				try {
					w.write(exportFollowup(e));
					w.newLine();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			w.close();
		}
	}

	private String exportFollowup(Encounter e) {
		Set<Obs> obss = e.getAllObs();
		String loc = map(e.getLocation().getName());
		String date = date(e.getEncounterDatetime());
		String height = NOT_AVAILABLE;
		String weight = NOT_AVAILABLE;
		PatientState s = currentProgramWorkflowStatus(1, e.getPatient(),
				e.getEncounterDatetime());
		String outcomeEnrollment = outcomeEnrollment(s);
		String outcome = NOT_AVAILABLE;
		String outcomeDate = ("".equals(outcomeEnrollment) ? "" : date(s
				.getStartDate()));
		String regimen = NOT_AVAILABLE;
		String sideEffects = "";
		String tb = NOT_AVAILABLE;
		String pillCount = NOT_AVAILABLE;
		String dosesMissed = NOT_AVAILABLE;
		String noOfArvGiven = NOT_AVAILABLE;
		String arvsGivenTo = NOT_AVAILABLE;
		String cptNo = NOT_AVAILABLE;
		String comments = NOT_AVAILABLE;
		String nextAppt = NOT_AVAILABLE;
		String unknownObs = "";
		for (Obs o : obss) {
			switch (o.getConcept().getConceptId()) {
			case 5090:
				height = numeric(o.getValueNumeric());
				break;
			case 5089:
				weight = numeric(o.getValueNumeric());
				break;
			case 2530:
				outcome = map(valueCoded(o.getValueCodedName()));
				break;
			case 2538:
				regimen = map(valueCoded(o.getValueCodedName()));
				break;
			case 2589:
				// new regimen
				break;
			case 2146:
				if (o.getValueNumeric() == 0) {
					sideEffects += "No ";
				}
				break;
			case 1297:
				sideEffects += map(valueCoded(o.getValueCoded().getName())) + " ";
				break;
			case 7459:
				// tbstatus
				break;
			case 2540:
				pillCount = numeric(o.getValueNumeric());
				break;
			// doses missed
			case 2929:
				noOfArvGiven = numeric(o.getValueNumeric());
				break;
			// cpt no
			// comment
			case 5096:
				nextAppt = date(o.getValueDatetime());
				break;
			case 1620:
			case 1623:
			case 6784:
			case 6785:
			case 2541:
			case 2922:
			case 968:
			case 2542:
			case 2972:
			case 1662: // really?
			case 2536: // really?
			case 2539: // really?
			case 5272: // really?
			case 2122: // really?
				break;
			default:
				unknownObs += o.getConcept().getName().getName() + " ("
						+ o.getConcept().getId() + ") " + " | ";
			}
		}

		return csv(loc, date, height, weight, outcomeEnrollment, outcome,
				outcomeDate, regimen, sideEffects, tb, pillCount, dosesMissed,
				noOfArvGiven, arvsGivenTo, cptNo, comments, nextAppt,
				unknownObs);

	}

	private String map(String key) {
		if (mapper.get(key) != null) {
			return mapper.get(key);
		}
		return key;
	}

	private String outcomeEnrollment(PatientState s) {
		if (s != null) {
			return map(s.getState().getConcept().getName().getName());
		}
		return "";
	}

	private String valueCoded(ConceptName valueCodedName) {
		return valueCodedName == null ? "" : valueCodedName.getName();
	}

	private String numeric(Double valueNumeric) {
		return "" + valueNumeric;
	}

	private String date(Date date) {
		return new SimpleDateFormat("dd MMM yyyy").format(date);
	}

	private String csv(String... strings) {
		String result = "";
		for (String s : strings) {
			result += s.replaceAll("\\r|\\n|\\t|;", " ").replaceAll("   ", " ").replaceAll("  ", " ").trim() + ";";
		}
		return result;
	}

	private String csv(String string, List<String> strings) {
		String result = string;
		for (String s : strings) {
			result += s + ";";
		}
		return result;
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private String exportInitial(Encounter encounter) {
		String r = "";

		HivMetadata hivMetadata = new HivMetadata();
		PatientState ps = h.getMostRecentStateAtLocation(encounter.getPatient(), hivMetadata.getTreatmentStatusWorkfow(), hivMetadata.getNenoHospital());
		if (ps != null) {
			r += csv("Outcome NNO", ps.getState().getConcept()
					.getName().getName(), "at location", map(h.getEnrollmentLocation(ps.getPatientProgram()
			).getName()));
		} else {
			r += csv("Outcome NNO", "Unknown");
		}
		r += NEWLINE;

		// ART no Pre-ART no Pre-ART start date OpenMRS ID VHW
		String artNos = identifierStrings(encounter.getPatient()
				.getPatientIdentifiers(
						Context.getPatientService().getPatientIdentifierType(
								"ARV Number")));
		String partNos = identifierStrings(encounter.getPatient()
				.getPatientIdentifiers(
						Context.getPatientService().getPatientIdentifierType(
								"PART Number")));
		String partStart = "(todo)";
		String patientId = "" + encounter.getPatientId();
		String vhwName = "(todo)";
		String name = h(encounter.getPatient().getGivenName()) + " "
				+ h(encounter.getPatient().getFamilyName());
		String stage = NOT_AVAILABLE;
		String tbStat = NOT_AVAILABLE;
		String datePlace = NOT_AVAILABLE;
		String type = NOT_AVAILABLE;
		String sex = encounter.getPatient().getGender();
		String dob = date(encounter.getPatient().getBirthdate());
		String phone = NOT_AVAILABLE;
		String cd4 = NOT_AVAILABLE;
		String cd4P = NOT_AVAILABLE;
		String ks = NOT_AVAILABLE;
		String addr = "";
		Set<PersonAddress> addresses = encounter.getPatient().getAddresses();
		for (PersonAddress a : addresses) {
			addr += h(a.getCityVillage()) + " " + h(a.getCountyDistrict())
					+ ", ";
		}
		String cd4Date = NOT_AVAILABLE;
		String preg = NOT_AVAILABLE;
		String d4TDate = date(encounter.getEncounterDatetime()); // assume date of initial is date of 1st line regimen
		String guardianName = "";
		String hgt = NOT_AVAILABLE;
		String wgt = NOT_AVAILABLE;
		String everArv = NOT_AVAILABLE;
		String alt1stL = NOT_AVAILABLE;
		String alt1stLDate = NOT_AVAILABLE;
		String fup = NOT_AVAILABLE;
		String grel = NOT_AVAILABLE;
		String gphone = NOT_AVAILABLE;
		String ageInit = "" + encounter.getPatient().getAge(encounter.getEncounterDatetime()); // assumption based on d4TDate
		String lastArv = NOT_AVAILABLE;
		String secondL = NOT_AVAILABLE;
		String secondLDate = NOT_AVAILABLE;
		String unknownObs = "";

		for (Obs o : encounter.getAllObs()) {
			switch (o.getConcept().getConceptId()) {
			case 2927:
				guardianName += o.getValueText() + " ";
				break;
			case 2928:
				guardianName += o.getValueText() + " ";
				break;
			case 2552:
				fup = valueCoded(o.getValueCoded().getName());
				break;
			case 2170:
				datePlace = h(o.getValueText()) + " ";
				break;
			case 2515:
				datePlace += date(o.getValueDatetime()) + " ";
				break;
			case 5089:
				wgt = numeric(o.getValueNumeric());
				break;
			case 5090:
				hgt = numeric(o.getValueNumeric());
				break;
			case 1480:
				stage = map(valueCoded(o.getValueCoded().getName()));
				break;
			case 5272:
				preg = map(valueCoded(o.getValueCoded().getName()));
				break;
			case 1251:
			case 2520:
			case 2298:
			case 2299:
			case 2122:
			case 2743:
				break;
			default:
				unknownObs += o.getConcept().getName().getName() + " ("
						+ o.getConcept().getId() + ") " + " | ";
			}
		}

		r += csv("ART no", artNos, "OpenMRS ID", patientId);
		r += NEWLINE + NEWLINE;
		r += csv("Patient Guardian details", "", "", "", "", "",
				"Status at ART initiation", "", "", "", "", "",
				"First positive HIV test", "");
		r += NEWLINE;
		r += csv("Patient name", name, "", "", "", "", "Clin Stage", stage, "",
				"", "TB Status at initiation", tbStat, "Date, Place",
				datePlace, "Type", type);
		r += NEWLINE;
		r += csv("Sex", sex, "DOB", dob, "Patient phone", phone, "CD4 count",
				cd4, "%", cd4P, "KS", ks, "ART Regimen", "", "Start date");
		r += NEWLINE;
		r += csv("Phys. Address", addr, "", "", "", "", "CD4 date", cd4Date,
				"", "", "Pregnant at initiation", preg, "1st Line",
				"d4T 3TC NVP", d4TDate);
		r += NEWLINE;
		r += csv("Guardian Name", guardianName, "", "", "", "", "Height", hgt,
				"Weight", wgt, "Ever taken ARVs", everArv, "Alt 1st Line",
				alt1stL, alt1stLDate);
		r += NEWLINE;
		r += csv("Agrees to FUP", fup, "Guardian Relation", grel,
				"Guardian Phone", gphone, "Age at init.", ageInit, "", "",
				"Last ARVs (drug, date)", lastArv, "2nd Line", secondL,
				secondLDate, "", "Unknown Obs", unknownObs);
		r += NEWLINE;

		return r;
	}

	private List<String> identifiers(List<PatientIdentifier> patientIdentifiers) {
		List<String> ids = new ArrayList<String>();
		for (PatientIdentifier pi : patientIdentifiers) {
			ids.add(pi.getIdentifier());
		}
		return ids;
	}

	private String identifierStrings(List<PatientIdentifier> patientIdentifiers) {
		String ids = "";
		for (PatientIdentifier pi : patientIdentifiers) {
			ids += pi.getIdentifier() + " ";
		}
		return ids;
	}

	private ProgramWorkflowService programWorkflowService() {
		return Context.getProgramWorkflowService();
	}

	private PatientState currentProgramWorkflowStatus(
			Integer programWorkflowId, Patient p, Date stateAtDate) {
		if (p == null || p.getId() == null) {
			return null;
		}
		ProgramWorkflow workflow = programWorkflowService().getWorkflow(
				programWorkflowId); // not sure if and how I want to reference
									// the UUID
		List<PatientProgram> pps = programWorkflowService().getPatientPrograms(
				p, workflow.getProgram(), null, null, null, null, false);
		TreeMap<Date, PatientState> sortedStates = new TreeMap<Date, PatientState>();

		// collect all states ordered by start date
		for (PatientProgram pp : pps) {
			if (!pp.isVoided()) {
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()) {
						sortedStates.put(ps.getStartDate(), ps);
					}
				}
			}
		}
		// get the one with the closest startdate before stateAtDate
		PatientState mostRecentState = null;
		for (Date startDate : sortedStates.keySet()) {
			if (!startDate.after(stateAtDate)) {
				mostRecentState = sortedStates.get(startDate);
			}
		}

		if (mostRecentState != null && mostRecentState.getState() != null
				&& mostRecentState.getState().getConcept().getName() != null) {
			return mostRecentState;
		}
		return null;
	}

	private String h(String string) {
		return string == null ? "" : string;
	}
}
