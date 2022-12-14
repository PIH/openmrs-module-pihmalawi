package org.openmrs.module.pihmalawi.patient.impl;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.pihmalawi.db.ExtendedEncounterDAO;
import org.openmrs.module.pihmalawi.patient.ExtendedEncounterService;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ExtendedEncounterServiceImpl extends BaseOpenmrsService implements ExtendedEncounterService {

    private ExtendedEncounterDAO extendedEncounterDAO;

    public ExtendedEncounterDAO getExtendedEncounterDAO() {
        return extendedEncounterDAO;
    }

    public void setExtendedEncounterDAO(ExtendedEncounterDAO extendedEncounterDAO) {
        this.extendedEncounterDAO = extendedEncounterDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getDuplicateEncounters(Date startDate, Date endDate, EncounterType encounterType) {
        EncounterService encounterService = Context.getEncounterService();
        List<Object[]> duplicateEncounters = extendedEncounterDAO.getDuplicateEncounters(startDate, endDate, encounterType);
        List<Encounter> encounterList = new ArrayList<>();
        for (Object[] duplicateEncounter : duplicateEncounters) {
            Integer encId = (Integer)duplicateEncounter[1];
            encounterList.add(encounterService.getEncounter(encId));
        }
        // sort the list of encounters by encounterdatetime desc
        Collections.sort(encounterList, new Comparator<Encounter>() {
            @Override
            public int compare(Encounter e1, Encounter e2) {
                return e2.getEncounterDatetime().compareTo(e1.getEncounterDatetime());
            }
        });
        return encounterList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getDeletedEncountersByVoidReason(Date startDate, Date endDate, String voidReason) {
        return extendedEncounterDAO.getDeletedEncountersByVoidReason(startDate, endDate, voidReason);
    }

    @Override
    public List<Encounter> deleteDuplicateEncounters(List<Encounter> duplicates) {
        List<Encounter> deletedEncounters = new ArrayList<>();
        EncounterService encounterService = Context.getEncounterService();
        for (Encounter enc : duplicates) {
            Set<Obs> encObs = enc.getObs();
            EncounterSearchCriteria criteria = new EncounterSearchCriteriaBuilder()
                    .setPatient(enc.getPatient())
                    .setEncounterTypes(Arrays.asList(enc.getEncounterType()))
                    .setFromDate(enc.getEncounterDatetime())
                    .setToDate(enc.getEncounterDatetime())
                    .setLocation(enc.getLocation())
                    .setIncludeVoided(false)
                    .createEncounterSearchCriteria();
            List<Encounter> encounters = encounterService.getEncounters(criteria);
            for (Encounter possibleDup : encounters) {
                if (possibleDup.getId().compareTo(enc.getId()) != 0) {
                    //compare the obs
                    Set<Obs> possibleDupObs = possibleDup.getObs();
                    boolean exactMatch =  true;
                    for (Obs possibleDupOb : possibleDupObs) {
                        Concept concept = possibleDupOb.getConcept();
                        String valueAsString = possibleDupOb.getValueAsString(Context.getLocale());
                        Obs foundObs = encObs.stream()
                                .filter(obs -> concept.getUuid().equals(obs.getConcept().getUuid()) && valueAsString.equals(obs.getValueAsString(Context.getLocale())))
                                .findAny()
                                .orElse(null);
                        if (foundObs == null ) {
                            //we did not find an exact match Obs
                            exactMatch = false;
                            break;
                        }

                    }
                    if (exactMatch && possibleDupObs.size() == encObs.size()) {
                        // this is a duplicate Encounter with the same Obs values
                        //delete the Encounter
                        Encounter deletedEncounter = encounterService.voidEncounter(possibleDup, "system deleting duplicate encounters");
                        deletedEncounters.add(deletedEncounter);
                    }

                }
            }
        }
        return deletedEncounters;
    }


}
