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
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.PersonAddress;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PatientIdentifierConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ProgramPatientIdentifierDataDefinition;
import org.openmrs.module.pihmalawi.reports.extension.InProgramAtProgramLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reports.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
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
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

	public Parameter getLocationParameter() {
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

	public PatientDataDefinition getFirstEncounterOfTypeByEndDate(EncounterType type, DataConverter converter) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setTypes(Arrays.asList(type));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentEncounterOfTypeByEndDate(EncounterType type, DataConverter converter) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setTypes(Arrays.asList(type));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getFirstObsByEndDate(Concept question, List<EncounterType> encounterTypes, DataConverter converter) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setQuestion(question);
		def.setEncounterTypeList(encounterTypes);
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), converter);
	}

	public PatientDataDefinition getMostRecentObsByEndDate(Concept question, EncounterType...encounterType) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setQuestion(question);
		def.setEncounterTypeList(Arrays.asList(encounterType));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(def, ObjectUtil.toMap("onOrBefore=endDate"), null);
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

	public EncounterDataDefinition getSingleObsForEncounter(Concept concept) {
		ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
		def.setQuestion(concept);
		def.setSingleObs(true);
		return def;
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
		return convert(cd, ObjectUtil.toMap("bornOnOrAfter=startDate-"+numWeeks+"w,bornOnOrBefore=endDate-"+numWeeks+"w"));
	}

	public CohortDefinition getPatientsWithIdentifierOfType(PatientIdentifierType type) {
		PatientIdentifierCohortDefinition cd = new PatientIdentifierCohortDefinition();
		cd.addTypeToMatch(type);
		return cd;
	}

	public CohortDefinition getPatientsWithIdentifierOfTypeAtLocation(PatientIdentifierType type) {
		PatientIdentifierCohortDefinition cd = new PatientIdentifierCohortDefinition();
		cd.addParameter(new Parameter("locationsToMatch", "Location", Location.class));
		cd.addTypeToMatch(type);
		return convert(cd, ObjectUtil.toMap("locationsToMatch=location"));
	}

	public CohortDefinition getEverEnrolledInStateAtLocationByEndDate(ProgramWorkflowState state) {
		PatientStateAtLocationCohortDefinition cd = new PatientStateAtLocationCohortDefinition();
		cd.setState(state);
		cd.addParameter(new Parameter("startedOnOrBefore", "Started on or before", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrBefore=endDate"));
	}

	public CohortDefinition getStartedInStateAtLocationDuringPeriod(ProgramWorkflowState state) {
		PatientStateAtLocationCohortDefinition cd = new PatientStateAtLocationCohortDefinition();
		cd.setState(state);
		cd.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
		cd.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("startedOnOrAfter=startDate,startedOnOrBefore=endDate"));
	}

	public CohortDefinition getActiveInStateAtLocationOnEndDate(ProgramWorkflowState state) {
		InStateAtLocationCohortDefinition cd = new InStateAtLocationCohortDefinition();
		cd.setState(state);
		cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate"));
	}

	public CohortDefinition getActiveInStateAtLocationNumMonthsBeforeEndDate(ProgramWorkflowState state, int numMonths) {
		InStateAtLocationCohortDefinition cd = new InStateAtLocationCohortDefinition();
		cd.setState(state);
		cd.addParameter(new Parameter("onDate", "OnDate", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate-"+numMonths+"m"));
	}

	public CohortDefinition getEverEnrolledInProgramByEndDate(Program program) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		return convert(cd, ObjectUtil.toMap("enrolledOnOrBefore=endDate"));
	}

	public CohortDefinition getActivelyEnrolledInProgramAtLocationOnEndDate(Program program) {
		InProgramAtProgramLocationCohortDefinition cd = new InProgramAtProgramLocationCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return convert(cd, ObjectUtil.toMap("onDate=endDate"));
	}

	public CohortDefinition getActivelyEnrolledInProgramAtLocationOnEndDate(Program program, Location location) {
		InProgramAtProgramLocationCohortDefinition cd = new InProgramAtProgramLocationCohortDefinition();
		cd.setPrograms(Arrays.asList(program));
		cd.setLocation(location);
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

	public CohortDefinition getPatientsWithAnyObsDuringPeriod(Concept question, List<EncounterType> restrictToTypes) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWithAnyObsWithinMonthsByEndDate(Concept question, List<EncounterType> restrictToTypes, int numMonths) {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setEncounterTypeList(restrictToTypes);
		cd.addParameter(new Parameter("onOrAfter", "On or After", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrAfter=endDate-"+numMonths+"m+1d,onOrBefore=endDate"));
	}

	public CohortDefinition getPatientsWithCodedObsByEndDate(Concept question, List<Concept> codedValues) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setTimeModifier(PatientSetService.TimeModifier.ANY);
		cd.setQuestion(question);
		cd.setOperator(SetComparator.IN);
		cd.setValueList(codedValues);
		cd.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		return convert(cd, ObjectUtil.toMap("onOrBefore=endDate"));
	}

	public CompositionCohortDefinition getPatientsInAll(CohortDefinition...elements) {
		List<Object> l = new ArrayList<Object>();
		for (CohortDefinition cd : elements) {
			if (!l.isEmpty()) {
				l.add("AND");
			}
			l.add(cd);
		}
		return createComposition(l.toArray());
	}

	public CompositionCohortDefinition getPatientsInAny(CohortDefinition...elements) {
		List<Object> l = new ArrayList<Object>();
		for (CohortDefinition cd : elements) {
			if (!l.isEmpty()) {
				l.add("OR");
			}
			l.add(cd);
		}
		return createComposition(l.toArray());
	}

	public CompositionCohortDefinition createComposition(Object...elements) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		int definitionCount = 0;
		for (Object o : elements) {
			String key = o.toString();
			if (o instanceof CohortDefinition) {
				CohortDefinition cohortDefinition = (CohortDefinition)o;
				definitionCount++;
				key = Integer.toString(definitionCount);
				for (Parameter p : cohortDefinition.getParameters()) {
					if (cd.getParameter(p.getName()) == null) {
						cd.addParameter(p);
					}
				}
				cd.addSearch(key, Mapped.mapStraightThrough(cohortDefinition));
			}
			s.append(s.length() > 0 ? " " : "").append(key);
		}
		cd.setCompositionString(s.toString());
		return cd;
	}

	// Converters

	public DataConverter getIdentifierCollectionConverter() {
		CollectionConverter collectionConverter = new CollectionConverter(new PatientIdentifierConverter(), true, null);
		return new ChainedConverter(collectionConverter, new ObjectFormatter(" "));
	}

	public DataConverter getEncounterDatetimeConverter() {
		return new PropertyConverter(Encounter.class, "encounterDatetime");
	}

	public DataConverter getEncounterLocationNameConverter() {
		return new ChainedConverter(new PropertyConverter(Encounter.class, "location"), new ObjectFormatter());
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
