/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.RelativeDateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Deprecated
public class ArvQuarterlyReport extends ApzuReportManager {

	public static final String EXCEL_REPORT_DESIGN_UUID = "619346d0-33d5-11e4-8c21-0800200c9a66";
	public static final String EXCEL_REPORT_RESOURCE_NAME = "ArvQuarterlyReport.xls";

	@Autowired
	private DataFactory df;

	@Autowired
	private BuiltInCohortDefinitionLibrary builtInCohorts;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	private HivPatientDataLibrary hivPatientData;

	@Autowired
	private HivMetadata hivMetadata;

	public ArvQuarterlyReport() {}

	@Override
	public String getUuid() {
		return "619346d1-33d5-11e4-8c21-0800200c9a66";
	}

	@Override
	public String getName() {
		return "ARV Quarterly (NOT YET READY FOR USE)";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "Start date (Start of quarter)", Date.class));
		l.add(new Parameter("endDate", "End date (End of quarter)", Date.class));
		l.add(df.getOptionalLocationParameter());
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());
		rd.setBaseCohortDefinition(Mapped.mapStraightThrough(getRegisteredEver()));

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("indicators", Mapped.mapStraightThrough(dsd));

		// Underlying Cohorts

		CohortDefinition reInitiated = hivCohorts.getPatientsWhoReinitiatedArvTreatmentAtLocationByEnd();
		CohortDefinition transferredIn = hivCohorts.getPatientsWhoTransferredInOnArtAtLocationByEnd();
		CohortDefinition firstTime = df.createPatientComposition(getRegisteredEver(), "AND NOT", reInitiated, "AND NOT", transferredIn);

		// TODO: Review re-initiated/transferred/firsttime calculations
		// What I have done is:
		// * re-initiated is anyone who had a defaulted or tx out state prior to their latest on arvs state
		// * transferred in is anyone who had an on arvs state prior to latest on arvs state or had Ever on ARVS true on art mastercard, and who is not re-initiated
		// * first time is anyone not in these 2 categories
		//
		// SPC calculations are:
		//  reInitiated:  Has defaulted or treatment stopped states before latest on arvs start date AND initiation date on art mastercard <= 7 days before first HIV program enrollment date
		//  transferredIn:  No defaulted or treatment stopped states before latest on arvs start date AND initiation date on art mastercard > 7 days before first HIV program enrollment date
		//  firstTime:  No defaulted or treatment stopped states before latest on arvs start date AND initiation date on art mastercard <= 7 days before first HIV program enrollment date
		// What if has defaulted or tx stopped states before, but initiation date is <= 7 days before.  This case doesn't seem to be covered here (though is likely a data issue)...
		// Since art initiation date is the art initial encounter date, not sure if the way things are done are to make this earlier than program enrollment date on txfer in

		CohortDefinition males = builtInCohorts.getMales();
		CohortDefinition pregnantFemales = df.getPatientsInAll(builtInCohorts.getFemales(), hivCohorts.getPatientsPregnantOnArtInitialAtLocationByEnd());
		CohortDefinition notPregnantFemales = df.createPatientComposition(builtInCohorts.getFemales(), "AND NOT", hivCohorts.getPatientsPregnantOnArtInitialAtLocationByEnd());

		// TODO: Review Pregnancy Calculations
		// Historical methods of this calculation (eg. from CN) and SPC definitions have additionally contained "pregnant at any time in any art encounter" or "pregnant after arvs start date"
		// It seems to me though that in order for us to accurately report on registrations, both in period and ever, that we are better off only considering pregnancy status at the time of initiation
		// So I am using this calculation for now.  If the ART Initiation section on the ART mastercard contains "Pregnant/Lactating" = "Preg"

		CohortDefinition under1 = hivCohorts.getPatientsUnder1YearsOldAtArtStateStartAtLocationByEndDate();
		CohortDefinition between1And2 = hivCohorts.getPatientsMoreThan1LessThan2YearsOldAtArtStateStartAtLocationByEndDate();
		CohortDefinition under2 = hivCohorts.getPatients0to1YearsOldAtArtStateStartAtLocationByEndDate();
		CohortDefinition between2and14 = hivCohorts.getPatients2to14YearsOldAtArtStateStartAtLocationByEndDate();
		CohortDefinition over14 = hivCohorts.getPatients15OrMoreYearsOldAtArtStateStartAtLocationByEndDate();

		// TODO: Review age Calculations
		// The ART mastercard has a question called "Age at initiation (yrs)" which is stored as a numeric obs.
		// It would seem that this is the most logical to use for this calculation and is probably what is intended when tallying mastercards is done to produce these reports
		// However, it's not clear how best to use this for a "less than 1 year" calculation, unless we are sure that "0" is entered into this field
		// For now, I am using the ART State Start Date as the basis for the age.  We could alternatively use the date of the ART Initial Encounter (eg. Initiation Date on the mastercard) if this may vary

		CohortDefinition presumedSevereHiv = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getReasonForStartingArtConcept(), hivMetadata.getPresumedSevereHivCriteriaPresentConcept());
		CohortDefinition positivePcr = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getFirstPositiveHivTestTypeConcept(), hivMetadata.getHivDnaPcrTestConcept());
		CohortDefinition presumedSevereHivUnder1 = df.getPatientsInAll(under1, presumedSevereHiv);
		CohortDefinition positivePcrUnder1 = df.getPatientsInAll(under1, positivePcr);
		CohortDefinition breastfeedingOnArtInitial = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getPregnantOrLactatingConcept(), hivMetadata.getCurrentlyBreastfeedingConcept());
		CohortDefinition lowCd4 = hivCohorts.getPatientsWithCd4BelowThresholdForArtEligibilityAtLocationByEnd();
		CohortDefinition lowTlc = new GenderCohortDefinition(); // TODO: Setting it this way to make result 0. Can fix after answering questions below...
		CohortDefinition whoStage3 = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getWhoStageConcept(), hivMetadata.getWhoStage3AdultAndPedsConcept(), hivMetadata.getWhoStage3AdultConcept(), hivMetadata.getWhoStage3PedsConcept());
		CohortDefinition whoStage4 = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getWhoStageConcept(), hivMetadata.getWhoStage4AdultAndPedsConcept(), hivMetadata.getWhoStage4AdultConcept(), hivMetadata.getWhoStage4PedsConcept());
		CohortDefinition knownReason = df.getPatientsInAny(presumedSevereHivUnder1, positivePcrUnder1, between1And2, pregnantFemales, breastfeedingOnArtInitial, lowCd4, lowTlc, whoStage3, whoStage4);

		// TODO: Review Reason for Starting Calculations
		//  Low TLC *** NEED TO REVIEW MASTERCARD.  IT IS RECORDING CD4% WHERE TLC IS EXPECTED?? ***
		//  WHO Stage:  Using any of Adult, Peds, or Adult/Peds answers to the WHO stage questions.  Is this needed?  What are the historical values here?

		CohortDefinition neverTb = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getTbTreatmentStatusConcept(), hivMetadata.getUnknownConcept());
		CohortDefinition recentTb = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getTbTreatmentStatusConcept(), hivMetadata.getTreatmentCompleteConcept());
		CohortDefinition currentTb = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getTbTreatmentStatusConcept(), hivMetadata.getCurrentlyInTreatmentConcept());
		CohortDefinition ks = hivCohorts.getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getKsSideEffectsWorseningOnArvsConcept(), hivMetadata.getTrueConcept(), hivMetadata.getYesConcept());

		// TODO: Review TB History Calculations
		// ***** Modeling seems wrong.  Question on screen says:  "TB Status At Init.", with Answers "Never", "Last", "Curr".  These map to:
		// Question: Tuberculosis treatment status (1568), Answers:  Never -> Unknown (1067), Last -> Treatment complete (1714), Curr -> Currently in treatment (1432)

		CohortDefinition aliveOnArt = hivCohorts.getInOnArtStateAtLocationOnEndDate();
		CohortDefinition died = hivCohorts.getInDiedStateAtLocationOnEndDate();
		CohortDefinition diedInMonth1 = getDiedWithinMonthsOfArtStartAtLocationByEndDate(1);
		CohortDefinition diedInFirst2 = getDiedWithinMonthsOfArtStartAtLocationByEndDate(2);
		CohortDefinition diedInFirst3 = getDiedWithinMonthsOfArtStartAtLocationByEndDate(3);
		CohortDefinition diedInMonth2 = df.createPatientComposition(diedInFirst2, "AND NOT", diedInMonth1);
		CohortDefinition diedInMonth3 = df.createPatientComposition(diedInFirst3, "AND NOT", diedInFirst2);
		CohortDefinition diedAfterMonth3 = df.createPatientComposition(died, "AND NOT", diedInFirst3);
		CohortDefinition defaulted = hivCohorts.getInDefaultedStateAtLocationOnEndDate();
		CohortDefinition stoppedTx = hivCohorts.getInTreatmentStoppedStateAtLocationOnEndDate();
		CohortDefinition transferredOut = hivCohorts.getInTransferredOutAtLocationOnEndDate();

		// TODO: Review Outcome Calculations
		// What about transferred internally, do we need to factor this in anywhere?
		// Also, I'm using 1/2/3 months, not 30/60/90 days, if this is a problem we can change it.

		// TODO: Review regimen calculations
		// Should we expect to fill all 1-9 of both adult and pediatric?

		CohortDefinition under7MissedDoses = df.getPatientsWithMostRecentNumericObsAtLocationByEnd(hivMetadata.getNumberOfHivDrugDosesMissedConcept(), hivMetadata.getArtEncounterTypes(), RangeComparator.LESS_EQUAL, 6.0);
		CohortDefinition over6MissedDoses = df.getPatientsWithMostRecentNumericObsAtLocationByEnd(hivMetadata.getNumberOfHivDrugDosesMissedConcept(), hivMetadata.getArtEncounterTypes(), RangeComparator.GREATER_THAN, 6.0);

		// TODO: Review missed dose calculations
		// For 0-6 missed doses, does a missing or null value qualify?


		// TODO: Review TB calculations
		// For TB not suspected, does a missing or null value qualify?

		// Add indicators

		addIndicator(dsd, "19_quarter", "Total registered in quarter", getRegisteredInPeriod());
		addIndicator(dsd, "19_ever", "Total registered ever", getRegisteredEver());
		addRegisteredSubsetIndicator(dsd, "20", "[FT] Patients initiated on ART first time", firstTime);
		addRegisteredSubsetIndicator(dsd, "21", "[Re] Patients re-initiated on ART", reInitiated);
		addRegisteredSubsetIndicator(dsd, "22", "[TI] Patients transferred in on ART", transferredIn);
		addRegisteredSubsetIndicator(dsd, "23", "[M] Males (all ages)", males);
		addRegisteredSubsetIndicator(dsd, "24", "[FNP] Non-pregnant Females (all ages)", notPregnantFemales);
		addRegisteredSubsetIndicator(dsd, "25", "[FP] Pregnant Females (all ages)", pregnantFemales);
		addRegisteredSubsetIndicator(dsd, "26", "[A] Children below 24 m at ART initiation", under2);
		addRegisteredSubsetIndicator(dsd, "27", "[B] Children 24 m - 14 yrs at ART initiation", between2and14);
		addRegisteredSubsetIndicator(dsd, "28", "[C] Adults (15 years or older at ART initiation)", over14);
		addRegisteredSubsetIndicator(dsd, "29", "[PSHD] Presumed Severe HIV Disease (<12 months)", presumedSevereHivUnder1);
		addRegisteredSubsetIndicator(dsd, "30", "[PCR] Infants < 12 months PCR+", positivePcrUnder1);
		addRegisteredSubsetIndicator(dsd, "31", "[U24] Children 12 - 23 months", between1And2);
		addRegisteredSubsetIndicator(dsd, "32", "[Preg] Pregnant women", pregnantFemales);
		addRegisteredSubsetIndicator(dsd, "33", "[BF] Breastfeeding mothers", breastfeedingOnArtInitial);
		addRegisteredSubsetIndicator(dsd, "34", "[CD4] CD4 below threshold", lowCd4);
		addRegisteredSubsetIndicator(dsd, "35", "[TLC] TLC < 1.,200/mm3", lowTlc);
		addRegisteredSubsetIndicator(dsd, "36", "[3] WHO stage 3", whoStage3);
		addRegisteredSubsetIndicator(dsd, "37", "[4] WHO stage 4", whoStage4);
		addRegisteredSubsetIndicator(dsd, "38", "[Unk] Unknown / reason outside guidelines", df.createPatientComposition(getRegisteredEver(), "AND NOT", knownReason));
		addRegisteredSubsetIndicator(dsd, "39", "[Nev/>2yrs] Never TB or TB over 2 years ago", neverTb);
		addRegisteredSubsetIndicator(dsd, "40", "[Last 2 yrs] TB within the last 2 years", recentTb);
		addRegisteredSubsetIndicator(dsd, "41", "[Curr] Current episode of TB", currentTb);
		addRegisteredSubsetIndicator(dsd, "42", "[KS] Kaposis Sarcoma", ks);
		addIndicator(dsd, "43", "Total Alive And On ART", aliveOnArt);
		addIndicator(dsd, "44", "Died total", died);
		addIndicator(dsd, "44a", "Died within 1st month after ART initiation", diedInMonth1);
		addIndicator(dsd, "44b", "Died within 2nd month after ART initiation", diedInMonth2);
		addIndicator(dsd, "44c", "Died within 3rd month after ART initiation", diedInMonth3);
		addIndicator(dsd, "44d", "Died after the end of the 3rd month after ART initiation", diedAfterMonth3);
		addIndicator(dsd, "45", "Defaulted", defaulted);
		addIndicator(dsd, "46", "Stopped taking ARVs", stoppedTx);
		addIndicator(dsd, "47", "Transferred out", transferredOut);
		addIndicator(dsd, "48_adult", "Regimen 1a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen1aConcept()));
		addIndicator(dsd, "49_adult", "Regimen 2a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen2aConcept()));
		addIndicator(dsd, "50_adult", "Regimen 3a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen3aConcept()));
		addIndicator(dsd, "51_adult", "Regimen 4a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen4aConcept()));
		addIndicator(dsd, "52_adult", "Regimen 5a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen5aConcept()));
		addIndicator(dsd, "53_adult", "Regimen 6a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen6aConcept()));
		addIndicator(dsd, "54_adult", "Regimen 7a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen7aConcept()));
		addIndicator(dsd, "55_adult", "Regimen 8a", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen8aConcept()));
		addIndicator(dsd, "48_pediatric", "Regimen 1p", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen1pConcept()));
		addIndicator(dsd, "49_pediatric", "Regimen 2p", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen2pConcept()));
		addIndicator(dsd, "50_pediatric", "Regimen 3p", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen3pConcept()));
		addIndicator(dsd, "51_pediatric", "Regimen 4p", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen4pConcept()));
		addIndicator(dsd, "56_pediatric", "Regimen 9p", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getArvRegimen9pConcept()));
		addIndicator(dsd, "57", "Other Regimen", hivCohorts.getPatientsTakingRegimenAtLocationAtEndDate(hivMetadata.getOtherConcept()));
		addIndicator(dsd, "58", "Total patients with any side effects", hivCohorts.getPatientsWithSideEffectsAtMostRecentVisitAtLocationByEnd());
		addIndicator(dsd, "59", "Patients with 0 - 6 doses missed at their last visit", under7MissedDoses);
		addIndicator(dsd, "60", "Patients with 7+ doses missed at their last visit", over6MissedDoses);
		addIndicator(dsd, "61", "TB not suspected", hivCohorts.getPatientsMostRecentTbStatusNotSuspectedAtLocationByEndDate());
		addIndicator(dsd, "62", "TB suspected", hivCohorts.getPatientsMostRecentTbStatusSuspectedAtLocationByEndDate());
		addIndicator(dsd, "63", "TB confirmed, not yet / currently not on TB treatment", hivCohorts.getPatientsMostRecentTbStatusConfirmedNotOnTreatmentAtLocationByEndDate());
		addIndicator(dsd, "64", "TB confirmed, on TB treatment", hivCohorts.getPatientsMostRecentTbStatusConfirmedOnTreatmentAtLocationByEndDate());

		return rd;
	}

	public CohortDefinition getRegisteredInPeriod() {
		// TODO: SPC - AND hivCohorts.getPatientsWithAnArvNumber()
		return hivCohorts.getStartedOnArtStateAtLocationDuringPeriod();
	}

	public CohortDefinition getRegisteredEver() {
		// TODO: SPC - AND hivCohorts.getPatientsWithAnArvNumber()
		return hivCohorts.getEverEnrolledInArtAtLocationByEndDate();
	}

	public CohortDefinition getDiedWithinMonthsOfArtStartAtLocationByEndDate(int atLeastMonths) {
		RelativeDateCohortDefinition cd = new RelativeDateCohortDefinition();
		cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		cd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		cd.setEarlierDateDefinition(Mapped.mapStraightThrough(hivPatientData.getMostRecentOnArvsStateStartDateAtLocationByEndDate()));
		cd.setLaterDateDefinition(Mapped.mapStraightThrough(hivPatientData.getMostRecentDiedStateStartDateAtLocationByEndDate()));
		cd.setDifferenceOperator(RangeComparator.LESS_EQUAL);
		cd.setDifferenceNumber(atLeastMonths);
		cd.setDifferenceUnit(DurationUnit.MONTHS);
		return cd;
	}

	public void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition cohortDefinition) {
		CohortIndicator ci = new CohortIndicator();
		ci.addParameter(ReportingConstants.START_DATE_PARAMETER);
		ci.addParameter(ReportingConstants.END_DATE_PARAMETER);
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		dsd.addColumn(key, label, Mapped.mapStraightThrough(ci), "");
	}

	public void addRegisteredSubsetIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition cohortDefinition) {
		addIndicator(dsd, key+"_ever", label + " ever", df.getPatientsInAll(getRegisteredEver(), cohortDefinition));
		addIndicator(dsd, key+"_quarter", label + " in quarter", df.getPatientsInAll(getRegisteredInPeriod(), cohortDefinition));
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(createExcelTemplateDesign(EXCEL_REPORT_DESIGN_UUID, reportDefinition, EXCEL_REPORT_RESOURCE_NAME));
		return l;
	}
}
