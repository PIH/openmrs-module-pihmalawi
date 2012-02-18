package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class InStateAfterStartedStateCohortDefinition  extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group = "onDateGroup")
	private Date onDate;
	
	@ConfigurationProperty(required = true)
	private Integer offsetAmount;
	
	@ConfigurationProperty(required = false)
	private Integer offsetDuration = -1;
	
	@ConfigurationProperty(required = false)
	private boolean offsetWithin = true;
	
	@ConfigurationProperty(required = false)
	private DurationUnit offsetUnit = DurationUnit.MONTHS;

	@ConfigurationProperty(required = false)
	private Location primaryStateLocation = null;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState relativeState;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState primaryState;
	
	public InStateAfterStartedStateCohortDefinition() {
		super();
	}
	
	public Location getPrimaryStateLocation() {
		return primaryStateLocation;
	}

	public void setPrimaryStateLocation(Location primaryStateLocation) {
		this.primaryStateLocation = primaryStateLocation;
	}
	
	public ProgramWorkflowState getRelativeState() {
		return relativeState;
	}

	public void setRelativeState(ProgramWorkflowState relativeState) {
		this.relativeState = relativeState;
	}
	
	public ProgramWorkflowState getPrimaryState() {
		return primaryState;
	}

	public void setPrimaryState(ProgramWorkflowState primaryState) {
		this.primaryState = primaryState;
	}

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}
	
	public Integer getOffsetAmount() {
		return offsetAmount;
	}

	public void setOffsetAmount(Integer offsetAmount) {
		this.offsetAmount = offsetAmount;
	}
	
	public Integer getOffsetDuration() {
		return offsetDuration;
	}

	public void setOffsetDuration(Integer offsetDuration) {
		this.offsetDuration = offsetDuration;
	}
	
	public boolean getOffsetWithin() {
		return offsetWithin;
	}

	public void setOffsetWithin(boolean offsetWithin) {
		this.offsetWithin = offsetWithin;
	}
	
	public DurationUnit getOffsetUnit() {
		return offsetUnit;
	}

	public void setOffsetUnit(DurationUnit offsetUnit) {
		this.offsetUnit = offsetUnit;
	}
}
