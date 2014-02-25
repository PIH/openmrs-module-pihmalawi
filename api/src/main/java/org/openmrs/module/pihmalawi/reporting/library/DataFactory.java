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
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.module.pihmalawi.reporting.data.converter.PatientIdentifierConverter;
import org.openmrs.module.reporting.common.ObjectUtil;
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
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DataFactory {

	public PatientDataDefinition getPreferredAddress(String property) {
		PreferredAddressDataDefinition d = new PreferredAddressDataDefinition();
		PropertyConverter converter = new PropertyConverter(PersonAddress.class, property);
		return convert(d, converter);
	}

	public PatientDataDefinition getPreferredIdentifierAtLocation(PatientIdentifierType pit, DataConverter converter) {
		PreferredIdentifierDataDefinition def = new PreferredIdentifierDataDefinition();
		def.setIdentifierType(pit);
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

	protected <T extends DataDefinition> void addAndConvertMappings(T copyFrom, ConvertedDataDefinition<T> copyTo, Map<String, String> renamedParameters, DataConverter converter) {
		copyTo.setDefinitionToConvert(ParameterizableUtil.copyAndMap(copyFrom, copyTo, renamedParameters));
		if (converter != null) {
			copyTo.setConverters(Arrays.asList(converter));
		}
	}
}
