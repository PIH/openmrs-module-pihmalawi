package org.openmrs.module.pihmalawi.reports.renderer;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.TbMetadata;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TbRegisterBreakdownRenderer extends BreakdownRowRenderer {

	Concept TREATMENT_UNIT = null;
	Concept DOT_OPTION = null;
	Concept DURATION_COUGH = null;
	Concept TB_CLASSIFICATION = null;
	Concept HIV_TEST_STATUS = null;
	Concept TIME_OF_HIV_TEST = null;
	Concept ARV_STATUS = null;
	Concept CPT_START = null;
	Concept REMARKS = null;

	Program tbProgram = null;
	ProgramWorkflow programWorkflow = null;
	ProgramWorkflowState onTreatment = null;

	List<EncounterType> encounterTypes = null;

	TbMetadata tbMetadata = new TbMetadata();

	public TbRegisterBreakdownRenderer() {
		init();
	}

	private void init() {
		// set up all concepts for all encountertypes to be included
		TREATMENT_UNIT = Context.getConceptService().getConceptByName(
				"Clinic location other");
		DOT_OPTION = Context.getConceptService().getConceptByName(
				"Site of TB disease");
		DURATION_COUGH = Context.getConceptService().getConceptByName(
				"Duration of current cough");
		TB_CLASSIFICATION = Context.getConceptService().getConceptByName(
				"Tuberculosis case type");
		HIV_TEST_STATUS = Context.getConceptService().getConceptByName(
				"HIV test history at registration");
		TIME_OF_HIV_TEST = Context.getConceptService().getConceptByName("");
		ARV_STATUS = Context.getConceptService().getConceptByName(
				"Taking antiretroviral drugs");
		CPT_START = Context.getConceptService().getConceptByName(
				"Taking co-trimoxazole preventive therapy");
		REMARKS = Context.getConceptService().getConceptByName(
				"Comments at conclusion of examination");

		tbProgram = tbMetadata.getTbProgram();
		programWorkflow = tbMetadata.getTreatmentStatusWorkfow();
		onTreatment = tbMetadata.getOnTreatmentState();
		encounterTypes = Arrays.asList(tbMetadata.getTbInitialEncounterType());
	}

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();
		DataSetColumn c = null;

		List<Encounter> es = Context.getEncounterService()
				.getEncounters(p, null, null, endDateParameter, null,
						encounterTypes, null, false);
		// take first one for now
		Encounter e = es.isEmpty() ? null : es.get(0);

		// exception handling looks really ugly, but its necessary...
		try {
			addCol(row, "TB #",
					patientLink(p, patientIdentifierType, locationParameter));
		} catch (Exception ex) {
			log.error(ex);
		}
		try {
			addCol(row, "All ARV #s",
					identifiers(p, lookupPatientIdentifierType("ARV Number")));
		} catch (Exception ex) {
			log.error(ex);
		}
		try {
			addCol(row, "All HCC #s",
					identifiers(p, lookupPatientIdentifierType("HCC Number")));
		} catch (Exception ex) {
			log.error(ex);
		}
		addDemographicCols(row, p, endDateParameter);
		addOutcomeCols(
				row,
				p,
				locationParameter, endDateParameter,
				lookupProgramWorkflow("Kaposis sarcoma program",
						"Treatment status"));

		// number of tb treatments (only first one taken here)
		c = new DataSetColumn("TB Treatment count", "TB Treatment count",
				String.class);
		row.addColumnValue(c, es.size());
		// reg date
		addFirstTimeEnrollmentCols(row, p, onTreatment, endDateParameter, "Registration date");
		c = new DataSetColumn("Treatment Unit", "Treatment Unit", String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, TREATMENT_UNIT));

		c = new DataSetColumn("DOT Option", "DOT Option", String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, DOT_OPTION));

		c = new DataSetColumn("Duration current cough",
				"Duration current cough", String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, DURATION_COUGH));

		c = new DataSetColumn("TB Classification", "TB Classification",
				String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, TB_CLASSIFICATION));

		c = new DataSetColumn("Sputum Examination...", "Sputum Examination...",
				String.class);

		c = new DataSetColumn("Treatment outcome", "Treatment outcome",
				String.class);
		PatientState ps = null;
		if (locationParameter == null) {
			// outcome from endDate, hopefully the one you are interested in
			ps = h.getMostRecentStateAtDate(p, programWorkflow,
					endDateParameter);
		} else {
			// enrollment outcome from location
			ps = h.getMostRecentStateAtLocation(p, programWorkflow,
					locationParameter);
		}

		c = new DataSetColumn("Outcome", "Outcome", String.class);
		if (ps != null) {
			row.addColumnValue(c, ps.getState().getConcept().getName()
					.getName());
		}

		c = new DataSetColumn("Outcome Date", "Outcome Date", String.class);
		if (ps != null) {
			row.addColumnValue(c, formatEncounterDate(ps.getStartDate()));
		}

		c = new DataSetColumn("HIV Test Status", "HIV Test Status",
				String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, HIV_TEST_STATUS));

		c = new DataSetColumn("Time of HIV Test", "Time of HIV Test",
				String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, TIME_OF_HIV_TEST));

		c = new DataSetColumn("ARV Status", "ARV Status", String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, ARV_STATUS));

		c = new DataSetColumn("CPT Start date", "CPT Start date", String.class);
		row.addColumnValue(c, obsDate(CPT_START, p, e));

		c = new DataSetColumn("Remarks", "Remarks", String.class);
		row.addColumnValue(c, obsFromEncounter(p, e, REMARKS));

		c = new DataSetColumn("Initial Phase Regimen", "Initial Phase Regimen",
				String.class);
		// row.addColumnValue(c, obsFromEncounter(p, e, ""));

		c = new DataSetColumn("Continuation Phase Regimen",
				"Continuation Phase Regimen", String.class);
		// row.addColumnValue(c, obsFromEncounter(p, e, ""));

		addVhwCol(row, p);
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("TB_INITIAL"),
				lookupEncounterType("TB_FOLLOWUP")), "");
		addAllEnrollmentsCol(row, p);

		return row;
	}

	private void exportAllEncountersAndObs(Patient p, DataSetRow row,
			Location location, Date endDate, EncounterType encounterType,
			int maxEncounters, ArrayList<Concept> encounterConcepts) {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				location, null, endDate, null, Arrays.asList(encounterType),
				null, false);
		for (int encounterCount = 1; encounterCount <= maxEncounters; encounterCount++) {
			Encounter e = null;
			if (encounterCount <= es.size()) {
				e = es.get(encounterCount - 1);
			}
			DataSetColumn col = new DataSetColumn("count_" + encounterCount
					+ "_" + encounterType.getName(), "count_" + encounterCount
					+ "_" + encounterType.getName(), Integer.class);
			row.addColumnValue(col, encounterCount);

			col = new DataSetColumn("encounterDate_" + encounterCount + "_"
					+ encounterType.getName(), "encounterDate_"
					+ encounterCount + "_" + encounterType.getName(),
					String.class);
			String value = (e != null ? h(formatEncounterDate(e
					.getEncounterDatetime())) : "");
			row.addColumnValue(col, value);

			col = new DataSetColumn("encounterId_" + encounterCount + "_"
					+ encounterType.getName(), "encounterId_" + encounterCount
					+ "_" + encounterType.getName(), String.class);
			value = (e != null ? e.getId() + "" : "");
			row.addColumnValue(col, value);

			for (Concept c : encounterConcepts) {
				value = "<empty>";
				String label = c.getName().getName() + "_" + encounterCount
						+ "_" + encounterType.getName();
				col = new DataSetColumn(label, label, String.class);
				try {
					if (e != null) {
						List<Obs> os = obs(e, c, location, endDate);
						for (Obs o : os) {
							value += o.getValueAsString(Context.getLocale())
									+ " ";
						}
					}
				} catch (Throwable t) {
					log.error(t);
				}
				row.addColumnValue(col, h(value));
			}
		}
	}

	private List<Obs> obs(Encounter e, Concept concept, Location location,
			Date endDate) {
		List<Obs> obs = Context.getObsService().getObservations(null,
				Arrays.asList(e), Arrays.asList(concept), null, null, null,
				null, null, null, null, endDate, false);
		return obs;
	}

	private String obsFromEncounter(Patient p, Encounter e, Concept concept) {
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), Arrays.asList(e),
				Arrays.asList(concept), null, null, null, null, 1, null, null,
				null, false);
		if (obs.iterator().hasNext()) {
			return obs.iterator().next().getValueAsString(Context.getLocale());
		}
		return "&nbsp";
	}

	private String obsDate(final Concept CPT_START, Patient p, Encounter e) {
		Obs o = obsFromEncounterAsObs(p, e, CPT_START);
		if (o != null) {
			return formatEncounterDate(o.getObsDatetime());
		}
		return "&nbsp;";
	}

	private Obs obsFromEncounterAsObs(Patient p, Encounter e, Concept concept) {
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), Arrays.asList(e),
				Arrays.asList(concept), null, null, null, null, 1, null, null,
				null, false);
		if (obs.iterator().hasNext()) {
			return obs.iterator().next();
		}
		return null;
	}

}
