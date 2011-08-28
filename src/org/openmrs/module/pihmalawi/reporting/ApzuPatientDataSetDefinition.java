package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class ApzuPatientDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	@ConfigurationProperty
	PatientIdentifierType patientIdentifierType = null;
	
	@ConfigurationProperty
	Collection<EncounterType> encounterTypes = null;
	
	@ConfigurationProperty
	boolean includeDefaulterActionTaken = false;
	
	@ConfigurationProperty
	boolean includeMissedAppointmentColumns = true;

	@ConfigurationProperty
	boolean includeFirstVisit = false;
	
	@ConfigurationProperty
	boolean includeProgramOutcome = false;
	
	@ConfigurationProperty
	boolean includeWeight = false;
	
	@ConfigurationProperty
	boolean includeMostRecentVitals = false;
	
	@ConfigurationProperty
	boolean includeChronicCareDiagnosis = false;
	
	@ConfigurationProperty
	boolean includeProgramEnrollments = false;
	
	@ConfigurationProperty
	ProgramWorkflow programWorkflow = null;

	@ConfigurationProperty
	private boolean includeArvNumber;
	
	public boolean getIncludeDefaulterActionTaken() {
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
	
	public void setIncludeMissedAppointmentColumns(boolean b) {
		this.includeMissedAppointmentColumns = b;
	}
	
	public boolean getIncludeMissedAppointmentColumns() {
		return this.includeMissedAppointmentColumns;
	}
	
	public Collection<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}
	
	public List<EncounterType> getEncounterTypesAsList() {
		List<EncounterType> l = new ArrayList<EncounterType>();
		for (EncounterType et : getEncounterTypes()) {
			l.add(et);
		}
		return l;
	}
	
	public void setEncounterTypes(Collection<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}

	public void setIncludeFirstVisit(boolean b) {
		this.includeFirstVisit = b;
	}
	
	public boolean getIncludeFirstVisit() {
		return this.includeFirstVisit;
	}

	public boolean getIncludeProgramOutcome() {
		return includeProgramOutcome;
	}
	
	public void setIncludeProgramOutcome(boolean b) {
		includeProgramOutcome = b;
	}

	public boolean getIncludeWeight() {
		return includeWeight;
	}
	
	public void setIncludeWeight(boolean b) {
		includeWeight = b;
	}

	public boolean getIncludeMostRecentVitals() {
		return includeMostRecentVitals;
	}

	public void setIncludeMostRecentVitals(boolean includeMostRecentVitals) {
		this.includeMostRecentVitals = includeMostRecentVitals;
	}

	public ProgramWorkflow getProgramWorkflow() {
		return programWorkflow;
	}

	public void setProgramWorkflow(ProgramWorkflow programWorkflow) {
		this.programWorkflow = programWorkflow;
	}

	public boolean getIncludeChronicCareDiagnosis() {
		return includeChronicCareDiagnosis;
	}

	public void setIncludeChronicCareDiagnosis(boolean includeChronicCareDiagnosis) {
		this.includeChronicCareDiagnosis = includeChronicCareDiagnosis;
	}

	public boolean getIncludeProgramEnrollments() {
		return includeProgramEnrollments;
	}

	public void setIncludeProgramEnrollments(boolean includeProgramEnrollments) {
		this.includeProgramEnrollments = includeProgramEnrollments;
	}

	public void setIncludeArvNumber(boolean b) {
		this.includeArvNumber = b;
	}

	public boolean getIncludeArvNumber() {
		return includeArvNumber;
	}
}
