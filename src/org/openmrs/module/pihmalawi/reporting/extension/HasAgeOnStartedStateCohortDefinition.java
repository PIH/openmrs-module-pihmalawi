package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class HasAgeOnStartedStateCohortDefinition  extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date startedOnOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date startedOnOrBefore;
	
	@ConfigurationProperty(required = false)
	private Integer minAge;
	
	@ConfigurationProperty(required = false)
	private DurationUnit minAgeUnit = DurationUnit.YEARS;
	
	@ConfigurationProperty(required = false)
	private Integer maxAge;
	
	@ConfigurationProperty(required = false)
	private DurationUnit maxAgeUnit = DurationUnit.YEARS;

	@ConfigurationProperty(required = false)
	private Location location = null;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState state;
	
	public HasAgeOnStartedStateCohortDefinition() {
		super();
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
	
	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	
	public DurationUnit getMinAgeUnit() {
		return minAgeUnit;
	}

	public void setMinAgeUnit(DurationUnit minAgeUnit) {
		this.minAgeUnit = minAgeUnit;
	}
	
	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	
	public DurationUnit getMaxAgeUnit() {
		return maxAgeUnit;
	}

	public void setMaxAgeUnit(DurationUnit maxAgeUnit) {
		this.maxAgeUnit = maxAgeUnit;
	}
}