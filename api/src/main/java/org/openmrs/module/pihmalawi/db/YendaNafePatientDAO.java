package org.openmrs.module.pihmalawi.db;

import org.openmrs.PatientIdentifier;

/**
 * This define the contract for CRUD requests related to YendaNafe Patient data
 */
public interface YendaNafePatientDAO {
    public PatientIdentifier getPatientIdentifierByYendaNafeUuid(String uuid);
}
