package org.openmrs.module.pihmalawi.reports.renderer;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.util.Arrays;
import java.util.Date;

public class GenericApzuBreakdownRenderer extends BreakdownRowRenderer {

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
		try {
			addCol(row, "All ARV #s",
					identifiers(p, lookupPatientIdentifierType("ARV Number")));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All TB #s",
					identifiers(p, lookupPatientIdentifierType("District TB Number")));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All National IDs",
					identifiers(p, lookupPatientIdentifierType("National ID")));
		} catch (Exception e) {
			log.error(e);
		}
        try {
            // MLW-142 ticket
            addCol(row, "Chronic Care Number",
                    identifiers(p, lookupPatientIdentifierType("Chronic Care Number")));
        } catch (Exception e) {
            log.error(e);
        }

		try {
			addCol(row, "All KS #s",
					identifiers(p, lookupPatientIdentifierType("KS Number")));
		} catch (Exception e) {
			log.error(e);
		}

		addDemographicCols(row, p, endDateParameter);
		
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("PATIENT_EVALUATION"),
				lookupEncounterType("CHEMOTHERAPY"),
				lookupEncounterType("CHRONIC_CARE_INITIAL"),
				lookupEncounterType("CHORNIC_CARE_FOLLOWUP"),
				lookupEncounterType("TB_INITIAL"),
				lookupEncounterType("TB_FOLLOWUP"),
				lookupEncounterType("ART_INITIAL"),
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_INITIAL"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_INITIAL"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")), "");
		addAllEnrollmentsCol(row, p);

		return row;
	}

}
