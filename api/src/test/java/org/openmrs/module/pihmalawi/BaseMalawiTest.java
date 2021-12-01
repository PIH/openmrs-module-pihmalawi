package org.openmrs.module.pihmalawi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.contrib.testdata.builder.PatientProgramBuilder;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.Metadata;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseMalawiTest extends BaseModuleContextSensitiveTest {

    public static final String METADATA_XML_FOLDER = "org/openmrs/module/pihmalawi/metadata";
    public static final String[] TABLES = {
        "concept_class", "concept_datatype", "concept", "concept_numeric", "concept_name", "concept_description",
        "drug", "concept_set", "concept_answer",
        "concept_map_type", "concept_reference_source", "concept_reference_term", "concept_reference_map", "concept_reference_term_map",
        "encounter_role", "encounter_type", "location", "location_attribute_type", "location_attribute", "location_tag", "location_tag_map",
        "order_type", "patient_identifier_type", "person_attribute_type", "privilege", "program", "program_workflow", "program_workflow_state",
        "relationship_type", "visit_type"
    };

    @Autowired
    protected TestDataManager tdm;

    @Autowired
    protected ConceptService conceptService;

	@Autowired
    protected HivPatientDataLibrary hivPatientDataLibrary;

    @Autowired
    protected ChronicCarePatientDataLibrary chronicCarePatientDataLibrary;

	@Autowired
    @Qualifier("reportingPatientDataService")
    protected PatientDataService patientDataService;

    @Autowired
    protected HivMetadata hivMetadata;

    @Autowired
    protected ChronicCareMetadata ccMetadata;

    @Override
    public Boolean useInMemoryDatabase() {
        return true;
    }

    public boolean isSetup() throws SQLException {
        boolean isSetup = false;
        try (PreparedStatement statement = getConnection().prepareStatement("select count(*) from concept")) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    isSetup = rs.getInt(1) > 100;
                }
            }
        }
        return isSetup;
    }

    @Before
    @Override
    public void baseSetupWithStandardDataAndAuthentication() throws SQLException {
        if (!Context.isSessionOpen()) {
            Context.openSession();
        }

        if (!isSetup()) {
            this.deleteAllData();
            if (this.useInMemoryDatabase()) {
                this.initializeInMemoryDatabase();
            } else {
                this.executeDataSet("org/openmrs/include/initialInMemoryTestDataSet.xml");
            }
            for (String table : TABLES) {
                executeDataSet(METADATA_XML_FOLDER + "/" + table + ".xml");
            }
            this.getConnection().commit();
            this.updateSearchIndex();
        }
        this.authenticate();
        Context.flushSession();
        Context.clearSession();
    }

    @After
    public void clearConceptCaches() {
        for (Metadata m : Context.getRegisteredComponents(Metadata.class)) {
            m.clearConceptCache();
        }
    }

    protected PatientBuilder createPatient() {
        return tdm.randomPatient();
    }

    protected EncounterBuilder createEncounter(Patient p, EncounterType type, Date encounterDate) {
        return tdm.encounter().patient(p).encounterDatetime(encounterDate).encounterType(type);
    }

    protected PatientProgramBuilder createPatientProgram(Patient p, Program program, Date dateEnrolled, Date dateCompleted) {
        return tdm.patientProgram().patient(p).program(program).dateEnrolled(dateEnrolled).dateCompleted(dateCompleted);
    }

    protected ObsBuilder createObs(Encounter e, Concept question, Object value) {
        ObsBuilder ob = tdm.obs().encounter(e).concept(question);
        if (value != null) {
            if (value instanceof Date) {
                ob.value((Date) value);
            }
            else if (value instanceof Concept) {
                ob.value((Concept) value);
            }
            else if (value instanceof String) {
                ob.value((String) value);
            }
            else if (value instanceof Number) {
                ob.value((Number) value);
            }
            else if (value instanceof Drug) {
                ob.value((Drug) value);
            }
            else {
                throw new IllegalArgumentException("Unable to handle value of type " + value.getClass());
            }
        }
        return ob;
    }

    protected PatientState createState(PatientProgram pp, ProgramWorkflowState state, Date startDate) {
        PatientState ps = new PatientState();
        ps.setPatientProgram(pp);
        ps.setState(state);
        ps.setStartDate(startDate);
        Set<PatientState> states = new HashSet<PatientState>();
        states.add(ps);
        pp.setStates(states);
        tdm.getProgramWorkflowService().savePatientProgram(pp);
        return ps;
    }

    protected Obs voidObs(Obs o) {
        return tdm.getObsService().voidObs(o, "Testing");
    }

    protected void assertBothNullOrEqual(Object o1, Object o2) {
        if (o1 == null) {
            Assert.assertNull(o2);
        }
        else if (o2 == null) {
            Assert.assertNull(o1);
        }
        else {
            Assert.assertEquals(o1, o2);
        }
    }
}
