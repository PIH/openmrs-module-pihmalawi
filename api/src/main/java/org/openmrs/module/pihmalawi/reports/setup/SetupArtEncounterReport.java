package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.dataset.AppointmentAdherencePatientDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.dataset.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.experimental.ApzuBMIPatientDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.renderer.ArtRegisterBreakdownRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SetupArtEncounterReport {

	public static final String REPORT_NAME = "ART Encounter Export";

	ReportHelper h = new ReportHelper();

	public SetupArtEncounterReport() { }

	public ReportDefinition[] setup() throws Exception {
		delete();
		ReportDefinition rd = new ReportDefinition();
		rd.setName(REPORT_NAME);
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery(getQuery());
		rd.addDataSetDefinition("encounters", dsd, new HashMap<String, Object>());
		h.replaceReportDefinition(rd);
		return new ReportDefinition[] { rd };
	}

	public void delete() {
		h.purgeDefinition(ReportDefinition.class, REPORT_NAME);
	}

	protected String getQuery() {
		StringBuilder q = new StringBuilder();
		q.append("select e.patient_id, e.encounter_id, t.name as encounter_type, ");
		q.append("		 e.encounter_datetime, l.name as location ");
		q.append("from	 encounter e ");
		q.append("inner join encounter_type t on e.encounter_type = t.encounter_type_id ");
		q.append("left outer join location l on e.location_id = l.location_id ");
		q.append("where t.name in ('ART_INITIAL','ART_FOLLOWUP')");
		return q.toString();
	}
}
