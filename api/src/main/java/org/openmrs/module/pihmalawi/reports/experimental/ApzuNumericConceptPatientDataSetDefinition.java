package org.openmrs.module.pihmalawi.reports.experimental;

import org.openmrs.Concept;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


public class ApzuNumericConceptPatientDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	@ConfigurationProperty
	PatientIdentifierType patientIdentifierType = null;

	@ConfigurationProperty
	Concept numericConcept = null;
	
	public ApzuNumericConceptPatientDataSetDefinition() {
		super();
	}
	
	public ApzuNumericConceptPatientDataSetDefinition(String name, String description) {
		super(name, description);
	}


	public PatientIdentifierType getPatientIdentifierType() {
		return patientIdentifierType;
	}

	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}

	public Concept getNumericConcept() {
		return numericConcept;
	}

	public void setNumericConcept(Concept numericConcept) {
		this.numericConcept = numericConcept;
	}
}
