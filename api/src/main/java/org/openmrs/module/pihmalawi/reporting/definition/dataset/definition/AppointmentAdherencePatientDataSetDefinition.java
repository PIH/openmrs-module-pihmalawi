package org.openmrs.module.pihmalawi.reporting.definition.dataset.definition;

import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class AppointmentAdherencePatientDataSetDefinition extends BaseDataSetDefinition {

	private static final long serialVersionUID = 6405583324151111487L;

	@ConfigurationProperty
	PatientIdentifierType patientIdentifierType = null;

	@ConfigurationProperty
	List<EncounterType> encounterTypes = null;

	public AppointmentAdherencePatientDataSetDefinition() {
		super();
	}

	public AppointmentAdherencePatientDataSetDefinition(String name,
			String description) {
		super(name, description);
	}

	public PatientIdentifierType getPatientIdentifierType() {
		return patientIdentifierType;
	}

	public void setPatientIdentifierType(
			PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}

	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}
}
