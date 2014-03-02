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

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class ApzuHivIndicatorsReport extends BaseReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "b98ab976-9c9d-4a28-9760-ac3119cbef58";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "ApzuHivIndicatorsReport.xls";

	protected String reportTag = "apzuhiv";

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	public ApzuHivIndicatorsReport() {}

	@Override
	public String getUuid() {
		return "167cf668-0715-488b-b159-d5f391774088";
	}

	@Override
	public String getName() {
		return "APZU HIV Indicators";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getStartDateParameter());
		l.add(df.getEndDateParameter());
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		MultiParameterDataSetDefinition multiPeriodDsd = new MultiParameterDataSetDefinition();
		multiPeriodDsd.setParameters(getParameters());
		rd.addDataSetDefinition("Apzu_Hiv_Indicators", Mapped.mapStraightThrough(multiPeriodDsd));

		// Base Data Set Definition

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		dsd.addParameter(df.getLocationParameter());
		multiPeriodDsd.setBaseDefinition(dsd);

		// Underlying cohorts

		CohortDefinition partPeriod = hivCohorts.getStartedPreArtWithHccNumberAtLocationInPeriod();
		CohortDefinition partActive = hivCohorts.getInPreArtStateWithHccNumberAtLocationOnEndDate();
		CohortDefinition partOnArt = hivCohorts.getTransitionedFromPreArtToArtAtLocationDuringPeriod();
		CohortDefinition artPeriod = hivCohorts.getStartedOnArtStateAtLocationDuringPeriod();
		CohortDefinition artActive = hivCohorts.getInOnArtStateAtLocationOnEndDate();
		CohortDefinition artEver = hivCohorts.getEverEnrolledInArtAtLocationByEndDate();
		CohortDefinition hivDefaulted = hivCohorts.getEverArtDefaultedDuringPeriodAndStillDefaultedOnEndDate();
		CohortDefinition artMissing3Weeks = hivCohorts.getInArtAndLastAppointmentDate3WeeksOrMoreByEndDate();
		CohortDefinition artMissing2Months = hivCohorts.getInArtAndLastAppointmentDate2MonthsOrMoreByEndDate();
		CohortDefinition partSixMonths = hivCohorts.getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(6);
		CohortDefinition partActiveWithVisit = df.getPatientsInAll(partSixMonths, partActive);
		CohortDefinition artThreeMonths = hivCohorts.getPatientsWithAnArtEncounterWithinMonthsOfEndDate(3);
		CohortDefinition artActiveWithVisit = df.getPatientsInAll(artThreeMonths, artActive);
		CohortDefinition cd4Reported = hivCohorts.getPatientsWithCd4RecordedAtHivEncounterWithinMonthsOfEndDate(6);
		CohortDefinition partActiveWithCd4 = df.getPatientsInAll(partActive, cd4Reported);
		CohortDefinition diedWithin3Months = hivCohorts.getDiedAtLocationWithinMonthsOfEndDate(3);
		CohortDefinition artEverAndDiedWithin3Months = df.getPatientsInAll(artEver, diedWithin3Months);

		// Monthly Indicators

		addColumn(dsd, "3m", reportTag + ": Pre-ART enrolled", partPeriod);
		addColumn(dsd, "4m", reportTag + ": Pre-ART active", partActive);
		addColumn(dsd, "5m", reportTag + ": Pre-ART Started ART", partOnArt);
		addColumn(dsd, "6m", reportTag + ": ART enrolled", artPeriod);
		addColumn(dsd, "7am", reportTag + ": ART overdue 3 weeks", artMissing3Weeks);
		addColumn(dsd, "7bm", reportTag + ": ART overdue 2 months", artMissing2Months);
		addColumn(dsd, "7cm", reportTag + ": Defaulted", hivDefaulted);

		// Quarterly Indicators

		addColumn(dsd, "3q", reportTag + ": Pre-ART with visit", partActiveWithVisit);
		addColumn(dsd, "4q", reportTag + ": Pre-ART with CD4", partActiveWithCd4);
		addColumn(dsd, "5q", reportTag + ": ART ever", artEver);
		addColumn(dsd, "6q", reportTag + ": ART with visit", artActiveWithVisit);
		addColumn(dsd, "7q", reportTag + ": died", artEverAndDiedWithin3Months);

		// Iterations - for each HIV static location, create an indicator data set for that location

		for (Location location : hivMetadata.getHivStaticLocations()) {
			Map<String, Object> iteration = Mapped.straightThroughMappings(dsd);
			iteration.put(df.getLocationParameter().getName(), location);
			multiPeriodDsd.addIteration(iteration);
		}

		return rd;
	}

	protected void addColumn(CohortIndicatorDataSetDefinition dsd, String name, String label, CohortDefinition cohortDefinition) {
		CohortIndicator ci = new CohortIndicator();
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setParameters(cohortDefinition.getParameters());
		ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		dsd.addColumn(name, label, Mapped.mapStraightThrough(ci), "");
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, EXCEL_REPORT_RESOURCE_NAME);
		design.addPropertyValue("repeatingSections", "sheet:1,column:5,dataset:Apzu_Hiv_Indicators");
		return Arrays.asList(design);
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
