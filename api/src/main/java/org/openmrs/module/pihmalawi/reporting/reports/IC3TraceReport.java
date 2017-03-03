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
import org.openmrs.module.pihmalawi.metadata.LocationTags;
import org.openmrs.module.pihmalawi.metadata.group.HccTreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.TraceDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.renderer.IC3TraceReportRenderer;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.pihmalawi.common.TraceConstants.TraceType;

@Component
public class IC3TraceReport extends ApzuReportManager {

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	HccTreatmentGroup hccTreatmentGroup;

	@Override
	public String getUuid() {
		return "dd538832-4eae-11e6-b0fb-e82aea237783";
	}

	@Override
	public String getName() {
		return "IC3 TRACE Report";
	}

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        return l;
    }

	public String getReportDesignUuid() {
		return "e4320808-4eae-11e6-b0fb-e82aea237783";
	}

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

        List<Location> locations = hivMetadata.getHivStaticSystemLocations();

        // Add 2 week trace data

        for (Location location : locations) {
            boolean isPhase1 = location.hasTag(LocationTags.TRACE_PHASE_1_LOCATION.name());

            TraceType traceType = (isPhase1 ? TraceType.TWO_WEEK_PHASE_1 : TraceType.TWO_WEEK_PHASE_2);

            TraceDataSetDefinition dsd = new TraceDataSetDefinition();
            dsd.setLocation(location);
            dsd.setTraceType(traceType);

            //if (location.equals(hivMetadata.getLuwaniHc())) // TODO: Remove when testing is done
            rd.addDataSetDefinition(location.getName() + " - " + traceType.getMinWeeks() + " weeks", Mapped.mapStraightThrough(dsd));
        }

        // Add 6 week data
        TraceDataSetDefinition dsd6wk = new TraceDataSetDefinition();
        dsd6wk.setLocation(null);
        dsd6wk.setTraceType(TraceType.SIX_WEEK);

        rd.addDataSetDefinition("6 weeks", Mapped.noMappings(dsd6wk));

        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        ReportDesign design = new ReportDesign();
        design.setUuid(getReportDesignUuid());
        design.setName("Excel");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(IC3TraceReportRenderer.class);
        l.add(design);
        return l;
    }
}
