package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class InStateAtDateObsCohortDefinition extends BaseObsCohortDefinition {
private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrBefore;

	@ConfigurationProperty(required = false)
	private Location location = null;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState state;
	
	@ConfigurationProperty(required=true, group="questionGroup")
	private TimeModifier timeModifier;
	
	@ConfigurationProperty(required=true, group="questionGroup")
	private Concept question;
	
	@ConfigurationProperty(group="otherGroup")
	private List<EncounterType> encounterTypeList;
	
	public InStateAtDateObsCohortDefinition() {
		super();
	}
	
	public Date getOnOrAfter() {
		return onOrAfter;
	}
	
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}
	
	public Date getOnOrBefore() {
		return onOrBefore;
	}
	
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public ProgramWorkflowState getState() {
		return state;
	}

	public void setState(ProgramWorkflowState state) {
		this.state = state;
	}
	
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

    public List<EncounterType> getEncounterTypeList() {
    	return encounterTypeList;
    }

    public void isadocd(List<EncounterType> encounterTypeList) {
    	this.encounterTypeList = encounterTypeList;
    }
}
