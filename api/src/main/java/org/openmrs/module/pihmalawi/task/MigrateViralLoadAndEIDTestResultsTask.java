package org.openmrs.module.pihmalawi.task;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

// TODO remove this after EIDTestResult migration is complete (will need to remove task as well)
public class MigrateViralLoadAndEIDTestResultsTask extends AbstractTask {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute() {
        migrateVL();
        migrateEID();
        addUnknownProviderToEIDScreeningEncounter();
    }

    public void migrateVL() {

        EncounterType artFollowUpEncounterType = Context.getEncounterService().getEncounterTypeByUuid("664b8650-977f-11e1-8993-905e29aff6c1");  // ART Follow up
        EncounterType viralLoadTestEncounterType = Context.getEncounterService().getEncounterTypeByUuid("9959A261-2122-4AE1-A89D-1CA444B712EA"); // Viral Load Screening
        Concept viralLoadTestConstruct = Context.getConceptService().getConceptByUuid("83931c6d-0e5a-4302-b8ce-a31175b6475e"); // Viral Load Test Set

        // get all Viral Load constructs
        List<Obs> viralLoadTests = Context.getObsService().getObservations(null, null, Collections.singletonList(viralLoadTestConstruct), null, null,
                null, null, null, null, null, null, false);

        Person unknown = Context.getPersonService().getPerson(16576);  // same on Neno and Lisungwi
        Provider unknownProvider = Context.getProviderService().getProvidersByPerson(unknown).iterator().next();
        EncounterRole unknownRole = Context.getEncounterService().getEncounterRole(1);  // same on Neno and Lisungwi

        for (Obs viralLoadTest : viralLoadTests) {
            // only migrate those found on the ART Follow-up Encounter Type
            if (viralLoadTest.getEncounter().getEncounterType().equals(artFollowUpEncounterType)) {

                // create the new encounter
                Encounter viralLoadEncounter = new Encounter();
                viralLoadEncounter.setPatient(Context.getPatientService().getPatientOrPromotePerson(viralLoadTest.getPerson().getPersonId()));
                viralLoadEncounter.setEncounterType(viralLoadTestEncounterType);
                viralLoadEncounter.setEncounterDatetime(viralLoadTest.getEncounter().getEncounterDatetime());
                viralLoadEncounter.setLocation(viralLoadTest.getLocation());
                viralLoadEncounter.setProvider(unknownRole, unknownProvider);  // currently the existing form just sets this to unknown

                // for some reason, we need to persist the encounter before moving the obs
                Context.getEncounterService().saveEncounter(viralLoadEncounter);

                // move the obs from the old encounter to the new
                viralLoadEncounter.addObs(viralLoadTest);
                for (Obs member : viralLoadTest.getGroupMembers()) {
                    viralLoadEncounter.addObs(member);
                }

                Context.getEncounterService().saveEncounter(viralLoadEncounter);
            }
        }
    }



    public void migrateEID() {

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

    private void addUnknownProviderToEIDScreeningEncounter() {
        // I didn't do this initially, but the existing EID Test Result form requires one (and sets it to unknown)

        Person unknown = Context.getPersonService().getPerson(16576);  // same on Neno and Lisungwi
        Provider unknownProvider = Context.getProviderService().getProvidersByPerson(unknown).iterator().next();
        EncounterRole unknownRole = Context.getEncounterService().getEncounterRole(1);  // same on Neno and Lisungwi

        EncounterType eidScreening = Context.getEncounterService().getEncounterTypeByUuid("8383DE35-5145-4953-A018-34876B797F3E");  // EID Screening

        for (Encounter encounter : Context.getEncounterService().getEncounters(null, null, null, null,
                null, Collections.singletonList(eidScreening), null,
                null, null, false)) {

            if (encounter.getEncounterProviders() == null || encounter.getEncounterProviders().size() == 0) {
                encounter.setProvider(unknownRole, unknownProvider);  // currently the existing form just sets this to unknown
                Context.getEncounterService().saveEncounter(encounter);
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
