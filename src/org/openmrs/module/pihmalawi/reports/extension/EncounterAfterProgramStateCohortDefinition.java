package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class EncounterAfterProgramStateCohortDefinition extends
		BaseCohortDefinition {

	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(required = true)
	private Date onDate;

	@ConfigurationProperty(required = true)
	private Location enrollmentLocation;

	@ConfigurationProperty(required = true)
	private List<Location> clinicLocations;

	@ConfigurationProperty(required = true)
	private List<EncounterType> encounterTypesAfterChangeToTerminalState;

	@ConfigurationProperty(required = true)
	private List<ProgramWorkflowState> terminalStates;


	public EncounterAfterProgramStateCohortDefinition() {
		super();
	}

	public Location getEnrollmentLocation() {
		return enrollmentLocation;
	}

	public void setEnrollmentLocation(Location enrollmentLocation) {
		this.enrollmentLocation = enrollmentLocation;
	}

	public List<Location> getClinicLocations() {
		return clinicLocations;
	}

	public void setClinicLocations(List<Location> clinicLocations) {
		this.clinicLocations = clinicLocations;
	}

	public List<EncounterType> getEncounterTypesAfterChangeToTerminalState() {
		return encounterTypesAfterChangeToTerminalState;
	}

	public void setEncounterTypesAfterChangeToTerminalState(
			List<EncounterType> encounterTypesAfterChangeToTerminalState) {
		this.encounterTypesAfterChangeToTerminalState = encounterTypesAfterChangeToTerminalState;
	}

	public List<ProgramWorkflowState> getTerminalStates() {
		return terminalStates;
	}

	public void setTerminalStates(List<ProgramWorkflowState> terminalStates) {
		this.terminalStates = terminalStates;
	}

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}
}