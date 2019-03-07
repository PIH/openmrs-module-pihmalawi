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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
                patientIdentifier.setIdentifier("IC3-" + patientIdentifier.getLocation().getId().toString() + "-" + new SimpleDateFormat("yyyy-MM-dd-").format(new Date()) + uuid.substring(1,2) + uuid.substring(11,12));
            }
        }
    }
}
