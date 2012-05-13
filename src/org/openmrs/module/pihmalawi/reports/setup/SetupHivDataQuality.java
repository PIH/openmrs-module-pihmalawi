package org.openmrs.module.pihmalawi.reports.setup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.extension.EncounterAfterProgramStateCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.InProgramAtProgramLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

public class SetupHivDataQuality {

	private final Program PROGRAM;

	ReportHelper h = new ReportHelper();

	private final ProgramWorkflowState STATE_DIED;
	private final ProgramWorkflowState STATE_ON_ART;
	private final ProgramWorkflowState STATE_PRE_ART;
	private final ProgramWorkflowState STATE_STOPPED;
	private final ProgramWorkflowState STATE_TRANSFERRED_OUT;
	private final ProgramWorkflowState STATE_TRANSFERRED_INTERNALLY;
	private final ProgramWorkflowState STATE_EXPOSED_CHILD;
	private final ProgramWorkflowState STATE_EXPOSED_CHILD_DISCHARGED;

	private final Map<Location, String> LOCATIONS;

	private final List<User> USERS;

	public SetupHivDataQuality(ReportHelper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		STATE_DIED = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Patient died");
		STATE_ON_ART = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals");
		STATE_PRE_ART = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Pre-ART (Continue)");
		STATE_STOPPED = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Treatment stopped");
		STATE_TRANSFERRED_OUT = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Patient transferred out");
		STATE_TRANSFERRED_INTERNALLY = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Transferred internally");
		STATE_EXPOSED_CHILD = PROGRAM.getWorkflowByName("Treatment status")
				.getStateByName("Exposed Child (Continue)");
		STATE_EXPOSED_CHILD_DISCHARGED = PROGRAM.getWorkflowByName(
				"Treatment status").getStateByName("Discharged uninfected");

		LOCATIONS = new HashMap<Location, String>();
		LOCATIONS.put(MetadataLookup.location("Lisungwi Community Hospital"), "LSI");
		LOCATIONS.put(MetadataLookup.location("Matope HC"), "MTE");
		LOCATIONS.put(MetadataLookup.location("Chifunga HC"), "CFGA");
		LOCATIONS.put(MetadataLookup.location("Zalewa HC"), "ZLA");
		LOCATIONS.put(MetadataLookup.location("Nkhula Falls RHC"), "NKA");
		LOCATIONS.put(MetadataLookup.location("Luwani RHC"), "LWAN");
		LOCATIONS.put(MetadataLookup.location("Neno District Hospital"), "NNO");
		LOCATIONS.put(MetadataLookup.location("Matandani Rural Health Center"), "MTDN");
		LOCATIONS.put(MetadataLookup.location("Ligowe HC"), "LGWE");
		LOCATIONS.put(MetadataLookup.location("Magaleta HC"), "MGT");
		LOCATIONS.put(MetadataLookup.location("Neno Mission HC"), "NOP");
		LOCATIONS.put(MetadataLookup.location("Nsambe HC"), "NSM");

		USERS = new ArrayList<User>();
		for (String user : new String[] { "benndo", "amahaka", "geomal",
				"qlement", "thandie", "cgoliath", "cneumann", "prichi",
				"harzam", "nelma", "moblack" }) {
			if (Context.getUserService().getUserByUsername(user) != null)
				USERS.add(Context.getUserService().getUserByUsername(user));
		}
	}

	public void setup() throws Exception {
		delete();

		g_lastUpdatingUser();
		createDimension();
		PeriodIndicatorReportDefinition[] rds = createReportDefinition();
		createCohortDefinitions(rds);

		h.replaceReportDefinition(rds[0]);
		// createHtmlBreakdownInternal(rds[0]);
		h.replaceReportDefinition(rds[1]);
		h.replaceReportDefinition(rds[2]);
		h.createXlsOverview(rds[2], "HIV_Data_Quality_For_All_Users.xls",
				"HIV Data Quality For All Users.xls (Excel)_", null);

	}

	private void createDimension() {
		CohortDefinitionDimension totalRegisteredTimeframe = new CohortDefinitionDimension();
		totalRegisteredTimeframe.setName("hivdq: Last updating user_");
		totalRegisteredTimeframe.addParameter(new Parameter("user", "User ID",
				Integer.class));
		totalRegisteredTimeframe.addCohortDefinition("user",
				h.cohortDefinition("hivdq: Last updating user_"),
				h.parameterMap("user", "${user}"));

		h.replaceDefinition(totalRegisteredTimeframe);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(
					"HIV Data Quality For All Users.xls (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "HIV Data Quality_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "HIV Data Quality_");
		h.purgeDefinition(DataSetDefinition.class,
				"HIV Data Quality By User_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "HIV Data Quality By User_");
		h.purgeDefinition(DataSetDefinition.class,
				"HIV Data Quality For All Users (SLOW)_ Data Set");
		h.purgeDefinition(ReportDefinition.class,
				"HIV Data Quality For All Users (SLOW)_");
		h.purgeDimension("hivdq: Last updating user_");
		h.purgeAll("hivdq: ");
	}

	private PeriodIndicatorReportDefinition[] createReportDefinition() {
		// catch all DQ report
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setName("HIV Data Quality_");
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd.addParameter(new Parameter("endDate", "End date (Today)", Date.class));
		rd.setupDataSetDefinition();

		// DQ report by User
		PeriodIndicatorReportDefinition rd2 = new PeriodIndicatorReportDefinition();
		rd2.setName("HIV Data Quality By User_");
		rd2.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd2.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd2.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd2.addParameter(new Parameter("endDate", "End date (Today)",
				Date.class));
		rd2.addParameter(new Parameter("user", "User ID", Integer.class));
		rd2.setBaseCohortDefinition(
				h.cohortDefinition("hivdq: Last updating user_"),
				ParameterizableUtil.createParameterMappings("user=${user}"));
		rd2.setupDataSetDefinition();

		// catch all DQ report with dimension
		PeriodIndicatorReportDefinition rd3 = new PeriodIndicatorReportDefinition();
		rd3.setName("HIV Data Quality For All Users (SLOW)_");
		rd3.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd3.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd3.removeParameter(ReportingConstants.END_DATE_PARAMETER);
		rd3.addParameter(new Parameter("endDate", "End date (Today)",
				Date.class));
		rd3.setupDataSetDefinition();
		for (User user : USERS) {
			rd3.addDimension(user.getUsername(),
					h.cohortDefinitionDimension("hivdq: Last updating user_"),
					h.parameterMap("user", user.getId()));
		}
		return new PeriodIndicatorReportDefinition[] { rd, rd2, rd3 };
	}

	private SqlCohortDefinition g_lastUpdatingUser() {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Last updating user_");
		String sql = "SELECT p.patient_id FROM patient p "
				+ "LEFT OUTER JOIN "
				+ "(SELECT patient_id, MAX(date_created) date_created FROM encounter WHERE voided = 0 GROUP BY patient_id) last_edit_dt "
				+ "ON p.patient_id = last_edit_dt.patient_id "
				+ "LEFT OUTER JOIN encounter last_edit "
				+ "ON p.patient_id = last_edit.patient_id AND last_edit.form_id is not null AND last_edit.voided = 0 AND last_edit_dt.date_created = last_edit.date_created "
				+ "WHERE last_edit.creator= :user ;";
		scd.setQuery(sql);
		scd.addParameter(new Parameter("user", "User ID", Integer.class));
		h.replaceCohortDefinition(scd);
		return scd;
	}

	private void createCohortDefinitions(PeriodIndicatorReportDefinition[] rds) {
		createProgramCompletionMismatch(rds);

		SqlCohortDefinition scd;
		String sql;

		multipleDeaths(rds);

		wrongArvFormat(rds);

		wrongPartFormat(rds);

		wrongHccFormat(rds);

		// gaps in numbers
		for (String s : LOCATIONS.values()) {
			createLastInRangeArvNumber(rds, s, s + " ");
		}
		createLastInRangeArvNumberForAllLocations(rds, LOCATIONS.values());

		// gaps in numbers
		for (String s : LOCATIONS.values()) {
			createLastInRangeHccNumber(rds, s, s + " ");
		}
		createLastInRangeHccNumberForAllLocations(rds, LOCATIONS.values());

		// multiple arv numbers
		for (String s : LOCATIONS.values()) {
			createMultipleArv(rds, s, s + " ");
		}
		createMultipleArvForAllLocations(rds, LOCATIONS.values());

		g_everInProgramOnDate();

		InProgramAtProgramLocationCohortDefinition igplcd = g_inProgramAtLocationOnDate();

		g_unknownLocations(rds);

		// wrong exit from care
		CodedObsCohortDefinition cocd = new CodedObsCohortDefinition();
		cocd.setName("hivdq: Wrong exit from care_");
		cocd.setQuestion(Context.getConceptService().getConcept(
				"REASON FOR EXITING CARE"));
		cocd.setTimeModifier(TimeModifier.LAST);
		cocd.setOperator(SetComparator.NOT_IN);
		cocd.setValueList(Arrays.asList(Context.getConceptService().getConcept(
				"Patient died")));
		h.replaceCohortDefinition(cocd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Wrong exit from care in HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"exit",
				new Mapped(h.cohortDefinition("hivdq: Wrong exit from care_"),
						h.parameterMap()));
		ccd.getSearches().put(
				"unknown",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("exit AND unknown");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "exit", h.parameterMap("endDate", "${endDate}"),
				rds);

		// deceased without exit from care
		BirthAndDeathCohortDefinition badcd = new BirthAndDeathCohortDefinition();
		badcd.setName("hivdq: Ever deceased_");
		// since big bang with millisecond = 0
		badcd.setDiedOnOrAfter(new Date(0));
		h.replaceCohortDefinition(badcd);

		cocd = new CodedObsCohortDefinition();
		cocd.setName("hivdq: With exit from care_");
		cocd.setQuestion(Context.getConceptService().getConcept(
				"REASON FOR EXITING CARE"));
		cocd.setTimeModifier(TimeModifier.ANY);
		h.replaceCohortDefinition(cocd);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Deceased without exit from care in HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"deceased",
				new Mapped(h.cohortDefinition("hivdq: Ever deceased_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"exitFromCare",
				new Mapped(h.cohortDefinition("hivdq: With exit from care_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("deceased AND hiv AND NOT exitFromCare");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "noexit", h.parameterMap("endDate", "${endDate}"),
				rds);

		// deceased but not in patient died
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In state patient died_");
		iscd.setStates(Arrays.asList(STATE_DIED));
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		h.replaceCohortDefinition(iscd);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Deceased but not in state died in HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"deceased",
				new Mapped(h.cohortDefinition("hivdq: Ever deceased_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"stateDied",
				new Mapped(h.cohortDefinition("hivdq: In state patient died_"),
						h.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("deceased AND hiv AND NOT stateDied");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "nostate",
				h.parameterMap("endDate", "${endDate}"), rds);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Died without exit from care_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"stateDied",
				new Mapped(h.cohortDefinition("hivdq: In state patient died_"),
						h.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"exitFromCare",
				new Mapped(h.cohortDefinition("hivdq: With exit from care_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("NOT exitFromCare AND hiv AND stateDied");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "noexit2",
				h.parameterMap("endDate", "${endDate}"), rds);

		multipleOpenStates();

		// on art but no arv number
		iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In state On ART_");
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		iscd.setStates(Arrays.asList(STATE_ON_ART));
		h.replaceCohortDefinition(iscd);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: ART number_");
		sql = "select patient_id from patient_identifier where identifier_type=4 and voided=0;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: On ART without number_");
		ccd.getSearches().put(
				"onArt",
				new Mapped(h.cohortDefinition("hivdq: In state On ART_"), h
						.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"artNumber",
				new Mapped(h.cohortDefinition("hivdq: ART number_"), h
						.parameterMap()));
		ccd.setCompositionString("onArt AND NOT artNumber");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "nonoart",
				h.parameterMap("endDate", "${endDate}"), rds);

		artEncounterWithoutNumber(rds);

		// on following but no pre-art number
		iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In state Following_");
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		iscd.setStates(Arrays.asList(STATE_PRE_ART));
		h.replaceCohortDefinition(iscd);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: PART number_");
		sql = "select patient_id from patient_identifier where identifier_type=13 and voided=0;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: HCC number_");
		sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: old PART number_");
		sql = "select patient_id from patient_identifier where identifier_type=5 and voided=0;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: Following without number (excluding Old Pre-ART numbers)_");
		ccd.getSearches().put(
				"following",
				new Mapped(h.cohortDefinition("hivdq: In state Following_"), h
						.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"hccNumber",
				new Mapped(h.cohortDefinition("hivdq: HCC number_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"partNumber",
				new Mapped(h.cohortDefinition("hivdq: PART number_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"oldPartNumber",
				new Mapped(h.cohortDefinition("hivdq: Old PART number_"), h
						.parameterMap()));
		ccd.setCompositionString("following AND NOT hccNumber AND NOT partNumber AND NOT oldPartNumber");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "nonopart",
				h.parameterMap("endDate", "${endDate}"), rds);

		// on following but arv number
		ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: Following with art number_");
		ccd.getSearches().put(
				"following",
				new Mapped(h.cohortDefinition("hivdq: In state Following_"), h
						.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"artNumber",
				new Mapped(h.cohortDefinition("hivdq: ART number_"), h
						.parameterMap()));
		ccd.setCompositionString("following AND artNumber");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "noart", h.parameterMap("endDate", "${endDate}"),
				rds);

		onArtWithoutEncounter(rds);

		followupWithoutInitialEncounter(rds);

		// not in relevant hiv state
		iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In relevant HIV state_");
		iscd.setStates(Arrays.asList(STATE_DIED, STATE_TRANSFERRED_OUT,
				STATE_TRANSFERRED_INTERNALLY, STATE_STOPPED, STATE_ON_ART,
				STATE_PRE_ART, STATE_EXPOSED_CHILD,
				STATE_EXPOSED_CHILD_DISCHARGED));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Not in relevant HIV state_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"state",
				new Mapped(h.cohortDefinition("hivdq: In relevant HIV state_"),
						h.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("NOT state AND hiv");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "state", h.parameterMap("endDate", "${endDate}"),
				rds);

		List<EncounterType> hivEncounterTypes = Arrays.asList(
				MetadataLookup.encounterType("ART_INITIAL"),
				MetadataLookup.encounterType("ART_FOLLOWUP"),
				MetadataLookup.encounterType("EXPOSED_CHILD_INITIAL"),
				MetadataLookup.encounterType("EXPOSED_CHILD_FOLLOWUP"),
				MetadataLookup.encounterType("PART_INITIAL"),
				MetadataLookup.encounterType("PART_FOLLOWUP"));
		List<ProgramWorkflowState> hivTerminalStates = Arrays.asList(
				STATE_DIED, STATE_STOPPED, STATE_TRANSFERRED_OUT,
				STATE_TRANSFERRED_INTERNALLY, STATE_EXPOSED_CHILD_DISCHARGED);

		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(MetadataLookup.location("Neno District Hospital")),
				MetadataLookup.location("Neno District Hospital"), "NNO");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Magaleta HC")),
				MetadataLookup.location("Magaleta HC"), "MGT");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Nsambe HC")),
				MetadataLookup.location("Nsambe HC"), "NSM");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(MetadataLookup.location("Neno Mission HC")),
				MetadataLookup.location("Neno Mission HC"), "NOP");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(MetadataLookup.location("Matandani Rural Health Center")),
				MetadataLookup.location("Matandani Rural Health Center"), "MTDN");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Ligowe HC")),
				MetadataLookup.location("Ligowe HC"), "LGWE");

		createEncounterAfterTerminalState(
				rds,
				hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(MetadataLookup.location("Lisungwi Community Hospital"),
						MetadataLookup.location("Midzemba HC")),
				MetadataLookup.location("Lisungwi Community Hospital"), "LSI");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Chifunga HC")),
				MetadataLookup.location("Chifunga HC"), "CFGA");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Matope HC")),
				MetadataLookup.location("Matope HC"), "MTE");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Zalewa HC")),
				MetadataLookup.location("Zalewa HC"), "ZLA");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(MetadataLookup.location("Luwani RHC")),
				MetadataLookup.location("Luwani RHC"), "LWAN");
		createEncounterAfterTerminalState(rds, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(MetadataLookup.location("Nkhula Falls RHC")),
				MetadataLookup.location("Nkhula Falls RHC"), "NKA");

		exposedInfants(rds);
	}

	private void createIndicator(CohortDefinition cd, String key,
			Map<String, Object> parameterMap,
			PeriodIndicatorReportDefinition[] rds) {
		if (parameterMap == null)
			parameterMap = new HashMap<String, Object>();
		String displayName = cd.getName().substring(6,
				cd.getName().length() - 1);
		CohortIndicator i = h.newCountIndicator(cd.getName(), cd.getName(),
				parameterMap);
		PeriodIndicatorReportUtil.addColumn(rds[0], key, displayName, i, null);
		PeriodIndicatorReportUtil.addColumn(rds[1], key, displayName, i, null);
		// PeriodIndicatorReportUtil.addColumn(rds[2], key,
		// displayName, i, null);
		for (User user : USERS) {
			// something seems wrong with my dimensions...
			PeriodIndicatorReportUtil.addColumn(rds[2], user.getUsername()
					+ key, displayName, i,
					h.hashMap(user.getUsername(), "user"));
		}
	}

	private void multipleOpenStates() {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple open states without end date_");
		String sql = "select pp.patient_id from  patient_program pp, patient_state ps where pp.patient_program_id = ps.patient_program_id and ps.end_date is null and ps.voided=0 group by ps.patient_program_id having count(*)>1";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
	}

	private void wrongPartFormat(PeriodIndicatorReportDefinition[] rds) {
		SqlCohortDefinition scd;
		String sql;
		// wrong part format
		scd = new SqlCohortDefinition();
		scd.setName("hivdq: Wrong Pre-ART identifier format_");
		String partFormat = "";
		for (String s : LOCATIONS.values()) {
			partFormat += "identifier NOT regexp '^P-[[:<:]]" + s
					+ "[[:>:]]-[0-9][0-9][0-9][0-9]$' AND ";
		}
		partFormat = partFormat.substring(0,
				partFormat.length() - " AND ".length());
		sql = "select patient_id from patient_identifier "
				+ "where identifier_type=13 and voided=0 and (" + partFormat
				+ ")";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "formatpart", null, rds);
	}

	private void wrongHccFormat(PeriodIndicatorReportDefinition[] rds) {
		SqlCohortDefinition scd;
		String sql;
		// wrong part format
		scd = new SqlCohortDefinition();
		scd.setName("hivdq: Wrong HCC identifier format_");
		String partFormat = "";
		for (String s : LOCATIONS.values()) {
			partFormat += "identifier NOT regexp '^[[:<:]]" + s
					+ "[[:>:]] [1-9][0-9]?[0-9]?[0-9]? HCC$' AND ";
		}
		partFormat = partFormat.substring(0,
				partFormat.length() - " AND ".length());
		sql = "select patient_id from patient_identifier "
				+ "where identifier_type=19 and voided=0 and (" + partFormat
				+ ")";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "formathcc", null, rds);
	}

	private void wrongArvFormat(PeriodIndicatorReportDefinition[] rds) {
		SqlCohortDefinition scd;
		String sql;
		// wrong arv format
		scd = new SqlCohortDefinition();
		scd.setName("hivdq: Wrong identifier format_");
		String artFormat = "";
		for (String s : LOCATIONS.values()) {
			artFormat += "identifier NOT regexp '^[[:<:]]" + s
					+ "[[:>:]] [1-9][0-9]?[0-9]?[0-9]?$' AND ";
		}
		artFormat = artFormat.substring(0,
				artFormat.length() - " AND ".length());
		sql = "select patient_id from patient_identifier "
				+ "where identifier_type=4 and voided=0 and (" + artFormat
				+ ")";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "format", null, rds);
	}

	private void multipleDeaths(PeriodIndicatorReportDefinition[] rds) {
		// multiple death
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple death_");
		String sql = "SELECT pp.patient_id "
				+ "FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id AND pws.program_workflow_id = 1 AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0 AND pws.program_workflow_state_id=3 "
				+ "GROUP BY pp.patient_id HAVING COUNT(*) > 1;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "died2x", null, rds);
	}

	private void g_unknownLocations(PeriodIndicatorReportDefinition[] rds) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Unknown location_");
		String sql = "select pp.patient_id from patient_program pp where pp.voided=0 and pp.program_id=1 and (pp.location_id is null or pp.location_id not in (";
		for (Location l : LOCATIONS.keySet()) {
			sql += l.getId() + ", ";
		}
		sql = sql.substring(0, sql.length() - 2) + "));";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "unknwnloc", null, rds);
	}

	private void createProgramCompletionMismatch(
			PeriodIndicatorReportDefinition[] rds) {
		/*
		 * -- Most recent state for a program enrollment -- not sure why the
		 * group_concat values can be used for a join, but it seems to work --
		 * this should be THE query to return the most recent state for a
		 * patient_program with the assumption -- that the highest
		 * patient_state_id is also the most recent one -- note: a temp table
		 * didn't work as mysql can't reuse a temp table in a subquery -- MySQL
		 * specific
		 * 
		 * SELECT pp.patient_id, pp.patient_program_id,
		 * pws.program_workflow_state_id, ps.patient_state_id FROM ( SELECT
		 * pp.patient_id a, pp.patient_program_id b,
		 * pws.program_workflow_state_id c, group_concat(ps.patient_state_id
		 * order by ps.patient_state_id desc) d FROM patient_program pp,
		 * program_workflow pw, program_workflow_state pws, patient_state ps
		 * WHERE pp.program_id = pw.program_id AND pw.program_workflow_id =
		 * pws.program_workflow_id AND pws.program_workflow_state_id = ps.state
		 * AND ps.patient_program_id = pp.patient_program_id AND
		 * pws.program_workflow_id = 1 AND pw.retired = 0 AND pp.voided = 0 AND
		 * ps.voided = 0 -- AND pp.patient_id=15925 GROUP BY pp.patient_id,
		 * pp.patient_program_id) most_recent_state, patient_program pp,
		 * program_workflow pw, program_workflow_state pws, patient_state ps
		 * WHERE most_recent_state.d=ps.patient_state_id AND pp.program_id =
		 * pw.program_id AND pw.program_workflow_id = pws.program_workflow_id
		 * AND pws.program_workflow_state_id = ps.state AND
		 * ps.patient_program_id = pp.patient_program_id AND
		 * pws.program_workflow_state_id in (2,3,6) and pp.date_completed is
		 * null
		 */
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Terminal state, program not completed_");
		String sql = "SELECT pp.patient_id "
				+ "FROM ( "
				+ "  SELECT pp.patient_id a, pp.patient_program_id b, pws.program_workflow_state_id c, group_concat(ps.patient_state_id order by ps.patient_state_id desc) d "
				+ "  FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "  WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id "
				+ "    AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id "
				+ "    AND pws.program_workflow_id = 1 "
				+ "    AND pw.retired = 0 AND pp.voided = 0 AND ps.voided = 0 "
				+ "GROUP BY pp.patient_id, pp.patient_program_id) most_recent_state, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "WHERE most_recent_state.d=ps.patient_state_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id "
				+ "  AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id "
				+ "  AND pws.program_workflow_state_id in (2,3,6,119) and pp.date_completed is  null ";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "prgnotcompleted", null, rds);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: Non terminal state, program completed_");
		sql = "SELECT pp.patient_id "
				+ "FROM ( "
				+ "  SELECT pp.patient_id a, pp.patient_program_id b, pws.program_workflow_state_id c, group_concat(ps.patient_state_id order by ps.patient_state_id desc) d "
				+ "  FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "  WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id "
				+ "    AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id "
				+ "    AND pws.program_workflow_id = 1 "
				+ "    AND pw.retired = 0 AND pp.voided = 0 AND ps.voided = 0 "
				+ "GROUP BY pp.patient_id, pp.patient_program_id) most_recent_state, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "WHERE most_recent_state.d=ps.patient_state_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id "
				+ "  AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id "
				+ "  AND pws.program_workflow_state_id not in (2,3,6,119) and pp.date_completed is not null ";

		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "prgcompleted", null, rds);
	}

	private void followupWithoutInitialEncounter(
			PeriodIndicatorReportDefinition[] rds) {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		ecd.addParameter(new Parameter("encounterTypeList",
				"encounterTypeList", EncounterType.class));
		ecd.setName("hivdq: With encounter_");
		ecd.setTimeQualifier(TimeQualifier.FIRST);
		h.replaceCohortDefinition(ecd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: Followup without Initial_");
		ccd.getSearches()
				.put("artFollowup",
						new Mapped(
								h.cohortDefinition("hivdq: With encounter_"),
								h.parameterMap("onOrBefore", "${endDate}",
										"encounterTypeList", Arrays.asList(MetadataLookup
												.encounterType("ART_FOLLOWUP")))));
		ccd.getSearches()
				.put("artInitial",
						new Mapped(
								h.cohortDefinition("hivdq: With encounter_"),
								h.parameterMap("onOrBefore", "${endDate}",
										"encounterTypeList", Arrays.asList(MetadataLookup
												.encounterType("ART_INITIAL")))));
		ccd.setCompositionString("artFollowup AND NOT artInitial");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "noinitial",
				h.parameterMap("endDate", "${endDate}"), rds);

		ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: Pre-ART Followup without Initial_");
		ccd.getSearches()
				.put("partFollowup",
						new Mapped(
								h.cohortDefinition("hivdq: With encounter_"),
								h.parameterMap(
										"onOrBefore",
										"${endDate}",
										"encounterTypeList",
										Arrays.asList(MetadataLookup
												.encounterType("PART_FOLLOWUP")))));
		ccd.getSearches()
				.put("partInitial",
						new Mapped(
								h.cohortDefinition("hivdq: With encounter_"),
								h.parameterMap("onOrBefore", "${endDate}",
										"encounterTypeList", Arrays.asList(MetadataLookup
												.encounterType("PART_INITIAL")))));
		ccd.setCompositionString("partFollowup AND NOT partInitial");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "partnoinitial",
				h.parameterMap("endDate", "${endDate}"), rds);
	}

	private Date date(String string) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return dfm.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void exposedInfants(PeriodIndicatorReportDefinition[] rds) {

		// on art but no arv number
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In state Exposed_");
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		iscd.setStates(Arrays.asList(STATE_EXPOSED_CHILD));
		h.replaceCohortDefinition(iscd);

		// too long in eid
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("hivdq: Above 25 months_");
		age.addParameter(new Parameter("effectiveDate", "effectiveDate",
				Date.class));
		age.setMinAge(26);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		h.replaceCohortDefinition(age);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: EID Above 25 months_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches()
				.put("eid",
						new Mapped(h
								.cohortDefinition("hivdq: In state Exposed_"),
								null));
		ccd.getSearches().put(
				"age",
				new Mapped(h.cohortDefinition("hivdq: Above 25 months_"), h
						.parameterMap("effectiveDate", "${endDate}")));
		ccd.setCompositionString("eid AND age");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "eidage", h.parameterMap("endDate", "${endDate}"),
				rds);
	}

	private InProgramAtProgramLocationCohortDefinition g_inProgramAtLocationOnDate() {
		InProgramAtProgramLocationCohortDefinition iplcd = new InProgramAtProgramLocationCohortDefinition();
		iplcd.setName("hivdq: At location_");
		iplcd.addParameter(new Parameter("programs", "programs", Program.class));
		iplcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iplcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iplcd);
		return iplcd;
	}

	private void g_everInProgramOnDate() {
		ProgramEnrollmentCohortDefinition ipcd = new ProgramEnrollmentCohortDefinition();
		ipcd.setName("hivdq: Ever in program_");
		ipcd.addParameter(new Parameter("enrolledOnOrBefore",
				"enrolledOnOrBefore", Date.class));
		ipcd.addParameter(new Parameter("programs", "programs", Program.class));
		h.replaceCohortDefinition(ipcd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: In program_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("programs", "programs", Program.class));
		ccd.getSearches().put(
				"state",
				new Mapped(h.cohortDefinition("hivdq: Ever in program_"), h
						.parameterMap("programs", "${programs}",
								"enrolledOnOrBefore", "${onDate}")));
		ccd.setCompositionString("state");
		h.replaceCohortDefinition(ccd);
	}

	private void onArtWithoutEncounter(PeriodIndicatorReportDefinition[] rds) {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ecd.setName("hivdq: No ART encounter_");
		ecd.setReturnInverse(true);
		ecd.setEncounterTypeList(Arrays.asList(MetadataLookup.encounterType("ART_INITIAL"),
				MetadataLookup.encounterType("ART_FOLLOWUP")));
		h.replaceCohortDefinition(ecd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: On ART without encounter_");
		ccd.getSearches().put(
				"onArt",
				new Mapped(h.cohortDefinition("hivdq: In state On ART_"), h
						.parameterMap("onOrAfter", "${endDate}")));
		ccd.getSearches().put(
				"artNumber",
				new Mapped(h.cohortDefinition("hivdq: No ART encounter_"), h
						.parameterMap("endDate", "${endDate}")));
		ccd.setCompositionString("onArt AND artNumber");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "noartenc",
				h.parameterMap("endDate", "${endDate}"), rds);
	}

	private void artEncounterWithoutNumber(PeriodIndicatorReportDefinition[] rds) {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ecd.setName("hivdq: ART encounter_");
		ecd.setEncounterTypeList(Arrays.asList(MetadataLookup.encounterType("ART_INITIAL"),
				MetadataLookup.encounterType("ART_FOLLOWUP")));
		h.replaceCohortDefinition(ecd);

		// depends on 'hivdq: ART number_'

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.setName("hivdq: ART encounter without number_");
		ccd.getSearches().put(
				"artEncounter",
				new Mapped(h.cohortDefinition("hivdq: ART encounter_"), h
						.parameterMap("endDate", "${endDate}")));
		ccd.getSearches().put(
				"artNumber",
				new Mapped(h.cohortDefinition("hivdq: ART number_"), h
						.parameterMap()));
		ccd.setCompositionString("artEncounter AND NOT artNumber");
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "artnono",
				h.parameterMap("endDate", "${endDate}"), rds);
	}

	private void createEncounterAfterTerminalState(
			PeriodIndicatorReportDefinition[] rds,
			List<EncounterType> hivEncounterTypes,
			List<ProgramWorkflowState> hivTerminalStates,
			List<Location> clinicLocations, Location enrollmentLocation,
			String siteCode) {
		EncounterAfterProgramStateCohortDefinition eapscd = new EncounterAfterProgramStateCohortDefinition();
		eapscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		eapscd.setName("hivdq: " + siteCode
				+ ": Hiv encounter after terminal state_");
		eapscd.setClinicLocations(clinicLocations);
		eapscd.setEnrollmentLocation(enrollmentLocation);
		eapscd.setEncounterTypesAfterChangeToTerminalState(hivEncounterTypes);
		eapscd.setTerminalStates(hivTerminalStates);
		h.replaceCohortDefinition(eapscd);
		createIndicator(eapscd, "term" + siteCode.toLowerCase(),
				h.parameterMap("onDate", "${endDate}"), rds);
	}

	private void createLastInRangeArvNumber(
			PeriodIndicatorReportDefinition[] rds, String loc, String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Last In-range ARV number before gap in sequence "
				+ loc + "_");
		scd.setQuery(sqlForOutOfRangeArvNumbers(prefix));
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "gap" + loc.toLowerCase(), null, rds);
	}

	private void createLastInRangeArvNumberForAllLocations(
			PeriodIndicatorReportDefinition[] rds, Collection<String> locations) {

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Last In-range ARV number before gap in sequence all_");
		for (String loc : locations) {
			ccd.getSearches().put(
				loc,
				new Mapped(h.cohortDefinition("hivdq: Last In-range ARV number before gap in sequence " + loc + "_"),
						h.parameterMap()));
		}
		String composition = "";
		for (String loc : locations) {
			composition += loc + " OR ";
		}
		ccd.setCompositionString(composition.substring(0, composition.length() - "OR ".length()));
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "gapall", h.parameterMap(),
				rds);
	}

	private void createLastInRangeHccNumber(
			PeriodIndicatorReportDefinition[] rds, String loc, String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Last In-range HCC number before gap in sequence "
				+ loc + "_");
		scd.setQuery(sqlForOutOfRangeHccNumbers(prefix));
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "hccgap" + loc.toLowerCase(), null, rds);
	}

	private void createLastInRangeHccNumberForAllLocations(
			PeriodIndicatorReportDefinition[] rds, Collection<String> locations) {

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Last In-range HCC number before gap in sequence all_");
		for (String loc : locations) {
			ccd.getSearches().put(
				loc,
				new Mapped(h.cohortDefinition("hivdq: Last In-range HCC number before gap in sequence " + loc + "_"),
						h.parameterMap()));
		}
		String composition = "";
		for (String loc : locations) {
			composition += loc + " OR ";
		}
		ccd.setCompositionString(composition.substring(0, composition.length() - "OR ".length()));
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "hccgapall", h.parameterMap(),
				rds);
	}

	private void createMultipleArv(PeriodIndicatorReportDefinition[] rds,
			String loc, String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple ARV numbers " + loc + "_");
		scd.setQuery(sqlForMultipleArvNumber(prefix));
		h.replaceCohortDefinition(scd);
		createIndicator(scd, "dup" + loc.toLowerCase(), null, rds);
	}

	private void createMultipleArvForAllLocations(
			PeriodIndicatorReportDefinition[] rds, Collection<String> locations) {

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Multiple ARV numbers all_");
		for (String loc : locations) {
			ccd.getSearches().put(
				loc,
				new Mapped(h.cohortDefinition("hivdq: Multiple ARV numbers " + loc + "_"),
						h.parameterMap()));
		}
		String composition = "";
		for (String loc : locations) {
			composition += loc + " OR ";
		}
		ccd.setCompositionString(composition.substring(0, composition.length() - "OR ".length()));
		h.replaceCohortDefinition(ccd);
		createIndicator(ccd, "dupall", h.parameterMap(),
				rds);
	}

	private String sqlForOutOfRangeArvNumbers(String locationPrefix) {
		String prefixLength = "" + (locationPrefix.length() + 1);
		String sql = "select patient_id from patient_identifier "
				+ "where identifier in ("
				+ "  select concat('"
				+ locationPrefix
				+ "', cast(c.start as char))"
				+ "  from (select a.id_number  as start"
				+ "  from "
				+ "    (select substring(identifier, "
				+ prefixLength
				+ ") as id_number "
				+ "    from patient_identifier where identifier_type=4 and identifier like '"
				+ locationPrefix
				+ "%' and voided = 0) as a"
				+ "  left outer join "
				+ "    (select substring(identifier,  "
				+ prefixLength
				+ ") as id_number "
				+ "    from patient_identifier where identifier_type=4 and identifier like '"
				+ locationPrefix
				+ "%' and voided = 0) as b on a.id_number + 1 = b.id_number"
				+ "  where b.id_number is null) as c" + ")";
		return sql;
	}

	private String sqlForOutOfRangeHccNumbers(String locationPrefix) {
		String prefixLength = "" + (locationPrefix.length() + 1);
		String sql = "select patient_id from patient_identifier "
				+ "where identifier in ("
				+ "  select concat('"
				+ locationPrefix
				+ "', cast(c.start as char), ' HCC')"
				+ "  from (select a.id_number  as start"
				+ "  from "
				+ "    (select replace(substring(identifier,  "
				+ prefixLength
				+ "), ' HCC', '') as id_number "
				+ "    from patient_identifier where identifier_type=19 and identifier like '"
				+ locationPrefix
				+ "%' and voided = 0) as a"
				+ "  left outer join "
				+ "    (select replace(substring(identifier,  "
				+ prefixLength
				+ "), ' HCC', '') as id_number "
				+ "    from patient_identifier where identifier_type=19 and identifier like '"
				+ locationPrefix
				+ "%' and voided = 0) as b on a.id_number + 1 = b.id_number"
				+ "  where b.id_number is null) as c" + ")";
		return sql;
	}

	private String sqlForMultipleArvNumber(String locationPrefix) {
		String sql = "select patient_id from patient_identifier "
				+ "where identifier_type=4 and voided=0 and identifier like '"
				+ locationPrefix + "%' "
				+ "group by patient_id having(count(patient_id)>1)";
		return sql;
	}
}
