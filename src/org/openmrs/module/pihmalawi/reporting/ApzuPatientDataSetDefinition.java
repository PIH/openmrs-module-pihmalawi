package org.openmrs.module.pihmalawi.reporting;

import java.util.Collection;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;

public class ApzuPatientDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	PatientIdentifierType patientIdentifierType = null;
	
	Collection<EncounterType> encounterTypes = null;
	
	boolean includeDefaulterActionTaken = false;
	
	boolean includeMissedAppointmentColumns = true;
	
	public boolean isIncludeDefaulterActionTaken() {
		return includeDefaulterActionTaken;
	}
	
	public void setIncludeDefaulterActionTaken(boolean includeDefaulterActionTaken) {
		this.includeDefaulterActionTaken = includeDefaulterActionTaken;
	}
	
	public ApzuPatientDataSetDefinition() {
		super();
	}
	
	public ApzuPatientDataSetDefinition(String name, String description) {
		super(name, description);
	}
	
	public PatientIdentifierType getPatientIdentifierType() {
		return patientIdentifierType;
	}
	
	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}
	
	public void setIncludeMissedappointmentColumns(boolean b) {
		this.includeMissedAppointmentColumns = b;
	}
	
	public boolean isIncludeMissedappointmentColumns() {
		return this.includeMissedAppointmentColumns;
	}
	
	public Collection<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}
	
	public void setEncounterTypes(Collection<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}
}
