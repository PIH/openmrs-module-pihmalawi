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

import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.springframework.stereotype.Component;

@Component
public class BasePatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

    @Override
    public Class<? super PatientDataDefinition> getDefinitionType() {
        return PatientDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "pihmalawi.patientData.";
    }

	/*
    @DocumentedDefinition("numberOfZlEmrIds")
    public PatientDataDefinition getNumberOfZlEmrIds() {
        return getIdentifiersOf(mirebalaisReportsProperties.getZlEmrIdentifierType(), new CountConverter());
    }

    @DocumentedDefinition("numberOfDossierNumbers")
    public PatientDataDefinition getNumberOfDossierNumbers() {
        return getIdentifiersOf(mirebalaisReportsProperties.getDossierNumberIdentifierType(), new CountConverter());
    }

    @DocumentedDefinition("numberOfHivEmrIds")
    public PatientDataDefinition getNumberOfHivEmrIds() {
        return getIdentifiersOf(mirebalaisReportsProperties.getHivEmrIdentifierType(), new CountConverter());
    }

    @DocumentedDefinition("preferredZlEmrId.identifier")
    public PatientDataDefinition getPreferredZlEmrIdIdentifier() {
        return getPreferredIdentifierOf(
                mirebalaisReportsProperties.getZlEmrIdentifierType(),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("mostRecentZlEmrId.identifier")
    public PatientDataDefinition getMostRecentZlEmrIdIdentifier() {
        return getMostRecentIdentifierOf(
                mirebalaisReportsProperties.getZlEmrIdentifierType(),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("mostRecentZlEmrId.location")
    public PatientDataDefinition getMostRecentZlEmrIdLocation() {
        return getMostRecentIdentifierOf(
                mirebalaisReportsProperties.getZlEmrIdentifierType(),
                new PropertyConverter(PatientIdentifier.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("mostRecentDossierNumber.identifier")
    public PatientDataDefinition getMostRecentDossierNumberIdentifier() {
        return getMostRecentIdentifierOf(
                mirebalaisReportsProperties.getDossierNumberIdentifierType(),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("mostRecentHivEmrId.identifier")
    public PatientDataDefinition getMostRecentHivEmrIdIdentifier() {
        return getMostRecentIdentifierOf(
                mirebalaisReportsProperties.getHivEmrIdentifierType(),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("unknownPatient.value")
    public PatientDataDefinition getUnknownPatient() {
        return new ConvertedPatientDataDefinition(
                new PersonToPatientDataDefinition(
                        new PersonAttributeDataDefinition(emrApiProperties.getUnknownPatientPersonAttributeType())),
                new PropertyConverter(PersonAttribute.class, "value"));
    }

    @DocumentedDefinition("preferredAddress.department")
    public PatientDataDefinition getPreferredAddressDepartment() {
        return getPreferredAddress("stateProvince");
    }

    @DocumentedDefinition("preferredAddress.commune")
    public PatientDataDefinition getPreferredAddressCommune() {
        return getPreferredAddress("cityVillage");
    }

    @DocumentedDefinition("preferredAddress.section")
    public PatientDataDefinition getPreferredAddressSection() {
        return getPreferredAddress("address3");
    }

    @DocumentedDefinition("preferredAddress.locality")
    public PatientDataDefinition getPreferredAddressLocality() {
        return getPreferredAddress("address1");
    }

    @DocumentedDefinition("preferredAddress.streetLandmark")
    public PatientDataDefinition getPreferredAddressStreetLandmark() {
        return getPreferredAddress("address2");
    }

    @DocumentedDefinition("registration.encounterDatetime")
    public PatientDataDefinition getRegistrationDatetime() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("admission.location")
    public PatientDataDefinition getAdmissionLocation() {
        return getAdmissionEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("admission.encounterDatetime")
    public PatientDataDefinition getAdmissionDatetime() {
        return getAdmissionEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("inpatient.location")
    public PatientDataDefinition getInpatientLocation() {
        return getAdmissionOrTransferEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("inpatient.encounterDatetime")
    public PatientDataDefinition getInpatientDatetime() {
        return getAdmissionOrTransferEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("registration.location")
    public PatientDataDefinition getRegistrationLocation() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("registration.creator.name")
    public PatientDataDefinition getRegistrationCreatorName() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "creator"),
                new PropertyConverter(User.class, "personName"),
                new ObjectFormatter("{givenName} {familyName}"));
    }

    @DocumentedDefinition("registration.age")
    public PatientDataDefinition getRegistrationAge() {
        MappedData<PatientDataDefinition> effectiveDate = new MappedData<PatientDataDefinition>(getRegistrationDatetime(), null);

        AgeAtDateOfOtherDataDefinition ageAtRegistration = new AgeAtDateOfOtherDataDefinition();
        ageAtRegistration.setEffectiveDateDefinition(effectiveDate);

        return new ConvertedPatientDataDefinition(new PersonToPatientDataDefinition(ageAtRegistration), new AgeConverter("{y:1}"));
    }

    private PatientDataDefinition getIdentifiersOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        return new ConvertedPatientDataDefinition(
                new PatientIdentifierDataDefinition(null, patientIdentifierType),
                converters);
    }

    private PatientDataDefinition getPreferredIdentifierOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        PatientIdentifierDataDefinition dd = new PatientIdentifierDataDefinition(null, patientIdentifierType);
        dd.setIncludeFirstNonNullOnly(true);
        if (converters.length > 0) {
            return new ConvertedPatientDataDefinition(dd, converters);
        }
        else {
            return dd;
        }
    }

    private PatientDataDefinition getMostRecentIdentifierOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        return getIdentifiersOf(patientIdentifierType,
                converters(new MostRecentlyCreatedConverter(PatientIdentifier.class), converters));
    }

    private PatientDataDefinition getPreferredAddress(String property) {
        return new ConvertedPatientDataDefinition(
                new PersonToPatientDataDefinition(
                        new PreferredAddressDataDefinition()),
                new PropertyConverter(PersonAddress.class, property));
    }

    private PatientDataDefinition getRegistrationEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition registrationEncounters = new EncountersForPatientDataDefinition();
        registrationEncounters.setTypes(Arrays.asList(mirebalaisReportsProperties.getRegistrationEncounterType()));

        return new ConvertedPatientDataDefinition(registrationEncounters,
                converters(new EarliestCreatedConverter(Encounter.class), converters));
    }
    private PatientDataDefinition getAdmissionEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition admissionEncounters = new EncountersForPatientDataDefinition();
        admissionEncounters.setTypes(Arrays.asList(emrApiProperties.getAdmissionEncounterType()));
        admissionEncounters.setOnlyInActiveVisit(true);
        admissionEncounters.setWhich(TimeQualifier.FIRST);
        return new ConvertedPatientDataDefinition(admissionEncounters, converters);
    }
    private PatientDataDefinition getAdmissionOrTransferEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition adtEncounters = new EncountersForPatientDataDefinition();
        adtEncounters.setTypes(Arrays.asList(emrApiProperties.getAdmissionEncounterType(),emrApiProperties.getTransferWithinHospitalEncounterType()));
        adtEncounters.setOnlyInActiveVisit(true);
        adtEncounters.setWhich(TimeQualifier.LAST);
        return new ConvertedPatientDataDefinition(adtEncounters,converters);
    }
    */
}
