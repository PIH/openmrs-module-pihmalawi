package org.openmrs.module.pihmalawi.task;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

// TODO remove this after EIDTestResult migration is complete (will need to remove task as well)
public class MigrateEIDTestResultsTask extends AbstractTask {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute() {

        EncounterType eidScreening = Context.getEncounterService().getEncounterTypeByUuid("8383DE35-5145-4953-A018-34876B797F3E");  // EID Screening
        EncounterType exposedChildInitial = Context.getEncounterService().getEncounterTypeByUuid("664bcbb0-977f-11e1-8993-905e29aff6c1");  // Exposed Child Initial
        Concept eidTestConstruct = Context.getConceptService().getConceptByMapping("2168", "PIH Malawi");  // HIV Test Construct

        Concept sampleDateConcept = Context.getConceptService().getConceptByMapping("6108", "PIH Malawi");    // Date of blood sample
        Concept resultDateConcept = Context.getConceptService().getConceptByMapping("6110", "PIH Malawi");   // Date of result concept

        // get all HIV Test Result constructs
        List<Obs> eidTests = Context.getObsService().getObservations(null, null, Collections.singletonList(eidTestConstruct), null, null,
                null, null, null, null, null, null, false);

        for (Obs eidTest : eidTests) {
            // only migrate those found on the Exposed Child Initial Form
            if (eidTest.getEncounter().getEncounterType().equals(exposedChildInitial)) {

                Date encounterDate = getSampleDate(eidTest, sampleDateConcept, resultDateConcept);

                if (encounterDate != null) {

                    // create the new encounter
                    Encounter eidScreeningEncounter = new Encounter();
                    eidScreeningEncounter.setPatient(Context.getPatientService().getPatientOrPromotePerson(eidTest.getPerson().getPersonId()));
                    eidScreeningEncounter.setEncounterType(eidScreening);
                    eidScreeningEncounter.setEncounterDatetime(encounterDate);
                    eidScreeningEncounter.setLocation(eidTest.getLocation());
                    // for the existing encounters, the provider is always Unknown, with Unknown role... do we need to propagate this? I am skipping for now

                    // for some reason, we need to persist the encounter before moving the obs
                    Context.getEncounterService().saveEncounter(eidScreeningEncounter);

                    // move the obs from the old encounter to the new
                    eidScreeningEncounter.addObs(eidTest);
                    for (Obs member : eidTest.getGroupMembers()) {
                        eidScreeningEncounter.addObs(member);
                    }

                    Context.getEncounterService().saveEncounter(eidScreeningEncounter);
                }
                else {
                    // this will happen if there's no sample date on the construct... this is rare but does seem to happen, we will identify and migrate those manually
                    log.error("No sample date for obsgroup " + eidTest.getId());
                }
            }
        }
    }


    // look for date of blood sample obs and use the value datetime from that, otherwise use result date, otherwise return null
    private Date getSampleDate(Obs eidTest, Concept sampleDateConcept, Concept resultDateConcept) {

        for (Obs member: eidTest.getGroupMembers()) {
            if (member.getConcept().equals(sampleDateConcept)) {
                return member.getValueDatetime();
            }
        }

        for (Obs member: eidTest.getGroupMembers()) {
            if (member.getConcept().equals(resultDateConcept)) {
                return member.getValueDatetime();
            }
        }

        return null;
    }
}
