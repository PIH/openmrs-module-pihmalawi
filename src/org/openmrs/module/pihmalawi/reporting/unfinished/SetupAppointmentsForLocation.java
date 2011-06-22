package org.openmrs.module.pihmalawi.reporting.unfinished;

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
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupAppointmentsForLocation {

	private static final int MAX_PREVIOUS_WEEKS = 12;

	/** List of encounter included in report */
	private final List<EncounterType> ENCOUNTER_TYPES;

	/** List of List of Locations to be grouped together */
	private final List<List<Location>> LOCATIONS_LIST;

	/** List of included Users */
	private final List<User> USERS;

	Helper h = new Helper();

	public SetupAppointmentsForLocation(Helper helper) {
		h = helper;
		ENCOUNTER_TYPES = Arrays.asList(et("ART_INITIAL"), et("ART_FOLLOWUP"),
				et("PART_INITIAL"), et("PART_FOLLOWUP"), et("EID_INITIAL"),
				et("EID_FOLLOWUP"), et("LAB"), et("TB_INITIAL"),
				et("TB_FOLLOWUP"), et("REGISTRATION"), et("VITALS"),
				et("OUTPATIENT DIAGNOSIS"), et("APPOINTMENT"));
		// wish-list, what about drugs & drug_orders
		/*
		 * ENCOUNTER_TYPES = Arrays .asList( et("ART_INITIAL"),
		 * et("ART_FOLLOWUP"), et("PART_INITIAL"), et("PART_FOLLOWUP"),
		 * et("EID_INITIAL"), et("EID_FOLLOWUP"), et("REGISTRATION"),
		 * et("VITALS"), et("OUTPATIENT DIAGNOSIS"), et("LAB"),
		 * et("TB_INITIAL"), et("TB_FOLLOWUP"), et("APPOINTMENT"),
		 * et("LAB ORDERS"), et("CHEMOTHERAPY"), et("PATIENT EVALUATION"));
		 */
		LOCATIONS_LIST = Arrays.asList(
				Arrays.asList(h.location("Neno District Hospital"),
						h.location("Outpatient"),
						h.location("Registration"),
						h.location("Vitals")),
				Arrays.asList(h.location("Magaleta HC")),
				Arrays.asList(h.location("Nsambe HC")),
				Arrays.asList(h.location("Lisungwi Community Hospital")),
				Arrays.asList(h.location("Chifunga HC")),
				Arrays.asList(h.location("Matope HC")));
		USERS = Arrays.asList(u("benndo"), u("amahaka"), u("geomal"),
				u("qlement"), u("thandie"), u("faydula"), u("cneumann"),
				u("prichi"), u("wilmwa"), u("nmakwaya"));
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

	public void setup(boolean b) throws Exception {
		delete();

		ReportDefinition rd = createReportDefinitionByLocation();
		h.createXlsOverview(rd, "Weekly_Encounter_By_Location.xls",
				"Weekly Encounter By Location.xls (Excel)_",
				excelOverviewProperties());

		rd = createReportDefinitionByUser();
		h.createXlsOverview(rd, "Weekly_Encounter_By_User.xls",
				"Weekly Encounter By User.xls (Excel)_",
				excelOverviewProperties());
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
		}
		h.replaceDataSetDefinition(ds);

		ReportDefinition rd = new ReportDefinition();
		rd.setName("Weekly Encounter By Location_");
		Map<String, Mapped<? extends DataSetDefinition>> map = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		map.put("dataset",
				new Mapped<DataSetDefinition>(ds, h.parameterMap("endDate",
						"${endDate}")));
		rd.setDataSetDefinitions(map);
		rd.addParameter(new Parameter("endDate", "endDate", Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}

	private ReportDefinition createReportDefinitionByUser() {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.setName("enc: What When Who_");
		ecd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
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
										.parameterMap("onOrAfter", onOrAfter,
												"onOrBefore", onOrBefore,
												"createdBy", USERS.get(u_id),
												"encounterTypeList", et)));
					}
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
		rd.addParameter(new Parameter("endDate", "endDate", Date.class));
		h.replaceReportDefinition(rd);

		return rd;
	}
}
