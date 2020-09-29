package org.openmrs.module.pihmalawi.patient;

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Class implements patient operations for patients extending to the PatientService
 */
public interface ExtendedPatientService extends OpenmrsService {

    @Authorized({"View Patient Identifiers"})
    @Transactional(readOnly = true)
    PatientIdentifier getPatientIdentifierByIdentifier(String identifier) throws APIException;
}
