package org.openmrs.module.pihmalawi;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.contrib.testdata.builder.ObsBuilder;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.metadata.Metadata;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

@SkipBaseSetup
public abstract class BaseMalawiTest extends BaseModuleContextSensitiveTest {

    public static final String METADATA_XML_FOLDER = "org/openmrs/module/pihmalawi/metadata";

    @Autowired
    protected TestDataManager tdm;

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

    @Before
    public void loadMetadata() throws Exception {

        initializeInMemoryDatabase();

        InputStream in = null;
        BufferedReader br = null;
        try {
            in = OpenmrsClassLoader.getInstance().getResourceAsStream(METADATA_XML_FOLDER);
            br = new BufferedReader(new InputStreamReader(in));
            for (String resource = br.readLine(); resource != null; resource = br.readLine()) {
                executeDataSet(METADATA_XML_FOLDER + "/" + resource);
            }
        }
        finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(in);
        }

        authenticate();

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
