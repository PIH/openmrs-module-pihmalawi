package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class ReportingSetupOutpatient extends BaseModuleContextSensitiveTest {
	
	Helper h = new Helper();
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
	
	@Test
	@Rollback(false)
	public void setupOPD() throws Exception {
		try {
			EncounterCohortDefinition ecd = new EncounterCohortDefinition();
			ecd.setName("Outpatient Diagnosis Encounter_");
			List<EncounterType> types = new ArrayList<EncounterType>();
			types.add(Context.getEncounterService().getEncounterType("OUTPATIENT DIAGNOSIS"));
			ecd.setEncounterTypeList(types);
			ecd.setAtLeastCount(1);
			ecd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(ecd);
			
			ecd = new EncounterCohortDefinition();
			ecd.setName("Registration & Vitals Encounter_");
			types = new ArrayList<EncounterType>();
			types.add(Context.getEncounterService().getEncounterType("REGISTRATION"));
			types.add(Context.getEncounterService().getEncounterType("VITALS"));
			ecd.setEncounterTypeList(types);
			ecd.setAtLeastCount(1);
			ecd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
			h.replaceCohortDefinition(ecd);
			
			ecd = new EncounterCohortDefinition();
			ecd.setName("Mastercard Encounter_");
			types = new ArrayList<EncounterType>();
			types.add(Context.getEncounterService().getEncounterType("ART_INITIAL"));
			types.add(Context.getEncounterService().getEncounterType("ART_FOLLOWUP"));
			types.add(Context.getEncounterService().getEncounterType("PART_INITIAL"));
			types.add(Context.getEncounterService().getEncounterType("PART_FOLLOWUP"));
			types.add(Context.getEncounterService().getEncounterType("EID_INITIAL"));
			types.add(Context.getEncounterService().getEncounterType("EID_FOLLOWUP"));
			types.add(Context.getEncounterService().getEncounterType("TB_INITIAL"));
			types.add(Context.getEncounterService().getEncounterType("TB_FOLLOWUP"));
			ecd.setEncounterTypeList(types);
			ecd.setAtLeastCount(1);
			ecd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
			h.replaceCohortDefinition(ecd);
			
			// -----------------------------------------------------
			// indicators
			
			h.newCountIndicator("Outpatient Diagnosis_", "Outpatient Diagnosis Encounter_", "endDate=${endDate}");
			h.newCountIndicator("Mastercard_", "Mastercard Encounter_", "endDate=${endDate}");
			h.newCountIndicator("Registration & Vitals_", "Registration & Vitals Encounter_", "endDate=${endDate}");

			// --------------------------------------------------------------
			// dimensions
			
			// hiv program location
//			CohortDefinitionDimension md = new CohortDefinitionDimension();
//			md.setName("User_");
//			md.addParameter(new Parameter("endDate", "End Date", Date.class));
//			md.addParameter(new Parameter("startDate", "Start Date", Date.class));
//			md.addParameter(new Parameter("location", "Location", Location.class));
//			Map<String, Object> m2 = new HashMap<String, Object>();
//			m2.put("user", Context.getUserService().getUserByUsername("cneumann"));
//			md.addCohortDefinition("Neno", h.cohortDefinition("Enrolled in program_"), m2);
//			m2 = new HashMap<String, Object>();
//			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
//			m2.put("endDate", "${endDate}");
//			m2.put("location", Context.getLocationService().getLocation("Nsambe HC"));
//			md.addCohortDefinition("Nsambe", h.cohortDefinition("Enrolled in program_"), m2);
//			m2 = new HashMap<String, Object>();
//			m2.put("program", Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM"));
//			m2.put("endDate", "${endDate}");
//			m2.put("location", Context.getLocationService().getLocation("Magaleta HC"));
//			md.addCohortDefinition("Magaleta", h.cohortDefinition("Enrolled in program_"), m2);
//			h.replaceDefinition(md);
			
			// --------------------------------------------------------------
			// reports
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// todo, very poor man's solution
	static int keyCount = 1;
	
	public void addColumnForLocations(PeriodIndicatorReportDefinition rd, String displayNamePrefix, String indicator) {
		PeriodIndicatorReportUtil.addColumn(rd, "" + keyCount++, displayNamePrefix + " at Neno", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Neno"));
		PeriodIndicatorReportUtil.addColumn(rd, "" + keyCount++, displayNamePrefix + " at Magaleta", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Magaleta"));
		PeriodIndicatorReportUtil.addColumn(rd, "" + keyCount++, displayNamePrefix + " at Nsambe", h
		        .cohortIndicator(indicator), h.hashMap("Location", "Nsambe"));
	}
	
}
