package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("pihmalawi.PatientStateAtLocationCohortDefinition")
public class PatientStateAtLocationCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	@ConfigurationProperty(required=true, group="statesGroup")
	private ProgramWorkflowState state;
	
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
	
	public PatientStateAtLocationCohortDefinition() {
		super();
	}

    public ProgramWorkflowState getState() {
    	return state;
    }
	
    public void setState(ProgramWorkflowState state) {
    	this.state = state;
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
    
}
