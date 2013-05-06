package org.openmrs.module.pihmalawi.reports.setup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.extension.ChronicCareVisitDataSetDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

public class SetupChronicCareVisits {

	ReportHelper h = new ReportHelper();

	public SetupChronicCareVisits(ReportHelper helper) {
		h = helper;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();
		ReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		return new ReportDefinition[] { rd };
	}

	public void delete() {
		AdministrationService as = Context.getAdministrationService();
		as.executeSQL("delete from serialized_object where name = 'chronicvisits: encounters';", false);
		as.executeSQL("delete from serialized_object where name = 'Chronic Care Visits_';", false);
	}

	public static ReportDefinition createReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setName("Chronic Care Visits_");
		rd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		rd.addParameter(new Parameter("toDate", "To Date", Date.class));
		rd.addParameter(new Parameter("location", "At Location", Location.class));
		rd.addParameter(new Parameter("which", "Which Encounters during this period", TimeQualifier.class, null , TimeQualifier.ANY, null));
		rd.addParameter(new Parameter("limitedToPatientsEnrolledAtEnd", "Only include patients enrolled at the period end", Boolean.class));

		ChronicCareVisitDataSetDefinition dsd = new ChronicCareVisitDataSetDefinition();
		dsd.setName("chronicvisits: encounters");
		dsd.addParameter(new Parameter("fromDate", "From Date", Date.class));
		dsd.addParameter(new Parameter("toDate", "To Date", Date.class));
		dsd.addParameter(new Parameter("location", "At Location", Location.class));
		dsd.addParameter(new Parameter("which", "Which Encounters during this period", TimeQualifier.class));
		dsd.addParameter(new Parameter("limitedToPatientsEnrolledAtEnd", "Only include patients enrolled at the period end", Boolean.class));

		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("fromDate", "${fromDate}");
		mappings.put("toDate", "${toDate}");
		mappings.put("location", "${location}");
		mappings.put("which", "${which}");
		mappings.put("limitedToPatientsEnrolledAtEnd", "${limitedToPatientsEnrolledAtEnd}");
		rd.addDataSetDefinition(dsd, mappings);

		return rd;
	}
}
