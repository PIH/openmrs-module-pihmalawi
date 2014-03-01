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
@Localized("pihmalawi.InStateAtLocationCohortDefinition")
public class InStateAtLocationCohortDefinition extends BaseCohortDefinition {
	
	// why is this not working?
	//	extends InStateCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required = true, group = "stateGroup")
	private ProgramWorkflowState state;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrBefore;
	
	@ConfigurationProperty(group = "onDateGroup")
	private Date onDate;
	
	@ConfigurationProperty(required = true, group = "locationGroup")
	private Location location;
	
	public InStateAtLocationCohortDefinition() {
		super();
	}
	
	public ProgramWorkflowState getState() {
		return state;
	}
	
	public void setState(ProgramWorkflowState state) {
		this.state = state;
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
	
	public Date getOnDate() {
		return onDate;
	}
	
	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
}
