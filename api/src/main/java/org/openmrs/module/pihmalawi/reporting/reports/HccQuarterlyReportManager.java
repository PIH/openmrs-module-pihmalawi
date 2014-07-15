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

import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class HccQuarterlyReportManager extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "59b45872-e6aa-11e3-89c9-0023156365e4";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "HccQuarterlyReport.xls";

	@Autowired
	private DataFactory df;

	@Autowired
	private BuiltInCohortDefinitionLibrary builtInCohorts;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	public HccQuarterlyReportManager() {}

	@Override
	public String getUuid() {
		return "62f5c617-e6aa-11e3-89c9-0023156365e4";
	}

	@Override
	public String getName() {
		return "HCC Quarterly";
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
		l.add(df.getOptionalLocationParameter());
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

		// Underlying cohorts

		CohortDefinition partEver = hivCohorts.getStartedPreArtWithHccNumberAtLocationByEndDate();
		CohortDefinition exposedEver = hivCohorts.getStartedExposedChildWithHccNumberAtLocationByEndDate();
		CohortDefinition hccEver = df.getPatientsInAny(partEver, exposedEver);

		CohortDefinition partPeriod = hivCohorts.getStartedPreArtWithHccNumberAtLocationInPeriod();
		CohortDefinition exposedPeriod = hivCohorts.getStartedExposedChildWithHccNumberAtLocationInPeriod();
		CohortDefinition hccPeriod = df.getPatientsInAny(partPeriod, exposedPeriod);

		CohortDefinition males = builtInCohorts.getMales();

		CohortDefinition preArt0to1m = hivCohorts.getPatients0to1MonthsOldAtPreArtStateStartAtLocationByEndDate();
		CohortDefinition exposed0to1m = hivCohorts.getPatients0to1MonthsOldAtExposedChildStateStartAtLocationByEndDate();
		CohortDefinition hcc0to1m = df.getPatientsInAny(preArt0to1m, exposed0to1m);

		CohortDefinition preArt2to23m = hivCohorts.getPatients2to23MonthsOldAtPreArtStateStartAtLocationByEndDate();
		CohortDefinition exposed2to23m = hivCohorts.getPatients2to23MonthsOldAtExposedChildStateStartAtLocationByEndDate();
		CohortDefinition hcc2to23m = df.getPatientsInAny(preArt2to23m, exposed2to23m);

		CohortDefinition preArt2to14y = hivCohorts.getPatients2to14YearsOldAtPreArtStateStartAtLocationByEndDate();
		CohortDefinition exposed2to14y = hivCohorts.getPatients2to14YearsOldAtExposedChildStateStartAtLocationByEndDate();
		CohortDefinition hcc2to14y = df.getPatientsInAny(preArt2to14y, exposed2to14y);

		CohortDefinition preArt15up = hivCohorts.getPatients15YearsUpAtPreArtStateStartAtLocationByEndDate();
		CohortDefinition exposed15up = hivCohorts.getPatients15YearsUpAtExposedChildStateStartAtLocationByEndDate();
		CohortDefinition hcc15up = df.getPatientsInAny(preArt15up, exposed15up);

		CohortDefinition partActive = hivCohorts.getInPreArtStateWithHccNumberAtLocationOnEndDate();
		CohortDefinition hccMissingAppointment = hivCohorts.getLastPreArtOrExposedAppointmentDate8weeksOrMoreByEndDate();
		CohortDefinition partActiveNoDefaulters = df.createPatientComposition(partActive, "AND NOT", hccMissingAppointment);
		CohortDefinition partToArt = hivCohorts.getTransitionedFromPreArtToArtAtLocationByEnd();
		CohortDefinition partDefaulted = df.getPatientsInAll(partActive, hccMissingAppointment);
		CohortDefinition partTransferredOut = hivCohorts.getTransferredOutOfPreArtAtLocationByEnd();
		CohortDefinition partDied = hivCohorts.getDiedWhilePreArtAtLocationByEnd();
		CohortDefinition partOutcomes = df.getPatientsInAny(partActiveNoDefaulters, partToArt, partDefaulted, partTransferredOut, partDied);

		// Add indicators

		addIndicator(dsd, "6_quarter", "Total registered in quarter", hccPeriod);
		addIndicator(dsd, "6_ever", "Total registered ever", hccEver);
		addIndicator(dsd, "10_quarter", "Males (all ages)", hccPeriod, males);
		addIndicator(dsd, "10_ever", "Males (all ages)", hccEver, males);
		addIndicator(dsd, "13_quarter", "Infants below 2 months at enrollment", hccPeriod, hcc0to1m);
		addIndicator(dsd, "13_ever", "Infants below 2 months at enrollment", hccEver, hcc0to1m);
		addIndicator(dsd, "14_quarter", "Children below 24 months at enrollment", hccPeriod, hcc2to23m);
		addIndicator(dsd, "14_ever", "Children below 24 months at enrollment", hccEver, hcc2to23m);
		addIndicator(dsd, "15_quarter", "Children at HCC enrollment", hccPeriod, hcc2to14y);
		addIndicator(dsd, "15_ever", "Children at HCC enrollment", hccEver, hcc2to14y);
		addIndicator(dsd, "16_quarter", "Adults at HCC enrollment", hccPeriod, hcc15up);
		addIndicator(dsd, "16_ever", "Adults at HCC enrollment", hccEver, hcc15up);
		addIndicator(dsd, "17_quarter", "Total exposed in quarter", exposedPeriod);
		addIndicator(dsd, "17_ever", "Total exposed ever", exposedEver);
		addIndicator(dsd, "18_quarter", "Total Pre-ART in quarter", partPeriod);
		addIndicator(dsd, "18_ever", "Total Pre-ART ever", partEver);
		addIndicator(dsd, "19", "Pre-ART active", partActiveNoDefaulters);
		addIndicator(dsd, "20", "Pre-ART started ART", partToArt);
		addIndicator(dsd, "21", "Pre-ART transferred out", partTransferredOut);
		addIndicator(dsd, "22", "Pre-ART defaulted", partDefaulted);
		addIndicator(dsd, "23", "Pre-ART died", partDied);
		addIndicator(dsd, "23_check", "Pre-ART any other outcome", df.createPatientComposition(partEver, "AND NOT", partOutcomes));

		return rd;
	}

	public void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition... cohortDefinitions) {
		CohortIndicator ci = new CohortIndicator();
		ci.addParameter(ReportingConstants.START_DATE_PARAMETER);
		ci.addParameter(ReportingConstants.END_DATE_PARAMETER);
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		if (cohortDefinitions.length == 1) {
			ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinitions[0]));
		}
		else {
			ci.setCohortDefinition(Mapped.mapStraightThrough(df.getPatientsInAll(cohortDefinitions)));
		}
		dsd.addColumn(key, label, Mapped.mapStraightThrough(ci), "");
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
