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
import org.openmrs.module.pihmalawi.metadata.TbMetadata;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.renderer.TbRegisterBreakdownRenderer;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
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
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TbRegister extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "e5e8b9b7-06aa-11e4-b4d8-54ee7513a7ff";

	@Autowired
	private DataFactory df;

	@Autowired
	private TbMetadata metadata;

	public TbRegister() {}

	@Override
	public String getUuid() {
		return "e6856be7-06aa-11e4-b4d8-54ee7513a7ff";
	}

	@Override
	public String getName() {
		return "Tuberculosis Register";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getEndDateParameter());
		l.add(df.getRequiredLocationParameter());
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
		rd.addDataSetDefinition("defaultDataSet", Mapped.mapStraightThrough(dsd));

		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setEncounterTypeList(Arrays.asList(metadata.getTbInitialEncounterType()));
		ecd.addParameter(new Parameter("locationList", "location", Location.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));

		CohortIndicator ci = new CohortIndicator();
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setParameters(getParameters());
		ci.setCohortDefinition(Mapped.map(ecd, "locationList=${location},onOrBefore=${endDate}"));

		dsd.addColumn("breakdown", "Breakdown", Mapped.mapStraightThrough(ci), "");

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();

		try {
			HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
			dsd.setPatientIdentifierType(metadata.getDistrictTbNumberIdentifierType());
			dsd.setHtmlBreakdownPatientRowClassname(TbRegisterBreakdownRenderer.class.getName());

			Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
			m.put("breakdown", Mapped.mapStraightThrough(dsd));

			ReportingSerializer serializer = new ReportingSerializer();
			String designXml = serializer.serialize(m);

			final ReportDesign design = new ReportDesign();
			design.setName("Tuberculosis Register");
			design.setReportDefinition(reportDefinition);
			design.setRendererType(CohortDetailReportRenderer.class);

			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("designFile");
			resource.setContents(designXml.getBytes());
			design.addResource(resource);
			resource.setReportDesign(design);

			l.add(design);
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to create renderer for Tuberculosis Register", e);
		}

		return l;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
