package org.openmrs.module.pihmalawi.patient;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ExtendedEncounterService extends OpenmrsService {

    @Transactional(readOnly = true)
    List<Encounter> getDuplicateEncounters(Date startDate, Date endDate, EncounterType encounterType);

    @Transactional(readOnly = true)
    List<Encounter> getDeletedEncountersByVoidReason(Date startDate, Date endDate, String voidReason);

    @Authorized( { PrivilegeConstants.EDIT_ENCOUNTERS })
    @Transactional
    List<Encounter> deleteDuplicateEncounters(List<Encounter> duplicates);
}
