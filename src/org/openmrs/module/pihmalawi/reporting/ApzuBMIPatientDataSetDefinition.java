package org.openmrs.module.pihmalawi.reporting;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


public class ApzuBMIPatientDataSetDefinition extends ApzuNumericConceptPatientDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	public ApzuBMIPatientDataSetDefinition() {
		super();
	}
	
	public ApzuBMIPatientDataSetDefinition(String name, String description) {
		super(name, description);
	}
}
