package org.openmrs.module.pihmalawi.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
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

public class ApzuReportElementsArt {

	static ReportHelper h = new ReportHelper();

	public static String[] hivRegimenConcepts = { "1A: d4T / 3TC / NVP (previous 1L)",
			"2A: AZT / 3TC / NVP (previous AZT)",
			"3A: d4T / 3TC + EFV (previous EFV)",
			"4A: AZT / 3TC + EFV (previous AZTEFV)", "5A: TDF + 3TC + EFV",
			"6A: TDF / 3TC + NVP", "7A: TDF / 3TC + LPV/r",
			"8A: AZT / 3TC + LPV/r", "1P: d4T / 3TC / NVP",
			"2P: AZT / 3TC / NVP", "3P: d4T / 3TC + EFV",
			"4P: AZT / 3TC + EFV", "9P: ABC / 3TC + LPV/r", "Other" };

	public static List<List<Location>> locations() {
		return Arrays.asList(
				Arrays.asList(MetadataLookup.location("Neno District Hospital"),
						MetadataLookup.location("Outpatient"),
						MetadataLookup.location("Registration"), MetadataLookup.location("Vitals")),
				Arrays.asList(MetadataLookup.location("Magaleta HC")),
				Arrays.asList(MetadataLookup.location("Nsambe HC")),
				Arrays.asList(MetadataLookup.location("Ligowe HC")),
				Arrays.asList(MetadataLookup.location("Matandani Rural Health Center")),
				Arrays.asList(MetadataLookup.location("Neno Mission HC")),
				Arrays.asList(MetadataLookup.location("Luwani RHC")),
				Arrays.asList(MetadataLookup.location("Lisungwi Community Hospital"),
						MetadataLookup.location("Midzemba HC")),
				Arrays.asList(MetadataLookup.location("Chifunga HC")),
				Arrays.asList(MetadataLookup.location("Matope HC")),
				Arrays.asList(MetadataLookup.location("Neno Mission HC")),
				Arrays.asList(MetadataLookup.location("Nkhula Falls RHC")),
				Arrays.asList(MetadataLookup.location("Zalewa HC")));
	}

	public static List<Location> hivLocations() {
		return Arrays.asList(MetadataLookup.location("Neno District Hospital"),
				MetadataLookup.location("Magaleta HC"), MetadataLookup.location("Nsambe HC"),
				MetadataLookup.location("Neno Mission HC"), MetadataLookup.location("Ligowe HC"),
				MetadataLookup.location("Matandani Rural Health Center"),
				MetadataLookup.location("Luwani RHC"),
				MetadataLookup.location("Lisungwi Community Hospital"),
				MetadataLookup.location("Matope HC"), MetadataLookup.location("Chifunga HC"),
				MetadataLookup.location("Zalewa HC"), MetadataLookup.location("Midzemba HC"),
				MetadataLookup.location("Nkhula Falls RHC"));
	}

	public static List<Location> hivStaticLocations() {
		return Arrays.asList(MetadataLookup.location("Neno District Hospital"),
				MetadataLookup.location("Magaleta HC"), MetadataLookup.location("Nsambe HC"),
				MetadataLookup.location("Neno Mission HC"), MetadataLookup.location("Ligowe HC"),
				MetadataLookup.location("Matandani Rural Health Center"),
				MetadataLookup.location("Luwani RHC"),
				MetadataLookup.location("Lisungwi Community Hospital"),
				MetadataLookup.location("Matope HC"), MetadataLookup.location("Chifunga HC"),
				MetadataLookup.location("Zalewa HC"),
				MetadataLookup.location("Nkhula Falls RHC"));
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

	public static List<EncounterType> hivEncounterTypes() {
		return Arrays
				.asList(MetadataLookup.encounterType("ART_INITIAL"),
						MetadataLookup.encounterType("ART_FOLLOWUP"),
						MetadataLookup.encounterType("PART_INITIAL"),
						MetadataLookup.encounterType("PART_FOLLOWUP"),
						MetadataLookup.encounterType("EXPOSED_CHILD_INITIAL"),
						MetadataLookup.encounterType("EXPOSED_CHILD_FOLLOWUP"));
	}

	public static CohortDefinition artActiveAtLocationOnDate(String prefix) {
		// On ART at end of period
		InStateAtLocationCohortDefinition iscd = new InStateAtLocationCohortDefinition();
		iscd.setName(prefix + ": On ART with location & date_");
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV program").getWorkflowByName("Treatment status")
		// .getStateByName("Transferred internally"));
		iscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition artEverEnrolledOnDate(String prefix) {
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName(prefix + ": Ever on ART_");
		pscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program",
				"Treatment status", "On antiretrovirals")));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}

	public static CohortDefinition artEverEnrolledAtLocationOnDate(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": ART ever at location_");
		pscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}

	public static CohortDefinition artEnrolledAtLocationInPeriod(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": ART at location_");
		pscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		pscd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}

	public static CohortDefinition hccEnrolledAtLocationInPeriod(
			String prefix, CohortDefinition partEnrolledAtLocationInPeriod, CohortDefinition exposedEnrolledAtLocationInPeriod) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": In HCC at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEnrolledAtLocationInPeriod, h
						.parameterMap("startedOnOrAfter",
								"${startedOnOrAfter}", "startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"exposed",
				new Mapped(exposedEnrolledAtLocationInPeriod, h.parameterMap("startedOnOrAfter",
						"${startedOnOrAfter}", 
						"startedOnOrBefore", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.setCompositionString("part OR exposed");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition hccEverEnrolledOnDate(
			String prefix, CohortDefinition partEverEnrolled, CohortDefinition exposedEverEnrolled) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": In Ever HCC_");
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolled, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}")));
		ccd.getSearches().put(
				"exposed",
				new Mapped(exposedEverEnrolled, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}")));
		ccd.setCompositionString("part OR exposed");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition hccEverEnrolledAtLocationOnDate(
			String prefix, CohortDefinition partEverEnrolled, CohortDefinition exposedEverEnrolled) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": In Ever HCC at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolled, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"exposed",
				new Mapped(exposedEverEnrolled, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.setCompositionString("part OR exposed");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partEnrolledAtLocationInPeriod(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": Pre-ART at location_");
		pscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)"));
		pscd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number part enrolled_");
		scd.addParameter(new Parameter("location", "location", Location.class));
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 and location_id = :location ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": In Pre-ART at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(pscd, h
						.parameterMap("startedOnOrAfter",
								"${startedOnOrAfter}","startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap("location",
								"${location}")));
		ccd.setCompositionString("part AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partEverEnrolledAtLocationOnDate(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": Pre-ART ever at location_");
		pscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)"));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number part ever enrolled_");
		scd.addParameter(new Parameter("location", "location", Location.class));
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 and location_id = :location ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Ever In Pre-ART at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(pscd, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap("location",
								"${location}")));
		ccd.setCompositionString("part AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partEverEnrolledIncludingOldPatientsAtLocationOnDate(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": Pre-ART ever at location inlc Old patients_");
		pscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)"));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);

		return pscd;
	}

	public static CohortDefinition partEverEnrolledOnDate(
			String prefix) {
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName(prefix + ": Pre-ART ever_");
		pscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)")));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number part ever enrolled_");
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Ever In Pre-ART_");
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(pscd, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap()));
		ccd.setCompositionString("part AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partEverEnrolledIncludingOldPatientsOnDate(
			String prefix) {
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName(prefix + ": Pre-ART ever_");
		pscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)")));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);

		return pscd;
	}

	public static CohortDefinition partDiedAtLocationOnDate(
			String prefix,
			CohortDefinition partEverEnrolledAtLocationOnDate,
			CohortDefinition artEverEnrolledAtLocationOnDate,
			CohortDefinition hivDiedAtLocationOnDate) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Died from Pre-ART at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"art",
				new Mapped(artEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"died",
				new Mapped(hivDiedAtLocationOnDate, h.parameterMap(
						"onDate", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.setCompositionString("part AND died AND NOT art");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partTransferredOutAtLocationOnDate(
			String prefix,
			CohortDefinition partEverEnrolledAtLocationOnDate,
			CohortDefinition artEverEnrolledAtLocationOnDate,
			CohortDefinition hivTransferredOutAtLocationOnDate) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Transferred from Pre-ART at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"art",
				new Mapped(artEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"to",
				new Mapped(hivTransferredOutAtLocationOnDate, h.parameterMap(
						"onDate", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.setCompositionString("part AND to AND NOT art");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partOnArtAtLocationOnDate(
			String prefix,
			CohortDefinition partEverEnrolledAtLocationOnDate,
			CohortDefinition artEverEnrolledAtLocationOnDate) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": On ART from Pre-ART at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"art",
				new Mapped(artEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.setCompositionString("part AND art");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition partOnArtAtLocationInPeriod(
			String prefix,
			CohortDefinition partEverEnrolledAtLocationOnDate,
			CohortDefinition artEnrolledAtLocationInPeriod) {
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": On ART from Pre-ART in period at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(partEverEnrolledAtLocationOnDate, h
						.parameterMap("startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.getSearches().put(
				"art",
				new Mapped(artEnrolledAtLocationInPeriod, h
						.parameterMap("startedOnOrAfter",
								"${startedOnOrAfter}", "startedOnOrBefore",
								"${startedOnOrBefore}", "location",
								"${location}")));
		ccd.setCompositionString("part AND art");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition exposedEverEnrolledAtLocationOnDate(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd2 = new PatientStateAtLocationCohortDefinition();
		pscd2.setName(prefix + ": Exposed Child ever at location_");
		pscd2.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)"));
		pscd2.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd2.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd2);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number ever exposed_");
		scd.addParameter(new Parameter("location", "location", Location.class));
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 and location_id = :location ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Ever In Exposed at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"exposed",
				new Mapped(pscd2, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}",
						"location", "${location}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap("location", "${location}")));
		ccd.setCompositionString("exposed AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition exposedEverEnrolledOnDate(
			String prefix) {
		PatientStateCohortDefinition pscd2 = new PatientStateCohortDefinition();
		pscd2.setName(prefix + ": Exposed Child ever_");
		pscd2.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)")));
		pscd2.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd2);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number ever exposed 2_");
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Ever In Exposed_");
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"exposed",
				new Mapped(pscd2, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h.parameterMap()));
		ccd.setCompositionString("exposed AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition exposedActiveAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition iscd = new InStateAtLocationCohortDefinition();
		iscd.setName(prefix + ": In Exposed Child with location & date_");
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV program").getWorkflowByName("Treatment status")
		// .getStateByName("Transferred internally"));
		iscd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)"));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition exposedActiveOnDate(String prefix) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(prefix + ": In Exposed Child with date_");
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV program").getWorkflowByName("Treatment status")
		// .getStateByName("Transferred internally"));
		iscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)")));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition exposedEnrolledAtLocationInPeriod(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd2 = new PatientStateAtLocationCohortDefinition();
		pscd2.setName(prefix + ": Exposed Child at location_");
		pscd2.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Exposed Child (Continue)"));
		pscd2.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd2.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		pscd2.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd2);

		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix + ": HCC number ever exposed_");
		scd.addParameter(new Parameter("location", "location", Location.class));
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 and location_id = :location ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(prefix + ": Newly enrolled in Exposed at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("startedOnOrAfter",
				"startedOnOrAfter", Date.class));
		ccd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		ccd.getSearches().put(
				"exposed",
				new Mapped(pscd2, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}", "startedOnOrAfter", "${startedOnOrAfter}",
						"location", "${location}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap("location", "${location}")));
		ccd.setCompositionString("exposed AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition genericInStateAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition hivTransferredOutAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": Transferred out at location_");
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		islcd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Patient transferred out"));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition hivDiedAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": Died at location_");
		islcd.addParameter(new Parameter("onDate", "onDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		islcd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Patient died"));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition transferredInternallyOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": transferred internally_");
		iscd.setStates(Arrays.asList(MetadataLookup.workflowState("HIV program",
				"Treatment status", "Transferred internally")));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition everInStateAtLocationOnDate(String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix
				+ ": Ever enrolled in program at location with state_");
		String sql = ""
				+ "SELECT pp.patient_id"
				+ " FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps"
				+ " WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id"
				+ "   AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id"
				+ "   AND pws.program_workflow_id = 1 AND ps.state = :state "
				+ "   AND pp.location_id = :location AND (ps.end_date <= :endDate OR ps.end_date IS NULL)"
				+ "   AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0"
				+ " GROUP BY pp.patient_id;";
		scd.setQuery(sql);
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		h.replaceCohortDefinition(scd);
		return scd;
	}

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

	public static CohortDefinition artMissedAppointmentAtLocationOnDate(String string) {
		Concept CONCEPT_APPOINTMENT_DATE = Context.getConceptService()
		.getConceptByName("Appointment date");
		EncounterType PART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
		.getEncounterType("ART_FOLLOWUP");

		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName(string + ": ART Missed Appointments_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(CONCEPT_APPOINTMENT_DATE);
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setEncounterTypeList(Arrays.asList(PART_FOLLOWUP_ENCOUNTER));
		dod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		dod.addParameter(new Parameter("location", "Location",
				Location.class));
		dod.addParameter(new Parameter("value1", "value1", Date.class));
		h.replaceCohortDefinition(dod);
		return dod;
	}

	public static CohortDefinition hccMissedAppointmentAtLocationOnDate(String string) {
		Concept CONCEPT_APPOINTMENT_DATE = Context.getConceptService()
		.getConceptByName("Appointment date");
		EncounterType PART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
		.getEncounterType("PART_FOLLOWUP");
		EncounterType EXPOSED_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
		.getEncounterType("EXPOSED_CHILD_FOLLOWUP");

		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName(string + ": HCC Missed Appointments_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(CONCEPT_APPOINTMENT_DATE);
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setEncounterTypeList(Arrays.asList(PART_FOLLOWUP_ENCOUNTER, EXPOSED_FOLLOWUP_ENCOUNTER));
		dod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		dod.addParameter(new Parameter("location", "Location",
				Location.class));
		dod.addParameter(new Parameter("value1", "value1", Date.class));
		h.replaceCohortDefinition(dod);
		return dod;
	}

	public static CohortDefinition partMissedAppointmentAtLocationOnDate(String string) {
		Concept CONCEPT_APPOINTMENT_DATE = Context.getConceptService()
		.getConceptByName("Appointment date");
		EncounterType PART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
		.getEncounterType("PART_FOLLOWUP");

		DateObsCohortDefinition dod = new DateObsCohortDefinition();
		dod.setName(string + ": Pre-ART Missed Appointments_");
		dod.setTimeModifier(TimeModifier.MAX);
		dod.setQuestion(CONCEPT_APPOINTMENT_DATE);
		dod.setOperator1(RangeComparator.LESS_THAN);
		dod.setEncounterTypeList(Arrays.asList(PART_FOLLOWUP_ENCOUNTER));
		dod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		dod.addParameter(new Parameter("location", "Location",
				Location.class));
		dod.addParameter(new Parameter("value1", "value1", Date.class));
		h.replaceCohortDefinition(dod);
		return dod;
	}

	public static CohortDefinition partActiveWithDefaultersAtLocationOnDate(
			String string) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(string + ": Active Pre-ART at location_");
		islcd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		islcd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"Pre-ART (Continue)"));
		h.replaceCohortDefinition(islcd);
		
		// excluding everyone without a hcc number for the location (old pre-art
		// and eid patients)
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(string + ": PART number part enrolled_");
		scd.addParameter(new Parameter("location", "location", Location.class));
		String sql = "select patient_id from patient_identifier where identifier_type=19 and voided=0 and location_id = :location ;";
		scd.setQuery(sql);
		h.replaceCohortDefinition(scd);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(string + ": In Pre-ART (x) at location_");
		ccd.addParameter(new Parameter("location", "location", Location.class));
		ccd.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		ccd.getSearches().put(
				"part",
				new Mapped(islcd, h
						.parameterMap("onDate",
								"${onDate}", "location",
								"${location}")));
		ccd.getSearches().put(
				"hccnumber",
				new Mapped(scd, h
						.parameterMap("location",
								"${location}")));
		ccd.setCompositionString("part AND hccnumber");
		h.replaceCohortDefinition(ccd);

		return ccd;
	}

	public static CohortDefinition artActiveWithDefaultersAtLocationOnDate(
			String string) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(string + ": Active ART at location_");
		islcd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		islcd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition artActiveWithDefaultersOnDate(
			String string) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(string + ": Active ART_");
		islcd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		islcd.setState(MetadataLookup.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}
}
