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
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class BasePatientDataLibrary extends BuiltInPatientDataLibrary {

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.";
    }

	// Address Data

	@DocumentedDefinition("village")
	public PatientDataDefinition getVillage() {
		return getPreferredAddress("cityVillage");
	}

	@DocumentedDefinition("traditionalAuthority")
	public PatientDataDefinition getTraditionalAuthority() {
		return getPreferredAddress("countyDistrict");
	}

	@DocumentedDefinition("district")
	public PatientDataDefinition getDistrict() {
		return getPreferredAddress("stateProvince");
	}

	@DocumentedDefinition("ageAtEndInMonths")
	public PatientDataDefinition getAgeAtEndInMonths() {
		PatientDataDefinition ageAtEnd = getAgeAtEnd();
		return convert(ageAtEnd, new AgeConverter(AgeConverter.MONTHS));
	}

	// Data Definition Convenience methods

	protected PatientDataDefinition getPreferredAddress(String property) {
		PreferredAddressDataDefinition d = new PreferredAddressDataDefinition();
		PropertyConverter converter = new PropertyConverter(PersonAddress.class, property);
		return convert(d, converter);
	}

	protected PatientDataDefinition getPreferredIdentifierAtLocation(PatientIdentifierType pit, DataConverter...converters) {
		PreferredIdentifierDataDefinition def = new PreferredIdentifierDataDefinition();
		def.setIdentifierType(pit);
		def.addParameter(new Parameter("location", "Location", Location.class));
		return new ConvertedPatientDataDefinition(def, converters);
	}

	protected PatientDataDefinition getAllIdentifiersOfType(PatientIdentifierType pit, DataConverter...converters) {
		PatientIdentifierDataDefinition def = new PatientIdentifierDataDefinition();
		def.setTypes(Arrays.asList(pit));
		return new ConvertedPatientDataDefinition(def, converters);
	}

	protected PatientDataDefinition getFirstEncounterOfType(EncounterType type, DataConverter...converters) {
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setTypes(Arrays.asList(type));
		return def;
	}

	protected PersonDataDefinition getFirstObsByEndDate(Concept question, EncounterType...encounterType) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		def.setQuestion(question);
		def.setEncounterTypeList(Arrays.asList(encounterType));
		def.addParameter(new Parameter("onOrBefore", "On or Before", Date.class));
		ConvertedPersonDataDefinition mappedDef = new ConvertedPersonDataDefinition();
		mappedDef.addParameter(new Parameter("endDate", "End Date", Date.class));
		mappedDef.setDefinitionToConvert(Mapped.<PersonDataDefinition>map(def, "onOrBefore=${endDate}"));
		return mappedDef;
	}

	protected PersonDataDefinition getMostRecentObsByEndDate(Concept question, EncounterType...encounterType) {
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

	// Converter Convenience methods

	protected DataConverter getIdentifierCollectionConverter() {
		CollectionConverter collectionConverter = new CollectionConverter(new PatientIdentifierConverter(), true, null);
		return new ChainedConverter(collectionConverter, new ObjectFormatter(" "));
	}

	protected DataConverter getObsDatetimeConverter() {
		return new PropertyConverter(Obs.class, "obsDatetime");
	}

	protected DataConverter getObsValueNumericConverter() {
		return new PropertyConverter(Obs.class, "valueNumeric");
	}

	protected DataConverter getObsValueDatetimeConverter() {
		return new PropertyConverter(Obs.class, "valueDatetime");
	}
}
