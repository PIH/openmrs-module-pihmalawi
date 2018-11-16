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

import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.AgeInDaysConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.MaxValueConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.BmiPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ChwOrGuardianPatientDataDefinition;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.ConcatenatedPropertyConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientObjectDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BasePatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private ChronicCareMetadata ncdMetadata;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData;

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.";
    }

	@Override
	public Class<? super PatientDataDefinition> getDefinitionType() {
		return PatientDataDefinition.class;
	}

    @DocumentedDefinition
    public PatientDataDefinition getUuid() {
        return df.convert(new PatientObjectDataDefinition(), new PropertyConverter(Patient.class, "uuid"));
    }

	// Address Data

	@DocumentedDefinition
	public PatientDataDefinition getBirthdate() {
		return df.convert(new BirthdateDataDefinition(), new PropertyConverter(Birthdate.class, "birthdate"));
	}

	@DocumentedDefinition
	public PatientDataDefinition getVillage() {
		return df.getPreferredAddress("cityVillage");
	}

	@DocumentedDefinition
	public PatientDataDefinition getTraditionalAuthority() {
		return df.getPreferredAddress("countyDistrict");
	}

	@DocumentedDefinition
	public PatientDataDefinition getDistrict() {
		return df.getPreferredAddress("stateProvince");
	}

	@DocumentedDefinition
	public PatientDataDefinition getAddressFull() {
		PreferredAddressDataDefinition pdd = new PreferredAddressDataDefinition();
		return df.convert(pdd, new ConcatenatedPropertyConverter(", ", "district", "traditionalAuthority", "village"));
	}

	// Relationship Data

	@DocumentedDefinition
	public PatientDataDefinition getChw() {
		return df.getRelationships(hivMetadata.getChwRelationshipType(), false, true);
	}

	@DocumentedDefinition
	public PatientDataDefinition getParentOrGuardian() {
		return df.getRelationships(hivMetadata.getGuardianRelationshipType(), false, true);
	}

	@DocumentedDefinition
	public PatientDataDefinition getChwOrGuardian() {
		return new ChwOrGuardianPatientDataDefinition();
	}

	@DocumentedDefinition
    public PatientDataDefinition getPhoneNumber() {
        PersonAttributeDataDefinition dd = new PersonAttributeDataDefinition();
        dd.setPersonAttributeType(hivMetadata.getTelephoneNumberAttributeType());
        return df.convert(dd, new PropertyConverter(PersonAttribute.class, "value"));
    }

	// Demographic Data

	@DocumentedDefinition
	public PatientDataDefinition getAgeAtEndInYears() {
		PatientDataDefinition ageAtEnd = builtInPatientData.getAgeAtEnd();
		return df.convert(ageAtEnd, new AgeConverter(AgeConverter.YEARS));
	}

	@DocumentedDefinition
	public PatientDataDefinition getAgeAtEndInMonths() {
		PatientDataDefinition ageAtEnd = builtInPatientData.getAgeAtEnd();
		return df.convert(ageAtEnd, new AgeConverter(AgeConverter.MONTHS));
	}

    @DocumentedDefinition
    public PatientDataDefinition getAgeAtEndInDays() {
        PatientDataDefinition ageAtEnd = builtInPatientData.getAgeAtEnd();
        return df.convert(ageAtEnd, new AgeInDaysConverter());
    }

	@DocumentedDefinition("preferredFamilyNames")
	public PatientDataDefinition getPreferredFamilyNames() {
		PreferredNameDataDefinition pdd = new PreferredNameDataDefinition();
		return df.convert(pdd, new ConcatenatedPropertyConverter(" ", "familyName", "familyName2"));
	}

	// Vitals

    @DocumentedDefinition
    public PatientDataDefinition getLatestHeightObs() {
        return df.getMostRecentObsByEndDate(hivMetadata.getHeightConcept());
    }

	@DocumentedDefinition("latestHeight")
	public PatientDataDefinition getLatestHeight() {
		return df.convert(getLatestHeightObs(), df.getObsValueNumericConverter());
	}

    @DocumentedDefinition
    public PatientDataDefinition getAllWeightObservations() {
        return df.getAllObsByEndDate(hivMetadata.getWeightConcept(), null, null);
    }

    @DocumentedDefinition
    public PatientDataDefinition getLatestWeightObs() {
        return df.getMostRecentObsByEndDate(hivMetadata.getWeightConcept());
    }

	@DocumentedDefinition("latestWeight")
	public PatientDataDefinition getLatestWeight() {
		return df.convert(getLatestWeightObs(), df.getObsValueNumericConverter());
	}

	@DocumentedDefinition("latestWeight.date")
	public PatientDataDefinition getLatestWeightDate() {
		return df.convert(getLatestWeightObs(), df.getObsDatetimeConverter());
	}

    @DocumentedDefinition
    public PatientDataDefinition getLatestBmiByEndDate() {
        BmiPatientDataDefinition d = new BmiPatientDataDefinition();
        d.addParameter(ReportingConstants.END_DATE_PARAMETER);
        return d;
    }

    @DocumentedDefinition
    public PatientDataDefinition getLatestBmiNumericValueByEndDate() {
        return df.convert(getLatestBmiByEndDate(), new PropertyConverter(BMI.class, "numericValue"));
    }

    // Encounters

    @DocumentedDefinition("allEncounters")
    public PatientDataDefinition getAllEncounters() {
        return new EncountersForPatientDataDefinition();
    }

    // Obs

    @DocumentedDefinition
    public PatientDataDefinition getNextAppointmentObsByEndDate(DataConverter converter) {
        ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
        def.setQuestion(hivMetadata.getAppointmentDateConcept());
        def.addParameter(new Parameter("valueDatetimeOnOrBefore", "On or before", Date.class));
        return df.convert(def, ObjectUtil.toMap("valueDatetimeOnOrBefore=endDate"), converter);
    }

    @DocumentedDefinition
    public PatientDataDefinition getLatestNextAppointmentDateValueByEndDate() {
        CollectionConverter collectionConverter = new CollectionConverter(df.getObsValueDatetimeConverter(), true, null);
        MaxValueConverter<Date> maxValueConverter = new MaxValueConverter<Date>();
        return getNextAppointmentObsByEndDate(new ChainedConverter(collectionConverter, maxValueConverter));
    }

    /**
     * // TODO: Determine if we should limit this by type.  With all of the new encounter types added, I am defaulting to no, as we may forget to update this
     */
    @DocumentedDefinition
    public PatientDataDefinition getLatestVisitDateByEndDate() {
        EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
        def.setWhich(TimeQualifier.LAST);
        def.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
        return df.convert(def, ObjectUtil.toMap("onOrBefore=endDate"), df.getEncounterDatetimeConverter());
    }
}
