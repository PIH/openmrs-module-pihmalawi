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
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.module.pihmalawi.reporting.data.converter.PatientIdentifierConverter;
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
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class PatientDataFactory {

	public PatientDataDefinition getPreferredAddress(String property) {
		PreferredAddressDataDefinition d = new PreferredAddressDataDefinition();
		PropertyConverter converter = new PropertyConverter(PersonAddress.class, property);
		return convert(d, converter);
	}

	public PatientDataDefinition getPreferredIdentifierAtLocation(PatientIdentifierType pit, DataConverter...converters) {
		PreferredIdentifierDataDefinition def = new PreferredIdentifierDataDefinition();
		def.setIdentifierType(pit);
		def.addParameter(new Parameter("location", "Location", Location.class));
		return new ConvertedPatientDataDefinition(def, converters);
	}

	public PatientDataDefinition getAllIdentifiersOfType(PatientIdentifierType pit, DataConverter...converters) {
		PatientIdentifierDataDefinition def = new PatientIdentifierDataDefinition();
		def.setTypes(Arrays.asList(pit));
		return new ConvertedPatientDataDefinition(def, converters);
	}

	public PatientDataDefinition getFirstEncounterOfTypeByEndDate(EncounterType type, DataConverter...converters) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setTypes(Arrays.asList(type));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		ConvertedPatientDataDefinition mappedDef = new ConvertedPatientDataDefinition();
		mappedDef.addParameter(new Parameter("endDate", "End Date", Date.class));
		mappedDef.setDefinitionToConvert(Mapped.<PatientDataDefinition>map(def, "onOrBefore=${endDate}"));
		if (converters != null) {
			mappedDef.setConverters(Arrays.asList(converters));
		}
		return mappedDef;
	}

	public PatientDataDefinition getFirstObsByEndDate(Concept question, EncounterType...encounterType) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setQuestion(question);
		def.setEncounterTypeList(Arrays.asList(encounterType));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		PersonToPatientDataDefinition pdd = new PersonToPatientDataDefinition(def);
		ConvertedPatientDataDefinition mappedDef = new ConvertedPatientDataDefinition();
		mappedDef.addParameter(new Parameter("endDate", "End Date", Date.class));
		mappedDef.setDefinitionToConvert(Mapped.<PatientDataDefinition>map(pdd, "onOrBefore=${endDate}"));
		return mappedDef;
	}

	public PersonDataDefinition getMostRecentObsByEndDate(Concept question, EncounterType...encounterType) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		def.setQuestion(question);
		def.setEncounterTypeList(Arrays.asList(encounterType));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		ConvertedPersonDataDefinition mappedDef = new ConvertedPersonDataDefinition();
		mappedDef.addParameter(new Parameter("endDate", "End Date", Date.class));
		mappedDef.setDefinitionToConvert(Mapped.<PersonDataDefinition>map(def, "onOrBefore=${endDate}"));
		return mappedDef;
	}

	// Converters

	public DataConverter getIdentifierCollectionConverter() {
		CollectionConverter collectionConverter = new CollectionConverter(new PatientIdentifierConverter(), true, null);
		return new ChainedConverter(collectionConverter, new ObjectFormatter(" "));
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

	// Convenience methods

	public PatientDataDefinition convert(PatientDataDefinition pdd, DataConverter... converters) {
		return new ConvertedPatientDataDefinition(pdd, converters);
	}

	public PatientDataDefinition convert(PersonDataDefinition pdd, DataConverter... converters) {
		return new ConvertedPatientDataDefinition(new PersonToPatientDataDefinition(pdd), converters);
	}
}
