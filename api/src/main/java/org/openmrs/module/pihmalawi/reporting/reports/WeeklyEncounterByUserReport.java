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
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.TbMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.EncounterBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeeklyEncounterByUserReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "d4f91c60-7872-4128-b486-2e292b8c6d98";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "WeeklyEncounterByUser.xls";

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private ChronicCareMetadata chronicCareMetadata;

	@Autowired
	private DataFactory df;

	public WeeklyEncounterByUserReport() {}

	@Override
	public String getUuid() {
		return "d33427da-8a5b-4a21-8002-964207af0216";
	}

	@Override
	public String getName() {
		return "Weekly Encounter By User";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
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

		EncounterBreakdownDataSetDefinition dsd = new EncounterBreakdownDataSetDefinition();
		dsd.setParameters(getParameters());
		dsd.setNumberOfWeeks(12);
		dsd.setGrouping(EncounterBreakdownDataSetDefinition.Grouping.User);

		List<EncounterType> types = new ArrayList<EncounterType>();
		types.add(hivMetadata.getArtInitialEncounterType());
		types.add(hivMetadata.getArtFollowupEncounterType());
		types.add(hivMetadata.getPreArtInitialEncounterType());
		types.add(hivMetadata.getPreArtFollowupEncounterType());
		types.add(hivMetadata.getExposedChildInitialEncounterType());
		types.add(hivMetadata.getExposedChildFollowupEncounterType());
		types.add(hivMetadata.getAppointmentEncounterType());
		types.add(hivMetadata.getLabEncounterType());
		types.add(chronicCareMetadata.getChronicCareInitialEncounterType());
		types.add(chronicCareMetadata.getChronicCareFollowupEncounterType());
		dsd.setTypes(types);

		rd.addDataSetDefinition("dataset", Mapped.mapStraightThrough(dsd));

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, EXCEL_REPORT_RESOURCE_NAME));
		return l;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
}
