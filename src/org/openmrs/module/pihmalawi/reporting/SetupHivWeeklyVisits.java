package org.openmrs.module.pihmalawi.reporting;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupHivWeeklyVisits {
	
	Helper h = new Helper();
	
	public SetupHivWeeklyVisits(Helper helper) {
		h = helper;
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
		
		h.newCountIndicator(namePrefix + " (Lisungwi)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
			    Arrays.asList(h.location("Lisungwi Community Hospital")), "onOrBefore", "${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (Lisungwi)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
			    "locationList", Arrays.asList(h.location("Lisungwi Community Hospital")), "onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (Lisungwi)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
			    "locationList", Arrays.asList(h.location("Lisungwi Community Hospital")), "onOrBefore", "${endDate-2w}"));
			h.newCountIndicator(namePrefix + " (Chifunga)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
			    Arrays.asList(h.location("Chifunga HC")), "onOrBefore", "${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (Chifunga)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
			    "locationList", Arrays.asList(h.location("Chifunga HC")), "onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (Chifunga)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
			    "locationList", Arrays.asList(h.location("Chifunga HC")), "onOrBefore", "${endDate-2w}"));
			h.newCountIndicator(namePrefix + " (Matope)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}",
			    "locationList", Arrays.asList(h.location("Matope HC")), "onOrBefore", "${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (Matope)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
			    "locationList", Arrays.asList(h.location("Matope HC")), "onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (Matope)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
			    "locationList", Arrays.asList(h.location("Matope HC")), "onOrBefore", "${endDate-2w}"));
			h.newCountIndicator(namePrefix + " (Midzemba)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}",
			    "locationList", Arrays.asList(h.location("Midzemba HC")), "onOrBefore", "${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (Midzemba)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
			    "locationList", Arrays.asList(h.location("Midzemba HC")), "onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (Midzemba)_", cohort, h.parameterMap("onOrAfter",
			    "${endDate-3w}", "locationList", Arrays.asList(h.location("Midzemba HC")), "onOrBefore", "${endDate-2w}"));
			h.newCountIndicator(namePrefix + " (Zalewa)_", cohort, h.parameterMap("onOrAfter", "${endDate-1w}", "locationList",
			    Arrays.asList(h.location("Zalewa HC")), "onOrBefore", "${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (Zalewa)_", cohort, h.parameterMap("onOrAfter", "${endDate-2w}",
			    "locationList", Arrays.asList(h.location("Zalewa HC")), "onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (Zalewa)_", cohort, h.parameterMap("onOrAfter", "${endDate-3w}",
			    "locationList", Arrays.asList(h.location("Zalewa HC")), "onOrBefore", "${endDate-2w}"));

	}
	
	public void createCohortDefinitions() {
		// ART
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("ART_INITIAL"), Context
		        .getEncounterService().getEncounterType("ART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList", Location.class));
		h.replaceCohortDefinition(ecd);
		
		// Pre-ART
		ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: Pre-ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService().getEncounterType("PART_INITIAL"), Context
		        .getEncounterService().getEncounterType("PART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList", Location.class));
		h.replaceCohortDefinition(ecd);
		
		// EID
		ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: EID Patient visits_");
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
		        .cohortIndicator("hiv: " +indicatorFragment + " (Neno)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "nsm", displayNamePrefix + " (Nsambe)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Nsambe)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "lig", displayNamePrefix + " (Ligowe)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Ligowe)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mgt", displayNamePrefix + " (Magaleta)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Magaleta)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mis", displayNamePrefix + " (Neno Mission)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Neno Mission)_"), null);

		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "lsi", displayNamePrefix + " (Lisungwi)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Lisungwi)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "zal", displayNamePrefix + " (Zalewa)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Zalewa)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mid", displayNamePrefix + " (Midzemba)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Midzemba)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "mte", displayNamePrefix + " (Matope)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Matope)_"), null);
		PeriodIndicatorReportUtil.addColumn(rd, indicatorKey + "cfa", displayNamePrefix + " (Chifunga)", h
		        .cohortIndicator("hiv: " +indicatorFragment + " (Chifunga)_"), null);
}
}
