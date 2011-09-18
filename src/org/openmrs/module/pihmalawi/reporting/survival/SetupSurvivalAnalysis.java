package org.openmrs.module.pihmalawi.reporting.survival;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupSurvivalAnalysis {

	private final Program PROGRAM;

	private final PatientIdentifierType PATIENT_IDENTIFIER_TYPE;

	private final ProgramWorkflowState START_STATE;

	Helper h = new Helper();

	public SetupSurvivalAnalysis(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		PATIENT_IDENTIFIER_TYPE = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");

		START_STATE = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals");
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
		createHtmlBreakdownExternal(rd);
		return new ReportDefinition[] {rd};
	}

	protected ReportDesign createHtmlBreakdownExternal(
			PeriodIndicatorReportDefinition rd) throws IOException,
			SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		SurvivalDataSetDefinition dsd = new SurvivalDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeMissedappointmentColumns(false);
		int columns = rd.getIndicatorDataSetDefinition().getColumns().size();
		m.put("outcome", new Mapped<DataSetDefinition>(dsd, null));
		dsd.setEncounterTypes(null);
		dsd.setPatientIdentifierType(PATIENT_IDENTIFIER_TYPE);

		return h.createHtmlBreakdown(rd, PROGRAM.getName()
				+ " Survival Breakdown_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(PROGRAM.getName() + " Survival Breakdown_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, PROGRAM.getName()
				+ " Survival_ Data Set");
		h.purgeDefinition(ReportDefinition.class, PROGRAM.getName()
				+ " Survival_");
		h.purgeAll("survival: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName(PROGRAM.getName() + " Survival_");
		rd.setupDataSetDefinition();
		return rd;
	}

	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		// having state at location
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName("survival: Having state at location_");
		pscd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
//		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
//		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		

		// Ever enrolled in program at location with state as of end date
//		SqlCohortDefinition scd = new SqlCohortDefinition();
//		scd.setName("survival: Ever enrolled in program at location with state_");
//		String sql = ""
//				+ "SELECT pp.patient_id"
//				+ " FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps"
//				+ " WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id"
//				+ "   AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id"
//				+ "   AND pws.program_workflow_id = 1 AND ps.state = :state "
//				+ "   AND pp.location_id = :location AND (ps.end_date <= :endDate OR ps.end_date IS NULL)"
//				+ "   AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0"
//				+ " GROUP BY pp.patient_id;";
//		scd.setQuery(sql);
//		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
//		scd.addParameter(new Parameter("location", "Location", Location.class));
//		scd.addParameter(new Parameter("state", "State",
//				ProgramWorkflowState.class));
//		h.replaceCohortDefinition(scd);

		
//		CohortIndicator i = h.newCountIndicator("survival: Ever On ART at location_",
//				"survival: Ever enrolled in program at location with state_", h
//						.parameterMap("endDate", "${endDate}", "location",
//								"${location}", "state", START_STATE));
		
		CohortIndicator i = h.newCountIndicator("survival: Ever On ART at location_",
				"survival: Having state at location_", h
						.parameterMap("startedOnOrBefore", "${endDate}", /*"location",
								"${location}",*/ "state", START_STATE));

		PeriodIndicatorReportUtil.addColumn(rd, "outcome",
				"outcome", i, null);
	}
}
