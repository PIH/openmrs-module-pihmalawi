package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;

public class ApzuPatientDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;
	
	PatientIdentifierType patientIdentifierType = null;
	
	Collection<EncounterType> encounterTypes = null;
	
	boolean includeDefaulterActionTaken = false;
	
	boolean includeMissedAppointmentColumns = true;

	boolean includeFirstVisit = false;
	
	boolean includeProgramOutcome = false;
	
	boolean includeWeight = false;
	
	boolean includeMostRecentVitals = false;
	
	boolean includeChronicCareDiagnosis = false;
	
	boolean includeProgramEnrollments = false;
	
	Program program = null;
	
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

	public boolean isIncludeProgramOutcome() {
		return includeProgramOutcome;
	}
	
	public void setIncludeProgramOutcome(boolean b) {
		includeProgramOutcome = b;
	}

	public boolean isIncludeWeight() {
		return includeWeight;
	}
	
	public void setIncludeWeight(boolean b) {
		includeWeight = b;
	}

	public boolean isIncludeMostRecentVitals() {
		return includeMostRecentVitals;
	}

	public void setIncludeMostRecentVitals(boolean includeMostRecentVitals) {
		this.includeMostRecentVitals = includeMostRecentVitals;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public boolean isIncludeChronicCareDiagnosis() {
		return includeChronicCareDiagnosis;
	}

	public void setIncludeChronicCareDiagnosis(boolean includeChronicCareDiagnosis) {
		this.includeChronicCareDiagnosis = includeChronicCareDiagnosis;
	}

	public boolean isIncludeProgramEnrollments() {
		return includeProgramEnrollments;
	}

	public void setIncludeProgramEnrollments(boolean includeProgramEnrollments) {
		this.includeProgramEnrollments = includeProgramEnrollments;
	}
	
}
