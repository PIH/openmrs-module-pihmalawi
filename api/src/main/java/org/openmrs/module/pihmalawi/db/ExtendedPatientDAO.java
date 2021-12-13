package org.openmrs.module.pihmalawi.db;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;

import java.util.Date;
import java.util.List;

/**
 * This define the contract for CRUD requests related to YendaNafe Patient data
 */
public interface ExtendedPatientDAO {
    public PatientIdentifier getPatientIdentifierByIdentifier(String uuid);

    public List<Patient> getPatientsByChangedDate(Date dateChanged);
}
