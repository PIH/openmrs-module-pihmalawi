package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class HccMissedAppointmentBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		try {
			addCol(row, "All HCC #s",
					identifiers(p, lookupPatientIdentifierType("HCC Number")));
		} catch (Exception e) {
			log.error(e);
		}
		addDemographicCols(row, p, endDateParameter);
		addOutcomeCols(row, p, locationParameter,
				lookupProgramWorkflow("HIV program", "Treatment status"));
		addMostRecentOutcomeWithinDatabaseCols(row, p,
				lookupProgramWorkflow("HIV program", "Treatment status"),
				endDateParameter);
		addVhwCol(row, p);
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP")), " in HCC");
		addVerificationCols(row, p);
		
		return row;
	}

	private void addVerificationCols(DataSetRow row, Patient p) {
		DataSetColumn c = new DataSetColumn("confirmed missed appt", "confirmed missed appt", String.class);
		row.addColumnValue(c, h(""));
		// not verified
		c = new DataSetColumn("unable to verify", "unable to verify",
				String.class);
		row.addColumnValue(c, h(""));
		// missed data entry
		c = new DataSetColumn("missed data entry", "missed data entry",
				String.class);
		row.addColumnValue(c, h(""));
		
	}

}
