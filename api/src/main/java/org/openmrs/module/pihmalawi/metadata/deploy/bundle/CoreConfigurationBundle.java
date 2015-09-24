package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.pihmalawi.metadata.Concepts;
import org.openmrs.module.pihmalawi.metadata.Locations;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CoreConfigurationBundle extends PihMetadataBundle {

    public static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy";
    public static final String DEFAULT_TIME_FORMAT = "h:mm aa";
    public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT;

    @Override
    public void install() throws Exception {

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // Html Form Entry
        properties.put(HtmlFormEntryConstants.GP_DATE_FORMAT, DEFAULT_DATE_FORMAT);
        properties.put(HtmlFormEntryConstants.GP_TIME_FORMAT, DEFAULT_TIME_FORMAT);
        properties.put(HtmlFormEntryConstants.GP_SHOW_DATE_FORMAT, "false");
        properties.put(HtmlFormEntryConstants.GP_UNKNOWN_CONCEPT, Concepts.UNKNOWN);

        // Reporting
        properties.put(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION, "");

        // UI Framework
        properties.put(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, DEFAULT_DATE_FORMAT);
        properties.put(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);

        // EMR API (all values are uuids of metadata)
        properties.put(EmrApiConstants.GP_UNKNOWN_LOCATION, Locations.UNKNOWN.uuid());
        properties.put(EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.CONSULTING_CLINICIAN);
        properties.put(EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.ORDERING_PROVIDER);
        properties.put(EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, EncounterRoleBundle.EncounterRoles.ADMINISTRATIVE_CLERK);
        properties.put(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);

        // TODO: Look into emr.primaryIdentifierType
        // TODO: Look into emr.extraPatientIdentifierTypes

        /*  TODO: For all below here, see if we need to create these or if something else will already

            TELEPHONE_ATTRIBUTE_TYPE_NAME = "Telephone Number";

            CONCEPT_CODE_DIAGNOSIS_CONCEPT_SET = "Diagnosis Concept Set";
            CONCEPT_CODE_CODED_DIAGNOSIS = "Coded Diagnosis";
            CONCEPT_CODE_NON_CODED_DIAGNOSIS = "Non-Coded Diagnosis";
            CONCEPT_CODE_DIAGNOSIS_ORDER = "Diagnosis Order"; // e.g. Primary or Secondary
            CONCEPT_CODE_DIAGNOSIS_ORDER_PRIMARY = "Primary";
            CONCEPT_CODE_DIAGNOSIS_ORDER_SECONDARY = "Secondary";
            CONCEPT_CODE_DIAGNOSIS_CERTAINTY = "Diagnosis Certainty"; // e.g. confirmed or presumed
            CONCEPT_CODE_DIAGNOSIS_CERTAINTY_CONFIRMED = "Confirmed";
            CONCEPT_CODE_DIAGNOSIS_CERTAINTY_PRESUMED = "Presumed";
            CONCEPT_CODE_DISPOSITION_CONCEPT_SET = "Disposition Concept Set";
            CONCEPT_CODE_DISPOSITION = "Disposition";
            CONCEPT_CODE_ADMISSION_LOCATION = "Admission Location";
            CONCEPT_CODE_INTERNAL_TRANSFER_LOCATION = "Internal Transfer Location";
            CONCEPT_CODE_DATE_OF_DEATH = "Date of Death";
            CONCEPT_CODE_UNKNOWN_CAUSE_OF_DEATH = "Unknown Cause of Death";
            CONCEPT_CODE_ADMISSION_DECISION = "Admission Decision";
            CONCEPT_CODE_DENY_ADMISSION = "Deny Admission";
            CONCEPT_CODE_DISPOSITION_CONCEPT = "Disposition";
         */

        // The below global properties are used by EMR API. I am not setting them yet.  If we need them for a particular feature we can add in then
        // TODO: properties.put(EmrApiConstants.GP_VISIT_NOTE_ENCOUNTER_TYPE, EncounterTypes.CONSULTATION.uuid());
        // TODO: properties.put(EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE, EncounterTypes.CHECK_IN.uuid());
        // TODO: properties.put(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, EncounterTypes.ADMISSION.uuid());
        // TODO: properties.put(EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, EncounterTypes.EXIT_FROM_CARE.uuid());
        // TODO: properties.put(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, EncounterTypes.TRANSFER.uuid());
        // TODO: properties.put(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, Concepts.DIAGNOSIS_SET_OF_SETS);

        // The below are optional or seem optional, so we can do later if we wish
        // emr.admissionForm (optional)
        // emr.exitFromInpatientForm (optional)
        // emr.transferWithinHospitalForm (optional)
        // emrapi.suppressedDiagnosisConcepts
        // emrapi.nonDiagnosisConceptSets
        // conditionList.endReasonConceptSetUuid
        // conditionList.nonCodedUuid
        // emrapi.lastViewedPatientSizeLimit (numeric, defaults to 50)
        // emrapi.visitExpireHours (numeric, defaults to 12)

        // REST
        // These do not use constants from the rest module due to the omod dependency when provided in maven.
        // These are used to increase the number of results that rest web services returns (for the appointment scheduling module)
        properties.put("webservices.rest.maxResultsAbsolute", "1000");
        properties.put("webservices.rest.maxResultsDefault", "500");

        setGlobalProperties(properties);
    }
}
