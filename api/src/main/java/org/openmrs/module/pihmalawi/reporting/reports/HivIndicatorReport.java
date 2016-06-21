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

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
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

@Component
public class HivIndicatorReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "cb82fd24-3266-11e6-9f57-54ee7513a7ff";

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

    @Autowired
    private BaseCohortDefinitionLibrary baseCohorts;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	public HivIndicatorReport() {}

	@Override
	public String getUuid() {
		return "c472062b-3266-11e6-9f57-54ee7513a7ff";
	}

	@Override
	public String getName() {
		return "HIV Indicators";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
        l.add(ReportingConstants.END_DATE_PARAMETER);
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		// Base Data Set Definition

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("indicators", Mapped.mapStraightThrough(dsd));

        // Base cohort is defined as all patients who are currently considered active HIV patients based on enrollment data
        // These are patients who are either in pre-art or art state in hiv program, and also who have an hcc or arv number
        // These identifiers are what distinguish current program patients vs. historical (old pre-art) patients who are not followed

        CohortDefinition inRequiredState = hivCohorts.getPatientsInPreArtOrOnArvsStateOnEndDate();
        CohortDefinition hasAnHccNumber = hivCohorts.getPatientsWithAnHccNumber();
        CohortDefinition hasAnArvNumber = hivCohorts.getPatientsWithAnArvNumber();
        CohortDefinition hivPatients = df.createPatientComposition(inRequiredState, " AND (", hasAnHccNumber, "OR", hasAnArvNumber, ")");

        CohortDefinition hivWithVisit = df.getPatientsInAll(hivPatients, df.getAnyEncounterOfTypesWithinMonthsByEndDate(hivMetadata.getHivAndExposedChildEncounterTypes(), 24));

        CohortDefinition adults = df.getPatientsInAll(hivWithVisit, baseCohorts.getAge15UpByEnd());
        CohortDefinition children = df.getPatientsInAll(hivWithVisit, baseCohorts.getAge0to14ByEnd());

		CohortDefinition art = df.getPatientsInAll(hivWithVisit, hivCohorts.getPatientsEverInOnArvsStateByEndDate());
        CohortDefinition notArt = df.createPatientComposition(hivWithVisit, "AND NOT", hivCohorts.getPatientsEverInOnArvsStateByEndDate());

        CohortDefinition visitWithin6Months = df.getAnyEncounterOfTypesWithinMonthsByEndDate(hivMetadata.getHivAndExposedChildEncounterTypes(), 6);
        CohortDefinition cd4InLast12Months = hivCohorts.getPatientsWithCd4RecordedWithinMonthsOfEndDate(12);
        CohortDefinition viralLoadEver = hivCohorts.getPatientsWithViralLoadRecordedByEndDate();

        // Indicators

		addColumn(dsd, "hivPatients", hivPatients);
        addColumn(dsd, "visitWithin24Months", hivWithVisit);
        addColumn(dsd, "visitWithin24Months_adults", adults);
        addColumn(dsd, "visitWithin24Months_children", children);
        addColumn(dsd, "visitWithin24Months_art", art);
        addColumn(dsd, "visitWithin24Months_art_visitWithin6Months", df.getPatientsInAll(art, visitWithin6Months));
        addColumn(dsd, "visitWithin24Months_art_cd4Within12Months", df.getPatientsInAll(art, cd4InLast12Months));
        addColumn(dsd, "visitWithin24Months_art_viralLoadEver", df.getPatientsInAll(art, viralLoadEver));
        addColumn(dsd, "visitWithin24Months_not_art", notArt);
        addColumn(dsd, "visitWithin24Months_not_art_visitWithin6Months", df.getPatientsInAll(notArt, visitWithin6Months));
        addColumn(dsd, "visitWithin24Months_not_art_cd4Within12Months", df.getPatientsInAll(notArt, cd4InLast12Months));

		return rd;
	}

	protected void addColumn(CohortIndicatorDataSetDefinition dsd, String name, CohortDefinition cohortDefinition) {
        CohortIndicator ci = new CohortIndicator();
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setParameters(cohortDefinition.getParameters());
		ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		dsd.addColumn(name, name, Mapped.mapStraightThrough(ci), "");
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = createExcelDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition);
		return Arrays.asList(design);
	}
}
