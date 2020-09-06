package org.openmrs.module.pihmalawi.patient;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

public interface YendaNafePatientService extends OpenmrsService {

    @Authorized({"View Patient Identifiers"})
    @Transactional(readOnly = true)
    PatientIdentifier getPatientIdentifierByYendaNafeUuid(String uuid) throws APIException;
}
