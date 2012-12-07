package org.openmrs.module.pihmalawi.reports.setup;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.ReportHelper;
import org.openmrs.module.pihmalawi.reports.dataset.HtmlBreakdownDataSetDefinition;
import org.openmrs.module.pihmalawi.reports.renderer.ArtRegisterBreakdownRenderer;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.serialization.SerializationException;

public class SetupHivDnaPcrResults {

	ReportHelper h = new ReportHelper();

	Concept pcr = Context.getConceptService().getConceptByName("DNA-PCR Testing Result");
	Concept pcr2 = Context.getConceptService().getConceptByName("DNA-PCR Testing Result 2");
	Concept pcr3 = Context.getConceptService().getConceptByName("DNA-PCR Testing Result 3");
	Concept pcrNegative = Context.getConceptService().getConceptByIdOrName("664");
	Concept pcrPositive = Context.getConceptService().getConceptByIdOrName("703");
	Concept pcrInderterminate = Context.getConceptService().getConceptByIdOrName("1138");

	private static final Date MIN_DATE_PARAMETER = new Date(100, 1, 1);

	public SetupHivDnaPcrResults(ReportHelper helper) {
		h = helper;
	}

	public ReportDefinition[] setup() throws Exception {
		delete();

		ReportDefinition rd = createReportDefinition("expdna");
		h.replaceReportDefinition(rd);
		h.createXlsOverview(rd, "hiv_dna_pcr_results.xls", "DNA-PCR Results Overview (Excel)_", excelOverviewProperties());

//		createHtmlBreakdown(rd, "ART Register_");
//		createAppointmentAdherenceBreakdown(rd);

//		rd = createReportDefinitionForAllLocations("artregcomplete");
//		h.replaceReportDefinition(rd);
//		createHtmlBreakdown(rd, "ART Register For All Locations_");

		return new ReportDefinition[] { rd };
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		for (int i = 0 ; i < ApzuReportElementsArt.hivStaticLocations().size(); i++) {
			properties.put("loc" + i + "name", ApzuReportElementsArt.hivStaticLocations().get(i).getName());
		}
		return properties;
	}

	protected ReportDesign createHtmlBreakdown(ReportDefinition rd, String name)
			throws IOException, SerializationException {
		Map<String, Mapped<? extends DataSetDefinition>> m = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();

		HtmlBreakdownDataSetDefinition dsd = new HtmlBreakdownDataSetDefinition();
		dsd.setPatientIdentifierType(Context.getPatientService()
				.getPatientIdentifierTypeByName("HCC Number"));
		dsd.setHtmlBreakdownPatientRowClassname(ArtRegisterBreakdownRenderer.class
				.getName());

		m.put("breakdown", new Mapped<DataSetDefinition>(dsd, null));

		return h.createHtmlBreakdown(rd, name, m);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if (rd.getName().startsWith("DNA-PCR Results Overview (Excel)_")) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeDefinition(DataSetDefinition.class, "HIV Exposed Children DNA-PCR Results_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "_");
		h.purgeAll("expdna");
	}

	private ReportDefinition createReportDefinition(String prefix) {
		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setName("HIV Exposed Children DNA-PCR Results_");
		rd.setupDataSetDefinition();

		// 1 currently enrolled
		CohortDefinition exposedEnrolled = ApzuReportElementsArt.exposedActiveAtLocationOnDate(prefix);

		// 1.1 without any result
		CohortDefinition anyValidResult = anyDnaPcrResultInPeriod(prefix + ": Any Valid DNA PCR", Arrays.asList(pcrPositive, pcrNegative));
		CompositionCohortDefinition withoutAnyResult = new CompositionCohortDefinition();
		withoutAnyResult.setName(prefix + ": Enrolled without any valid DNA PCR_");
		withoutAnyResult.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		withoutAnyResult.addParameter(new Parameter("location", "location", Location.class));
		withoutAnyResult.getSearches().put(
				"exposedEnrolled",
				new Mapped(exposedEnrolled, h.parameterMap("onDate", "${startedOnOrBefore}", "location", "${location}")));
		withoutAnyResult.getSearches().put(
				"anyDnaPcrResult",
				new Mapped(anyValidResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", MIN_DATE_PARAMETER)));
		withoutAnyResult.setCompositionString("exposedEnrolled AND NOT anyDnaPcrResult");
		h.replaceCohortDefinition(withoutAnyResult);

		// 1.2 with any result
		CompositionCohortDefinition withAnyResult = new CompositionCohortDefinition();
		withAnyResult.setName(prefix + ": Enrolled with any valid DNA PCR_");
		withAnyResult.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		withAnyResult.addParameter(new Parameter("location", "location", Location.class));
		withAnyResult.getSearches().put(
				"exposedEnrolled",
				new Mapped(exposedEnrolled, h.parameterMap("onDate", "${startedOnOrBefore}", "location", "${location}")));
		withAnyResult.getSearches().put(
				"anyDnaPcrResult",
				new Mapped(anyValidResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", MIN_DATE_PARAMETER)));
		withAnyResult.setCompositionString("exposedEnrolled AND anyDnaPcrResult");
		h.replaceCohortDefinition(withAnyResult);

		// 1.3 positive result, not yet on ARVs
		CohortDefinition anyPositiveResult = anyDnaPcrResultInPeriod("expdna: Any Positive DNA PCR", Arrays.asList(pcrPositive));
		CohortDefinition everOnArt = ApzuReportElementsArt.artEverEnrolledOnDate(prefix);
		CompositionCohortDefinition positiveNotYetOnArvs = new CompositionCohortDefinition();
		positiveNotYetOnArvs.setName(prefix + ": Enrolled and Positive DNA PCR not yet on ARVs_");
		positiveNotYetOnArvs.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		positiveNotYetOnArvs.addParameter(new Parameter("location", "location", Location.class));
		positiveNotYetOnArvs.getSearches().put(
				"exposedEnrolled",
				new Mapped(exposedEnrolled, h.parameterMap("onDate", "${startedOnOrBefore}", "location", "${location}")));
		positiveNotYetOnArvs.getSearches().put(
				"everOnArt",
				new Mapped(everOnArt, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}")));
		positiveNotYetOnArvs.getSearches().put(
				"anyPositiveResult",
				new Mapped(anyPositiveResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", MIN_DATE_PARAMETER)));
		positiveNotYetOnArvs.setCompositionString("exposedEnrolled AND anyPositiveResult AND NOT everOnArt");
		h.replaceCohortDefinition(positiveNotYetOnArvs);
		
		// 1.4 pending result
		// todo
		
		// 2 
		CohortDefinition everExposed = ApzuReportElementsArt.exposedEnrolledAtLocationInPeriod(prefix);

		// 3 
		CohortDefinition anyResult = anyDnaPcrResultInPeriod("expdna: Any DNA PCR", Arrays.asList(pcrInderterminate, pcrPositive, pcrNegative));
		CompositionCohortDefinition exposedResultsEver = new CompositionCohortDefinition();
		exposedResultsEver.setName(prefix + ": Newly enrolled with any DNA PCR");
		exposedResultsEver.addParameter(new Parameter("location", "location", Location.class));
		exposedResultsEver.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		exposedResultsEver.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		exposedResultsEver.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", MIN_DATE_PARAMETER, "startedOnOrBefore", "${startedOnOrBefore}")));
		exposedResultsEver.getSearches().put(
				"anyResult",
				new Mapped(anyResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		exposedResultsEver.setCompositionString("anyResult AND everExposed");
		h.replaceCohortDefinition(exposedResultsEver);
		
		// 3.1
		CohortDefinition anyNegativeResult = anyDnaPcrResultInPeriod("expdna: Any Negative DNA PCR", Arrays.asList(pcrNegative));;
		CompositionCohortDefinition exposedNegativeResultsEver = new CompositionCohortDefinition();
		exposedNegativeResultsEver.setName(prefix + ": Newly enrolled and negative DNA PCR_");
		exposedNegativeResultsEver.addParameter(new Parameter("location", "location", Location.class));
		exposedNegativeResultsEver.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		exposedNegativeResultsEver.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		exposedNegativeResultsEver.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", MIN_DATE_PARAMETER, "startedOnOrBefore", "${endDate}")));
		exposedNegativeResultsEver.getSearches().put(
				"anyNegativeResult",
				new Mapped(anyNegativeResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		exposedNegativeResultsEver.setCompositionString("anyNegativeResult AND everExposed");
		h.replaceCohortDefinition(exposedNegativeResultsEver);

		// 3.2
		CohortDefinition anyIndeterminateResult = anyDnaPcrResultInPeriod("expdna: Any Indeterminate DNA PCR", Arrays.asList(pcrInderterminate));;
		CompositionCohortDefinition exposedIndeterminateResultsEver = new CompositionCohortDefinition();
		exposedIndeterminateResultsEver.setName(prefix + ": Newly enrolled and indeterminate DNA PCR_");
		exposedIndeterminateResultsEver.addParameter(new Parameter("location", "location", Location.class));
		exposedIndeterminateResultsEver.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		exposedIndeterminateResultsEver.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		exposedIndeterminateResultsEver.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", MIN_DATE_PARAMETER, "startedOnOrBefore", "${endDate}")));
		exposedIndeterminateResultsEver.getSearches().put(
				"anyIndeterminateResult",
				new Mapped(anyIndeterminateResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		exposedIndeterminateResultsEver.setCompositionString("anyIndeterminateResult AND everExposed");
		h.replaceCohortDefinition(exposedIndeterminateResultsEver);

		// 3.3
		CompositionCohortDefinition exposedPositiveResultsEver = new CompositionCohortDefinition();
		exposedPositiveResultsEver.setName(prefix + ": Newly enrolled and positive DNA PCR_");
		exposedPositiveResultsEver.addParameter(new Parameter("location", "location", Location.class));
		exposedPositiveResultsEver.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		exposedPositiveResultsEver.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		exposedPositiveResultsEver.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", MIN_DATE_PARAMETER, "startedOnOrBefore", "${endDate}")));
		exposedPositiveResultsEver.getSearches().put(
				"anyPositiveResult",
				new Mapped(anyPositiveResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		exposedPositiveResultsEver.setCompositionString("anyPositiveResult AND everExposed");
		h.replaceCohortDefinition(exposedPositiveResultsEver);

		// 3.3.1
		CompositionCohortDefinition positiveOnArvs = new CompositionCohortDefinition();
		positiveOnArvs.setName(prefix + ": Positive DNA PCR on ARVs_");
		positiveOnArvs.addParameter(new Parameter("location", "location", Location.class));
		positiveOnArvs.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		positiveOnArvs.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		positiveOnArvs.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", MIN_DATE_PARAMETER, "startedOnOrBefore", "${endDate}")));
		positiveOnArvs.getSearches().put(
				"everOnArt",
				new Mapped(everOnArt, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}")));
		positiveOnArvs.getSearches().put(
				"anyPositiveResult",
				new Mapped(anyPositiveResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		positiveOnArvs.setCompositionString("anyPositiveResult AND everExposed AND everOnArt");
		h.replaceCohortDefinition(positiveOnArvs);
		
		// 3.3.1.1
//		averageAgeOf positiveOnArvs;
		
		// 3.3.2
		CohortDefinition exposedEnrolledAnywhere = ApzuReportElementsArt.exposedActiveOnDate(prefix);
		CompositionCohortDefinition positiveNotOnArvs = new CompositionCohortDefinition();
		positiveNotOnArvs.setName(prefix + ": Positive DNA PCR not on ARVs_");
		positiveNotOnArvs.addParameter(new Parameter("location", "location", Location.class));
		positiveNotOnArvs.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		positiveNotOnArvs.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		positiveNotOnArvs.getSearches().put(
				"exposedEnrolled",
				new Mapped(exposedEnrolled, h.parameterMap("location", "${location}", "onDate", "${startedOnOrBefore}")));
		positiveNotOnArvs.getSearches().put(
				"anyPositiveResult",
				new Mapped(anyPositiveResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		positiveNotOnArvs.setCompositionString("anyPositiveResult AND exposedEnrolled");
		h.replaceCohortDefinition(positiveNotOnArvs);

		//		// 3.3.3
		CompositionCohortDefinition positiveOther = new CompositionCohortDefinition();
		positiveOther.setName(prefix + ": Positive DNA PCR other outcome_");
		positiveOther.addParameter(new Parameter("location", "location", Location.class));
		positiveOther.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		positiveOther.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		positiveOther.getSearches().put(
				"everExposed",
				new Mapped(everExposed, h.parameterMap("location", "${location}", "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}")));
		positiveOther.getSearches().put(
				"exposedEnrolled",
				new Mapped(exposedEnrolled, h.parameterMap("location", "${location}", "onDate", "${startedOnOrBefore}")));
		positiveOther.getSearches().put(
				"anyPositiveResult",
				new Mapped(anyPositiveResult, h.parameterMap("onOrBefore", "${startedOnOrBefore}", "onOrAfter", "${startedOnOrAfter}")));
		positiveOther.getSearches().put(
				"everOnArt",
				new Mapped(everOnArt, h.parameterMap("startedOnOrBefore", "${startedOnOrBefore}")));
		positiveOther.setCompositionString("anyPositiveResult AND everExposed AND NOT exposedEnrolled AND NOT everOnArt");
		h.replaceCohortDefinition(positiveOther);

		// 3.4
//		averageAgeOf exposedResultsEver;

		for (int j = 0 ; j < ApzuReportElementsArt.hivStaticLocations().size(); j++) {
			Location l = ApzuReportElementsArt.hivStaticLocations().get(j);
//			String siteCode = ApzuReportElementsArt.hivSiteCode(l);
			String siteCode = "loc" + j;
			CohortIndicator i = h.newCountIndicator(exposedEnrolled.getName() + "-" + siteCode, exposedEnrolled,
					h.parameterMap("location", l, "onDate", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_1", exposedEnrolled.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(withoutAnyResult.getName() + "-" + siteCode, withoutAnyResult,
					h.parameterMap("location", l, "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_1_1", withoutAnyResult.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(withAnyResult.getName() + "-" + siteCode, withAnyResult,
					h.parameterMap("location", l, "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_1_2", withAnyResult.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(positiveNotYetOnArvs.getName() + "-" + siteCode, positiveNotYetOnArvs,
					h.parameterMap("location", l, "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_1_3", positiveNotYetOnArvs.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(everExposed.getName() + "-" + siteCode, everExposed,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_2", everExposed.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(exposedResultsEver.getName() + "-" + siteCode, exposedResultsEver,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3", exposedResultsEver.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(exposedNegativeResultsEver.getName() + "-" + siteCode, exposedNegativeResultsEver,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_1", exposedNegativeResultsEver.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(exposedIndeterminateResultsEver.getName() + "-" + siteCode, exposedIndeterminateResultsEver,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_2", exposedIndeterminateResultsEver.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(exposedPositiveResultsEver.getName() + "-" + siteCode, exposedPositiveResultsEver,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_3", exposedPositiveResultsEver.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(positiveOnArvs.getName() + "-" + siteCode, positiveOnArvs,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_3_1", positiveOnArvs.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(positiveNotOnArvs.getName() + "-" + siteCode, positiveNotOnArvs,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_3_2", positiveNotOnArvs.getName() + "-" + siteCode, i, null);
			i = h.newCountIndicator(positiveOther.getName() + "-" + siteCode, positiveOther,
					h.parameterMap("location", l, "startedOnOrAfter", "${startDate}", "startedOnOrBefore", "${endDate}"));
			PeriodIndicatorReportUtil.addColumn(rd, siteCode + "_3_3_3", positiveOther.getName() + "-" + siteCode, i, null);
		}
		
		return rd;
	}

	private CohortDefinition anyDnaPcrResultInPeriod(String name, List<Concept> resultConcepts) {
		CodedObsCohortDefinition cocd = new CodedObsCohortDefinition();
		cocd.setName(name + " pcr_");
		cocd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cocd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cocd.setQuestion(pcr);
		cocd.setTimeModifier(TimeModifier.ANY);
		cocd.setOperator(SetComparator.IN);
		cocd.setValueList(resultConcepts);
		h.replaceCohortDefinition(cocd);

		CodedObsCohortDefinition cocd2 = new CodedObsCohortDefinition();
		cocd2.setName(name + " pcr 2_");
		cocd2.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cocd2.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cocd2.setQuestion(pcr2);
		cocd2.setTimeModifier(TimeModifier.ANY);
		cocd2.setOperator(SetComparator.IN);
		cocd2.setValueList(resultConcepts);
		h.replaceCohortDefinition(cocd2);

		CodedObsCohortDefinition cocd3 = new CodedObsCohortDefinition();
		cocd3.setName(name + " pcr 3_");
		cocd3.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cocd3.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cocd3.setQuestion(pcr3);
		cocd3.setTimeModifier(TimeModifier.ANY);
		cocd3.setOperator(SetComparator.IN);
		cocd3.setValueList(resultConcepts);
		h.replaceCohortDefinition(cocd3);

		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(name + "_");
		ccd.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		ccd.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		ccd.getSearches().put(
				"pcr",
				new Mapped(cocd, h.parameterMap("onOrBefore", "${onOrBefore}", "onOrAfter", "${onOrAfter}")));
		ccd.getSearches().put(
				"pcr2",
				new Mapped(cocd2, h.parameterMap("onOrBefore", "${onOrBefore}", "onOrAfter", "${onOrAfter}")));
		ccd.getSearches().put(
				"pcr3",
				new Mapped(cocd3, h.parameterMap("onOrBefore", "${onOrBefore}", "onOrAfter", "${onOrAfter}")));
		ccd.setCompositionString("pcr OR pcr2 OR pcr3");
		h.replaceCohortDefinition(ccd);

		return cocd;
	}

}
