package org.openmrs.module.pihmalawi.reports;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ApzuReportElementsArt {

	static ReportHelper h = new ReportHelper();
	static HivMetadata hivMetadata = new HivMetadata();

	public static List<Location> hivLocations() {
		return hivMetadata.getHivLocations();
	}

	public static List<Location> hivStaticLocations() {
		return hivMetadata.getHivStaticLocations();
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

	@Deprecated
	public static List<EncounterType> hivEncounterTypes() {
		return hivMetadata.getHivAndExposedChildEncounterTypes();
	}

	@Deprecated
	public static CohortDefinition hivDefaultedAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": Defaulted at location_");
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		islcd.setState(hivMetadata.getDefaultedState());
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition transferredInternallyOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": transferred internally_");
		iscd.setStates(Arrays.asList(hivMetadata.getTransferredInternallyState()));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	@Deprecated // hivCohorts.getInOnArtStateOnEndDate();
	public static CohortDefinition onArtOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": On ART_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition inHccOnDate(String prefix) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(prefix + ": In HCC without number_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Pre-ART (Continue)"));
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Exposed Child (Continue)"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number for In state_");
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": In HCC _");
		ccd.addParameter(new Parameter("onDate", "onDate", Date.class));
		ccd.getSearches().put(
				"hcc",
				new Mapped(iscd, h.parameterMap("onDate",
						"${onDate}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap()));
		ccd.setCompositionString("hcc AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}
}
