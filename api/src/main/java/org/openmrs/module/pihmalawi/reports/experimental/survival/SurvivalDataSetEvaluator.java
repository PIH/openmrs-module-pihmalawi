package org.openmrs.module.pihmalawi.reports.experimental.survival;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { SurvivalDataSetDefinition.class })
public class SurvivalDataSetEvaluator  implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public SurvivalDataSetEvaluator() {

	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		final Concept CD4_COUNT = Context.getConceptService().getConceptByName(
				"CD4 count");
		final RelationshipType VHW_RELATIONSHIP_TYPE = Context
				.getPersonService().getRelationshipType(7);
		// // VHW_RELATIONSHIP_TYPE =
		// Context.getPersonService().getRelationshipTypeByUuid(
		// "19124428-a89d-11df-bba5-000c297f1161");

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		SurvivalDataSetDefinition definition = (SurvivalDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();
		Collection<EncounterType> encounterTypes = definition
				.getEncounterTypes();

		 context = ObjectUtil.nvl(context, new EvaluationContext());
		 Cohort cohort = context.getBaseCohort();

//		 By default, get all patients
		 if (cohort == null) {
		 cohort = Context.getPatientSetService().getAllPatients();
		 }
		
		 if (context.getLimit() != null) {
		 CohortUtil.limitCohort(cohort, context.getLimit());
		 }
		
		 // Get a list of patients based on the cohort members
		 List<Patient> patients = Context.getPatientSetService().getPatients(
		 cohort.getMemberIds());

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;

			// todo, get current id and/or preferred
			String id = "";
			for (PatientIdentifier pi : p
					.getPatientIdentifiers(patientIdentifierType)) {
				if (pi.getIdentifier().startsWith("NNO-")) {
					id = pi.getIdentifier() + " " + id;
				} else if (pi.getIdentifier().startsWith("LSI-")) {
					id = pi.getIdentifier() + " " + id;
				} else {
					id += pi.getIdentifier() + " ";
				}
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, id);
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// age
			c = new DataSetColumn("Age", "Age", Integer.class);
			row.addColumnValue(c, p.getAge());
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			// last visit & loc
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, null, null, encounterTypes,
							null, false);
			c = new DataSetColumn("Last visit", "Last visit", String.class);
			if (!encounters.isEmpty()) {
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c,
						formatEncounterDate(e.getEncounterDatetime()) + " ("
								+ e.getLocation() + ")");
				// rvd from last encounter
				// String rvd = "";
				// Set<Obs> observations = e.getObs();
				// for (Obs o : observations) {
				// if
				// (o.getConcept().equals(Context.getConceptService().getConceptByName("Appointment date")))
				// {
				// rvd = o.getValueAsString(Context.getLocale());
				// c = new DataSetColumn("RVD", "RVD", String.class);
				// row.addColumnValue(c, h(rvd));
				// }
				// }
			} else {
				row.addColumnValue(c, h(""));
			}
			// vhw given & last
			c = new DataSetColumn("VHW", "VHW", String.class);
			String vhw = "";
			List<Relationship> ships = Context.getPersonService()
					.getRelationshipsByPerson(p);
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(VHW_RELATIONSHIP_TYPE)) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName();
					break;
				}
			}
			row.addColumnValue(c, h(vhw));

			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

			Program program = Context.getProgramWorkflowService()
					.getProgramByName("HIV program");

			// first on art on
			c = new DataSetColumn("ART started on", "ART started on",
					String.class);
			row.addColumnValue(c, artStatedOn(p, program));

			// first on art at
			c = new DataSetColumn("ART started at", "ART started at",
					String.class);
			row.addColumnValue(c, artStartedAt(p, program));

			// survival analysis
			c = new DataSetColumn("12 months outcome", "12 months outcome",
					String.class);
			int monthsInProgram = 12;
			// Location location = (Location) Context.getLocationService()
			// .getLocation(2);
			Location location = null;
			// p = Context.getPatientService().getPatient(17208);
			String[] outcome = new SurvivalRateCalc().outcome(p, location,
					monthsInProgram, program);
			row.addColumnValue(c, outcome[0]);
			c = new DataSetColumn("12 months outcome location",
					"12 months outcome location", String.class);
			row.addColumnValue(c, outcome[1]);
			c = new DataSetColumn("Date of relevant event for 12 months outcome",
					"Date of relevant event for 12 months outcome", String.class);
			row.addColumnValue(c, outcome[2]);

			Encounter initial = firstArtInitial(p);

			c = new DataSetColumn("1st positive HIV test",
					"1st positive HIV test", String.class);
			row.addColumnValue(c,
					obsFromEncounter(initial, "DATE OF HIV DIAGNOSIS"));

			c = new DataSetColumn("1st positive HIV test place",
					"1st positive HIV test place", String.class);
			row.addColumnValue(c,
					obsFromEncounter(initial, "LOCATION WHERE TEST TOOK PLACE"));

			c = new DataSetColumn("Initial Encounter date",
					"Initial Encounter date", String.class);
			row.addColumnValue(c, (initial != null ? initial.getEncounterDatetime() : ""));

			c = new DataSetColumn("Initial Encounter place",
					"Initial Encounter place", String.class);
			row.addColumnValue(c, (initial != null ? initial.getLocation().getName() : ""));

			c = new DataSetColumn("Transfer in", "Transfer in", String.class);
			row.addColumnValue(c, obsFromEncounter(initial, "TRANSFER IN"));

			c = new DataSetColumn("Agrees to followup", "Agrees to followup",
					String.class);
			row.addColumnValue(c,
					obsFromEncounter(initial, "FOLLOW UP AGREEMENT"));

			c = new DataSetColumn("Reason for starting", "Reaons for starting",
					String.class);
			row.addColumnValue(c,
					obsFromEncounter(initial, "REASOn antiretrovirals STARTED"));

			c = new DataSetColumn("WHO Stage", "WHO Stage", String.class);
			row.addColumnValue(c, obsFromEncounter(initial, "WHO STAGE"));

			c = new DataSetColumn("Weight at initiation", "Weight at initiation", String.class);
			row.addColumnValue(c, obsFromEncounter(initial, "Weight (kg)"));

			c = new DataSetColumn("Height at initiation", "Height at initiation", String.class);
			row.addColumnValue(c, obsFromEncounter(initial, "Height (cm)"));

			c = new DataSetColumn("Age at initiation in Neno district",
					"Age at initiation (in Neno)", Integer.class);
			row.addColumnValue(c, (initial != null ? p.getAge(initial.getEncounterDatetime()) : ""));

			dataSet.addRow(row);
		}
		return dataSet;
	}

	private String obsFromEncounter(Encounter initial, String name) {
		if (initial != null) {
			Concept c = Context.getConceptService().getConcept(name);
			List<Obs> os = Context.getObsService().getObservations(null,
					Arrays.asList(initial), Arrays.asList(c), null, null, null,
					null, 1, null, null, null, false);
			if (!os.isEmpty()) {
				Obs o = os.get(0);
				String value = "";
				if (o.getValueNumeric() != null)
					value = "" + o.getValueNumeric();
				if (o.getValueDatetime() != null)
					value = "" + o.getValueDatetime();
				if (o.getValueText() != null)
					value = "" + o.getValueText();
				if (o.getValueCoded() != null)
					value = "" + o.getValueCoded().getDisplayString();
				return value;
			}
		}
		return "";
	}

	private Encounter firstArtInitial(Patient p) {
		Collection c = new Vector();
		c.add(Context.getEncounterService().getEncounterType("ART_INITIAL"));
		List es = Context.getEncounterService().getEncounters(p, null, null,
				null, null, c, null, false);
		if (!es.isEmpty()) {
			return (Encounter) es.get(0);
		}
		return null;
	}

	private Date artStatedOn(Patient p, Program program) {
		PatientState ps = getFirstOnArtState(p, program);
		if (ps != null) {
			return ps.getStartDate();
		}
		return null;
	}

	private PatientState getFirstOnArtState(Patient p, Program program) {

		ProgramWorkflowState onArt = Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals");

		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p, program, null, null, null, null, false);
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			if (!pp.isVoided()) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getState().getId().equals(onArt.getId())
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					PatientState state = (PatientState) validPatientStates
							.get(key);
					// just take the first one
					return state;
				}
			}
		}
		return null;
	}

	private Location artStartedAt(Patient p, Program program) {
		PatientState ps = getFirstOnArtState(p, program);
		if (ps != null) {
			return new SurvivalRateCalc().getEnrollmentLocation(ps
					.getPatientProgram());
		}
		return null;
	}

	private String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("dd-MMM-yyyy").format(encounterDatetime);
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
}
