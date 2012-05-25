package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class HccRegisterBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		// exception handling looks really ugly, but its necessary...
		try {
			addCol(row, "HCC #",
					patientLink(p, patientIdentifierType, locationParameter));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All HCC #s",
					identifiers(p, lookupPatientIdentifierType("HCC Number")));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All ARV #s",
					identifiers(p, lookupPatientIdentifierType("ARV Number")));
		} catch (Exception e) {
			log.error(e);
		}
		addFirstEncounterCols(row, p, lookupEncounterType("PART_INITIAL"),
				"Pre-ART initial visit");
		addFirstChangeToStateCols(
				row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"Pre-ART (Continue)"), "Pre-ART state");
		addFirstEncounterCols(row, p,
				lookupEncounterType("EXPOSED_CHILD_INITIAL"), "Exposed initial visit");
		addFirstChangeToStateCols(
				row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"Exposed Child (Continue)"), "Exposed state");
		addDemographicCols(row, p, endDateParameter);
		addOutcomeColsForHcc(row, p, locationParameter,
				lookupProgramWorkflow("HIV program", "Treatment status"));
		addCol(row, "Missing 2+ months since", "(tbd)");
		addMostRecentOutcomeWithinDatabaseCols(row, p,
				lookupProgramWorkflow("HIV program", "Treatment status"),
				endDateParameter);
		addVhwCol(row, p);
		addVisitColsOfVisitX(row, p, Arrays.asList(
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), 1, " in HCC");
		addVisitColsOfVisitX(row, p, Arrays.asList(
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), 2, " in HCC");
		addVisitColsOfVisitX(row, p, Arrays.asList(
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), 3, " in HCC");
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), " in HCC");
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), " in HIV");
		addMostRecentNumericObsCols(row, p, lookupConcept("CD4 count"),
				endDateParameter);
		// tb status
		addMostRecentObsCols(row, p, lookupConcept("TB status"),
				endDateParameter);
		// side effects
		addMostRecentObsCols(row, p, lookupConcept("Malawi ART side effects"), endDateParameter);
		addAllEnrollmentsCol(row, p);
		return row;
	}

	protected void addOutcomeColsForHcc(DataSetRow row, Patient p,
			Location locationParameter, ProgramWorkflow pw) {
		try {
			PatientState ps = null;
			// enrollment outcome from location
			ps = h.getStateAfterStateAtLocation(p, pw, Arrays.asList(
					lookupProgramWorkflowState("HIV program",
							"Treatment status", "Exposed Child (Continue)"),
					lookupProgramWorkflowState("HIV program",
							"Treatment status", "Pre-ART (Continue)")),
					locationParameter, sessionFactory().getCurrentSession());

			DataSetColumn c = new DataSetColumn("Outcome in HCC", "Outcome in HCC",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
			}
			c = new DataSetColumn("Outcome in HCC change date", "Outcome in HCC change Date",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, formatEncounterDate(ps.getStartDate()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

}
