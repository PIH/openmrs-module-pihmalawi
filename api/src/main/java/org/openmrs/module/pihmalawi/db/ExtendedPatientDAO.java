package org.openmrs.module.pihmalawi.db;

import org.openmrs.PatientIdentifier;

/**
 * This define the contract for CRUD requests related to YendaNafe Patient data
 */
public interface ExtendedPatientDAO {
    public PatientIdentifier getPatientIdentifierByIdentifier(String uuid);
}
