package org.openmrs.module.pihmalawi.reports.setup;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.ApzuReportElementsArt;
import org.openmrs.module.pihmalawi.reports.Helper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;

public class SetupArvRegimen {

	protected static final Log log = LogFactory
			.getLog(HibernatePihMalawiQueryDao.class);

	private final Concept CONCEPT_REGIMEN;

	Helper h = new Helper();

	private final EncounterType ART_FOLLOWUP_ENCOUNTER;

	public SetupArvRegimen(Helper helper) {
		h = helper;
		CONCEPT_REGIMEN = Context.getConceptService().getConceptByName(
				"Malawi Antiretroviral drugs received");
		ART_FOLLOWUP_ENCOUNTER = Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP");
	}

	public void setup() throws Exception {
		delete();

		PeriodIndicatorReportDefinition rd = createReportDefinition();
		h.replaceReportDefinition(rd);
		h.createXlsOverview(rd, "Arv_Regimen.xls", "ARV Regimen (Excel)_", null);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ARV Regimen (Excel)_".equals(rd.getName().toString())) {
				rs.purgeReportDesign(rd);
			}
		}

		h.purgeDimension("arvregimen: by location_");
		h.purgeDefinition(DataSetDefinition.class, "ARV Regimen_ Data Set");
		h.purgeDefinition(ReportDefinition.class, "ARV Regimen_");
		h.purgeAll("arvregimen: ");
	}

	private PeriodIndicatorReportDefinition createReportDefinition() {

		CohortDefinition onArt = ApzuReportElementsArt
				.artActiveWithDefaultersOnDate("arvregimen");
		CohortDefinition onArtAtLocation = ApzuReportElementsArt
				.artActiveWithDefaultersAtLocationOnDate("arvregimen");

		CohortDefinitionDimension byLocation = new CohortDefinitionDimension();
		byLocation.setName("arvregimen: by location_");
		byLocation.addParameter(new Parameter("onDate", "onDate", Date.class));
		for (Location l : ApzuReportElementsArt.hivStaticLocations()) {
			byLocation.addCohortDefinition(
					ApzuReportElementsArt.hivSiteCode(l), onArtAtLocation,
					h.parameterMap("onDate", "${onDate}", "location", l));
		}
		h.replaceDefinition(byLocation);

		PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
		rd.setBaseCohortDefinition(onArt,
				h.parameterMap("onDate", "${endDate}"));
		rd.setName("ARV Regimen_");
		rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
		rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
		rd.setupDataSetDefinition();
		rd.addDimension("location", byLocation,
				h.parameterMap("onDate", "${endDate}"));

		for (String r : ApzuReportElementsArt.hivRegimenConcepts) {
			// technically it should be posible to hand in the regimen concepts
			// as a parameter list, however i can;t get it to work
			// so i'm breaking down the regimen concepts in separate
			// cohortdefinitions
			CodedObsCohortDefinition current_regimen = new CodedObsCohortDefinition();
			current_regimen.setName("arvregimen: current_regimen_"
					+ r.substring(0, 3));
			current_regimen.addParameter(new Parameter("endDate", "endDate",
					Date.class));
			current_regimen.setQuestion(CONCEPT_REGIMEN);
			current_regimen.setTimeModifier(TimeModifier.LAST);
			current_regimen.setOperator(SetComparator.IN);
			current_regimen.setEncounterTypeList(Arrays
					.asList(ART_FOLLOWUP_ENCOUNTER));
			current_regimen.setValueList(Arrays.asList(Context
					.getConceptService().getConcept(r)));
			h.replaceCohortDefinition(current_regimen);
			CohortIndicator i = h.newCountIndicator(
					"arvregimen: " + r.substring(0, 3), current_regimen,
					h.parameterMap("endDate", "${endDate}"));
			for (Location l : ApzuReportElementsArt.hivStaticLocations()) {
				PeriodIndicatorReportUtil.addColumn(
						rd,
						r.substring(0, 3)
								+ ApzuReportElementsArt.hivSiteCode(l),
						r.substring(0, 3)
								+ ApzuReportElementsArt.hivSiteCode(l),
						i,
						h.hashMap("location",
								ApzuReportElementsArt.hivSiteCode(l)));
			}
		}

		return rd;
	}
}