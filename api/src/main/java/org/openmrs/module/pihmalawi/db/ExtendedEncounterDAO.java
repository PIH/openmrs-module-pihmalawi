package org.openmrs.module.pihmalawi.db;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;

import java.util.Date;
import java.util.List;

public interface ExtendedEncounterDAO {

    public List<Object[]> getDuplicateEncounters(Date startDate, Date endDate, EncounterType encounterType);

    public List<Encounter> getDeletedEncountersByVoidReason(Date startDate, Date endDate, String voidReason);
}
