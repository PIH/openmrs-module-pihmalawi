package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 Utility class to group any non-liquibase migrations we need to make
 **/
public class Migrations {

    protected static final Log log = LogFactory.getLog(Migrations.class);

    public static void moveEIDTestResultsToEIDScreeningEncounters() {

        // TEMP
       // Patient patient = Context.getPatientService().getPatient(23871);

        EncounterType eidScreening = Context.getEncounterService().getEncounterTypeByUuid("8383DE35-5145-4953-A018-34876B797F3E");
        EncounterType exposedChildInitial = Context.getEncounterService().getEncounterTypeByUuid("664bcbb0-977f-11e1-8993-905e29aff6c1");
        Concept eidTestConstruct = Context.getConceptService().getConceptByMapping("2168", "PIH Malawi");

        List<Obs> eidTests = Context.getObsService().getObservations(null, null, Collections.singletonList(eidTestConstruct), null, null,
                null, null, null, null, null, null, false);

        for (Obs eidTest : eidTests) {
            if (eidTest.getEncounter().getEncounterType().equals(exposedChildInitial)) {
                Date encounterDate = getSampleDate(eidTest);
                if (encounterDate != null) {

                    Encounter eidScreeningEncounter = new Encounter();
                    eidScreeningEncounter.setPatient(Context.getPatientService().getPatientOrPromotePerson(eidTest.getPerson().getPersonId()));
                    eidScreeningEncounter.setEncounterType(eidScreening);
                    eidScreeningEncounter.setEncounterDatetime(encounterDate);
                    eidScreeningEncounter.setLocation(eidTest.getLocation());

                    // need to save before adding obs?
                    Context.getEncounterService().saveEncounter(eidScreeningEncounter);

                    eidScreeningEncounter.addObs(eidTest);
                    for (Obs member : eidTest.getGroupMembers()) {
                        eidScreeningEncounter.addObs(member);
                    }

                    Context.getEncounterService().saveEncounter(eidScreeningEncounter);
                }
                else {
                    // TODO there were some use cases here
                    log.error("No sample date for obsgroup " + eidTest.getId());
                }
            }

        }

        return;
    }

    private static Date getSampleDate(Obs eidTest) {
        Concept sampleDate = Context.getConceptService().getConceptByMapping("6108", "PIH Malawi");
        for (Obs member: eidTest.getGroupMembers()) {
            if (member.getConcept().equals(sampleDate)) {
                return member.getValueDatetime();
            }
        }
        return null;
    }

}
