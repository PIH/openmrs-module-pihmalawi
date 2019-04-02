package org.openmrs.module.pihmalawi.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;

/**
 * This is support the registration of new patients at the IC3 clinic who have yet to be enrolled in a program.
 * If the user selects "IC3 Identifier Type" when registering a patient, but does *not* enter an identifier,
 * the system will assign one using the following pattern:
 *
 * IC3-{first two digits of location id, with a padded zero if needed}{4 digits from a random uuid}
 */
@Handler(supports = { Patient.class }, order = 10)
public class MalawiPatientValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return Patient.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        PatientIdentifierType ic3IdentifierType =
                Context.getPatientService().getPatientIdentifierTypeByUuid(PihMalawiPatientIdentifierTypes.IC3_IDENTIFIER.uuid());

        Patient patient = (Patient) o;

        for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
            if (StringUtils.isBlank(patientIdentifier.getIdentifier()) && patientIdentifier.getIdentifierType().equals(ic3IdentifierType)) {
                String uuid = UUID.randomUUID().toString();
                patientIdentifier.setIdentifier("IC3-"
                        + StringUtils.leftPad(patientIdentifier.getLocation().getId().toString(), 2, "0").substring(0,2).toUpperCase()
                        + uuid.substring(1,3).toUpperCase() + uuid.substring(24,26).toUpperCase());
            }
        }
    }
}
