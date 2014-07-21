/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.QueryCountIndicator;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.PatientEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChronicCareIndicatorReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "27c5d192-75b6-493b-86c2-f9347f1442b3";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "ChronicCareIndicatorReport.xls";

	protected enum ColumnKey {
		Total,
		Asthma,
		CHF,
		// CKD,
		Diabetes,
		Epilepsy,
		Hypertension,
		// Mental_Health,
		Other
	}

	@Autowired
	private DataFactory df;

	@Autowired
	private BuiltInCohortDefinitionLibrary coreCohorts;

	@Autowired
	private ChronicCareCohortDefinitionLibrary ccCohorts;

	@Autowired
	private ChronicCareEncounterQueryLibrary ccEncounterQueries;

	public ChronicCareIndicatorReport() { }

	@Override
	public String getUuid() {
		return "5168d392-40ec-4687-9f86-4daef3406b5c";
	}

	@Override
	public String getName() {
		return "Chronic Care Indicators";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
		l.add(df.getOptionalLocationParameter());
		return l;
	}

	public Map<ColumnKey, CohortDefinition> getColumnDimensions() {
		Map<ColumnKey, CohortDefinition> ret = new LinkedHashMap<ColumnKey, CohortDefinition>();
		ret.put(ColumnKey.Total, null);
		ret.put(ColumnKey.Asthma, ccCohorts.getPatientsWithAsthmaDiagnosisByEndDate());
		ret.put(ColumnKey.CHF, ccCohorts.getPatientsWithHeartFailureDiagnosisByEndDate());
		//ret.put(ColumnKey.CKD, );
		ret.put(ColumnKey.Diabetes, ccCohorts.getPatientsWithDiabetesDiagnosisByEndDate());
		ret.put(ColumnKey.Epilepsy, ccCohorts.getPatientsWithEpilepsyDiagnosisByEndDate());
		ret.put(ColumnKey.Hypertension, ccCohorts.getPatientsWithHypertensionDiagnosisByEndDate());
		//ret.put(ColumnKey.Mental_Health, );
		ret.put(ColumnKey.Other, ccCohorts.getPatientsWithOtherNonCodedDiagnosisByEndDate());
		return ret;
	}


	@Override
	public ReportDefinition constructReportDefinition() {

		// Underlying cohorts and queries

		CohortDefinition onTreatmentAtEnd = ccCohorts.getPatientsInOnTreatmentStateAtLocationOnEndDate();
		CohortDefinition visitWithin3Months = ccCohorts.getPatientsWithChronicCareEncounterWithin3MonthsOfEndDate();
		CohortDefinition activeOnTx = df.getPatientsInAll(onTreatmentAtEnd, visitWithin3Months);
		CohortDefinition initialEncountersDuringPeriod = ccCohorts.getPatientsWithChronicCareInitialVisitDuringPeriod();
		CohortDefinition hospitalizedDuringPeriod = ccCohorts.getPatientsHospitalizedDuringPeriod();
		CohortDefinition hospitalizedForNcdDuringPeriod = ccCohorts.getPatientsHospitalizedForNcdDuringPeriod();
		CohortDefinition hasMoreThanMildAsthma = ccCohorts.getPatientsWithMoreThanMildPersistentAsthmaDuringPeriod();
		CohortDefinition onBeclomethasone = ccCohorts.getPatientsOnBeclomethasoneDuringPeriod();
		CohortDefinition onInsulin = ccCohorts.getPatientsOnInsulinDuringPeriod();
		CohortDefinition hadSeizuresRecorded = ccCohorts.getPatientsWithNumberOfSeizuresRecordedDuringPeriod();
		CohortDefinition hadMoreThanTwoSeizures = ccCohorts.getPatientsWithMoreThanTwoSeizuresPerMonthRecordedDuringPeriod();
		CohortDefinition hadSbpOver180 = ccCohorts.getPatientsWithSystolicBloodPressureOver180DuringPeriod();
		CohortDefinition hadDbpOver110 = ccCohorts.getPatientsWithDiastolicBloodPressureOver110DuringPeriod();
		CohortDefinition hadHighBp = df.getPatientsInAny(hadSbpOver180, hadDbpOver110);
		CohortDefinition onMultipleHypertensionMeds = ccCohorts.getPatientsOnMoreThanOneHypertensionMedicationDuringPeriod();
		CohortDefinition male = coreCohorts.getMales();
		CohortDefinition referredDuringPeriod = ccCohorts.getNewPatientsReferredDuringPeriod();
		CohortDefinition referredFromOpd = ccCohorts.getNewPatientsReferredFromOPDDuringPeriod();
		CohortDefinition referredFromInpatient = ccCohorts.getNewPatientsReferredFromInpatientWardDuringPeriod();
		CohortDefinition referredFromHealthCenter = ccCohorts.getNewPatientsReferredFromHealthCenterDuringPeriod();
		CohortDefinition referredFromOther = df.createPatientComposition(referredDuringPeriod, "AND NOT (", referredFromOpd, "OR", referredFromInpatient, "OR", referredFromHealthCenter, ")");
		CohortDefinition hadVisitInLastMonth = ccCohorts.getPatientsWithNoChronicCareEncounterWithin1MonthOfEndDate();
		CohortDefinition overAMonthLate = ccCohorts.getPatientsWhoseMostRecentAppointmentDateIsMoreThanOneMonthBeforeEndDate();
		CohortDefinition lateAndNoRecentVisit = df.createPatientComposition(overAMonthLate, "AND NOT", hadVisitInLastMonth);

		EncounterQuery visits = ccEncounterQueries.getChronicCareFollowupEncountersDuringPeriod();
		EncounterQuery visitsWithPeakFlow = ccEncounterQueries.getChronicCareEncountersWithPeakFlowRecordedDuringPeriod();
		EncounterQuery visitsWithWeight = ccEncounterQueries.getChronicCareEncountersWithWeightRecordedDuringPeriod();
		EncounterQuery visitsWithFingerstick = ccEncounterQueries.getChronicCareEncountersWithSerumGlucoseRecordedDuringPeriod();
		EncounterQuery visitsWithFingerstickOver200 = ccEncounterQueries.getChronicCareEncountersWithSerumGlucoseGreaterThan200DuringPeriod();
		EncounterQuery visitsWithSeizures = ccEncounterQueries.getChronicCareEncountersWithSeizuresRecordedDuringPeriod();
		EncounterQuery visitsWithSbp = ccEncounterQueries.getChronicCareEncountersWithSystolicBloodPressureRecordedDuringPeriod();
		EncounterQuery visitsWithSbpOver180 = ccEncounterQueries.getChronicCareEncountersWithSystolicBloodPressureOver180DuringPeriod();
		EncounterQuery visitsWithDbpOver110 = ccEncounterQueries.getChronicCareEncountersWithDiastolicBloodPressureOver110DuringPeriod();
		EncounterQuery visitsWithHighBp = df.getEncountersInAny(visitsWithSbpOver180, visitsWithDbpOver110);
		EncounterQuery visitsWithTxOutOfStock = ccEncounterQueries.getChronicCareEncountersWithPreferredTreatmentStockedOutDuringPeriod();

		// Construct Report Definition

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		SimpleIndicatorDataSetDefinition dsd = new SimpleIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("indicators", Mapped.mapStraightThrough(dsd));

		// Add Indicators

		addIndicatorForDiagnosisColumns(dsd, "1", "Patients active in CCC", activeOnTx);
		addIndicatorForDiagnosisColumns(dsd, "2", "Total new patient visits", initialEncountersDuringPeriod);
		addIndicatorForDiagnosisColumns(dsd, "3", "Total number of visits", visits);
		addIndicatorForDiagnosisColumns(dsd, "4N", "Number of patients with documented hospitalization", df.getPatientsInAll(activeOnTx, hospitalizedDuringPeriod));
		addIndicatorForDiagnosisColumns(dsd, "4D", "Number of active patients", "1");
		addIndicatorForDiagnosisColumns(dsd, "5N", "Number of patients with documented hospitalizations that are secondary to NCD", df.getPatientsInAll(activeOnTx, hospitalizedForNcdDuringPeriod));
		addIndicatorForDiagnosisColumns(dsd, "5D", "Number of patients with documented hospitalization", "4N");
		addIndicatorForDiagnosisColumns(dsd, "6N", "Asthma visits with PF recorded", df.getEncountersInAll(visits, visitsWithPeakFlow), ColumnKey.Asthma);
		addIndicatorForDiagnosisColumns(dsd, "6D", "Total asthma visits", "3", ColumnKey.Asthma);
		addIndicatorForDiagnosisColumns(dsd, "7N", "Asthma patients with > mild persistent asthma on beclomethasone", df.getPatientsInAll(hasMoreThanMildAsthma, onBeclomethasone), ColumnKey.Asthma);
		addIndicatorForDiagnosisColumns(dsd, "7D", "Asthma patients with > mild persistent asthma", hasMoreThanMildAsthma, ColumnKey.Asthma);
		addIndicatorForDiagnosisColumns(dsd, "8N", "CHF visits with weight recorded", df.getEncountersInAll(visits, visitsWithWeight), ColumnKey.CHF);
		addIndicatorForDiagnosisColumns(dsd, "8D", "CHF Visits", "3", ColumnKey.CHF);
		addIndicatorForDiagnosisColumns(dsd, "9N", "Diabetes visits with FS recorded", df.getEncountersInAll(visits, visitsWithFingerstick), ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "9D", "Diabetes visits", "3", ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "10N", "Diabetes patients on insulin", df.getPatientsInAll(activeOnTx, onInsulin), ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "10D", "Active diabetes patients", "1", ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "11N", "Diabetes visits with fingerstick value > 200", df.getEncountersInAll(visits, visitsWithFingerstickOver200), ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "11D", "Diabetes visits with fingerstick recorded", "9N", ColumnKey.Diabetes);
		addIndicatorForDiagnosisColumns(dsd, "12N", "Epilepsy visits with seizures recorded", df.getEncountersInAll(visits, visitsWithSeizures), ColumnKey.Epilepsy);
		addIndicatorForDiagnosisColumns(dsd, "12D", "Epilepsy visits", "3", ColumnKey.Epilepsy);
		addIndicatorForDiagnosisColumns(dsd, "13N", "Epilepsy patients with > 2 seizures per month", df.getPatientsInAll(hadSeizuresRecorded, hadMoreThanTwoSeizures), ColumnKey.Epilepsy);
		addIndicatorForDiagnosisColumns(dsd, "13D", "Epilepsy patients with seizures recorded", hadSeizuresRecorded, ColumnKey.Epilepsy);
		addIndicatorForDiagnosisColumns(dsd, "14N", "Hypertension visits with systolic BP recorded", df.getEncountersInAll(visits, visitsWithSbp), ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "14D", "Hypertension visits", "3", ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "15N", "Hypertension patients with SBP > 180 or DBP > 110 on 2 or more medications", df.getPatientsInAll(hadHighBp, onMultipleHypertensionMeds), ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "15D", "Hypertension patients with SBP > 180 or DBP > 110", hadHighBp, ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "16N", "Hypertension visits with SBP > 180 or DBP > 110", visitsWithHighBp, ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "16D", "Hypertension visits", "3", ColumnKey.Hypertension);
		addIndicatorForDiagnosisColumns(dsd, "17", "Number of new patients enrolled who are male", df.getPatientsInAll(initialEncountersDuringPeriod, male));
		addIndicatorForDiagnosisColumns(dsd, "18", "Number of new patients referred from OPD", referredFromOpd);
		addIndicatorForDiagnosisColumns(dsd, "19", "Number of new patients referred from inpatient ward", referredFromInpatient);
		addIndicatorForDiagnosisColumns(dsd, "20", "Number of new patients referred from health center", referredFromHealthCenter);
		addIndicatorForDiagnosisColumns(dsd, "21", "Number of new patients referred from other (community event, ANC, other)", referredFromOther);
		addIndicatorForDiagnosisColumns(dsd, "22N", "Number of currently enrolled patients without a visit > 1 month past their last scheduled appointment", df.getPatientsInAll(onTreatmentAtEnd, lateAndNoRecentVisit));
		addIndicatorForDiagnosisColumns(dsd, "22D", "Number currently enrolled", onTreatmentAtEnd);
		addIndicatorForDiagnosisColumns(dsd, "23", "Number of total visits with 'preferred treatment stocked out'", visitsWithTxOutOfStock);

		return rd;
	}

	/**
	 * Utility method to add an indicator using the given query
	 */
	protected void addIndicator(SimpleIndicatorDataSetDefinition dsd, String name, String label, Query query) {
		QueryCountIndicator ci = new QueryCountIndicator();
		ci.setParameters(query.getParameters());
		ci.setQuery(Mapped.mapStraightThrough(query));
		dsd.addColumn(name, label, Mapped.mapStraightThrough(ci));
	}

	/**
	 * Add an indicator for all of the passed in diagnosis columns.  If no columns are passed in, assume it is for all diagnosis columns
	 */
	protected void addIndicatorForDiagnosisColumns(SimpleIndicatorDataSetDefinition dsd, String name, String label, Query query, ColumnKey... diagnosisColumns) {
		if (diagnosisColumns == null || diagnosisColumns.length == 0) {
			diagnosisColumns = ColumnKey.values();
		}
		for (ColumnKey diagnosisColumn : diagnosisColumns) {
			String columnName = name + "." + diagnosisColumn;
			String columnLabel = label + " - " + diagnosisColumn;
			Query columnQuery = query;
			CohortDefinition diagnosisCohortDefinition = getColumnDimensions().get(diagnosisColumn);
			if (diagnosisColumn != ColumnKey.Total) {
				if (diagnosisCohortDefinition == null) {
					throw new IllegalArgumentException("No Cohort Definition found for " + diagnosisColumn);
				}
				if (query instanceof CohortDefinition) {
					columnQuery = df.getPatientsInAll((CohortDefinition) query, diagnosisCohortDefinition);
				}
				else if (query instanceof EncounterQuery) {
					columnQuery = df.getEncountersInAll((EncounterQuery) query, new PatientEncounterQuery(diagnosisCohortDefinition));
				}
			}
			addIndicator(dsd, columnName, columnLabel, columnQuery);
		}
	}

	/**
	 * If the same value has previously been added, use this to refer to that value by indicatorNum, rather than duplicate the same logic
	 */
	protected void addIndicatorForDiagnosisColumns(SimpleIndicatorDataSetDefinition dsd, String name, String label, String existingColumnName, ColumnKey... diagnosisColumns) {
		if (diagnosisColumns == null || diagnosisColumns.length == 0) {
			diagnosisColumns = ColumnKey.values();
		}
		for (ColumnKey diagnosisColumn : diagnosisColumns) {
			String columnNameToFind = existingColumnName + "." + diagnosisColumn;
			String columnName = name + "." + diagnosisColumn;
			String columnLabel = label + " - " + diagnosisColumn;
			Mapped<? extends Indicator> indicator = null;
			for (SimpleIndicatorDataSetDefinition.SimpleIndicatorColumn column : dsd.getColumns()) {
				if (column.getName().equals(columnNameToFind)) {
					indicator = column.getIndicator();
				}
			}
			if (indicator == null) {
				throw new IllegalArgumentException("No existing column with number <" + columnNameToFind + "> can be found");
			}
			dsd.addColumn(columnName, columnLabel, indicator);
		}
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, EXCEL_REPORT_RESOURCE_NAME));
		return l;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
