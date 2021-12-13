package org.openmrs.module.pihmalawi.patient;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 *  Class implements patient operations for patients extending to the PatientService
 */
public interface ExtendedPatientService extends OpenmrsService {

    @Authorized({"View Patient Identifiers"})
    @Transactional(readOnly = true)
    PatientIdentifier getPatientIdentifierByIdentifier(String identifier) throws APIException;

    @Authorized({"View People"})
    @Transactional(readOnly = true)
    List<Patient> getPatientsByDateChanged(Date dateChanged) throws APIException;
}
