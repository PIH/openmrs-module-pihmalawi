package org.openmrs.module.pihmalawi.reporting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.EncounterAfterProgramStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.InProgramAtProgramLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
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
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

public class SetupHivDataQuality {

	private final Program PROGRAM;

	Helper h = new Helper();

	private final ProgramWorkflowState STATE_DIED;
	private final ProgramWorkflowState STATE_ON_ART;
	private final ProgramWorkflowState STATE_FOLLOWING;
	private final ProgramWorkflowState STATE_STOPPED;
	private final ProgramWorkflowState STATE_TRANSFERRED_OUT;
	private final ProgramWorkflowState STATE_TRANSFERRED_INTERNALLY;

	private final Map<Location, String> LOCATIONS;

	public SetupHivDataQuality(Helper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV PROGRAM");
		STATE_DIED = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("PATIENT DIED");
		STATE_ON_ART = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("ON ANTIRETROVIRALS");
		STATE_FOLLOWING = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("FOLLOWING");
		STATE_STOPPED = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("TREATMENT STOPPED");
		STATE_TRANSFERRED_OUT = PROGRAM.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("PATIENT TRANSFERRED OUT");
		STATE_TRANSFERRED_INTERNALLY = PROGRAM.getWorkflowByName(
				"TREATMENT STATUS").getStateByName("TRANSFERRED INTERNALLY");

		LOCATIONS = new HashMap<Location, String>();
		LOCATIONS.put(h.location("Lisungwi Community Hospital"), "LSI");
		LOCATIONS.put(h.location("Matope HC"), "MTE");
		LOCATIONS.put(h.location("Chifunga HC"), "CFA");
		LOCATIONS.put(h.location("Zalewa HC"), "ZLA");
		LOCATIONS.put(h.location("Nkhula Falls RHC"), "NKA");
		LOCATIONS.put(h.location("Neno District Hospital"), "NNO");
		LOCATIONS.put(h.location("Magaleta HC"), "MGT");
		LOCATIONS.put(h.location("Neno Mission HC"), "NOP");
		LOCATIONS.put(h.location("Nsambe HC"), "NSM");
	}

	public void setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition[] rds = createReportDefinition();
		createCohortDefinitions(rds);
		h.replaceReportDefinition(rds[0]);
		h.replaceReportDefinition(rds[1]);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			// if (rd.getName().equals(PROGRAM.getName() +
			// " Changes Breakdown_")) {
			// rs.purgeReportDesign(rd);
			// }
		}
		h.purgeDefinition(DataSetDefinition.class, "HIV Data Quality_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "HIV Data Quality_");
		h.purgeDefinition(DataSetDefinition.class,
				"HIV Data Quality By User_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "HIV Data Quality By User_");
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
		rd2.setBaseCohortDefinition(g_lastUpdatingUser(),
				ParameterizableUtil.createParameterMappings("user=${user}"));
		rd2.setupDataSetDefinition();
		return new PeriodIndicatorReportDefinition[] { rd, rd2 };
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

	private void createCohortDefinitions(PeriodIndicatorReportDefinition[] rd) {
		createProgramCompletionMismatch(rd);

		SqlCohortDefinition scd;
		String sql;
		CohortIndicator i;
		
		multipleDeaths(rd);

		wrongArvFormat(rd);

		wrongPartFormat(rd);

		// gaps in numbers
		for (String s : LOCATIONS.values()) {
			createLastInRangeNumber(rd, s, s + " ");
		}

		// multiple arv numbers
		for (String s : LOCATIONS.values()) {
			createMultipleArv(rd, s, s + " ");
		}

		g_everInProgramOnDate();
		
		InProgramAtProgramLocationCohortDefinition igplcd = g_inProgramAtLocationOnDate();

		g_unknownLocations(rd);

		// wrong exit from care
		CodedObsCohortDefinition cocd = new CodedObsCohortDefinition();
		cocd.setName("hivdq: Wrong exit from care_");
		cocd.setQuestion(Context.getConceptService().getConcept(
				"REASON FOR EXITING CARE"));
		cocd.setTimeModifier(TimeModifier.LAST);
		cocd.setOperator(SetComparator.NOT_IN);
		cocd.setValueList(Arrays.asList(Context.getConceptService().getConcept(
				"PATIENT DIED")));
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
		i = h.newCountIndicator("hivdq: Wrong exit from care in HIV_",
				"hivdq: Wrong exit from care in HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "exit",
				"Wrong exit from care", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "exit",
				"Wrong exit from care", i, null);

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

		i = h.newCountIndicator(
				"hivdq: Deceased without exit from care in HIV_",
				"hivdq: Deceased without exit from care in HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "noexit",
				"Deceased without exit from care", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "noexit",
				"Deceased without exit from care", i, null);

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

		i = h.newCountIndicator(
				"hivdq: Deceased but not in state died in HIV_",
				"hivdq: Deceased but not in state died in HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "nostate",
				"Deceased but not in state died", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "nostate",
				"Deceased but not in state died", i, null);

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

		i = h.newCountIndicator("hivdq: Died without exit from care_",
				"hivdq: Died without exit from care_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "noexit2",
				"Died without exit from care", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "noexit2",
				"Died without exit from care", i, null);

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
		i = h.newCountIndicator("hivdq: On ART without number_",
				"hivdq: On ART without number_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "nonoart",
				"On ART without number", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "nonoart",
				"On ART without number", i, null);

		artEncounterWithoutNumber(rd);
		
		// on following but no pre-art number
		iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In state Following_");
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		iscd.setStates(Arrays.asList(STATE_FOLLOWING));
		h.replaceCohortDefinition(iscd);

		scd = new SqlCohortDefinition();
		scd.setName("hivdq: PART number_");
		sql = "select patient_id from patient_identifier where identifier_type=13 and voided=0;";
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
				"partNumber",
				new Mapped(h.cohortDefinition("hivdq: PART number_"), h
						.parameterMap()));
		ccd.getSearches().put(
				"oldPartNumber",
				new Mapped(h.cohortDefinition("hivdq: Old PART number_"), h
						.parameterMap()));
		ccd.setCompositionString("following AND NOT partNumber AND NOT oldPartNumber");
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator(
				"hivdq: Following without number (excluding Old Pre-ART numbers)_",
				"hivdq: Following without number (excluding Old Pre-ART numbers)_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "nonopart",
				"Following without number (excluding Old Pre-ART numbers)", i,
				null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "nonopart",
				"Following without number (excluding Old Pre-ART numbers)", i,
				null);

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
		i = h.newCountIndicator("hivdq: Following with art number_",
				"hivdq: Following with art number_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "noart",
				"Following with art number", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "noart",
				"Following with art number", i, null);

		onArtWithoutEncounter(rd);
		
		followupWithoutInitialEncounter(rd);

		// not in relevant hiv state
		iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In relevant HIV state_");
		iscd.setStates(Arrays.asList(STATE_DIED, STATE_TRANSFERRED_OUT,
				STATE_TRANSFERRED_INTERNALLY, STATE_STOPPED, STATE_ON_ART,
				STATE_FOLLOWING));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);

		 ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Not in relevant HIV state_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"state",
				new Mapped(h.cohortDefinition("hivdq: In relevant HIV state_"), h
						.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.setCompositionString("NOT state AND hiv");
		h.replaceCohortDefinition(ccd);

		i = h.newCountIndicator("hivdq: Not in relevant HIV state_",
				"hivdq: Not in relevant HIV state_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "state",
				"Not in relevant HIV state", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "state",
				"Not in relevant HIV state", i, null);

		List<EncounterType> hivEncounterTypes = Arrays.asList(
				h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP"),
				h.encounterType("PART_INITIAL"),
				h.encounterType("PART_FOLLOWUP"));
		List<ProgramWorkflowState> hivTerminalStates = Arrays.asList(
				STATE_DIED, STATE_STOPPED, STATE_TRANSFERRED_OUT,
				STATE_TRANSFERRED_INTERNALLY);

		// createEncounterAfterTerminalState(rd, hivEncounterTypes,
		// hivTerminalStates,
		// Arrays.asList(h.location("Neno District Hospital") /*
		// * TODO:
		// * deal with
		// * mobile
		// * clinics ,
		// * h.location
		// * (
		// * "Ligowe HC"
		// * )
		// */),
		// h.location("Neno District Hospital"), "NNO");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(h.location("Magaleta HC")),
				h.location("Magaleta HC"), "MGT");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(h.location("Nsambe HC")),
				h.location("Nsambe HC"), "NSM");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(h.location("Neno Mission HC")),
				h.location("Neno Mission HC"), "NOP");

		// createEncounterAfterTerminalState(rd, hivEncounterTypes,
		// hivTerminalStates,
		// Arrays.asList(h.location("Lisungwi Community Hospital") /*
		// * TODO
		// * deal
		// * with
		// * mobile
		// * clinics
		// * , h.
		// * location
		// * (
		// * "Midzemba HC"
		// * )
		// */),
		// h.location("Lisungwi Community Hospital"), "LSI");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(h.location("Chifunga HC")),
				h.location("Chifunga HC"), "CFA");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(h.location("Matope HC")),
				h.location("Matope HC"), "MTE");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates, Arrays.asList(h.location("Zalewa HC")),
				h.location("Zalewa HC"), "ZLA");
		createEncounterAfterTerminalState(rd, hivEncounterTypes,
				hivTerminalStates,
				Arrays.asList(h.location("Nkhula Falls RHC")),
				h.location("Nkhula Falls RHC"), "NKA");

//		intermediateEid(rd);
	}

	private void multipleOpenStates() {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple open states without end date_");
		String sql = "select pp.patient_id from  patient_program pp, patient_state ps where pp.patient_program_id = ps.patient_program_id and ps.end_date is null and ps.voided=0 group by ps.patient_program_id having count(*)>1";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
	}

	private void wrongPartFormat(PeriodIndicatorReportDefinition[] rd) {
		SqlCohortDefinition scd;
		String sql;
		CohortIndicator i;
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
		i = h.newCountIndicator("hivdq: Wrong Pre-ART identifier format_",
				"hivdq: Wrong Pre-ART identifier format_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "formatpart",
				"Wrong Pre-ART identifier format", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "formatpart",
				"Wrong Pre-ART identifier format", i, null);
	}

	private void wrongArvFormat(PeriodIndicatorReportDefinition[] rd) {
		SqlCohortDefinition scd;
		String sql;
		CohortIndicator i;
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
		i = h.newCountIndicator("hivdq: Wrong identifier format_",
				"hivdq: Wrong identifier format_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "format",
				"Wrong identifier format", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "format",
				"Wrong identifier format", i, null);
	}

	private void multipleDeaths(PeriodIndicatorReportDefinition[] rd) {
		// multiple death
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple death_");
		String sql = "SELECT pp.patient_id "
				+ "FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps "
				+ "WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id AND pws.program_workflow_id = 1 AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0 AND pws.program_workflow_state_id=3 "
				+ "GROUP BY pp.patient_id HAVING COUNT(*) > 1;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		CohortIndicator i = h.newCountIndicator("hivdq: Multiple death_",
				"hivdq: Multiple death_", new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "died2x",
				"Died multiple times", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "died2x",
				"Died multiple times", i, null);
	}

	private void g_unknownLocations(PeriodIndicatorReportDefinition[] rd) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Unknown location_");
		String sql = "select pp.patient_id from patient_program pp where pp.voided=0 and pp.program_id=1 and pp.location_id not in (";
		for (Location l : LOCATIONS.keySet()) {
			sql += l.getId() + ", ";
		}
		sql = sql.substring(0, sql.length() - 2) + ");";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		CohortIndicator i = h.newCountIndicator("hivdq: Unknown location_",
				"hivdq: Unknown location_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "unknwnloc",
				"Unknown location", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "unknownloc",
				"Unknwon location", i, null);
	}

	private void createProgramCompletionMismatch(
			PeriodIndicatorReportDefinition[] rd) {
		/*
-- Most recent state for a program enrollment
-- not sure why the group_concat values can be used for a join, but it seems to work
-- this should be THE query to return the most recent state for a patient_program with the assumption
-- that the highest patient_state_id is also the most recent one
-- note: a temp table didn't work as mysql can't reuse a temp table in a subquery
-- MySQL specific

SELECT pp.patient_id, pp.patient_program_id, pws.program_workflow_state_id, ps.patient_state_id
FROM (
  SELECT pp.patient_id a, pp.patient_program_id b, pws.program_workflow_state_id c, group_concat(ps.patient_state_id order by ps.patient_state_id desc) d
  FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps 
  WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id 
    AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id 
    AND pws.program_workflow_id = 1 
    AND pw.retired = 0 AND pp.voided = 0 AND ps.voided = 0  
--  AND pp.patient_id=15925
  GROUP BY pp.patient_id, pp.patient_program_id) most_recent_state, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps 
WHERE most_recent_state.d=ps.patient_state_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id 
  AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id 
  AND pws.program_workflow_state_id in (2,3,6) and pp.date_completed is  null 

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
		+ "  AND pws.program_workflow_state_id in (2,3,6) and pp.date_completed is  null ";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		CohortIndicator i = h.newCountIndicator("hivdq: Terminal state, program not completed_",
				"hivdq: Terminal state, program not completed_", new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "prgnotcompleted",
				" Terminal state, program not completed", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "prgnotcompleted",
				" Terminal state, program not completed", i, null);

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
		+ "  AND pws.program_workflow_state_id not in (2,3,6) and pp.date_completed is not null ";
		 
		 scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		 i = h.newCountIndicator("hivdq: Non terminal state, program completed_",
				"hivdq: Non terminal state, program completed_", new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "prgcompleted",
				" Non terminal state, program completed", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "prgcompleted",
				" Non terminal state, program completed", i, null);
	}

	private void followupWithoutInitialEncounter(PeriodIndicatorReportDefinition[] rd) {
			EncounterCohortDefinition ecd = new EncounterCohortDefinition();
			ecd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			ecd.addParameter(new Parameter("encounterTypeList", "encounterTypeList", EncounterType.class));
			ecd.setName("hivdq: With encounter_");
			ecd.setTimeQualifier(TimeQualifier.FIRST);
			h.replaceCohortDefinition(ecd);

			CompositionCohortDefinition ccd = new CompositionCohortDefinition();
			ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
			ccd.setName("hivdq: Followup without Initial_");
			ccd.getSearches().put(
					"artFollowup",
					new Mapped(h.cohortDefinition("hivdq: With encounter_"), h
							.parameterMap("onOrBefore", "${endDate}", "encounterTypeList", Arrays.asList(h.encounterType("ART_FOLLOWUP")))));
			ccd.getSearches().put(
					"artInitial",
					new Mapped(h.cohortDefinition("hivdq: With encounter_"), h
							.parameterMap("onOrBefore", "${endDate}", "encounterTypeList", Arrays.asList(h.encounterType("ART_INITIAL")))));
			ccd.setCompositionString("artFollowup AND NOT artInitial");
			h.replaceCohortDefinition(ccd);
			CohortIndicator i = h.newCountIndicator(
					"hivdq: Followup without Initial_",
					"hivdq: Followup without Initial_",
					h.parameterMap("endDate", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd[0], "noinitial",
					"Followup without Initial", i, null);
			PeriodIndicatorReportUtil.addColumn(rd[1], "noinitial",
					"Followup without Initial", i, null);

			ccd = new CompositionCohortDefinition();
			ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
			ccd.setName("hivdq: Pre-ART Followup without Initial_");
			ccd.getSearches().put(
					"partFollowup",
					new Mapped(h.cohortDefinition("hivdq: With encounter_"), h
							.parameterMap("onOrBefore", "${endDate}", "encounterTypeList", Arrays.asList(h.encounterType("PART_FOLLOWUP")))));
			ccd.getSearches().put(
					"partInitial",
					new Mapped(h.cohortDefinition("hivdq: With encounter_"), h
							.parameterMap("onOrBefore", "${endDate}", "encounterTypeList", Arrays.asList(h.encounterType("PART_INITIAL")))));
			ccd.setCompositionString("partFollowup AND NOT partInitial");
			h.replaceCohortDefinition(ccd);
			 i = h.newCountIndicator(
					"hivdq: Pre-ART Followup without Initial_",
					"hivdq: Pre-ART Followup without Initial_",
					h.parameterMap("endDate", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd[0], "partnoinitial",
					"Pre-ART Followup without Initial", i, null);
			PeriodIndicatorReportUtil.addColumn(rd[1], "partnoinitial",
					"Pre-ART Followup without Initial", i, null);
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

	// old stuff regarding the old eid program, it was never properly defined anyways.
	private void intermediateEid(PeriodIndicatorReportDefinition[] rd) {
		final Program EID_PROGRAM = Context.getProgramWorkflowService()
				.getProgramByName("EARLY INFANT DIAGNOSIS PROGRAM");

		// in both programs
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: In EID and HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"eid",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		ccd.setCompositionString("eid AND hiv");
		h.replaceCohortDefinition(ccd);
		CohortIndicator i = h.newCountIndicator("hivdq: In EID and HIV_",
				"hivdq: In EID and HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidandhiv",
				"In EID and HIV", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidandhiv",
				"In EID and HIV", i, null);

		// too long in eid
		AgeCohortDefinition age = new AgeCohortDefinition();
		age.setName("hivdq: Above 17 months_");
		age.addParameter(new Parameter("effectiveDate", "effectiveDate",
				Date.class));
		age.setMinAge(18);
		age.setMinAgeUnit(DurationUnit.MONTHS);
		h.replaceCohortDefinition(age);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: EID Above 17 months_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"eid",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		ccd.getSearches().put(
				"age",
				new Mapped(h.cohortDefinition("hivdq: Above 17 months_"), h
						.parameterMap("effectiveDate", "${endDate}")));
		ccd.setCompositionString("eid AND age");
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator("hivdq: EID Above 17 months_",
				"hivdq: EID Above 17 months_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidage",
				"EID Above 17 months", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidage",
				"EID Above 17 months", i, null);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: EID Above 17 months not in HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"eid",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.getSearches().put(
				"age",
				new Mapped(h.cohortDefinition("hivdq: Above 17 months_"), h
						.parameterMap("effectiveDate", "${endDate}")));
		ccd.setCompositionString("eid AND age AND NOT hiv");
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator("hivdq: EID Above 17 months not in HIV_",
				"hivdq: EID Above 17 months not in HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidagenothiv",
				"EID Above 17 months not in HIV", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidagenothiv",
				"EID Above 17 months not in HIV", i, null);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: EID Above 17 months in HIV_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"eid",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		ccd.getSearches().put(
				"hiv",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								PROGRAM)));
		ccd.getSearches().put(
				"age",
				new Mapped(h.cohortDefinition("hivdq: Above 17 months_"), h
						.parameterMap("effectiveDate", "${endDate}")));
		ccd.setCompositionString("eid AND age AND hiv");
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator("hivdq: EID Above 17 months in HIV_",
				"hivdq: EID Above 17 months in HIV_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidagehiv",
				"EID Above 17 months in HIV", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidagehiv",
				"EID Above 17 months in HIV", i, null);

		// wrong eid format
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Wrong EID identifier format_");
		String eidFormat = "identifier NOT regexp '^[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]$'";
		String sql = "select patient_id from patient_identifier "
				+ "where identifier_type=9 and voided=0 and (" + eidFormat
				+ ")";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);
		i = h.newCountIndicator("hivdq: Wrong EID identifier format_",
				"hivdq: Wrong EID identifier format_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidformat",
				"Wrong EID identifier format", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidformat",
				"Wrong EID identifier format", i, null);

		// unknown location
		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Unknown EID locations_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		String composition = "";
		int c = 1;
		for (Location l : LOCATIONS.keySet()) {
			ccd.getSearches().put(
					"" + c,
					new Mapped(h.cohortDefinition("hivdq: Not at location_"), h
							.parameterMap("onDate", "${endDate}", "location",
									l, "programs", EID_PROGRAM)));
			composition += c + " AND ";
			c++;
		}
		ccd.getSearches().put(
				"unknown",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		composition += " unknown";
		ccd.setCompositionString(composition);
		h.replaceCohortDefinition(ccd);
		i = h.newCountIndicator("hivdq: Unknown EID locations_",
				"hivdq: Unknown EID locations_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidunknown",
				"Unknown EID locations", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidunknown",
				"Unknown EID locations", i, null);

		ProgramWorkflowState EID_STATE_FOLLOWING = EID_PROGRAM
				.getWorkflowByName("PATIENT STATUS")
				.getStateByName("FOLLOWING");
		ProgramWorkflowState EID_STATE_DISCHARGED = EID_PROGRAM
				.getWorkflowByName("PATIENT STATUS").getStateByName(
						"DISCHARGED");
		ProgramWorkflowState EID_STATE_ON_ART = EID_PROGRAM.getWorkflowByName(
				"PATIENT STATUS").getStateByName("ON ANTIRETROVIRALS");
		ProgramWorkflowState EID_STATE_TRANSFERRED_OUT = EID_PROGRAM
				.getWorkflowByName("PATIENT STATUS").getStateByName(
						"PATIENT TRANSFERRED OUT");
		ProgramWorkflowState EID_STATE_DIED = EID_PROGRAM.getWorkflowByName(
				"PATIENT STATUS").getStateByName("PATIENT DIED");

		// not in relevant eid state
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("hivdq: In relevant EID state_");
		iscd.setStates(Arrays.asList(EID_STATE_FOLLOWING, EID_STATE_DISCHARGED,
				EID_STATE_ON_ART, EID_STATE_TRANSFERRED_OUT, EID_STATE_DIED));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);

		ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: Not in relevant EID state_");
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.getSearches().put(
				"state",
				new Mapped(h.cohortDefinition("hivdq: In relevant EID state_"), h
						.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put(
				"eid",
				new Mapped(h.cohortDefinition("hivdq: In program_"), h
						.parameterMap("onDate", "${endDate}", "programs",
								EID_PROGRAM)));
		ccd.setCompositionString("NOT state AND eid");
		h.replaceCohortDefinition(ccd);

		i = h.newCountIndicator("hivdq: Not in relevant EID state_",
				"hivdq: Not in relevant EID state_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "eidstate",
				"Not in relevant EID state", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "eidstate",
				"Not in relevant EID state", i, null);

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
		ipcd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		ipcd.addParameter(new Parameter("programs", "programs", Program.class));
		h.replaceCohortDefinition(ipcd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("hivdq: In program_");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.addParameter(new Parameter("programs", "programs", Program.class));
		ccd.getSearches().put(
				"state",
				new Mapped(h.cohortDefinition("hivdq: Ever in program_"), h
						.parameterMap("programs", "${programs}", "enrolledOnOrBefore", "${onDate}")));
		ccd.setCompositionString("state");
		h.replaceCohortDefinition(ccd);
	}
	
	private void onArtWithoutEncounter(PeriodIndicatorReportDefinition[] rd) {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ecd.setName("hivdq: No ART encounter_");
		ecd.setReturnInverse(true);
		ecd.setEncounterTypeList(Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP")));
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
		CohortIndicator i = h.newCountIndicator(
				"hivdq: On ART without encounter_",
				"hivdq: On ART without encounter_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "noartenc",
				"On ART without encounter", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "noartenc",
				"On ART without encounter", i, null);
	}

	private void artEncounterWithoutNumber(PeriodIndicatorReportDefinition[] rd) {
		EncounterCohortDefinition ecd = new EncounterCohortDefinition();
		ecd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ecd.setName("hivdq: ART encounter_");
		ecd.setEncounterTypeList(Arrays.asList(h.encounterType("ART_INITIAL"),
				h.encounterType("ART_FOLLOWUP")));
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
		CohortIndicator i = h.newCountIndicator("hivdq: ART encounter without number_",
				"hivdq: ART encounter without number_",
				h.parameterMap("endDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0], "artnono",
				"ART encounter without number", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "artnono",
				"ART encounter without number", i, null);

	}

	private void createEncounterAfterTerminalState(
			PeriodIndicatorReportDefinition[] rd,
			List<EncounterType> hivEncounterTypes,
			List<ProgramWorkflowState> hivTerminalStates,
			List<Location> clinicLocations, Location enrollmentLocation,
			String siteCode) {
		CohortIndicator i;
		EncounterAfterProgramStateCohortDefinition eapscd = new EncounterAfterProgramStateCohortDefinition();
		eapscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		eapscd.setName("hivdq: " + siteCode
				+ ": Hiv encounter after terminal state_");
		eapscd.setClinicLocations(clinicLocations);
		eapscd.setEnrollmentLocation(enrollmentLocation);
		eapscd.setEncounterTypesAfterChangeToTerminalState(hivEncounterTypes);
		eapscd.setTerminalStates(hivTerminalStates);
		h.replaceCohortDefinition(eapscd);
		i = h.newCountIndicator("hivdq: " + siteCode
				+ ": Hiv encounter after terminal state_", "hivdq: " + siteCode
				+ ": Hiv encounter after terminal state_",
				h.parameterMap("onDate", "${endDate}"));
		PeriodIndicatorReportUtil.addColumn(rd[0],
				"term" + siteCode.toLowerCase(), siteCode
						+ ": Hiv encounter after terminal state", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1],
				"term" + siteCode.toLowerCase(), siteCode
						+ ": Hiv encounter after terminal state", i, null);
	}

	private void createLastInRangeNumber(PeriodIndicatorReportDefinition[] rd,
			String loc, String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Out of range for " + loc + "_");
		scd.setQuery(sqlForOutOfRangeNumbers(prefix));
		h.replaceCohortDefinition(scd);
		CohortIndicator i = h.newCountIndicator("hivdq: Out of range for "
				+ loc + "_", "hivdq: Out of range for " + loc + "_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "gap" + loc.toLowerCase(),
				loc + ": Last In-range ARV number before gap in sequence", i,
				null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "gap" + loc.toLowerCase(),
				loc + ": Last In-range ARV number before gap in sequence", i,
				null);
	}

	private void createMultipleArv(PeriodIndicatorReportDefinition[] rd,
			String loc, String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName("hivdq: Multiple ARV numbers " + loc + "_");
		scd.setQuery(sqlForMultipleArvNumber(prefix));
		h.replaceCohortDefinition(scd);
		CohortIndicator i = h.newCountIndicator("hivdq: Multiple ARV numbers "
				+ loc + "_", "hivdq: Multiple ARV numbers " + loc + "_",
				new HashMap<String, Object>());
		PeriodIndicatorReportUtil.addColumn(rd[0], "dup" + loc.toLowerCase(),
				loc + ": Multiple ARV numbers", i, null);
		PeriodIndicatorReportUtil.addColumn(rd[1], "dup" + loc.toLowerCase(),
				loc + ": Multiple ARV numbers", i, null);
	}

	private String sqlForOutOfRangeNumbers(String locationPrefix) {
		String sql = "select patient_id from patient_identifier "
				+ "where identifier in ("
				+ "  select concat('"
				+ locationPrefix
				+ "', cast(c.start as char))"
				+ "  from (select a.id_number  as start"
				+ "  from "
				+ "    (select substring(identifier, 5) as id_number "
				+ "    from patient_identifier where identifier_type=4 and identifier like '"
				+ locationPrefix
				+ "%' and voided = 0) as a"
				+ "  left outer join "
				+ "    (select substring(identifier, 5) as id_number "
				+ "    from patient_identifier where identifier_type=4 and identifier like '"
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
