package org.openmrs.module.pihmalawi.reports.renderer;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.util.Arrays;
import java.util.Date;

// TODO: Delete this class once the ART Missed Appointment Report is done
@Deprecated
public class ArtMissedAppointmentBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p, PatientIdentifierType patientIdentifierType, Location locationParameter, Date startDateParameter, Date endDateParameter) {
		DataSetRow row = new DataSetRow();
		try {
			addCol(row, "All ARV #s", identifiers(p, lookupPatientIdentifierType("ARV Number")));
		}
		catch (Exception e) {
			log.error(e);
		}
		addDemographicCols(row, p, endDateParameter);
		addOutcomeCols(row, p, locationParameter, endDateParameter, lookupProgramWorkflow("HIV program", "Treatment status"));
		addMostRecentOutcomeWithinDatabaseCols(row, p, lookupProgramWorkflow("HIV program", "Treatment status"));
		addVhwCol(row, p);
		addLastVisitCols(row, p, Arrays.asList(lookupEncounterType("ART_FOLLOWUP")), " in ART");
		row.addColumnValue(new DataSetColumn("confirmed missed appt", "confirmed missed appt", String.class), h(""));
		row.addColumnValue(new DataSetColumn("unable to verify", "unable to verify", String.class), h(""));
		row.addColumnValue(new DataSetColumn("missed data entry", "missed data entry", String.class), h(""));
		return row;
	}
}
