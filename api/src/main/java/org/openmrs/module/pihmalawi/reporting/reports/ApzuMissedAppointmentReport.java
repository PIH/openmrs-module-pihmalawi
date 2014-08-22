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
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition.Mode;
import org.openmrs.module.pihmalawi.reporting.definition.renderer.MissedAppointmentReportRenderer;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class ApzuMissedAppointmentReport extends ApzuReportManager {

	/**
	 * @see ApzuReportManager#getDescription() () ()
	 */
	@Override
	public String getDescription() {
		return "";
	}

	/**
	 * @see ApzuReportManager#getParameters() ()
	 */
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("endDate", "End date (Sunday)", Date.class));
		l.add(new Parameter("mode", "Mode", Mode.class));
		return l;
	}

	/**
	 * @return the Cohort Definition that should be used to filter the set of eligible patients for the report
	 */
	public abstract CohortDefinition getBaseCohort();

	/**
	 * @return the list of Locations that should be included in the report.
	 */
	public abstract List<Location> getLocations();

	/**
	 * @return the TreatmentGroup to run this report for
	 */
	public abstract TreatmentGroup getTreatmentGroup();

	/**
	 * @return the uuid for the renderer report design
	 */
	public abstract String getReportDesignUuid();

	/**
	 * @see ApzuReportManager#constructReportDefinition()
	 */
	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		MissedAppointmentDataSetDefinition dsd = new MissedAppointmentDataSetDefinition();
		dsd.setParameters(getParameters()); // endDate and mode
		dsd.setBaseCohort(getBaseCohort());
		dsd.setLocations(getLocations());
		dsd.setTreatmentGroup(getTreatmentGroup());
		rd.addDataSetDefinition("data", Mapped.mapStraightThrough(dsd));

		return rd;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		ReportDesign design = new ReportDesign();
		design.setUuid(getReportDesignUuid());
		design.setName("Excel");
		design.setReportDefinition(reportDefinition);
		design.setRendererType(MissedAppointmentReportRenderer.class);
		design.addPropertyValue("reportName", getName());
		design.addPropertyValue("baseCohort", getBaseCohort().getName());
		l.add(design);
		return l;
	}
}
