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
package org.openmrs.module.pihmalawi.reporting.library;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.RelativeDateCohortDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Defines all of the Cohort Definition instances we want to expose for Pih Malawi
 */
@Component
public class HivCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "pihmalawi.cohortDefinition.hiv.";

	@Autowired
	private DataFactory df;

    @Autowired
    private HivMetadata hivMetadata;

	@Autowired
	private HivPatientDataLibrary hivPatientData;

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

	@DocumentedDefinition(value = "hasAnHccNumber")
	public CohortDefinition getPatientsWithAnHccNumber() {
		return df.getPatientsWithIdentifierOfType(hivMetadata.getHccNumberIdentifierType());
	}

	@DocumentedDefinition(value = "hasAnHccNumberAtLocation")
	public CohortDefinition getPatientsWithAnHccNumberAtLocation() {
		return df.getPatientsWithIdentifierOfTypeAtLocation(hivMetadata.getHccNumberIdentifierType());
	}

	@DocumentedDefinition(value = "hasAnArvNumber")
	public CohortDefinition getPatientsWithAnArvNumber() {
		return df.getPatientsWithIdentifierOfType(hivMetadata.getArvNumberIdentifierType());
	}

	@DocumentedDefinition(value = "hasAnyPreArvNumber")
	public CohortDefinition getPatientsWithAnyPreArvNumber() {
		return df.getPatientsWithIdentifierOfType(hivMetadata.getHccNumberIdentifierType(), hivMetadata.getOldPartNumberIdentifierType(), hivMetadata.getOldPreArtNumberOldFormatIdentifierType());
	}

	@DocumentedDefinition(value = "enrolledInHivProgramByEndDate")
	public CohortDefinition getEverEnrolledInHivProgramByEndDate() {
		return df.getEverEnrolledInProgramByEndDate(hivMetadata.getHivProgram());
	}

	@DocumentedDefinition(value = "activelyEnrolledInHivProgramOnEndDate")
	public CohortDefinition getActivelyEnrolledInHivProgramAtLocationOnEndDate() {
		return df.getActivelyEnrolledInProgramAtLocationOnEndDate(hivMetadata.getHivProgram());
	}

	@DocumentedDefinition(value = "enrolledInHivProgramDuringPeriod")
	public CohortDefinition getEnrolledInHivProgramDuringPeriod() {
		return df.getEnrolledInProgramDuringPeriod(hivMetadata.getHivProgram());
	}

	@DocumentedDefinition(value = "startedPreArtWithHccNumberAtLocationInPeriod")
	public CohortDefinition getStartedPreArtWithHccNumberAtLocationInPeriod() {
		CohortDefinition startedPreArtState = df.getStartedInStateAtLocationDuringPeriod(hivMetadata.getPreArtState());
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(startedPreArtState, hasHccNumber);
	}

	@DocumentedDefinition(value = "startedPreArtWithHccNumberAtLocationByEndDate")
	public CohortDefinition getStartedPreArtWithHccNumberAtLocationByEndDate() {
		CohortDefinition everPreArtState = df.getEverEnrolledInStateAtLocationByEndDate(hivMetadata.getPreArtState());
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(everPreArtState, hasHccNumber);
	}

	@DocumentedDefinition(value = "startedPreArtIncludingOldPatientsAtLocationByEndDate")
	public CohortDefinition getStartedPreArtIncludingOldPatientsAtLocationByEndDate() {
		return df.getEverEnrolledInStateAtLocationByEndDate(hivMetadata.getPreArtState());
	}

	@DocumentedDefinition(value = "inPreArtOrExposedChildStateWithHccNumberAtLocationOnEndDate")
	public CohortDefinition getInPreArtOrExposedChildStateWithHccNumberAtLocationOnEndDate() {
		List<ProgramWorkflowState> l = Arrays.asList(hivMetadata.getPreArtState(), hivMetadata.getExposedChildState());
		CohortDefinition inPreArtOrExposedChild = df.getActiveInStatesAtLocationOnEndDate(l);
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(inPreArtOrExposedChild, hasHccNumber);
	}

	@DocumentedDefinition(value = "inPreArtStateWithHccNumberAtLocationOnEndDate")
	public CohortDefinition getInPreArtStateWithHccNumberAtLocationOnEndDate() {
		CohortDefinition inPreArt = df.getActiveInStateAtLocationOnEndDate(hivMetadata.getPreArtState());
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(inPreArt, hasHccNumber);
	}

	@DocumentedDefinition(value = "startedExposedChildWithHccNumberAtLocationByEndDate")
	public CohortDefinition getStartedExposedChildWithHccNumberAtLocationByEndDate() {
		CohortDefinition everExposedChildState = df.getEverEnrolledInStateAtLocationByEndDate(hivMetadata.getExposedChildState());
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(everExposedChildState, hasHccNumber);
	}

	@DocumentedDefinition(value = "startedExposedChildWithHccNumberAtLocationInPeriod")
	public CohortDefinition getStartedExposedChildWithHccNumberAtLocationInPeriod() {
		CohortDefinition exposedInPeriod = df.getStartedInStateAtLocationDuringPeriod(hivMetadata.getExposedChildState());
		CohortDefinition hasHccNumber = getPatientsWithAnHccNumberAtLocation();
		return df.getPatientsInAll(exposedInPeriod, hasHccNumber);
	}

	@DocumentedDefinition(value = "everEnrolledInArtAtLocationByEndDate")
	public CohortDefinition getEverEnrolledInArtAtLocationByEndDate() {
		return df.getEverEnrolledInStateAtLocationByEndDate(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "startedOnArtStateAtLocationDuringPeriod")
	public CohortDefinition getStartedOnArtStateAtLocationDuringPeriod() {
		return df.getStartedInStateAtLocationDuringPeriod(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "inOnArtStateAtLocationOnEndDate")
	public CohortDefinition getInOnArtStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "inTransferredOutAtLocationOnEndDate")
	public CohortDefinition getInTransferredOutAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getTransferredOutState());
	}

	@DocumentedDefinition("inTransferredInternallyAtLocationOnEndDate")
	public CohortDefinition getInTransferredInternallyAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getTransferredInternallyState());
	}

	@DocumentedDefinition(value = "transitionedFromPreArtToArtAtLocationDuringPeriod")
	public CohortDefinition getTransitionedFromPreArtToArtAtLocationDuringPeriod() {
		CohortDefinition preArt = getStartedPreArtWithHccNumberAtLocationByEndDate();
		CohortDefinition art = getStartedOnArtStateAtLocationDuringPeriod();
		return df.getPatientsInAll(preArt, art);
	}

	@DocumentedDefinition(value = "transitionedFromPreArtToArtAtLocationByEnd")
	public CohortDefinition getTransitionedFromPreArtToArtAtLocationByEnd() {
		CohortDefinition preArt = getStartedPreArtWithHccNumberAtLocationByEndDate();
		CohortDefinition art = getEverEnrolledInArtAtLocationByEndDate();
		return df.getPatientsInAll(preArt, art);
	}

	@DocumentedDefinition(value = "transferredOutOfPreArtAtLocationByEnd")
	public CohortDefinition getTransferredOutOfPreArtAtLocationByEnd() {
		CohortDefinition preArt = getStartedPreArtWithHccNumberAtLocationByEndDate();
		CohortDefinition art = getEverEnrolledInArtAtLocationByEndDate();
		CohortDefinition txOut = getInTransferredOutAtLocationOnEndDate();
		return df.createPatientComposition(preArt, "AND", txOut, "AND NOT", art);
	}

	@DocumentedDefinition(value = "diedWhilePreArtAtLocationByEnd")
	public CohortDefinition getDiedWhilePreArtAtLocationByEnd() {
		CohortDefinition preArt = getStartedPreArtWithHccNumberAtLocationByEndDate();
		CohortDefinition art = getEverEnrolledInArtAtLocationByEndDate();
		CohortDefinition died = getInDiedStateAtLocationOnEndDate();
		return df.createPatientComposition(preArt, "AND", died, "AND NOT", art);
	}

	@DocumentedDefinition(value = "inArtAndLastAppointmentDate3WeeksOrMoreByEndDate")
	public CohortDefinition getInArtAndLastAppointmentDate3WeeksOrMoreByEndDate() {
		CohortDefinition onArt = getInOnArtStateAtLocationOnEndDate();
		CohortDefinition missing = df.getPatientsWhoseMostRecentObsDateIsOlderThanValueAtLocationByEndDate(hivMetadata.getAppointmentDateConcept(), Arrays.asList(hivMetadata.getArtFollowupEncounterType()), "3w");
		return df.getPatientsInAll(onArt, missing);
	}

	@DocumentedDefinition(value = "inArtAndLastAppointmentDate2MonthsOrMoreByEndDate")
	public CohortDefinition getInArtAndLastAppointmentDate2MonthsOrMoreByEndDate() {
		CohortDefinition onArt = getInOnArtStateAtLocationOnEndDate();
		CohortDefinition missing = df.getPatientsWhoseMostRecentObsDateIsOlderThanValueAtLocationByEndDate(hivMetadata.getAppointmentDateConcept(), Arrays.asList(hivMetadata.getArtFollowupEncounterType()), "2m");
		return df.getPatientsInAll(onArt, missing);
	}

	@DocumentedDefinition(value = "lastPreArtOrExposedAppointmentDate8weeksOrMoreByEndDate")
	public CohortDefinition getLastPreArtOrExposedAppointmentDate8weeksOrMoreByEndDate() {
		List<EncounterType> types = Arrays.asList(hivMetadata.getPreArtFollowupEncounterType(), hivMetadata.getExposedChildFollowupEncounterType());
		return df.getPatientsWhoseMostRecentObsDateIsOlderThanValueAtLocationByEndDate(hivMetadata.getAppointmentDateConcept(), types, "8w");
	}

	@DocumentedDefinition(value = "startedDefaultedStateAtLocationDuringPeriod")
	public CohortDefinition getStartedDefaultedStateAtLocationDuringPeriod() {
		return df.getStartedInStateAtLocationDuringPeriod(hivMetadata.getDefaultedState());
	}

	@DocumentedDefinition(value = "inDefaultedStateAtLocationOnEndDate")
	public CohortDefinition getInDefaultedStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getDefaultedState());
	}

	@DocumentedDefinition
	public CohortDefinition getInTreatmentStoppedStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getTreatmentStoppedState());
	}

	@DocumentedDefinition(value = "everArtDefaultedDuringPeriodAndStillDefaultedOnEndDate")
	public CohortDefinition getEverArtDefaultedDuringPeriodAndStillDefaultedOnEndDate() {
		CohortDefinition artEver = getEverEnrolledInArtAtLocationByEndDate();
		CohortDefinition defaultedDuring = getStartedDefaultedStateAtLocationDuringPeriod();
		CohortDefinition defaultedAtEnd = getInDefaultedStateAtLocationOnEndDate();
		return df.getPatientsInAll(artEver, defaultedDuring, defaultedAtEnd);
	}

	@DocumentedDefinition(value = "startedDiedStateAtLocationDuringPeriod")
	public CohortDefinition getStartedDiedStateAtLocationDuringPeriod() {
		return df.getStartedInStateAtLocationDuringPeriod(hivMetadata.getDiedState());
	}

	@DocumentedDefinition(value = "inDiedStateAtLocationOnEndDate")
	public CohortDefinition getInDiedStateAtLocationOnEndDate() {
		return df.getActiveInStateAtLocationOnEndDate(hivMetadata.getDiedState());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAPreArtEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAPreArtEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtEncounterByEndDate")
	public CohortDefinition getPatientsWithAPreArtEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getPreArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadPreArtInitialEncounterByEndDate")
	public CohortDefinition getPatientsWithAPreArtInitialEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(Arrays.asList(hivMetadata.getPreArtInitialEncounterType()));
	}

	@DocumentedDefinition(value = "hadPreArtFollowupEncounterByEndDate")
	public CohortDefinition getPatientsWithAPreArtFollowupEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(Arrays.asList(hivMetadata.getPreArtFollowupEncounterType()));
	}

	@DocumentedDefinition(value = "hadArtEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAnArtEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadArtEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAnArtEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadArtEncounterByEndDate")
	public CohortDefinition getPatientsWithAnArtEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getArtEncounterTypes());
	}

	@DocumentedDefinition(value = "hadArtInitialEncounterByEndDate")
	public CohortDefinition getPatientsWithAnArtInitialEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(Arrays.asList(hivMetadata.getArtInitialEncounterType()));
	}

	@DocumentedDefinition(value = "hadArtFollowupEncounterByEndDate")
	public CohortDefinition getPatientsWithAnArtFollowupEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(Arrays.asList(hivMetadata.getArtFollowupEncounterType()));
	}

	@DocumentedDefinition(value = "hadHivEncounterBeforeStartDate")
	public CohortDefinition getPatientsWithAnHivEncounterBeforeStartDate() {
		return df.getAnyEncounterOfTypesBeforeStartDate(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "hadHivEncounterDuringPeriod")
	public CohortDefinition getPatientsWithAnHivEncounterDuringPeriod() {
		return df.getAnyEncounterOfTypesDuringPeriod(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "hadHivEncounterByEndDate")
	public CohortDefinition getPatientsWithAnHivEncounterByEndDate() {
		return df.getAnyEncounterOfTypesByEndDate(hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition(value = "inExposedChildStateOnEndDate")
	public CohortDefinition getPatientsInExposedChildStateOnEndDate() {
		return df.getCurrentlyInStateOnEndDate(hivMetadata.getExposedChildState());
	}

	@DocumentedDefinition(value = "inPreArtStateOnEndDate")
	public CohortDefinition getPatientsInPreArtStateOnEndDate() {
		return df.getCurrentlyInStateOnEndDate(hivMetadata.getPreArtState());
	}

	@DocumentedDefinition(value = "inOnArvsStateOnEndDate")
	public CohortDefinition getPatientsInOnArvsStateOnEndDate() {
		return df.getCurrentlyInStateOnEndDate(hivMetadata.getOnArvsState());
	}

    @DocumentedDefinition
    public CohortDefinition getPatientsInPreArtOrOnArvsStateOnEndDate() {
        return df.getCurrentlyInStateOnEndDate(hivMetadata.getPreArtState(), hivMetadata.getOnArvsState());
    }

	@DocumentedDefinition(value = "everInExposedChildStateByEndDate")
	public CohortDefinition getPatientsEverInExposedChildStateByEndDate() {
		return df.getEverInStateByEndDate(hivMetadata.getExposedChildState());
	}

	@DocumentedDefinition(value = "everInPreArtStateByEndDate")
	public CohortDefinition getPatientsEverInPreArtStateByEndDate() {
		return df.getEverInStateByEndDate(hivMetadata.getPreArtState());
	}

	@DocumentedDefinition(value = "everInOnArvsStateByEndDate")
	public CohortDefinition getPatientsEverInOnArvsStateByEndDate() {
		return df.getEverInStateByEndDate(hivMetadata.getOnArvsState());
	}

	@DocumentedDefinition(value = "0to1MonthsOldAtPreArtStateStartAtLocationByEndDate")
	public CohortDefinition getPatients0to1MonthsOldAtPreArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getPreArtState(), null, null, 1, Age.Unit.MONTHS);
	}

	@DocumentedDefinition(value = "2to23MonthsOldAtPreArtStateStartAtLocationByEndDate")
	public CohortDefinition getPatients2to23MonthsOldAtPreArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getPreArtState(), 2, Age.Unit.MONTHS, 23, Age.Unit.MONTHS);
	}

	@DocumentedDefinition(value = "2to14YearsOldAtPreArtStateStartAtLocationByEndDate")
	public CohortDefinition getPatients2to14YearsOldAtPreArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getPreArtState(), 2, Age.Unit.YEARS, 14, Age.Unit.YEARS);
	}

	@DocumentedDefinition(value = "15YearsUpAtPreArtStateStartAtLocationByEndDate")
	public CohortDefinition getPatients15YearsUpAtPreArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getPreArtState(), 15, Age.Unit.YEARS, null, null);
	}

	@DocumentedDefinition(value = "0to1MonthsOldAtExposedChildStateStartAtLocationByEndDate")
	public CohortDefinition getPatients0to1MonthsOldAtExposedChildStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getExposedChildState(), null, null, 1, Age.Unit.MONTHS);
	}

	@DocumentedDefinition(value = "2to23MonthsOldAtExposedChildStateStartAtLocationByEndDate")
	public CohortDefinition getPatients2to23MonthsOldAtExposedChildStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getExposedChildState(), 2, Age.Unit.MONTHS, 23, Age.Unit.MONTHS);
	}

	@DocumentedDefinition(value = "2to14YearsOldAtExposedChildStateStartAtLocationByEndDate")
	public CohortDefinition getPatients2to14YearsOldAtExposedChildStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getExposedChildState(), 2, Age.Unit.YEARS, 14, Age.Unit.YEARS);
	}

	@DocumentedDefinition(value = "15YearsUpAtExposedChildStateStartAtLocationByEndDate")
	public CohortDefinition getPatients15YearsUpAtExposedChildStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getExposedChildState(), 15, Age.Unit.YEARS, null, null);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsUnder1YearsOldAtArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getOnArvsState(), null, null, 0, Age.Unit.YEARS);
	}

	@DocumentedDefinition
	public CohortDefinition getPatients0to1YearsOldAtArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getOnArvsState(), null, null, 1, Age.Unit.YEARS);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMoreThan1LessThan2YearsOldAtArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getOnArvsState(), 1, Age.Unit.YEARS, 1, Age.Unit.YEARS);
	}

	@DocumentedDefinition
	public CohortDefinition getPatients2to14YearsOldAtArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getOnArvsState(), 2, Age.Unit.YEARS, 14, Age.Unit.YEARS);
	}

	@DocumentedDefinition
	public CohortDefinition getPatients15OrMoreYearsOldAtArtStateStartAtLocationByEndDate() {
		return df.getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(hivMetadata.getOnArvsState(), 15, Age.Unit.YEARS, null, null);
	}

	@DocumentedDefinition(value = "hadWeightAtHivEncounterDuringPeriod")
	public CohortDefinition getPatientsWithWeightAtHivEncounterDuringPeriod() {
		return df.getPatientsWithAnyObsDuringPeriod(hivMetadata.getWeightConcept(), hivMetadata.getHivEncounterTypes());
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoStartedArtAtLocationAfterPreviousDefaultByEnd() {
		RelativeDateCohortDefinition cd = new RelativeDateCohortDefinition();
		cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		cd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		cd.setEarlierDateDefinition(Mapped.mapStraightThrough(hivPatientData.getEarliestDefaultedStateStartDateByEndDate()));
		cd.setLaterDateDefinition(Mapped.mapStraightThrough(hivPatientData.getMostRecentOnArvsStateStartDateAtLocationByEndDate()));
		cd.setDifferenceOperator(RangeComparator.LESS_THAN);
		return cd;
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoStartedArtAtLocationAfterPreviousTreatmentStopByEnd() {
		RelativeDateCohortDefinition cd = new RelativeDateCohortDefinition();
		cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		cd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		cd.setEarlierDateDefinition(Mapped.mapStraightThrough(hivPatientData.getEarliestTreatmentStoppedStateStartDateByEndDate()));
		cd.setLaterDateDefinition(Mapped.mapStraightThrough(hivPatientData.getMostRecentOnArvsStateStartDateAtLocationByEndDate()));
		cd.setDifferenceOperator(RangeComparator.LESS_THAN);
		return cd;
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoReinitiatedArvTreatmentAtLocationByEnd() {
		CohortDefinition c1 = getPatientsWhoStartedArtAtLocationAfterPreviousDefaultByEnd();
		CohortDefinition c2 = getPatientsWhoStartedArtAtLocationAfterPreviousTreatmentStopByEnd();
		return df.getPatientsInAny(c1, c2);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoStartedArtAtLocationAfterPreviousOnArvsByEnd() {
		RelativeDateCohortDefinition cd = new RelativeDateCohortDefinition();
		cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		cd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		cd.setEarlierDateDefinition(Mapped.mapStraightThrough(hivPatientData.getEarliestOnArvsStateStartDateByEndDate()));
		cd.setLaterDateDefinition(Mapped.mapStraightThrough(hivPatientData.getMostRecentOnArvsStateStartDateAtLocationByEndDate()));
		cd.setDifferenceOperator(RangeComparator.LESS_THAN);
		return cd;
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoEverReceivedArtOnArtInitialAtLocationByEnd() {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.addEncounterType(hivMetadata.getArtInitialEncounterType());
		cd.setQuestion(hivMetadata.getEverReceivedArtConcept());
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setOperator(SetComparator.IN);
		cd.addValue(hivMetadata.getYesConcept());
		cd.addParameter(new Parameter("onOrBefore", "endDate", Date.class));
		cd.addParameter(new Parameter("locationList", "Location List", List.class));
		return df.convert(cd, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWhoTransferredInOnArtAtLocationByEnd() {
		CohortDefinition c1 = getPatientsWhoStartedArtAtLocationAfterPreviousOnArvsByEnd();
		CohortDefinition c2 = getPatientsWhoEverReceivedArtOnArtInitialAtLocationByEnd();
		CohortDefinition c3 = getPatientsWhoReinitiatedArvTreatmentAtLocationByEnd();
		return df.createPatientComposition("(", c1, "OR", c2, ") AND NOT", c3);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsPregnantOnArtInitialAtLocationByEnd() {
		return getPatientsWithObsValueAtArtInitiationAtLocationByEnd(hivMetadata.getPregnantOrLactatingConcept(), hivMetadata.getPatientPregnantConcept());
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWithCd4BelowThresholdForArtEligibilityAtLocationByEnd() {
		List<EncounterType> encounterTypes = Arrays.asList(hivMetadata.getArtInitialEncounterType());
		CohortDefinition under350 = df.getPatientsWithNumericObsAtLocationByEnd(hivMetadata.getCd4CountConcept(), encounterTypes, RangeComparator.LESS_THAN, 350.0);
		CohortDefinition under500 = df.getPatientsWithNumericObsAtLocationByEnd(hivMetadata.getCd4CountConcept(), encounterTypes, RangeComparator.LESS_THAN, 500.0);
		MappedParametersCohortDefinition mpcd = (MappedParametersCohortDefinition)under500;
		NumericObsCohortDefinition nocd = (NumericObsCohortDefinition)mpcd.getWrapped().getParameterizable();
		nocd.setOnOrAfter(DateUtil.getDateTime(2014, 6, 1)); // As of June 1, 2014, CD4 < 500 is the new norm for eligibility criteria
		return df.getPatientsInAny(under350, under500);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsWithSideEffectsAtMostRecentVisitAtLocationByEnd() {
		Concept[] answers = {hivMetadata.getPeripheralNeuropathyConcept(), hivMetadata.getHepatitisConcept(), hivMetadata.getSkinRashConcept(), hivMetadata.getLipodystrophyyConcept(), hivMetadata.getOtherConcept()};
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getArtSideEffectsConcept(), hivMetadata.getArtEncounterTypes(), answers);
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMostRecentTbStatusNotSuspectedAtLocationByEndDate() {
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getTbStatusConcept(), hivMetadata.getArtEncounterTypes(), hivMetadata.getTbNotSuspectedConcept());
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMostRecentTbStatusSuspectedAtLocationByEndDate() {
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getTbStatusConcept(), hivMetadata.getArtEncounterTypes(), hivMetadata.getTbSuspectedConcept());
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMostRecentTbStatusConfirmedNotOnTreatmentAtLocationByEndDate() {
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getTbStatusConcept(), hivMetadata.getArtEncounterTypes(), hivMetadata.getTbConfirmedNotOnTreatmentConcept());
	}

	@DocumentedDefinition
	public CohortDefinition getPatientsMostRecentTbStatusConfirmedOnTreatmentAtLocationByEndDate() {
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getTbStatusConcept(), hivMetadata.getArtEncounterTypes(), hivMetadata.getTbConfirmedOnTreatmentConcept());
	}

	// Convenience methods

	public CohortDefinition getPatientsTakingRegimenAtLocationAtEndDate(Concept regimen) {
		return df.getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(hivMetadata.getArvDrugsReceivedConcept(), hivMetadata.getArtEncounterTypes(), regimen);
	}

	public CohortDefinition getPatientsWithObsValueAtArtInitiationAtLocationByEnd(Concept question, Concept...values) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.addEncounterType(hivMetadata.getArtInitialEncounterType());
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Arrays.asList(values));
		cd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Location List", List.class));
		return df.convert(cd, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getDiedAtLocationWithinMonthsOfEndDate(int numMonths) {
		CohortDefinition trueAtEnd = getInDiedStateAtLocationOnEndDate();
		CohortDefinition trueMonthsBefore = df.getActiveInStateAtLocationNumMonthsBeforeEndDate(hivMetadata.getDiedState(), numMonths);
		return df.createPatientComposition(trueAtEnd, "AND NOT", trueMonthsBefore);
	}

	public CohortDefinition getPatientsWithAPreArtEncounterWithinMonthsOfEndDate(int numMonths) {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(hivMetadata.getPreArtEncounterTypes(), numMonths);
	}

	public CohortDefinition getPatientsWithAnArtEncounterWithinMonthsOfEndDate(int numMonths) {
		return df.getAnyEncounterOfTypesWithinMonthsByEndDate(hivMetadata.getArtEncounterTypes(), numMonths);
	}

    public CohortDefinition getPatientsWithViralLoadRecordedByEndDate() {
        return df.getPatientsWithAnyObsByEndDate(hivMetadata.getHivViralLoadConcept());
    }

    public CohortDefinition getPatientsWithCd4RecordedWithinMonthsOfEndDate(int numMonths) {
        CohortDefinition cd4 = df.getPatientsWithAnyObsWithinMonthsByEndDate(hivMetadata.getCd4CountConcept(), numMonths);
        CohortDefinition clinicianReported = df.getPatientsWithAnyObsWithinMonthsByEndDate(hivMetadata.getClinicianReportedCd4Concept(), numMonths);
        return df.getPatientsInAny(cd4, clinicianReported);
    }

	public CohortDefinition getPatientsWithCd4MeasuredInLabWithinMonthsOfEndDate(int numMonths) {
		return df.getPatientsWithAnyObsWithinMonthsByEndDate(hivMetadata.getCd4CountConcept(), numMonths);
	}
}
