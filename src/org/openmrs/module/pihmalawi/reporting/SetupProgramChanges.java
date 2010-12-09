package org.openmrs.module.pihmalawi.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.InProgramAtProgramLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.serialization.SerializationException;

public class SetupProgramChanges {
	
	private final Program PROGRAM;
	
	private final PatientIdentifierType PATIENT_IDENTIFIER_TYPE;
	
	private final List<ProgramWorkflowState> STATES;
	
	private final List<Location> LOCATIONS;
	
	Helper h = new Helper();
	
	public SetupProgramChanges(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		PATIENT_IDENTIFIER_TYPE = Context.getPatientService().getPatientIdentifierTypeByName("ARV Number");
		
		STATES = Arrays.asList(PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName("FOLLOWING"), PROGRAM
		        .getWorkflowByName("TREATMENT STATUS").getStateByName("ON ANTIRETROVIRALS"), PROGRAM.getWorkflowByName(
		    "TREATMENT STATUS").getStateByName("PATIENT DIED"), PROGRAM.getWorkflowByName("TREATMENT STATUS")
		        .getStateByName("PATIENT TRANSFERRED OUT"), PROGRAM.getWorkflowByName("TREATMENT STATUS").getStateByName(
		    "TREATMENT STOPPED"));
		LOCATIONS = Arrays.asList(
					h.location("Lisungwi Community Hospital"), 
		    			h.location("Matope RHC"), 
		    			h.location("Chifunga RHC")
//		    h.location("Neno District Hospital"), h.location("Magaleta HC"), h.location("Nsambe HC")
		    );
		
	}
	
	public void setup() throws Exception {
		delete();
		
		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
		createHtmlBreakdownExternal(rd);
	}
	
	protected ReportDesign createHtmlBreakdownExternal(PeriodIndicatorReportDefinition rd) throws IOException,
	                                                                                      SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeMissedappointmentColumns(false);
		int columns = rd.getIndicatorDataSetDefinition().getColumns().size();
		for (int i = 1; i <= columns; i++) {
			m.put("" + i, new Mapped<DataSetDefinition>(dsd, null));
		}
		dsd.setEncounterTypes(null);
		dsd.setPatientIdentifierType(PATIENT_IDENTIFIER_TYPE);
		
		return h.createHtmlBreakdown(rd, PROGRAM.getName() + " Changes Breakdown_", m);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(PROGRAM.getName() + " Changes Breakdown_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, PROGRAM.getName() + " Changes_ Data Set");
		h.purgeDefinition(ReportDefinition.class, PROGRAM.getName() + " Changes_");
		h.purgeAll("changes: ");
	}
	
	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName(PROGRAM.getName() + " Changes_");
		rd.setupDataSetDefinition();
		return rd;
	}
	
	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("changes: In state at location_");
		islcd.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("changes: New in state at location_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("state", "state", ProgramWorkflowState.class));
		ccd.getSearches().put(
		    "1",
		    new Mapped(h.cohortDefinition("changes: In state at location_"), h.parameterMap("onDate", "${startDate}",
		        "state", "${state}", "location", "${location}")));
		ccd.getSearches().put(
		    "2",
		    new Mapped(h.cohortDefinition("changes: In state at location_"), h.parameterMap("onDate", "${endDate}", "state",
		        "${state}", "location", "${location}")));
		ccd.setCompositionString("NOT 1 AND 2");
		h.replaceCohortDefinition(ccd);
		
		int c = 0;
		for (Location l : LOCATIONS) {
			for (ProgramWorkflowState s : STATES) {
				c++;
				CohortIndicator i = h.newCountIndicator("changes: " + s.getConcept().getName() + " at " + l.getName() + "_",
				    "changes: New in state at location_", h.parameterMap("endDate", "${endDate}", "startDate",
				        "${startDate}", "location", l, "state", s));
				PeriodIndicatorReportUtil.addColumn(rd, "" + c, "New " + s.getConcept().getName() + " at " + l.getName(), i,
				    null);
			}
		}
		
	
		InProgramCohortDefinition ipcd = new InProgramCohortDefinition();
		ipcd.setName("changes: In program_");
		ipcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ipcd.addParameter(new Parameter("programs", "programs", Program.class));
		h.replaceCohortDefinition(ipcd);

		// Not at location
		InProgramAtProgramLocationCohortDefinition iplcd = new InProgramAtProgramLocationCohortDefinition();
		iplcd.setName("changes: At location_");
		iplcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iplcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iplcd);
		
		// At location
		InverseCohortDefinition icd = new InverseCohortDefinition();
		icd.setName("changes: Not at location_");
		icd.setBaseDefinition(iplcd);
		h.replaceCohortDefinition(icd);
		
		ccd = new CompositionCohortDefinition();
		ccd.setName("changes: New at unknown location_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		String composition = "";
		c = 1;
		for (Location l : LOCATIONS) {
			ccd.getSearches().put(
			    "" + c,
			    new Mapped(h.cohortDefinition("changes: Not at location_"), h.parameterMap("onDate", "${endDate}", "location", l)));
			composition += "NOT " + c + " AND ";
			c++;
		}
		ccd.getSearches().put(
		    "unknown",
		    new Mapped(h.cohortDefinition("changes: In program_"), h.parameterMap("onDate", "${endDate}", "program", PROGRAM)));
		composition += " unknown";
		ccd.setCompositionString(composition);
		h.replaceCohortDefinition(ccd);
		
		CohortIndicator i = h.newCountIndicator("changes: unknown locations_", "changes: New at unknown location_", h
		        .parameterMap("endDate", "${endDate}", "startDate", "${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "unknown", "New at unknown locations", i, null);
	}
}
