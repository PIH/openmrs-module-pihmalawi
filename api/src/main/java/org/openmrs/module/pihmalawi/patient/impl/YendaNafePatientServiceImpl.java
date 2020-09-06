package org.openmrs.module.pihmalawi.patient.impl;

import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.pihmalawi.db.YendaNafePatientDAO;
import org.openmrs.module.pihmalawi.patient.YendaNafePatientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class YendaNafePatientServiceImpl extends BaseOpenmrsService implements YendaNafePatientService {

    private YendaNafePatientDAO yendaNafePatientDAO;
    public YendaNafePatientDAO getYendaNafePatientDAO() {
        return yendaNafePatientDAO;
    }

    public void setYendaNafePatientDAO(YendaNafePatientDAO yendaNafePatientDAO) {
        this.yendaNafePatientDAO = yendaNafePatientDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public PatientIdentifier getPatientIdentifierByYendaNafeUuid(String uuid) throws APIException {
        return yendaNafePatientDAO.getPatientIdentifierByYendaNafeUuid(uuid);
    }
}
