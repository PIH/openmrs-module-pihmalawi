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

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.dataset.FindPatientsToMergeSoundexDataSetDefinition;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// @Component - This report current fails due to a StackOverflowError (and did before it was migrated). Commenting out and we can decide if we want to fix or not
public class FindPatientsToMergeReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "b4384d30-9135-46bf-9abb-486a2491566a";

	@Autowired
	HivMetadata hivMetadata;

	public FindPatientsToMergeReport() {}

	@Override
	public String getUuid() {
		return "838c2121-ea79-40b2-8331-953754af76bc";
	}

	@Override
	public String getName() {
		return "Find patients to merge (SLOW)";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
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
		rd.addDataSetDefinition("defaultDataSet", Mapped.mapStraightThrough(dsd));

		CohortIndicator allPatients = new CohortIndicator();
		allPatients.setType(CohortIndicator.IndicatorType.COUNT);
		allPatients.setCohortDefinition(Mapped.mapStraightThrough(new AllPatientsCohortDefinition()));
		dsd.addColumn("patients", "Patients", Mapped.mapStraightThrough(allPatients), "");

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createReportDesign("Find patients to merge in HCC Soundex", reportDefinition, hivMetadata.getHivEncounterTypes(), hivMetadata.getHccNumberIdentifierType()));
		l.add(createReportDesign("Find patients to merge in Pre-ART Soundex", reportDefinition, hivMetadata.getHivAndExposedChildEncounterTypes(), null));
		return l;
	}

	private ReportDesign createReportDesign(String name, ReportDefinition reportDefinition, List<EncounterType> encounterTypesForSummary, PatientIdentifierType identifierTypeRequiredToLookForDuplicates) {
		final ReportDesign design = new ReportDesign();
		try {
			FindPatientsToMergeSoundexDataSetDefinition dsd = new FindPatientsToMergeSoundexDataSetDefinition();
			dsd.setEncounterTypesToLookForDuplicates(hivMetadata.getPreArtEncounterTypes());
			dsd.setEncounterTypesForSummary(encounterTypesForSummary);
			dsd.setProgramWorkflowForSummary(hivMetadata.getTreatmentStatusWorkfow());
			dsd.setPatientIdentifierTypeRequiredToLookForDuplicates(identifierTypeRequiredToLookForDuplicates);

			Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
			m.put("patients", new Mapped<DataSetDefinition>(dsd, null));

			ReportingSerializer serializer = new ReportingSerializer();
			String designXml = serializer.serialize(m);

			design.setName(name);
			design.setReportDefinition(reportDefinition);
			design.setRendererType(CohortDetailReportRenderer.class);

			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("designFile");
			resource.setContents(designXml.getBytes());
			design.addResource(resource);
			resource.setReportDesign(design);
		}
		catch (Exception e) {
			throw new IllegalStateException("Error occurred creating report design", e);
		}
		return design;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
