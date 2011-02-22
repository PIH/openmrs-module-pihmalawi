package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.pihmalawi.reporting.BeforeAfter;
import org.openmrs.module.pihmalawi.reporting.Event;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class StateRelativeToStateCohortDefinition  extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrBefore;
	
	@ConfigurationProperty(group = "onDateGroup")
	private Date onDate;
	
	@ConfigurationProperty(required = true)
	private Integer offsetAmount;
	
	@ConfigurationProperty(required = false)
	private DurationUnit offsetUnit = DurationUnit.MONTHS;

	@ConfigurationProperty(required = false)
	private Location relativeStateLocation = null;

	@ConfigurationProperty(required = false)
	private Location primaryStateLocation = null;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState relativeState;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState primaryState;
	
	@ConfigurationProperty(required = false)
	private Event primaryStateEvent = Event.STARTED;
	
	@ConfigurationProperty(required = false)
	private Event relativeStateEvent = Event.STARTED;
	
	@ConfigurationProperty(required = false)
	private BeforeAfter beforeAfter = BeforeAfter.AFTER;
	
	public StateRelativeToStateCohortDefinition() {
		super();
	}

	public Location getRelativeStateLocation() {
		return relativeStateLocation;
	}

	public void setRelativeStateLocation(Location relativeStateLocation) {
		this.relativeStateLocation = relativeStateLocation;
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
	
	public Integer getOffsetAmount() {
		return offsetAmount;
	}

	public void setOffsetAmount(Integer offsetAmount) {
		this.offsetAmount = offsetAmount;
	}
	
	public DurationUnit getOffsetUnit() {
		return offsetUnit;
	}

	public void setOffsetUnit(DurationUnit offsetUnit) {
		this.offsetUnit = offsetUnit;
	}
	
	public Event getPrimaryStateEvent() {
		return primaryStateEvent;
	}

	public void setPrimaryStateEvent(Event primaryStateEvent) {
		this.primaryStateEvent = primaryStateEvent;
	}
	
	public Event getRelativeStateEvent() {
		return relativeStateEvent;
	}

	public void setRelativeStateEvent(Event relativeStateEvent) {
		this.relativeStateEvent = relativeStateEvent;
	}
	
	public BeforeAfter getBeforeAfter() {
		return beforeAfter;
	}

	public void setBeforeAfter(BeforeAfter beforeAfter) {
		this.beforeAfter = beforeAfter;
	}
}
