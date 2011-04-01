package org.openmrs.module.pihmalawi.reporting;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;

public class ArcGisPatientDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	PatientIdentifierType patientIdentifierType = null;
	
	public ArcGisPatientDataSetDefinition() {
		super();
	}
	
	public ArcGisPatientDataSetDefinition(String name, String description) {
		super(name, description);
	}
	
	public PatientIdentifierType getPatientIdentifierType() {
		return patientIdentifierType;
	}
	
	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}
}