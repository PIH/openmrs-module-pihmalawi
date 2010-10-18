package org.openmrs.module.pihmalawi.reporting;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupHivWeeklyVisits {
	
	Helper h = new Helper();
	
	public SetupHivWeeklyVisits(Helper helper) {
		h = helper;
	}
	
	public void deleteReportElements() {
		purgeIndicatorForLocationWithState("hivvst: ART Patient visits");
		purgeIndicatorForLocationWithState("hivvst: EID Patient visits");
		purgeIndicatorForLocationWithState("hivvst: Pre-ART Patient visits");

		h.purgeDefinition(CohortDefinition.class, "hivvst: ART Patient visits_");
		h.purgeDefinition(CohortDefinition.class, "hivvst: EID Patient visits_");
		h.purgeDefinition(CohortDefinition.class, "hivvst: Pre-ART Patient visits_");
	}
	
	private void purgeIndicatorForLocationWithState(String name) {
		h.purgeDefinition(CohortIndicator.class, name + " (Neno)_");
		h.purgeDefinition(CohortIndicator.class, name + " (Magaleta)_");
		h.purgeDefinition(CohortIndicator.class, name + " (Nsambe)_");
		h.purgeDefinition(CohortIndicator.class, name + " (Neno Mission)_");
		h.purgeDefinition(CohortIndicator.class, name + " (Ligowe)_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago (Neno)_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago (Magaleta)_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago (Nsambe)_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago (Ligowe)_");
		h.purgeDefinition(CohortIndicator.class, name + " 1 week ago (Neno Mission)_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago (Neno)_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago (Magaleta)_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago (Nsambe)_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago (Neno Mission)_");
		h.purgeDefinition(CohortIndicator.class, name + " 2 weeks ago (Ligowe)_");
	}
	

	public void newCountIndicatorForVisits(String namePrefix, String cohort) {
		h.newCountIndicator(namePrefix + " (Neno)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
		    Arrays.asList(h.location("Neno District Hospital")), "onOrBefore", "${endDate}"));
		h.newCountIndicator(namePrefix + " 1 week ago (Neno)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
		    "locationList", Arrays.asList(h.location("Neno District Hospital")), "onOrBefore", "${endDate-1w}"));
		h.newCountIndicator(namePrefix + " 2 weeks ago (Neno)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
		    "locationList", Arrays.asList(h.location("Neno District Hospital")), "onOrBefore", "${endDate-2w}"));
		h.newCountIndicator(namePrefix + " (Nsambe)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
		    Arrays.asList(h.location("Nsambe HC")), "onOrBefore", "${endDate}"));
		h.newCountIndicator(namePrefix + " 1 week ago (Nsambe)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
		    "locationList", Arrays.asList(h.location("Nsambe HC")), "onOrBefore", "${endDate-1w}"));
		h.newCountIndicator(namePrefix + " 2 weeks ago (Nsambe)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
		    "locationList", Arrays.asList(h.location("Nsambe HC")), "onOrBefore", "${endDate-2w}"));
		h.newCountIndicator(namePrefix + " (Magaleta)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}",
		    "locationList", Arrays.asList(h.location("Magaleta HC")), "onOrBefore", "${endDate}"));
		h.newCountIndicator(namePrefix + " 1 week ago (Magaleta)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
		    "locationList", Arrays.asList(h.location("Magaleta HC")), "onOrBefore", "${endDate-1w}"));
		h.newCountIndicator(namePrefix + " 2 weeks ago (Magaleta)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
		    "locationList", Arrays.asList(h.location("Magaleta HC")), "onOrBefore", "${endDate-2w}"));
		h.newCountIndicator(namePrefix + " (Neno Mission)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}",
		    "locationList", Arrays.asList(h.location("Neno Mission HC")), "onOrBefore", "${endDate}"));
		h.newCountIndicator(namePrefix + " 1 week ago (Neno Mission)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
		    "locationList", Arrays.asList(h.location("Neno Mission HC")), "onOrBefore", "${endDate-1w}"));
		h.newCountIndicator(namePrefix + " 2 weeks ago (Neno Mission)_", cohort, h.parameterMap("onOrAfter",
		    "${endDate-3w}", "locationList", Arrays.asList(h.location("Neno Mission HC")), "onOrBefore", "${endDate-2w}"));
		h.newCountIndicator(namePrefix + " (Ligowe)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
		    Arrays.asList(h.location("Ligowe HC")), "onOrBefore", "${endDate}"));
		h.newCountIndicator(namePrefix + " 1 week ago (Ligowe)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
		    "locationList", Arrays.asList(h.location("Ligowe HC")), "onOrBefore", "${endDate-1w}"));
		h.newCountIndicator(namePrefix + " 2 weeks ago (Ligowe)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
		    "locationList", Arrays.asList(h.location("Ligowe HC")), "onOrBefore", "${endDate-2w}"));
	}
	
	public void createCohortDefinitions() {
		// ART
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("hivvst: ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("ART_INITIAL"), Context
		        .getEncounterService().getEncounterType("ART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList", Location.class));
		h.replaceCohortDefinition(ecd);
		
		// Pre-ART
		ecd = new EncounterCohortDefinition();
		ecd.setName("hivvst: Pre-ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("PART_INITIAL"), Context
		        .getEncounterService().getEncounterType("PART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList", Location.class));
		h.replaceCohortDefinition(ecd);
		
		// EID
		ecd = new EncounterCohortDefinition();
		ecd.setName("hivvst: EID Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("EID_INITIAL"), Context
		        .getEncounterService().getEncounterType("EID_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList", Location.class));
		h.replaceCohortDefinition(ecd);
	}
	
	public void addColumnForLocationsForVisits(PeriodIndicatorReportDefinition rd, String displayNamePrefix,
	                                           String indicatorFragment, String indicatorKey) {
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "ndh", displayNamePrefix + " (Neno)", h
		        .cohortIndicator(indicatorFragment + " (Neno)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " (Nsambe)", h
		        .cohortIndicator(indicatorFragment + " (Nsambe)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "lig", displayNamePrefix + " (Ligowe)", h
		        .cohortIndicator(indicatorFragment + " (Ligowe)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " (Magaleta)", h
		        .cohortIndicator(indicatorFragment + " (Magaleta)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mis", displayNamePrefix + " (Neno Mission)", h
		        .cohortIndicator(indicatorFragment + " (Neno Mission)_"), null);
	}
}
