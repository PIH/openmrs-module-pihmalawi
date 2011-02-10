package org.openmrs.module.pihmalawi.reporting.mohquarterlyart;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupArvQuarterly {
	
	private final Program PROGRAM;
		
	Helper h = new Helper();

	private final ProgramWorkflowState STATE_DIED;

	private final ProgramWorkflowState STATE_ON_ART;

	private final ProgramWorkflowState STATE_STOPPED;

	private final ProgramWorkflowState STATE_TRANSFERRED_OUT;

	public SetupArvQuarterly(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		STATE_DIED = PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName("PATIENT DIED");
		STATE_ON_ART = PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName("ON ANTIRETROVIRALS");
		STATE_STOPPED = PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName("ON ANTIRETROVIRALS");
		STATE_TRANSFERRED_OUT = PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName("ON ANTIRETROVIRALS");
	}
	
	public void setup() throws Exception {
		delete();
		
		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
//			if (rd.getName().equals(PROGRAM.getName() + " Changes Breakdown_")) {
//				rs.purgeReportDesign(rd);
//			}
		}
		h.purgeDefinition(DataSetDefinition.class, "ARV Quarterly_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ARV Quarterly_");
		h.purgeAll("arvquarterly: ");
	}
	
	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("ARV Quarterly_");
		rd.setupDataSetDefinition();
		return rd;
	}
	
	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("arvquarterly: In state at location_");
		islcd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		CohortIndicator i = h.newCountIndicator("arvquarterly: Total alive and On ART_", "arvquarterly: In state at location_", h
		        .parameterMap("onDate", "${endDate}", "state", STATE_ON_ART, "location", "${location}" ));
		PeriodIndicatorReportUtil.addColumn(rd, "27", "Total alive and On ART", i, null);
		
		i = h.newCountIndicator("arvquarterly: Died total_", "arvquarterly: In state at location_", h
		        .parameterMap("onDate", "${endDate}", "state", STATE_DIED, "location", "${location}" ));
		PeriodIndicatorReportUtil.addColumn(rd, "32", "Died total", i, null);
		
		i = h.newCountIndicator("arvquarterly: Stopped taking ARVs_", "arvquarterly: In state at location_", h
		        .parameterMap("onDate", "${endDate}", "state", STATE_STOPPED, "location", "${location}" ));
		PeriodIndicatorReportUtil.addColumn(rd, "34", "Stopped taking ARVs", i, null);
		
		i = h.newCountIndicator("arvquarterly: Transferred out_", "arvquarterly: In state at location_", h
		        .parameterMap("onDate", "${endDate}", "state", STATE_TRANSFERRED_OUT, "location", "${location}" ));
		PeriodIndicatorReportUtil.addColumn(rd, "35", "Transferred out", i, null);
		
		
		// NOTE: HIV specific implementation
		// Started ART during period
//		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
//		pscd.setName("arvquarterly: Started ART_");
//		pscd.setStates(Arrays.asList(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS")));
//		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
//		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
//		h.replaceCohortDefinition(pscd);
//		
//		i = h.newCountIndicator("arvquarterly: Started ART from Following_", "arvquarterly: Started ART from Following during period_", h
//		        .parameterMap("endDate", "${endDate}", "startDate", "${startDate}" ));
//		PeriodIndicatorReportUtil.addColumn(rd, "initiated", "Started ART from Following across all locations", i, null);
		
	}
}
