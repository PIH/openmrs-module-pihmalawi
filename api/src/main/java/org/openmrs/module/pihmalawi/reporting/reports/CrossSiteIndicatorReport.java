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

import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CrossSiteIndicatorReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "1e359bfd-7451-4e74-8663-00d3927f0c61";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "CrossSiteIndicatorReport.xls";

	@Autowired
	private DataFactory df;

	@Autowired
	private BaseCohortDefinitionLibrary baseCohorts;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	public CrossSiteIndicatorReport() {}

	@Override
	public String getUuid() {
		return "3217226d-2cb3-4875-aef7-48f613544503";
	}

	@Override
	public String getName() {
		return "Cross-site Indicator Report";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "Start date (Start of quarter)", Date.class));
		l.add(new Parameter("endDate", "End date (End of quarter)", Date.class));
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("indicators", Mapped.mapStraightThrough(dsd));

		// Dimensions

		CohortDefinitionDimension adultChild = new CohortDefinitionDimension();
		adultChild.addParameter(ReportingConstants.END_DATE_PARAMETER);
		adultChild.addCohortDefinition("adult", Mapped.mapStraightThrough(baseCohorts.getAge15UpByEnd()));
		adultChild.addCohortDefinition("child", Mapped.mapStraightThrough(baseCohorts.getAge0to14ByEnd()));
		dsd.addDimension("age", Mapped.mapStraightThrough(adultChild));

		// Indicators

		// HIV Q1
		CohortDefinition hasAnHccNumber = hivCohorts.getPatientsWithAnHccNumber();
		CohortDefinition hasAnArvNumber = hivCohorts.getPatientsWithAnArvNumber();
		CohortDefinition everPreArtState = hivCohorts.getPatientsEverInPreArtStateByEndDate();
		CohortDefinition everOnArvsState = hivCohorts.getPatientsEverInOnArvsStateByEndDate();
		CohortDefinition hivQ1 = df.createPatientComposition("(", everPreArtState, "OR", everOnArvsState, ") AND (", hasAnHccNumber, "OR", hasAnArvNumber, ")");
		addAdultChildIndicator(dsd, "hivq1", "Ever registered in the HIV program", hivQ1);

		// HIV Q2
		CohortDefinition everEnrolledInHivProgram = hivCohorts.getEverEnrolledInHivProgramByEndDate();
		CohortDefinition hadHivVisitBeforePeriod = hivCohorts.getPatientsWithAnHivEncounterBeforeStartDate();
		CohortDefinition hadHivVisitDuringPeriod = hivCohorts.getPatientsWithAnHivEncounterDuringPeriod();
		CohortDefinition hivQ2 = df.createPatientComposition(everEnrolledInHivProgram, "AND", hadHivVisitDuringPeriod, "AND NOT", hadHivVisitBeforePeriod);
		addAdultChildIndicator(dsd, "hivq2", "Enrolled in HIV care during the quarter", hivQ2);

		// HIV Q3
		CohortDefinition inPreArtState = hivCohorts.getPatientsInPreArtStateOnEndDate();
		CohortDefinition hivQ3 = df.createPatientComposition(inPreArtState, "AND", hasAnHccNumber);
		addAdultChildIndicator(dsd, "hivq3", "HIV patients who have not started ART", hivQ3);

		// HIV Q3b
		CohortDefinition hadPreArtVisitByEnd = hivCohorts.getPatientsWithAPreArtEncounterByEndDate();
		CohortDefinition hivQ3b = df.createPatientComposition(hivQ3, "AND NOT", hadPreArtVisitByEnd);
		addAdultChildIndicator(dsd, "hivq3b", "HIV patients who have not started ART without any visit", hivQ3b);

		// HIV Q3c
		CohortDefinition hadPreArtVisitInLast24Months = hivCohorts.getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(24);
		CohortDefinition hivQ3c = df.createPatientComposition(hivQ3, "AND NOT", hadPreArtVisitInLast24Months);
		addAdultChildIndicator(dsd, "hivq3c", "HIV patients who have not started ART without visits in last 24 months", hivQ3c);

		// HIV Q4
		CohortDefinition hadPreArtVisitInLast6Months = hivCohorts.getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(6);
		CohortDefinition hivQ4 = df.createPatientComposition(hivQ3, "AND", hadPreArtVisitInLast6Months);
		addAdultChildIndicator(dsd, "hivq4", "Patients with a visit recorded in the last 6 months", hivQ4);

		CohortDefinition hadPreArtVisitInLast12Months = hivCohorts.getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(12);
		CohortDefinition hivQ4b = df.createPatientComposition(hivQ3, "AND", hadPreArtVisitInLast12Months);
		addAdultChildIndicator(dsd, "hivq4b", "Patients with a visit recorded in the last 12 months", hivQ4b);

		CohortDefinition hadPreArtVisitInLast18Months = hivCohorts.getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(18);
		CohortDefinition hivQ4c = df.createPatientComposition(hivQ3, "AND", hadPreArtVisitInLast18Months);
		addAdultChildIndicator(dsd, "hivq4c", "Patients with a visit recorded in the last 18 months", hivQ4c);

		CohortDefinition hivQ4d = df.createPatientComposition(hivQ3, "AND", hadPreArtVisitInLast24Months);
		addAdultChildIndicator(dsd, "hivq4d", "Patients with a visit recorded in the last 24 months", hivQ4d);

		// HIV Q5
		CohortDefinition everOnArvs = hivCohorts.getPatientsEverInOnArvsStateByEndDate();
		CohortDefinition hivQ5 = everOnArvs;
		addAdultChildIndicator(dsd, "hivq5", "Ever on ART", hivQ5);

		// HIV Q6
		CohortDefinition onArvsAtEnd = hivCohorts.getPatientsInOnArvsStateOnEndDate();
		CohortDefinition hivQ6 = onArvsAtEnd;
		addAdultChildIndicator(dsd, "hivq6", "ART patients who do not have a final program exit", hivQ6);

		// HIV Q7
		CohortDefinition hadArtVisitInLast3Months = hivCohorts.getPatientsWithAnArtEncounterWithinMonthsOfEndDate(3);
		CohortDefinition hivQ7 = df.createPatientComposition(hivQ6, "AND", hadArtVisitInLast3Months);
		addAdultChildIndicator(dsd, "hivq7", "Patients with a visit recorded in the three months", hivQ7);

		// HIV Q7b
		CohortDefinition hivQ7b = df.createPatientComposition(hivQ6, "AND NOT", hadArtVisitInLast3Months);
		addAdultChildIndicator(dsd, "hivq7b", "Patients without a visit recorded in the three months", hivQ7b);

		// HIV Q8
		CohortDefinition hadArtVisitBeforePeriod = hivCohorts.getPatientsWithAnArtEncounterBeforeStartDate();
		CohortDefinition hadArtVisitDuringPeriod = hivCohorts.getPatientsWithAnArtEncounterDuringPeriod();
		CohortDefinition hivQ8 = df.createPatientComposition(hivQ5, "AND", hadArtVisitDuringPeriod, "AND NOT", hadArtVisitBeforePeriod);
		addAdultChildIndicator(dsd, "hivq8", "Initiated ART during the quarter", hivQ8);

		// HIV Q9
		CohortDefinition hadCd4RecordedInLast6Months = hivCohorts.getPatientsWithCd4RecordedWithinMonthsOfEndDate(6);
		CohortDefinition hivQ9 = df.createPatientComposition(hadCd4RecordedInLast6Months, "AND", inPreArtState);
		addAdultChildIndicator(dsd, "hivq9", "Pre-ART patients with CD4 count recorded in the last two quarters", hivQ9);

		// HIV Q9b
		CohortDefinition hasCd4InLabInLast6Months = hivCohorts.getPatientsWithCd4MeasuredInLabWithinMonthsOfEndDate(6);
		CohortDefinition hivQ9b = df.createPatientComposition(hasCd4InLabInLast6Months, "AND", inPreArtState);
		addAdultChildIndicator(dsd, "hivq9b", "Pre-ART patients with CD4 count measured in the last two quarters", hivQ9b);

		// HIV Q10
		CohortDefinition hivQ10 = df.createPatientComposition(hadCd4RecordedInLast6Months, "AND", onArvsAtEnd);
		addAdultChildIndicator(dsd, "hivq10", "ART patients with CD4 count recorded in the last two quarters", hivQ10);

		// HIV Q10b
		CohortDefinition hivQ10b = df.createPatientComposition(hasCd4InLabInLast6Months, "AND", onArvsAtEnd);
		addAdultChildIndicator(dsd, "hivq10b", "ART patients with CD4 count measured in the last two quarters", hivQ10b);

		// HIV Q11_den
		CohortDefinition anyHivVisitDuringPeriod = hivCohorts.getPatientsWithAnHivEncounterDuringPeriod();
		CohortDefinition hivQ11Den = anyHivVisitDuringPeriod;
		addAdultChildIndicator(dsd, "hivq11_den", "Patients with HIV visits in period", hivQ11Den);

		// HIV Q11
		CohortDefinition weighedDuringPeriod = hivCohorts.getPatientsWithWeightAtHivEncounterDuringPeriod();
		CohortDefinition hivQ11 = df.createPatientComposition(hivQ11Den, "AND", weighedDuringPeriod);
		addAdultChildIndicator(dsd, "hivq11", "Patients with weight recorded", hivQ11);

		// HIV Q11b
		CohortDefinition hivQ11b = df.createPatientComposition(hivQ11Den, "AND NOT", weighedDuringPeriod);
		addAdultChildIndicator(dsd, "hivq11b", "Patients without weight recorded", hivQ11b);

		return rd;
	}

	public void addAdultChildIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition cohortDefinition) {
		addIndicator(dsd, key+"_a", label + " (Adult)", cohortDefinition, "age=adult");
		addIndicator(dsd, key+"_c", label + " (Child)", cohortDefinition, "age=child");
	}

	public void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition cohortDefinition, String dimensionOptions) {
		CohortIndicator ci = new CohortIndicator();
		ci.addParameter(ReportingConstants.START_DATE_PARAMETER);
		ci.addParameter(ReportingConstants.END_DATE_PARAMETER);
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		dsd.addColumn(key, label, Mapped.mapStraightThrough(ci), dimensionOptions);
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, EXCEL_REPORT_RESOURCE_NAME));
		return l;
	}
}
