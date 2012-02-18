package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class DemographicsOnlyBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		addDemographicCols(row, p, endDateParameter);
		return row;
	}

}
