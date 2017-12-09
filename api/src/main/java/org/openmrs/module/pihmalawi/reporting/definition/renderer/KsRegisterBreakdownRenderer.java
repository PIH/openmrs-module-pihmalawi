package org.openmrs.module.pihmalawi.reporting.definition.renderer;

import java.util.*;

import org.openmrs.*;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.common.ProgramHelper;
import org.openmrs.module.pihmalawi.common.PatientDataHelper;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class KsRegisterBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p, PatientIdentifierType patientIdentifierType, Location location, Date startDate, Date endDate) {

		PatientDataHelper pdh = new PatientDataHelper();
		ProgramHelper ph = new ProgramHelper();
		HivMetadata hivMetadata = new HivMetadata();

		DataSetRow row = new DataSetRow();
		pdh.addCol(row, "#", p.getPatientId());
		pdh.addCol(row, "KS ID", pdh.preferredIdentifierAtLocation(p, patientIdentifierType, location));
		pdh.addCol(row, "ART ID", pdh.preferredIdentifierAtLocation(p, lookupPatientIdentifierType("ARV Number"), location));
		pdh.addCol(row, "Birthdate", pdh.getBirthdate(p));
		pdh.addCol(row, "Gender", pdh.getGender(p));
		pdh.addCol(row, "VHW", pdh.vhwName(p, false));
		pdh.addCol(row, "Village", pdh.getVillage(p));

		PatientProgram latestKsProgram = ph.getMostRecentProgramEnrollmentAtLocation(p, lookupProgram("Kaposis sarcoma program"), location);

		Date latestKsProgramDate = null;
		if (latestKsProgram != null) {
			latestKsProgramDate = latestKsProgram.getDateEnrolled();
			pdh.addCol(row, "KS Program Enrollment Date", pdh.formatYmd(latestKsProgramDate));

			for (PatientState ps : ph.getActiveStatesOnDate(p, latestKsProgramDate)) {
				String programName = ps.getPatientProgram().getProgram().getName();
				pdh.addCol(row, programName + " Status at Enrollment", pdh.formatStateName(ps));
				pdh.addCol(row, programName + " Status Date at Enrollment", pdh.formatStateStartDate(ps));
			}
		}

		Obs mostRecentDxDate = pdh.getLatestObs(p, "DATE OF HIV DIAGNOSIS", null, endDate);
		pdh.addCol(row, "Date HIV Diagnosis", pdh.formatValueDatetime(mostRecentDxDate));

		Program hivProgram = hivMetadata.getHivProgram();
		ProgramWorkflowState onArvState = hivMetadata.getOnArvsState();

		PatientState earliestOnArvsState = ph.getFirstTimeInState(p, hivProgram, onArvState, endDate);
		Date arvStartDate = (earliestOnArvsState == null ? null : earliestOnArvsState.getStartDate());

		if (latestKsProgramDate != null) {
			pdh.addCol(row, "On ART at KS Enrollment", arvStartDate != null && arvStartDate.compareTo(latestKsProgramDate) <= 0);
		}

		Map<String, String> reasonsForStartingArvs = pdh.getReasonStartingArvs(p, endDate);
		for (String reasonKey : reasonsForStartingArvs.keySet()) {
			pdh.addCol(row, "ARV Reason " + reasonKey, reasonsForStartingArvs.get(reasonKey));
		}

		if (arvStartDate != null) {
			pdh.addCol(row, "Start Date for ART", pdh.formatStateStartDate(earliestOnArvsState));
			if (latestKsProgramDate != null) {
				pdh.addCol(row, "Months on ART at KS Enrollment", DateUtil.monthsBetween(arvStartDate, latestKsProgramDate));
			}
		}

		if (latestKsProgramDate != null) {
			Obs artRegimen = pdh.getLatestObs(p, "Malawi Antiretroviral drugs received", null, latestKsProgramDate);
			pdh.addCol(row, "ART Regimen Obs at Enrollment", pdh.formatValue(artRegimen));

			Set<Concept> drugOrdersAtEnrollment = pdh.getDrugsTakingOnDate(p, latestKsProgramDate);
			pdh.addCol(row, "Drugs Taking at Enrollment", pdh.formatConcepts(drugOrdersAtEnrollment, "+"));

			Obs cd4 = pdh.getLatestObs(p, "CD4 count", null, latestKsProgramDate);
			pdh.addCol(row, "CD4 at enrollment", pdh.formatValue(cd4));
			pdh.addCol(row, "CD4 at enrollment date", pdh.formatObsDatetime(cd4));
		}

		Obs height = pdh.getLatestObs(p, "Height (cm)", null, endDate);
		pdh.addCol(row, "Latest Height", pdh.formatValue(height));
		pdh.addCol(row, "Latest Height Date", pdh.formatObsDatetime(height));

		Obs firstTaxolObs = pdh.getEarliestObs(p, "Paclitaxel (taxol) dose received", null, endDate);
		pdh.addCol(row, "First Taxol Dose Received Date", pdh.formatObsDatetime(firstTaxolObs));

		DrugOrder firstTaxolOrder = pdh.getEarliestDrugOrder(p, "Paclitaxel", endDate);
		pdh.addCol(row, "First Taxol Drug Order Date", pdh.formatOrderStartDate(firstTaxolOrder));

		Date firstTaxolDate = null;
		if (firstTaxolObs != null) {
			firstTaxolDate = firstTaxolObs.getObsDatetime();
		}
		if (firstTaxolOrder != null) {
			if (firstTaxolDate == null || firstTaxolOrder.getStartDate().before(firstTaxolDate)) {
				firstTaxolDate = firstTaxolOrder.getStartDate();
			}
		}
		pdh.addCol(row, "First Taxol Date", pdh.formatYmd(firstTaxolDate));

		if (firstTaxolDate != null) {
			Obs cd4AtTaxol = pdh.getLatestObs(p, "CD4 count", null, firstTaxolDate);
			pdh.addCol(row, "Most recent CD4 at First Taxol", pdh.formatValue(cd4AtTaxol));
			pdh.addCol(row, "Most recent CD4 at First Taxol Date", pdh.formatObsDatetime(cd4AtTaxol));
		}

		for (PatientState ps : ph.getActiveStatesOnDate(p, endDate)) {
			String ed = pdh.formatYmd(endDate);
			String programName = ps.getPatientProgram().getProgram().getName();
			pdh.addCol(row, programName + " Status on " + ed, pdh.formatStateName(ps));
			pdh.addCol(row, programName + " Status Date on " + ed, pdh.formatStateStartDate(ps));
		}

		pdh.addCol(row, "Death Date", pdh.formatYmd(p.getDeathDate()));

		return row;
	}
}
