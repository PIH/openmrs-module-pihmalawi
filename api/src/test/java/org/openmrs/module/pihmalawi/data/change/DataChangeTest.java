/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.data.change;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.PatientBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Contains tests for testing the core functionality of the module
 */
@SuppressWarnings("deprecation")
public class DataChangeTest extends BaseModuleContextSensitiveTest {

    @Autowired
    TestDataManager tdm;

    @Autowired
    PatientService patientService;

    @Autowired
    LocationService locationService;

    @Autowired
    TestDataTransactionHandler th;

    @Before
    public void initializeTestData() {
        th.getTransactions().clear();
    }


	@Test
	@NotTransactional
	public void shouldRecordPatientInserts() {
	    th.getTransactions().isEmpty();
	    createTestPatient();
        assertThat(th.getTransactions().size(), is(1));
        DataTransaction dtx = th.getTransactions().get(0);
        assertThat(dtx.getChanges().size(), is(3));
        assertChangeType(dtx, Patient.class, 1);
        assertChangeType(dtx, PersonName.class, 1);
        assertChangeType(dtx, PatientIdentifier.class, 1);
	}

    @Test
    @NotTransactional
    public void shouldRecordPatientUpdates() {
        th.getTransactions().isEmpty();
        Patient p = createTestPatient();
        assertThat(th.getTransactions().size(), is(1));
        p.setGender("F");
        p.getNames().iterator().next().setMiddleName("Middle");
        patientService.savePatient(p);
        assertThat(th.getTransactions().size(), is(2));
        DataTransaction dtx = th.getTransactions().get(1);
        assertThat(dtx.getChanges().size(), is(2));
        assertChangeType(dtx, Patient.class, 1);
        assertChangeType(dtx, PersonName.class, 1);
    }

	protected Patient createTestPatient() {
        PatientBuilder pb = tdm.patient().name("First", "Last").birthdate("1984-02-15").male();
	    pb.identifier(getOldIdNumberType(), "12345", getUnknownLocation());
	    return pb.save();
    }

    private void assertChangeType(DataTransaction dtx, Class<? extends OpenmrsObject> type, int num) {
	    int numFound = 0;
	    for (DataChange c : dtx.getChanges()) {
	        if (c.getObjectType().equals(type)) {
	            numFound++;
            }
        }
        assertThat(numFound, is(num));
    }

    protected PatientIdentifierType getOldIdNumberType() {
	    return patientService.getPatientIdentifierType(2);
    }

    protected Location getUnknownLocation() {
        return locationService.getLocation(1);
    }
}
