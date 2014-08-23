package org.openmrs.module.pihmalawi.reports.setup;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

import java.util.Arrays;
import java.util.Date;

public class SetupHivWeeklyVisits {

	ReportHelper h = new ReportHelper();

	public SetupHivWeeklyVisits(ReportHelper helper) {
		h = helper;
	}

	private HivMetadata getHivMetadata() {
		return Context.getRegisteredComponents(HivMetadata.class).get(0);
	}

	public void newCountIndicatorForVisits(String namePrefix, String cohort) {
		for (Location loc : getHivMetadata().getHivLocations()) {
			h.newCountIndicator(namePrefix + " (" + loc.getName() + ")_",
					cohort, h.parameterMap("onOrAfter", "${endDate-1w}",
							"locationList", Arrays.asList(loc), "onOrBefore",
							"${endDate}"));
			h.newCountIndicator(namePrefix + " 1 week ago (" + loc.getName()
					+ ")_", cohort, h.parameterMap("onOrAfter",
					"${endDate-2w}", "locationList", Arrays.asList(loc),
					"onOrBefore", "${endDate-1w}"));
			h.newCountIndicator(namePrefix + " 2 weeks ago (" + loc.getName()
					+ ")_", cohort, h.parameterMap("onOrAfter",
					"${endDate-3w}", "locationList", Arrays.asList(loc),
					"onOrBefore", "${endDate-2w}"));
		}
	}

	public void createCohortDefinitions() {
		// ART
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("ART_INITIAL"), Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList",
				Location.class));
		h.replaceCohortDefinition(ecd);

		// Pre-ART
		ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: Pre-ART Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("PART_INITIAL"), Context
				.getEncounterService().getEncounterType("PART_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList",
				Location.class));
		h.replaceCohortDefinition(ecd);

		// EID
		ecd = new EncounterCohortDefinition();
		ecd.setName("hiv: Exposed Child Patient visits_");
		ecd.setEncounterTypeList(Arrays.asList(Context.getEncounterService()
				.getEncounterType("EXPOSED_CHILD_INITIAL"), Context.getEncounterService()
				.getEncounterType("EXPOSED_CHILD_FOLLOWUP")));
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("locationList", "locationList",
				Location.class));
		h.replaceCohortDefinition(ecd);
	}

	public void addColumnForLocationsForVisits(
			PeriodIndicatorReportDefinition rd, String displayNamePrefix,
			String indicatorFragment, String indicatorKey) {
		for (Location loc : getHivMetadata().getHivLocations()) {
			PeriodIndicatorReportUtil.addColumn(
					rd,
					indicatorKey + hivSiteCode(loc),
					displayNamePrefix + " (" + loc.getName() + ")",
					h.cohortIndicator("hiv: " + indicatorFragment + " ("
							+ loc.getName() + ")_"), null);
		}
	}

	public static String hivSiteCode(Location l) {
		if ("Neno District Hospital".equals(l.getName()))
			return "ndh";
		if ("Magaleta HC".equals(l.getName()))
			return "mgt";
		if ("Nsambe HC".equals(l.getName()))
			return "nsm";
		if ("Neno Mission HC".equals(l.getName()))
			return "nop";
		if ("Ligowe HC".equals(l.getName()))
			return "lgwe";
		if ("Matandani Rural Health Center".equals(l.getName()))
			return "mtdn";
		if ("Lisungwi Community Hospital".equals(l.getName()))
			return "lsi";
		if ("Matope HC".equals(l.getName()))
			return "mte";
		if ("Chifunga HC".equals(l.getName()))
			return "cfga";
		if ("Zalewa HC".equals(l.getName()))
			return "zla";
		if ("Midzemba HC".equals(l.getName()))
			return "mid";
		if ("Nkhula Falls RHC".equals(l.getName()))
			return "nka";
		if ("Luwani RHC".equals(l.getName()))
			return "lwan";
		return null;
	}
}
