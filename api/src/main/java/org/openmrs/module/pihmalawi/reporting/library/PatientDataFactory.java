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
import org.openmrs.PersonAddress;
import org.openmrs.module.pihmalawi.reporting.data.converter.PatientIdentifierConverter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PatientDataFactory {

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

	public DataConverter getObjectFormatter() {
		return new ObjectFormatter();
	}

	// Convenience methods

	public PatientDataDefinition convert(PatientDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		ConvertedPatientDataDefinition converted = new ConvertedPatientDataDefinition();
		Map<String, Object> mappings = new HashMap<String, Object>();
		for (Parameter p : pdd.getParameters()) {
			String paramName = p.getName();
			if (renamedParameters != null && renamedParameters.containsKey(paramName)) {
				paramName = renamedParameters.get(paramName);
			}
			mappings.put(p.getName(), "${" + paramName + "}");
			Parameter newParameter = new Parameter();
			newParameter.setName(paramName);
			newParameter.setLabel(p.getLabel());
			newParameter.setType(p.getType());
			newParameter.setCollectionType(p.getCollectionType());
			newParameter.setDefaultValue(p.getDefaultValue());
			newParameter.setWidgetConfiguration(p.getWidgetConfiguration());
			converted.addParameter(newParameter);
		}
		converted.setDefinitionToConvert(new Mapped<PatientDataDefinition>(pdd, mappings));
		if (converter != null) {
			converted.setConverters(Arrays.asList(converter));
		}
		return converted;
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
}
