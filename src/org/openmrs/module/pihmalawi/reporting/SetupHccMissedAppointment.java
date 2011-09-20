package org.openmrs.module.pihmalawi.reporting;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.repository.ArtReportElements;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;

public class SetupHccMissedAppointment extends SetupGenericMissedAppointment {

	boolean upperNeno;

	public SetupHccMissedAppointment(Helper helper, boolean upperNeno) {
		super(helper);
		this.upperNeno = upperNeno;
		if (upperNeno) {
			configure(
					"HCC Missed Appointment Upper Neno",
					"hccappt",
					helper.programWorkflow("HIV program", "Treatment status"),
					Context.getLocationService().getLocation(
							"Neno District Hospital"), Context
							.getLocationService().getLocation("Magaleta HC"),
							Context.getLocationService().getLocation("Nsambe HC"),
							Context.getLocationService().getLocation("Neno Mission HC"),
					false);
		} else {
			configure(
					"HCC Missed Appointment Lower Neno",
					"hccappt",
					helper.programWorkflow("HIV program", "Treatment status"),
					Context.getLocationService().getLocation(
							"Lisungwi Community Hospital"), Context
							.getLocationService().getLocation("Chifunga HC"),
							Context.getLocationService().getLocation("Matope HC"),
							Context.getLocationService().getLocation("Zalewa HC"),
							Context.getLocationService().getLocation("Nkhula Falls RHC"),
							null,
					false);
		}
	}

	protected Map excelOverviewProperties() {
		Map properties = new HashMap();
		if (upperNeno) {
			properties.put("title", "HCC Missed Appointment - Upper Neno");
			properties.put("baseCohort", "In HCC");
			properties.put("loc1name", "Neno");
			properties.put("loc2name", "Magaleta");
			properties.put("loc3name", "Nsambe");
			properties.put("loc4name", "Neno Mission");
		} else {
			properties.put("title", "HCC Missed Appointment - Lower Neno");
			properties.put("baseCohort", "In HCC");
			properties.put("loc1name", "Lisungwi");
			properties.put("loc2name", "Chifunga");
			properties.put("loc3name", "Matope");
			properties.put("loc4name", "Zalewa");
			properties.put("loc5name", "Nkhula Falls");
		}
		return properties;
	}

	public void setup(boolean useTestPatientCohort) throws Exception {
		super.setup();
	}

	@Override
	protected List<EncounterType> getEncounterTypes() {
		return Arrays.asList(h.encounterType("PART_INITIAL"), h.encounterType("PART_FOLLOWUP"), h.encounterType("EXPOSED_CHILD_INITIAL"), h.encounterType("EXPOSED_CHILD_FOLLOWUP"));
	}

	@Override
	protected PatientIdentifierType getPatientIdentifierType() {
		return Context.getPatientService().getPatientIdentifierTypeByName(
				"HCC Number");
	}

	protected void createBaseCohort(PeriodIndicatorReportDefinition rd) {
		rd.setBaseCohortDefinition(h.cohortDefinition(reportTag + ": In HCC or internal transfer_"),
				ParameterizableUtil
						.createParameterMappings("endDate=${endDate}"));

		addColumnForLocations(rd, "In HCC", "In HCC or internal transfer_", "base");
		addColumnForLocations(rd, "In HCC 1 week ago", "In HCC or internal transfer 1 week ago_",
				"base1");
		addColumnForLocations(rd, "In HCC 2 weeks ago", "In HCC or internal transfer 2 weeks ago_",
				"base2");
	}

	protected void createCohortDefinitions() {
		super.createCohortDefinitions();

		CohortDefinition cd1 = null;
		CohortDefinition cd2 = null;
		
		cd1 = ArtReportElements.inHccOnDate(reportTag);

		cd2 = ArtReportElements.transferredInternallyOnDate(reportTag);

//		ArtReportElements.everOnArtStartedOnOrBefore(reportTag);
//		CohortDefinition internal = ArtReportElements.transferredInternallyFromArt(reportTag);
//		
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(reportTag + ": In HCC or internal transfer_");
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.getSearches()
				.put("1",
						new Mapped(
								cd1,
								h.parameterMap("onDate", "${endDate}")));
		ccd.getSearches().put(
				"2",
				new Mapped(cd2, h.parameterMap("onDate", "${endDate}")));
		ccd.setCompositionString("1 OR 2");
		h.replaceCohortDefinition(ccd);
	}

	protected void createIndicators() {
		super.createIndicators();

		h.newCountIndicator(reportTag + ": In HCC or internal transfer_", reportTag + ": In HCC or internal transfer_",
				"endDate=${endDate}");
		h.newCountIndicator(reportTag + ": In HCC or internal transfer 1 week ago_", reportTag
				+ ": In HCC or internal transfer_", "endDate=${endDate-1w}");
		h.newCountIndicator(reportTag + ": In HCC or internal transfer 2 weeks ago_", reportTag
				+ ": In HCC or internal transfer_", "endDate=${endDate-2w}");
	}

	public void deleteReportElements() {
		super.deleteReportElements();

		h.purgeDefinition(CohortDefinition.class, reportTag + ": In HCC_");
		h.purgeDefinition(CohortDefinition.class, reportTag + ": In HCC or internal transfer_");
		purgeIndicator(reportTag + ": In HCC");
	}
	
}
