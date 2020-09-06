package org.openmrs.module.pihmalawi.patient;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Responsible for patient GET operations for patients captured from medic mobile
 */
public interface YendaNafePatientService extends OpenmrsService {

    @Authorized({"View Patient Identifiers"})
    @Transactional(readOnly = true)
    PatientIdentifier getPatientIdentifierByYendaNafeUuid(String uuid) throws APIException;
}
