package org.openmrs.module.pihmalawi.reports.setup.outdated;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.experimental.ApzuPatientDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
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
import org.openmrs.serialization.SerializationException;

public class SetupProgramChanges {

	private final Program PROGRAM;

	private final PatientIdentifierType PATIENT_IDENTIFIER_TYPE;

	private final List<ProgramWorkflowState> STATES;

	private final List<Location> LOCATIONS;

	ReportHelper h = new ReportHelper();

	public SetupProgramChanges(ReportHelper helper) {
		h = helper;
		PROGRAM = Context.getProgramWorkflowService().getProgramByName(
				"HIV program");
		PATIENT_IDENTIFIER_TYPE = Context.getPatientService()
				.getPatientIdentifierTypeByName("ARV Number");

		STATES = Arrays.asList(
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Pre-ART (Continue)"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"On antiretrovirals"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Patient died"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Patient transferred out"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Transferred internally"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Exposed Child (Continue)"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Discharged uninfected"),
				PROGRAM.getWorkflowByName("Treatment status").getStateByName(
						"Treatment stopped"));
		LOCATIONS = Arrays.asList(MetadataLookup.location("Neno District Hospital"),
				MetadataLookup.location("Magaleta HC"), MetadataLookup.location("Nsambe HC"),
				MetadataLookup.location("Neno Mission HC"),
				MetadataLookup.location("Lisungwi Community Hospital"),
				MetadataLookup.location("Matope HC"), MetadataLookup.location("Chifunga HC"),
				MetadataLookup.location("Zalewa HC"), MetadataLookup.location("Nkhula Falls RHC"),
				MetadataLookup.location("Matandani rural Health Center"),
				MetadataLookup.location("Ligowe HC"),
				MetadataLookup.location("Luwani RHC")
				);

	}

	public void setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		createCohortDefinitions(rd);
		h.replaceReportDefinition(rd);
		createHtmlBreakdownExternal(rd);
	}

	protected ReportDesign createHtmlBreakdownExternal(
			PeriodIndicatorReportDefinition rd) throws IOException,
			SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		ApzuPatientDataSetDefinition dsd = new ApzuPatientDataSetDefinition();
		dsd.setIncludeDefaulterActionTaken(false);
		dsd.setIncludeMissedAppointmentColumns(false);
		int columns = rd.getIndicatorDataSetDefinition().getColumns().size();
		for (int i = 1; i <= columns; i++) {
			m.put("" + i, new Mapped<DataSetDefinition>(dsd, null));
		}
		m.put("initiated", new Mapped<DataSetDefinition>(dsd, null));
		dsd.setEncounterTypes(null);
		dsd.setPatientIdentifierType(PATIENT_IDENTIFIER_TYPE);

		return h.createHtmlBreakdown(rd, PROGRAM.getName()
				+ " Changes Breakdown_", m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().equals(PROGRAM.getName() + " Changes Breakdown_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, PROGRAM.getName()
				+ " Changes_ Data Set");
		h.purgeDefinition(ReportDefinition.class, PROGRAM.getName()
				+ " Changes_");
		h.purgeAll("changes: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setName(PROGRAM.getName() + " Changes_");
		rd.setupDataSetDefinition();
		return rd;
	}

	private void createCohortDefinitions(PeriodIndicatorReportDefinition rd) {
		// In state at location
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName("changes: In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName("changes: New in state at location_");
		ccd.addParameter(new Parameter("startDate", "startDate", Date.class));
		ccd.addParameter(new Parameter("endDate", "endDate", Date.class));
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("state", "state",
				ProgramWorkflowState.class));
		ccd.getSearches().put(
				"1",
				new Mapped(
						h.cohortDefinition("changes: In state at location_"), h
								.parameterMap("onDate", "${startDate}",
										"state", "${state}", "location",
										"${location}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("changes: In state at location_"),
								h.parameterMap("onDate", "${endDate}", "state",
										"${state}", "location", "${location}")));
		ccd.setCompositionString("NOT 1 AND 2");
		h.replaceCohortDefinition(ccd);

		int c = 0;
		for (Location l : LOCATIONS) {
			for (ProgramWorkflowState s : STATES) {
				c++;
				CohortIndicator i = h.newCountIndicator(
						"changes: " + s.getConcept().getName() + " at "
								+ l.getName() + "_",
						"changes: New in state at location_", h.parameterMap(
								"endDate", "${endDate}", "startDate",
								"${startDate}", "location", l, "state", s));
				PeriodIndicatorReportUtil.addColumn(rd, "" + c, "New "
						+ s.getConcept().getName() + " at " + l.getName(), i,
						null);
			}
		}

		InProgramCohortDefinition ipcd = new InProgramCohortDefinition();
		ipcd.setName("changes: In program_");
		ipcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ipcd.addParameter(new Parameter("programs", "programs", Program.class));
		h.replaceCohortDefinition(ipcd);

		// NOTE: HIV specific implementation
		// Started ART during period
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName("changes: Started ART_");
		pscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program",
				"Treatment status", "On antiretrovirals")));
		pscd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter",
				Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);

		// Following during period
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName("changes: Following_");
		iscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program",
				"Treatment status", "Pre-ART (Continue)")));
		iscd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		iscd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		h.replaceCohortDefinition(iscd);

		// Started ART from Following during period
		ccd = new CompositionCohortDefinition();
		ccd.setName("changes: Started ART from Following during period_");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								h.cohortDefinition("changes: Started ART_"),
								ParameterizableUtil
										.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}")));
		ccd.getSearches()
				.put("2",
						new Mapped(
								h.cohortDefinition("changes: Following_"),
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		ccd.setCompositionString("1 AND 2");
		h.replaceCohortDefinition(ccd);

		CohortIndicator i = h.newCountIndicator(
				"changes: Started ART from Following_",
				"changes: Started ART from Following during period_", h
						.parameterMap("endDate", "${endDate}", "startDate",
								"${startDate}"));
		PeriodIndicatorReportUtil.addColumn(rd, "initiated",
				"Started ART from Following across all locations", i, null);

	}
}
