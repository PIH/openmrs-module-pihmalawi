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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAddress;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.AppointmentStatusCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.InAgeRangeAtStateStartCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ProgramPatientIdentifierDataDefinition;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.ConvertedDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DataSetRowConverter;
import org.openmrs.module.reporting.data.converter.ListConverter;
import org.openmrs.module.reporting.data.converter.MostRecentlyCreatedConverter;
import org.openmrs.module.reporting.data.converter.NullValueConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.CodedObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.CompositionEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.NumericObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.ObsForEncounterQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DataFactory {

	public Parameter getStartDateParameter() {
		return new Parameter("startDate", "Start Date", Date.class);
	}

	public Parameter getEndDateParameter() {
		return new Parameter("endDate", "End Date", Date.class);
	}

	public Parameter getRequiredLocationParameter() {
		return new Parameter("location", "Location", Location.class);
	}

	public Parameter getOptionalLocationParameter() {
		Parameter p = new Parameter("location", "Location", Location.class);
		p.addToWidgetConfiguration("optionHeader", "All Locations");
		p.setRequired(false);
		return p;
	}

	public PatientDataDefinition getPreferredAddress(String property) {
		PreferredAddressDataDefinition d = new PreferredAddressDataDefinition();
		PropertyConverter converter = new PropertyConverter(PersonAddress.class, property);
		return convert(d, converter);
	}

	public PatientDataDefinition getPreferredProgramIdentifierAtLocation(PatientIdentifierType pit, Program program, DataConverter converter) {
		ProgramPatientIdentifierDataDefinition def = new ProgramPatientIdentifierDataDefinition();
		def.setIdentifierType(pit);
		def.setProgram(program);
		def.addParameter(new Parameter("location", "Location", Location.class));
		return convert(def, converter);
	}

	public PatientDataDefinition getAllIdentifiersOfType(PatientIdentifierType pit, DataConverter converter) {
		PatientIdentifierDataDefinition def = new PatientIdentifierDataDefinition();
		def.setTypes(Arrays.asList(pit));
		return new ConvertedPatientDataDefinition(def, converter);
	}

	public PatientDataDefinition getPreferredIdentifierOfTypes(PatientIdentifierType... types) {
		PatientIdentifierDataDefinition cd = new PatientIdentifierDataDefinition();
		for (PatientIdentifierType type : types) {
			cd.addType(type);
		}
		cd.setIncludeFirstNonNullOnly(true);
		return cd;
	}

	public PatientDataDefinition getFirstEncounterOfTypeByEndDate(List<EncounterType> types, DataConverter converter) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setTypes(types);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentEncounterOfTypesByEndDate(List<EncounterType> types, DataConverter converter) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setTypes(types);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentEncounterOfTypeByEndDate(EncounterType type, DataConverter converter) {
		return getMostRecentEncounterOfTypesByEndDate(Arrays.asList(type), converter);
	}

	public PatientDataDefinition getFirstObsByEndDate(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setQuestion(question);
		def.setEncounterTypeList(encounterTypes);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentObsByEndDate(Concept question) {
		return getMostRecentObsByEndDate(question, null, null);
	}

	public PatientDataDefinition getMostRecentObsByEndDate(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setQuestion(question);
		def.setEncounterTypeList(encounterTypes);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentObsAtLocationByEndDate(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setQuestion(question);
		def.setEncounterTypeList(encounterTypes);
		def.addParameter(new Parameter("locationList", "Locations", Location.class));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"), converter);
	}

	public PatientDataDefinition getAllObsByEndDate(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setQuestion(question);
		def.setEncounterTypeList(encounterTypes);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

    public PatientDataDefinition getAllObsWithCodedValueByEndDate(Concept question, List<Concept> codedValues, DataConverter converter) {
        ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
        def.setQuestion(question);
        def.setValueCodedList(codedValues);
        def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
        return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
    }

    public PatientDataDefinition getObsWhoseValueDatetimeIsDuringPeriodAtLocation(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
        ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
        def.setQuestion(question);
        def.setEncounterTypeList(encounterTypes);
        def.addParameter(new Parameter("valueDatetimeOrAfter", "Value On Or After", Date.class));
        def.addParameter(new Parameter("valueDatetimeOnOrBefore", "Value On Or Before", Date.class));
        def.addParameter(new Parameter("locationList", "Locations", Location.class));
        return convert(def, ObjectUtil.toMap("valueDatetimeOrAfter=startDate,valueDatetimeOnOrBefore=endDate,locationList=location"), converter);
    }

    public PatientDataDefinition getAppointmentStatus(List<ProgramWorkflowState> states, List<EncounterType> encounterTypes) {
        AppointmentStatusDataDefinition def = new AppointmentStatusDataDefinition();
        def.setActiveStates(states);
        def.setEncounterTypes(encounterTypes);
        return def;
    }

    public PatientDataDefinition getAppointmentStatus(TreatmentGroup treatmentGroup) {
        AppointmentStatusDataDefinition def = new AppointmentStatusDataDefinition();
        def.setActiveStates(treatmentGroup.getActiveStates());
        def.setEncounterTypes(treatmentGroup.getEncounterTypes());
        return def;
    }

	public PatientDataDefinition getEarliestProgramEnrollmentByEndDate(Program program, DataConverter converter) {
		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setWhichEnrollment(TimeQualifier.FIRST);
		def.setProgram(program);
		def.addParameter(new Parameter("enrolledOnOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("enrolledOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentStateForWorkflowByEndDate(ProgramWorkflow workflow, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setWorkflow(workflow);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentStateForWorkflowAtLocationByEndDate(ProgramWorkflow workflow, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setWorkflow(workflow);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		def.addParameter(new Parameter("location", "Location", Location.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentStateAtLocationByEndDate(ProgramWorkflowState state, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setState(state);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		def.addParameter(new Parameter("location", "Location", Location.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentStateByEndDate(ProgramWorkflowState state, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setState(state);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getEarliestStateAtLocationByEndDate(ProgramWorkflowState state, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setState(state);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		def.addParameter(new Parameter("location", "Location", Location.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getEarliestStateByEndDate(ProgramWorkflowState state, DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setState(state);
		def.addParameter(new Parameter("startedOnOrBefore", "Started on or Before", Date.class));
		return convert(def, ObjectUtil.toMap("startedOnOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getAllActiveStatesOnEndDate(DataConverter converter) {
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.addParameter(new Parameter("activeOnDate", "Active on Date", Date.class));
		return convert(def, ObjectUtil.toMap("activeOnDate=endDate"), converter);
	}

	public PatientDataDefinition getRelationships(RelationshipType type, Boolean valuesArePersonA, Boolean valuesArePersonB) {
		RelationshipsForPersonDataDefinition def = new RelationshipsForPersonDataDefinition();
		def.addRelationshipType(type);
		def.setValuesArePersonA(valuesArePersonA);
		def.setValuesArePersonB(valuesArePersonB);
		ChainedConverter c = new ChainedConverter();
		c.addConverter(new MostRecentlyCreatedConverter(Relationship.class));
		c.addConverter(new PropertyConverter(Relationship.class, "personB"));
		c.addConverter(new ObjectFormatter());
		return convert(def, c);
	}

	// Encounter Data Definitions

	public EncounterDataDefinition getSingleObsForEncounter(Concept concept) {
		ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
		def.setQuestion(concept);
		def.setSingleObs(true);
		return def;
	}

	public EncounterDataDefinition getListOfObsForEncounter(Concept concept) {
		ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
		def.setQuestion(concept);
		def.setSingleObs(false);
		return def;
	}

	public EncounterDataDefinition getSingleObsValueNumericForEncounter(Concept concept) {
		return convert(getSingleObsForEncounter(concept), getObsValueNumericConverter());
	}

	// Cohort Definitions

	public CohortDefinition getAgeByEndDate(Integer minAge, Integer maxAge) {
		AgeCohortDefinition cd = new AgeCohortDefinition();
		cd.setMinAge(minAge);
		cd.setMaxAge(maxAge);
		cd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		return convert(cd, ObjectUtil.toMap("effectiveDate=endDate"));
	}

	public CohortDefinition getPatientsWhoTurnedWeeksOldDuringPeriod(int numWeeks) {
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.addParameter(new Parameter("bornOnOrAfter", "Born On Or After", Date.class));
		cd.addParameter(new Parameter("bornOnOrBefore", "Born On Or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("bornOnOrAfter=startDate-" + numWeeks + "w,bornOnOrBefore=endDate-" + numWeeks + "w"));
	}

    public CohortDefinition getPatientsWhoDiedByEndDate(ProgramWorkflowState diedState) {
        BirthAndDeathCohortDefinition diedDef = new BirthAndDeathCohortDefinition();
        diedDef.addParameter(new Parameter("diedOnOrBefore", "Died On Or Before", Date.class));
        CohortDefinition cd1 = convert(diedDef, ObjectUtil.toMap("diedOnOrBefore=endDate"));
        CohortDefinition cd2 = getCurrentlyInStateOnEndDate(diedState);
        return getPatientsInAny(cd1, cd2);
    }

	public CohortDefinition getPatientsWithIdentifierOfType(PatientIdentifierType... types) {
		PatientIdentifierCohortDefinition cd = new PatientIdentifierCohortDefinition();
		for (PatientIdentifierType type : types) {
			cd.addTypeToMatch(type);
		}
		return cd;
	}

	public CohortDefinition getPatientsWithIdentifierOfTypeAtLocation(PatientIdentifierType type) {
		PatientIdentifierCohortDefinition cd = new PatientIdentifierCohortDefinition();
		cd.addParameter(new Parameter("locationsToMatch", "Location", Location.class));
		cd.addTypeToMatch(type);
		return convert(cd, ObjectUtil.toMap("locationsToMatch=location"));
	}

	public CohortDefinition getEverEnrolledInStateAtLocationByEndDate(ProgramWorkflowState state) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.addState(state);
		cd.addParameter(new Parameter("startedOnOrBefore", "Started on or before", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getStartedInStateAtLocationDuringPeriod(ProgramWorkflowState state) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.addState(state);
		cd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		cd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrAfter=startDate,startedOnOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getActiveInStateAtLocationOnEndDate(ProgramWorkflowState state) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.addState(state);
		cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		cd.addParameter(new Parameter("locations", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate,locations=location"));
	}

	public CohortDefinition getActiveInStatesAtLocationOnEndDate(List<ProgramWorkflowState> states) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(states);
		cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		cd.addParameter(new Parameter("locations", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate,locations=location"));
	}

	public CohortDefinition getActiveInStateAtLocationNumMonthsBeforeEndDate(ProgramWorkflowState state, int numMonths) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.addState(state);
		cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		cd.addParameter(new Parameter("locations", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate-"+numMonths+"m,locations=location"));
	}

	public CohortDefinition getEverEnrolledInProgramByEndDate(Program program) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("enrolledOnOrBefore=endDate"));
	}

	public CohortDefinition getActivelyEnrolledInProgramAtLocationOnEndDate(Program program) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("locations", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate,locations=location"));
	}

	public CohortDefinition getActivelyEnrolledInProgramAtLocationOnEndDate(Program program, Location location) {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addLocation(location);
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate"));
	}

	public CohortDefinition getEnrolledInProgramDuringPeriod(Program program) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		cd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("enrolledOnOrAfter=startDate,enrolledOnOrBefore=endDate"));
	}

	public CohortDefinition getStartedInStateDuringPeriod(ProgramWorkflowState state) {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStates(Arrays.asList(state));
		cd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		cd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrAfter=startDate,startedOnOrBefore=endDate"));
	}

	public CohortDefinition getEverInStateDuringPeriod(ProgramWorkflowState state) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Arrays.asList(state));
		cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
	}

	public CohortDefinition getEverInStateByEndDate(ProgramWorkflowState state) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Arrays.asList(state));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public CohortDefinition getCurrentlyInStateOnEndDate(ProgramWorkflowState... state) {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Arrays.asList(state));
		cd.addParameter(new Parameter("onDate", "onDate", Date.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate"));
	}

	public CohortDefinition getAnyEncounterOfTypesDuringPeriod(List<EncounterType> types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
	}

	public CohortDefinition getAnyEncounterOfTypesAtLocationDuringPeriod(List<EncounterType> types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getAnyEncounterOfTypesBeforeStartDate(List<EncounterType> types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=startDate-1ms"));
	}

	public CohortDefinition getAnyEncounterOfTypesByEndDate(List<EncounterType> types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public CohortDefinition getAnyEncounterOfTypesWithinMonthsByEndDate(List<EncounterType> types, int numMonths) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=endDate-"+numMonths+"m+1d,onOrBefore=endDate"));
	}

	public CohortDefinition getAnyEncounterOfTypesAtLocationByEndDate(List<EncounterType> types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getAnyEncounterOfTypesAtLocationWithinMonthsByEndDate(List<EncounterType> types, int numMonths) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=endDate-"+numMonths+"m+1d,onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithAnyObsDuringPeriod(Concept question, List<EncounterType> restrictToTypes) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
	}

    public CohortDefinition getPatientsWithAnyObsByEndDate(Concept question) {
        NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
        cd.setTimeModifier(TimeModifier.ANY);
        cd.setQuestion(question);
        cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
        return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
    }

	public CohortDefinition getPatientsWithAnyObsAtLocationDuringPeriod(Concept question, List<EncounterType> restrictToTypes) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithNumericObsAtLocationDuringPeriod(Concept question, List<EncounterType> restrictToTypes, RangeComparator operator, Double value) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.setOperator1(operator);
		cd.setValue1(value);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithNumericObsAtLocationByEnd(Concept question, List<EncounterType> restrictToTypes, RangeComparator operator, Double value) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.setOperator1(operator);
		cd.setValue1(value);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithMostRecentNumericObsAtLocationByEnd(Concept question, List<EncounterType> restrictToTypes, RangeComparator operator, Double value) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.setOperator1(operator);
		cd.setValue1(value);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithAnyObsWithinMonthsByEndDate(Concept question, int numMonths) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=endDate-"+numMonths+"m+1d,onOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWithCodedObsAtLocationDuringPeriod(Concept question, List<Concept> codedValues) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(codedValues);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		cd.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
	}

	public CohortDefinition getPatientsWithCodedObsByEndDate(Concept question, List<Concept> codedValues) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(codedValues);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWhoNeverHadObsAtLocationByEndDate(Concept question, List<EncounterType> encounterTypes) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.NO);
		cd.setQuestion(question);
		cd.setEncounterTypeList(encounterTypes);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWhoStartedStateWhenInAgeRangeAtLocationByEndDate(ProgramWorkflowState state, Integer minAge, Age.Unit minAgeUnit,  Integer maxAge, Age.Unit maxAgeUnit) {
		InAgeRangeAtStateStartCohortDefinition cd = new InAgeRangeAtStateStartCohortDefinition();
		cd.setState(state);
		cd.setMinAge(minAge);
		cd.setMinAgeUnit(minAgeUnit);
		cd.setMaxAge(maxAge);
		cd.setMaxAgeUnit(maxAgeUnit);
		cd.addParameter(new Parameter("startedOnOrBefore", "Started On Or Before", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWhoseMostRecentObsDateIsOlderThanValueAtLocationByEndDate(Concept dateConcept, List<EncounterType> types, String olderThan) {
		return getPatientsWhoseMostRecentObsDateIsBetweenValuesAtLocationByEndDate(dateConcept, types, olderThan, null);
	}

	public CohortDefinition getPatientsWhoseMostRecentCodedObsInValuesAtLocationByEndDate(Concept question, List<EncounterType> types, Concept...values) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.MAX);
		cd.setQuestion(question);
		cd.setEncounterTypeList(types);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Arrays.asList(values));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("locationList=location,onOrBefore=endDate"));
	}

    public CohortDefinition getPatientsWhoseObsValueDateIsBetweenStartDateAndEndDateAtLocation(Concept dateConcept, List<EncounterType> types) {
        DateObsCohortDefinition cd = new DateObsCohortDefinition();
        cd.setTimeModifier(TimeModifier.ANY);
        cd.setQuestion(dateConcept);
        cd.setEncounterTypeList(types);
        cd.addParameter(new Parameter("locationList", "Location", Location.class));
        cd.setOperator1(RangeComparator.GREATER_EQUAL);
        cd.addParameter(new Parameter("value1", "value1", Date.class));
        cd.setOperator2(RangeComparator.LESS_EQUAL);
        cd.addParameter(new Parameter("value2", "value2", Date.class));
        return convert(cd, ObjectUtil.toMap("locationList=location,value1=startDate,value2=endDate"));
    }

	public CohortDefinition getPatientsWhoseMostRecentObsDateIsBetweenValuesAtLocationByEndDate(Concept dateConcept, List<EncounterType> types, String olderThan, String onOrPriorTo) {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.MAX);
		cd.setQuestion(dateConcept);
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		Map<String, String> params = ObjectUtil.toMap("locationList=location,onOrBefore=endDate");
		if (olderThan != null) {
			cd.setOperator1(RangeComparator.LESS_THAN);
			cd.addParameter(new Parameter("value1", "value1", Date.class));
			params.put("value1", "endDate-"+olderThan);
		}
		if (onOrPriorTo != null) {
			cd.setOperator2(RangeComparator.GREATER_EQUAL);
			cd.addParameter(new Parameter("value2", "value2", Date.class));
			params.put("value2", "endDate-"+onOrPriorTo);
		}
		return convert(cd, params);
	}

	public CohortDefinition getPatientsLateForAppointment(List<ProgramWorkflowState> states, List<EncounterType> encounterTypes, Integer minDaysOverdue, Integer maxDaysOverdue) {
        AppointmentStatusCohortDefinition cd = new AppointmentStatusCohortDefinition();
        cd.setActiveStates(states);
        cd.setEncounterTypes(encounterTypes);
        cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
        cd.addParameter(new Parameter("locations", "Locations", Location.class));
        cd.setNoAppointmentIncluded(false);
        cd.setMinDaysOverdue(minDaysOverdue);
        cd.setMaxDaysOverdue(maxDaysOverdue);
        return convert(cd, ObjectUtil.toMap("onDate=endDate,locations=location"));
    }

	public CohortDefinition getPatientsWhoseMostRecentObsDateIsBetweenValuesByEndDate(Concept dateConcept, List<EncounterType> types, String olderThan, String onOrPriorTo) {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.MAX);
		cd.setQuestion(dateConcept);
		cd.setEncounterTypeList(types);
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		Map<String, String> params = ObjectUtil.toMap("onOrBefore=endDate");
		if (olderThan != null) {
			cd.setOperator1(RangeComparator.LESS_THAN);
			cd.addParameter(new Parameter("value1", "value1", Date.class));
			params.put("value1", "endDate-"+olderThan);
		}
		if (onOrPriorTo != null) {
			cd.setOperator2(RangeComparator.GREATER_EQUAL);
			cd.addParameter(new Parameter("value2", "value2", Date.class));
			params.put("value2", "endDate-"+onOrPriorTo);
		}
		return convert(cd, params);
	}

	public CompositionCohortDefinition getPatientsInAll(CohortDefinition...elements) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.initializeFromQueries(BooleanOperator.AND, elements);
		return cd;
	}

	public CompositionCohortDefinition getPatientsInAny(CohortDefinition...elements) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.initializeFromQueries(BooleanOperator.OR, elements);
		return cd;
	}

	public CompositionCohortDefinition createPatientComposition(Object... elements) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.initializeFromElements(elements);
		return cd;
	}

	// Encounter Queries

	public EncounterQuery getEncountersOfTypeAtLocationByEndDate(List<EncounterType> encounterTypes) {
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.setEncounterTypes(encounterTypes);
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("locationList", "Locations", Location.class));
		return convert(q, ObjectUtil.toMap("onOrBefore=endDate,locationList=location"));
	}

    public EncounterQuery getEncountersOfTypeAtLocationDuringPeriod(List<EncounterType> encounterTypes) {
        BasicEncounterQuery q = new BasicEncounterQuery();
        q.setEncounterTypes(encounterTypes);
        q.addParameter(new Parameter("onOrAfter", "On or after", Date.class));
        q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
        q.addParameter(new Parameter("locationList", "Locations", Location.class));
        return new MappedParametersEncounterQuery(q, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,locationList=location"));
    }

	public EncounterQuery getEncountersOfTypeByEndDate(List<EncounterType> encounterTypes) {
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.setEncounterTypes(encounterTypes);
		q.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
		return convert(q, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public EncounterQuery getEncountersWithObsRecordedAtLocationDuringPeriod(Concept question, List<EncounterType> encounterTypes) {
		ObsForEncounterQuery q = new ObsForEncounterQuery();
		q.setQuestion(question);
		q.setEncounterTypes(encounterTypes);
		q.addParameter(new Parameter("encounterOnOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("encounterOnOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("encounterLocations", "Locations", Location.class));
		return convert(q, ObjectUtil.toMap("encounterOnOrAfter=startDate,encounterOnOrBefore=endDate,encounterLocations=location"));
	}

	public EncounterQuery getEncountersWithNumericObsValuesRecordedAtLocationDuringPeriod(Concept question, List<EncounterType> encounterTypes, RangeComparator operator, Double value) {
		NumericObsForEncounterQuery q = new NumericObsForEncounterQuery();
		q.setQuestion(question);
		q.setEncounterTypes(encounterTypes);
		q.setOperator1(operator);
		q.setValue1(value);
		q.addParameter(new Parameter("encounterOnOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("encounterOnOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("encounterLocations", "Locations", Location.class));
		return convert(q, ObjectUtil.toMap("encounterOnOrAfter=startDate,encounterOnOrBefore=endDate,encounterLocations=location"));
	}

	public EncounterQuery getEncountersWithCodedObsValuesRecordedAtLocationDuringPeriod(Concept question, List<EncounterType> encounterTypes, Concept... valuesToInclude) {
		CodedObsForEncounterQuery q = new CodedObsForEncounterQuery();
		q.setQuestion(question);
		q.setEncounterTypes(encounterTypes);
		q.setConceptsToInclude(Arrays.asList(valuesToInclude));
		q.addParameter(new Parameter("encounterOnOrAfter", "On or after", Date.class));
		q.addParameter(new Parameter("encounterOnOrBefore", "On or before", Date.class));
		q.addParameter(new Parameter("encounterLocations", "Locations", Location.class));
		return convert(q, ObjectUtil.toMap("encounterOnOrAfter=startDate,encounterOnOrBefore=endDate,encounterLocations=location"));
	}

	public CompositionEncounterQuery getEncountersInAll(EncounterQuery...elements) {
		CompositionEncounterQuery cd = new CompositionEncounterQuery();
		cd.initializeFromQueries(BooleanOperator.AND, elements);
		return cd;
	}

	public CompositionEncounterQuery getEncountersInAny(EncounterQuery...elements) {
		CompositionEncounterQuery cd = new CompositionEncounterQuery();
		cd.initializeFromQueries(BooleanOperator.OR, elements);
		return cd;
	}

	public CompositionEncounterQuery getEncounterComposition(Object...elements) {
		CompositionEncounterQuery cd = new CompositionEncounterQuery();
		cd.initializeFromElements(elements);
		return cd;
	}

	// Converters

	public DataConverter getIdentifierCollectionConverter() {
		CollectionConverter collectionConverter = new CollectionConverter(new PatientIdentifierConverter(), true, null);
		return new ChainedConverter(collectionConverter, new ObjectFormatter(" "));
	}

	public DataConverter getIdentifierConverter() {
		return new PropertyConverter(PatientIdentifier.class, "identifier");
	}

	public DataConverter getEncounterDatetimeConverter() {
		return new PropertyConverter(Encounter.class, "encounterDatetime");
	}

	public DataConverter getEncounterLocationNameConverter() {
		return new ChainedConverter(new PropertyConverter(Encounter.class, "location"), new ObjectFormatter());
	}

	public DataConverter getEncounterTypeNameConverter() {
		return new ChainedConverter(new PropertyConverter(Encounter.class, "type"), new ObjectFormatter());
	}

	public DataConverter getObsDatetimeConverter() {
		return new PropertyConverter(Obs.class, "obsDatetime");
	}

	public DataConverter getObsValueNumericConverter() {
		return new PropertyConverter(Obs.class, "valueNumeric");
	}

	public DataConverter getObsValueDatetimeConverter() {
		return new PropertyConverter(Obs.class, "valueDatetime");
	}

    public DataConverter getObsValueDatetimeCollectionConverter() {
        ChainedConverter itemConverter = new ChainedConverter(getObsValueDatetimeConverter(), getObjectFormatter());
        CollectionConverter collectionConverter = new CollectionConverter(itemConverter, true, null);
        return new ChainedConverter(collectionConverter, new ObjectFormatter(" "));
    }

	public DataConverter getObsValueCodedConverter() {
		return new PropertyConverter(Obs.class, "valueCoded");
	}

	public DataConverter getObsValueCodedNameConverter() {
		return new ChainedConverter(getObsValueCodedConverter(), new ObjectFormatter());
	}

	public DataConverter getObsValueTextConverter() {
		return new PropertyConverter(Obs.class, "valueText");
	}

	public DataConverter getProgramEnrollmentDateConverter() {
		return new PropertyConverter(PatientProgram.class, "dateEnrolled");
	}

	public DataConverter getStateNameConverter() {
		return new ChainedConverter(new PropertyConverter(PatientState.class, "state.concept"), getObjectFormatter());
	}

	public DataConverter getStateStartDateConverter() {
		return new PropertyConverter(PatientState.class, "startDate");
	}

	public DataConverter getStateLocationConverter() {
		return new ChainedConverter(new PropertyConverter(PatientState.class, "patientProgram.location"), new ObjectFormatter());
	}

	public DataConverter getStateProgramEnrollmentDateConverter() {
		return new PropertyConverter(PatientState.class, "patientProgram.dateEnrolled");
	}

	public DataConverter getStateNameAndDateFormatter() {
		return new ObjectFormatter("{patientProgram.program}: {state} (since {startDate|yyyy-MM-dd})");
	}

	public DataConverter getActiveStatesAsStringConverter() {
		ChainedConverter converter = new ChainedConverter();
		converter.addConverter(new CollectionConverter(getStateNameAndDateFormatter(), false, null));
		converter.addConverter(new ObjectFormatter("; "));
		return converter;
	}

	public DataConverter getObjectFormatter() {
		return new ObjectFormatter();
	}

	public DataConverter getListItemConverter(Integer index, DataConverter... converters) {
		ChainedConverter ret = new ChainedConverter();
		ret.addConverter(new ListConverter(index, Object.class));
		for (DataConverter converter : converters) {
			ret.addConverter(converter);
		}
		return ret;
	}

	public DataConverter getLastListItemConverter(DataConverter... converters) {
		ChainedConverter ret = new ChainedConverter();
		ret.addConverter(new ListConverter(TimeQualifier.LAST, 1, Object.class));
		for (DataConverter converter : converters) {
			ret.addConverter(converter);
		}
		return ret;
	}

	public DataConverter getDataSetItemConverter(Integer index, String columnName, Object nullReplacement) {
		ChainedConverter ret = new ChainedConverter();
		ret.addConverter(new ListConverter(index, DataSetRow.class));
		ret.addConverter(new DataSetRowConverter(columnName));
		if (nullReplacement != null) {
			ret.addConverter(new NullValueConverter(nullReplacement));
		}
		return ret;
	}

	public DataConverter getLastDataSetItemConverter(String columnName, Object nullReplacement) {
		ChainedConverter ret = new ChainedConverter();
		ret.addConverter(new ListConverter(TimeQualifier.LAST, 1, DataSetRow.class));
		ret.addConverter(new DataSetRowConverter(columnName));
		if (nullReplacement != null) {
			ret.addConverter(new NullValueConverter(nullReplacement));
		}
		return ret;
	}

	// Convenience methods

	public PatientDataDefinition convert(PatientDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
        ConvertedPatientDataDefinition convertedDefinition = new ConvertedPatientDataDefinition();
        addAndConvertMappings(pdd, convertedDefinition, renamedParameters, converter);
        return convertedDefinition;
	}

	public PatientDataDefinition convert(PatientDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}

	public PatientDataDefinition convert(PersonDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		return convert(new PersonToPatientDataDefinition(pdd), renamedParameters, converter);
	}

	public PatientDataDefinition convert(PersonDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}

	public EncounterDataDefinition convert(EncounterDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		ConvertedEncounterDataDefinition convertedDefinition = new ConvertedEncounterDataDefinition();
		addAndConvertMappings(pdd, convertedDefinition, renamedParameters, converter);
		return convertedDefinition;
	}

	public EncounterDataDefinition convert(EncounterDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}

	public EncounterQuery convert(EncounterQuery query, Map<String, String> renamedParameters) {
		return new MappedParametersEncounterQuery(query, renamedParameters);
	}

	public CohortDefinition convert(CohortDefinition cd, Map<String, String> renamedParameters) {
		return new MappedParametersCohortDefinition(cd, renamedParameters);
	}

	protected <T extends DataDefinition> void addAndConvertMappings(T copyFrom, ConvertedDataDefinition<T> copyTo, Map<String, String> renamedParameters, DataConverter converter) {
		copyTo.setDefinitionToConvert(ParameterizableUtil.copyAndMap(copyFrom, copyTo, renamedParameters));
		if (converter != null) {
			copyTo.setConverters(Arrays.asList(converter));
		}
	}
}
