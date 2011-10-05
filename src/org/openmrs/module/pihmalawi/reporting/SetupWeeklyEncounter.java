package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupWeeklyEncounter {

	private static final int MAX_PREVIOUS_WEEKS = 12;

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	/** List of encounter counted as others */
	private final List<EncounterType> OTHER_ENCOUNTER_TYPES;

	/** List of List of Locations to be grouped together */
	private final List<List<Location>> LOCATIONS_LIST;

	/** List of Locations included as others */
	private final List<Location> OTHER_LOCATIONS;

	/** List of included Users */
	private final List<User> USERS;

	/** List of included Users */
	private final List<User> OTHER_USERS;

	Helper h = new Helper();

	public SetupWeeklyEncounter(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(et("ART_INITIAL"), et("ART_FOLLOWUP"),
				et("PART_INITIAL"), et("PART_FOLLOWUP"), et("EID_INITIAL"),
				et("EID_FOLLOWUP"), et("LAB"), et("TB_INITIAL"),
				et("TB_FOLLOWUP"), et("REGISTRATION"), et("VITALS"),
				et("OUTPATIENT DIAGNOSIS"), et("APPOINTMENT"),
				et("CHRONIC_CARE_INITIAL"), et("CHRONIC_CARE_FOLLOWUP"));
		List<EncounterType> ets = Context.getEncounterService()
				.getAllEncounterTypes(false);
		OTHER_ENCOUNTER_TYPES = new ArrayList<EncounterType>();
		for (EncounterType et : ets) {
			if (!ENCOUNTER_TYPES.contains(et)) {
				OTHER_ENCOUNTER_TYPES.add(et);
			}
		}

		LOCATIONS_LIST = Arrays.asList(
				Arrays.asList(h.location("Neno District Hospital"),
						h.location("Ligowe HC"), h.location("Outpatient"),
						h.location("Registration"), h.location("Vitals"),
						h.location("Matandani Rural Health Center")),
				Arrays.asList(h.location("Magaleta HC")),
				Arrays.asList(h.location("Nsambe HC")),
				Arrays.asList(h.location("Neno Mission HC")),
				Arrays.asList(h.location("Lisungwi Community Hospital"),
						h.location("Midzemba HC")),
				Arrays.asList(h.location("Chifunga HC")),
				Arrays.asList(h.location("Matope HC")),
				Arrays.asList(h.location("Nkhula Falls RHC")),
				Arrays.asList(h.location("Zalewa HC")));

		List<Location> flatKnownLocations = new ArrayList<Location>();
		for (List<Location> knownLocations : LOCATIONS_LIST) {
			// i just dont know it better right now...
			flatKnownLocations.addAll(knownLocations);
		}
		OTHER_LOCATIONS = new ArrayList<Location>();
		for (Location l : Context.getLocationService().getAllLocations(false)) {
			if (!flatKnownLocations.contains(l)) {
				OTHER_LOCATIONS.add(l);
			}
		}

		USERS = Arrays.asList(u("benndo"), u("amahaka"), u("geomal"),
				u("qlement"), u("thandie"), u("cgoliath"), u("cneumann"),
				u("prichi"), u("harzam"), u("nelma"), u("moblack"));

		List<User> us = Context.getUserService().getAllUsers();
		OTHER_USERS = new ArrayList<User>();
		for (User u : us) {
			if (!USERS.contains(u)) {
				OTHER_USERS.add(u);
			}
		}
	}

	private Map<String, String> excelOverviewProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		for (int i = 0; i < LOCATIONS_LIST.size(); i++) {
			String n = LOCATIONS_LIST.get(i).get(0).getName();
			properties.put("loc" + (i + 1) + "name",
					n.substring(0, n.indexOf(' ')));
		}
		for (int i = 0; i < USERS.size(); i++) {
			if (USERS.get(i) != null) {
				properties.put("user" + (i + 1) + "name", USERS.get(i)
						.getUsername());
			}
		}
		return properties;
	}

	private EncounterType et(String encounterType) {
		return Context.getEncounterService().getEncounterType(encounterType);
	}

	private User u(String username) {
		return Context.getUserService().getUserByUsername(username);
	}

	public ReportDefinition[] setup(boolean b) throws Exception {
		delete();

		ReportDefinition rd1 = createReportDefinitionByLocation();
		h.createXlsOverview(rd1, "Weekly_Encounter_By_Location.xls",
				"Weekly Encounter By Location.xls (Excel)_",
				excelOverviewProperties());

		ReportDefinition rd2 = createReportDefinitionByUser();
		h.createXlsOverview(rd2, "Weekly_Encounter_By_User.xls",
				"Weekly Encounter By User.xls (Excel)_",
				excelOverviewProperties());

		return new ReportDefinition[] { rd1, rd2 };
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName()
					.equals("Weekly Encounter By Location.xls (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
			if (rd.getName().equals("Weekly Encounter By User.xls (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "enc: What When Where_");
		h.purgeDefinition(ReportDefinition.class,
				"Weekly Encounter By Location_");
		h.purgeDefinition(DataSetDefinition.class, "enc: What When Who_");
		h.purgeDefinition(ReportDefinition.class, "Weekly Encounter By User_");
		h.purgeAll("enc: ");
	}

	/** Weekly Encounter by Location */
	private ReportDefinition createReportDefinitionByLocation() {
		// currently unused as CrossTabDataSetDefinition Mapping and Indicators
		// don't take lists of something
		// EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		// ecd.setName("enc: What When Where_");
		// ecd.addParameter(new Parameter("onOrAfter", "onOrAfter",
		// Date.class));
		// ecd.addParameter(new Parameter("onOrBefore", "onOrBefore",
		// Date.class));
		// ecd.addParameter(new Parameter("locationList", "locationList",
		// Location.class, List.class, false));
		// ecd.addParameter(new Parameter("encounterTypeList",
		// "encounterTypeList", EncounterType.class, List.class, false));
		// h.replaceCohortDefinition(ecd);

		// breakdown collection of locations into separate cohortdefs
		List<EncounterCohortDefinition> defs = new ArrayList<EncounterCohortDefinition>();
		for (int l_id = 0; l_id < LOCATIONS_LIST.size(); l_id++) {
			EncounterCohortDefinition ecd = new EncounterCohortDefinition();
			ecd.setName("enc: What When loc" + (l_id + 1) + "_");
			ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
			ecd.addParameter(new Parameter("onOrBefore", "onOrBefore",
					Date.class));
			ecd.addParameter(new Parameter("encounterTypeList",
					"encounterTypeList", EncounterType.class, List.class, false));
			ecd.setLocationList(LOCATIONS_LIST.get(l_id));
			defs.add(ecd);
			h.replaceCohortDefinition(ecd);
		}

		CohortCrossTabDataSetDefinition ds = new CohortCrossTabDataSetDefinition();
		ds.setName("enc: What When Where_");
		ds.addParameter(new Parameter("endDate", "endDate", Date.class));
		for (int l_id = 0; l_id < defs.size(); l_id++) {
			// known encounters
			EncounterCohortDefinition ecd = defs.get(l_id);
			for (int e_id = 0; e_id < ENCOUNTER_TYPES.size(); e_id++) {
				EncounterType et = ENCOUNTER_TYPES.get(e_id);
				for (int period = 0; period < MAX_PREVIOUS_WEEKS; period++) {
					String onOrBefore = "${endDate-" + period + "w}";
					String onOrAfter = "${endDate-" + (period + 1) + "w}";
					ds.addRow(
							"loc" + (l_id + 1) + "enc" + (e_id + 1) + "ago"
									+ period,
							new Mapped<CohortDefinition>(ecd, h.parameterMap(
									"onOrAfter", onOrAfter, "onOrBefore",
									onOrBefore, "encounterTypeList", et)));
				}
			}

			// other encounters
			for (int period = 0; period < MAX_PREVIOUS_WEEKS; period++) {
				String onOrBefore = "${endDate-" + period + "w}";
				String onOrAfter = "${endDate-" + (period + 1) + "w}";
				ds.addRow(
						"loc" + (l_id + 1) + "otherencago" + period,
						new Mapped<CohortDefinition>(ecd, h.parameterMap(
								"onOrAfter", onOrAfter, "onOrBefore",
								onOrBefore, "encounterTypeList",
								OTHER_ENCOUNTER_TYPES)));
			}
		}
		EncounterCohortDefinition ecd2 = new EncounterCohortDefinition();
		ecd2.setName("enc: What When other loc_");
		ecd2.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd2.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd2.addParameter(new Parameter("encounterTypeList",
				"encounterTypeList", EncounterType.class, List.class, null));
		ecd2.setLocationList(OTHER_LOCATIONS);
		defs.add(ecd2);
		h.replaceCohortDefinition(ecd2);
		// other locations
		for (int period = 0; period < MAX_PREVIOUS_WEEKS; period++) {
			String onOrBefore = "${endDate-" + period + "w}";
			String onOrAfter = "${endDate-" + (period + 1) + "w}";
			ds.addRow(
					"otherlocallencago" + period,
					new Mapped<CohortDefinition>(ecd2, h.parameterMap(
							"onOrAfter", onOrAfter, "onOrBefore", onOrBefore)));
		}
		h.replaceDataSetDefinition(ds);

		ReportDefinition rd = new ReportDefinition();
		rd.setName("Weekly Encounter By Location_");
		Map<String, Mapped<? extends DataSetDefinition>> map = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		map.put("dataset",
				new Mapped<DataSetDefinition>(ds, h.parameterMap("endDate",
						"${endDate}")));
		rd.setDataSetDefinitions(map);
		rd.addParameter(new Parameter("endDate", "End date (Sunday)",
				Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}

	/** Weekly Encounter by User */
	private ReportDefinition createReportDefinitionByUser() {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("enc: What When Who_");
		ecd.addParameter(new Parameter("createdOnOrAfter", "createdOnOrAfter",
				Date.class));
		ecd.addParameter(new Parameter("createdOnOrBefore",
				"createdOnOrBefore", Date.class));
		ecd.addParameter(new Parameter("createdBy", "createdBy", User.class));
		ecd.addParameter(new Parameter("encounterTypeList",
				"encounterTypeList", EncounterType.class, List.class, false));
		h.replaceCohortDefinition(ecd);

		CohortCrossTabDataSetDefinition ds = new CohortCrossTabDataSetDefinition();
		ds.setName("enc: What When Who_");
		ds.addParameter(new Parameter("endDate", "endDate", Date.class));
		for (int u_id = 0; u_id < USERS.size(); u_id++) {
			if (USERS.get(u_id) != null) {
				for (int e_id = 0; e_id < ENCOUNTER_TYPES.size(); e_id++) {
					EncounterType et = ENCOUNTER_TYPES.get(e_id);
					for (int period = 0; period < MAX_PREVIOUS_WEEKS; period++) {
						String onOrBefore = "${endDate-" + period + "w}";
						String onOrAfter = "${endDate-" + (period + 1) + "w}";
						ds.addRow(
								"user" + (u_id + 1) + "enc" + (e_id + 1)
										+ "ago" + period,
								new Mapped<CohortDefinition>(ecd, h
										.parameterMap("createdOnOrAfter",
												onOrAfter, "createdOnOrBefore",
												onOrBefore, "createdBy",
												USERS.get(u_id),
												"encounterTypeList", et)));
					}
				}
				// other encounter types
				for (int period = 0; period < MAX_PREVIOUS_WEEKS; period++) {
					String onOrBefore = "${endDate-" + period + "w}";
					String onOrAfter = "${endDate-" + (period + 1) + "w}";
					ds.addRow(
							"user" + (u_id + 1) + "otherencago" + period,
							new Mapped<CohortDefinition>(ecd, h.parameterMap(
									"createdOnOrAfter", onOrAfter,
									"createdOnOrBefore", onOrBefore,
									"createdBy", USERS.get(u_id),
									"encounterTypeList", OTHER_ENCOUNTER_TYPES)));
				}
			}
		}
		h.replaceDataSetDefinition(ds);

		ReportDefinition rd = new ReportDefinition();
		rd.setName("Weekly Encounter By User_");
		Map<String, Mapped<? extends DataSetDefinition>> map = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		map.put("dataset",
				new Mapped<DataSetDefinition>(ds, h.parameterMap("endDate",
						"${endDate}")));
		rd.setDataSetDefinitions(map);
		rd.addParameter(new Parameter("endDate", "End date (Sunday)",
				Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}
}
