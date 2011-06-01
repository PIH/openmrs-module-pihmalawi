package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class ObsAfterStateStartCohortDefinition  extends BaseCohortDefinition {

private static final long serialVersionUID = 1L;

@ConfigurationProperty(required=true, group="questionGroup")
private TimeModifier timeModifier;

@ConfigurationProperty(required=true, group="questionGroup")
private Concept question;

@ConfigurationProperty(group="otherGroup")
private List<Location> locationList;

@ConfigurationProperty(group="otherGroup")
private List<EncounterType> encounterTypeList;

@ConfigurationProperty(group="startedDate")
private Date startedOnOrAfter;

@ConfigurationProperty(group="startedDate")
private Date startedOnOrBefore;

@ConfigurationProperty(group="endedDate")
private Date endedOnOrAfter;

@ConfigurationProperty(group="endedDate")
private Date endedOnOrBefore;

@ConfigurationProperty(required = true, group = "locationGroup")
private Location location;
	
	@ConfigurationProperty(group="constraint")
	SetComparator operator;
	
	@ConfigurationProperty(group="constraint")
	List<Concept> valueList;
	
	@ConfigurationProperty(required = true)
	private ProgramWorkflowState state;
	
	public ObsAfterStateStartCohortDefinition() { }

	
    public TimeModifier getTimeModifier() {
		return timeModifier;
	}


	public void setTimeModifier(TimeModifier timeModifier) {
		this.timeModifier = timeModifier;
	}


	public Concept getQuestion() {
		return question;
	}


	public void setQuestion(Concept question) {
		this.question = question;
	}


	public List<Location> getLocationList() {
		return locationList;
	}


	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}


	public List<EncounterType> getEncounterTypeList() {
		return encounterTypeList;
	}


	public void setEncounterTypeList(List<EncounterType> encounterTypeList) {
		this.encounterTypeList = encounterTypeList;
	}


	public Date getStartedOnOrAfter() {
		return startedOnOrAfter;
	}


	public void setStartedOnOrAfter(Date startedOnOrAfter) {
		this.startedOnOrAfter = startedOnOrAfter;
	}


	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}


	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}


	public Date getEndedOnOrAfter() {
		return endedOnOrAfter;
	}


	public void setEndedOnOrAfter(Date endedOnOrAfter) {
		this.endedOnOrAfter = endedOnOrAfter;
	}


	public Date getEndedOnOrBefore() {
		return endedOnOrBefore;
	}


	public void setEndedOnOrBefore(Date endedOnOrBefore) {
		this.endedOnOrBefore = endedOnOrBefore;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	/**
     * @return the operator
     */
    public SetComparator getOperator() {
    	return operator;
    }

	
    /**
     * @param operator the operator to set
     */
    public void setOperator(SetComparator operator) {
    	this.operator = operator;
    }

	
    /**
     * @return the valueList
     */
    public List<Concept> getValueList() {
    	return valueList;
    }

	
    /**
     * @param valueList the valueList to set
     */
    public void setValueList(List<Concept> valueList) {
    	this.valueList = valueList;
    }


	public ProgramWorkflowState getState() {
		return state;
	}


	public void setState(ProgramWorkflowState state) {
		this.state = state;
	}
}
