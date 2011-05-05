package org.openmrs.module.pihmalawi.reporting.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

public class ArtReportElements {
	
	static Helper h = new Helper();

	public static CohortDefinition onArtWithLocationOnDate(String prefix) {
		// In state at location
//		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
//		islcd.setName(prefix + ": In state at location_");
//		islcd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
//		islcd.addParameter(new Parameter("onDate", "endDate", Date.class));
//		islcd.addParameter(new Parameter("location", "location", Location.class));
//		h.replaceCohortDefinition(islcd);
		
		// On ART at end of period
		InStateAtLocationCohortDefinition iscd = new InStateAtLocationCohortDefinition();
		iscd.setName(prefix + ": On ART with location & date_");
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM").getWorkflowByName("TREATMENT STATUS")
		// .getStateByName("TRANSFERRED INTERNALLY"));
		iscd.setState(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition everOnArtWithLocationStartedOnOrBefore(String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": Having state at location_");
		pscd.setState(h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"));
		pscd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}
	public static void a(String prefix) {
		// GENERIC
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": In state at location_");
		islcd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "endDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		// Ever enrolled in program at location with state as of end date
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": Ever enrolled in program at location with state_");
		String sql = "" + "SELECT pp.patient_id"
		        + " FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps"
		        + " WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id"
		        + "   AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id"
		        + "   AND pws.program_workflow_id = 1 AND ps.state = :state "
		        + "   AND pp.location_id = :location AND (ps.end_date <= :endDate OR ps.end_date IS NULL)"
		        + "   AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0"
		        + " GROUP BY pp.patient_id;";
		scd.setQuery(sql);
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		h.replaceCohortDefinition(scd);
		

		
		
		// SPECIFIC
		// Ever On Art at location with state
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName(prefix + ": In state at location from On ART_");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		cd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition(prefix + "In state at location_"), ParameterizableUtil
		            .createParameterMappings("onDate=${onDate},location=${location},state=${state}")));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endDate", "${onDate}");
		map.put("location", "${location}");
		map.put("state", h.workflowState("HIV PROGRAM", "TREATMENT STATUS", "ON ANTIRETROVIRALS"));
		cd.getSearches().put("2",
		    new Mapped(h.cohortDefinition(prefix + "Ever enrolled in program at location with state_"), map));
		cd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(cd);

	}
}
