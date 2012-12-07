package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class ChronicCareMissedAppointmentBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		// exception handling looks really ugly, but its necessary...
		try {
			addCol(row, "All National IDs",
					identifiers(p, lookupPatientIdentifierType("National IDs")));
		} catch (Exception e) {
			log.error(e);
		}
		addDemographicCols(row, p, endDateParameter);
		addOutcomeCols(row, p, locationParameter, endDateParameter,
				lookupProgramWorkflow("Chronic care program", "Chronic care treatment status"));
		addVhwCol(row, p);
		addChronicCareCols(row, p);
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("CHRONIC_CARE_FOLLOWUP")), "");
		addVerificationCols(row, p);
		
		return row;
	}

	private void addChronicCareCols(DataSetRow row, Patient p) {
		try {
			DataSetColumn c;
			List<Obs> obs;
			Iterator<Obs> i;
			c = new DataSetColumn("Status", "Status", String.class);
			boolean deceased = (p.getDead() /*
											 * || p.getCauseOfDeath() != null ||
											 * p .getDeathDate() != null
											 */);
			row.addColumnValue(c, (deceased ? "died" : "&nbsp;"));

			// cc diagnosis
			String diag = "";
			obs = Context.getObsService().getObservationsByPersonAndConcept(
					p,
					Context.getConceptService().getConceptByName(
							"CHRONIC CARE DIAGNOSIS"));
			i = obs.iterator();
			while (i.hasNext()) {
				diag += i.next().getValueAsString(Context.getLocale()) + ", ";
			}
			c = new DataSetColumn("Diagnosis", "Diagnosis", String.class);
			row.addColumnValue(c, h(diag));
		} catch (Exception e) {
			log.error(e);
		}
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
