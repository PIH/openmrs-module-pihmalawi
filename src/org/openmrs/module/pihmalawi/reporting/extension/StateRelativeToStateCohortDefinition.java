package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
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
	private Location firstStateLocation = null;

	@ConfigurationProperty(required = false)
	private Location secondStateLocation = null;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState firstState;

	@ConfigurationProperty(required = true)
	private ProgramWorkflowState secondState;
	
	public StateRelativeToStateCohortDefinition() {
		super();
	}

	public Location getFirstStateLocation() {
		return firstStateLocation;
	}

	public void setFirstStateLocation(Location firstStateLocation) {
		this.firstStateLocation = firstStateLocation;
	}
	
	public Location getSecondStateLocation() {
		return secondStateLocation;
	}

	public void setSecondStateLocation(Location secondStateLocation) {
		this.secondStateLocation = secondStateLocation;
	}
	
	public ProgramWorkflowState getFirstState() {
		return firstState;
	}

	public void setFirstState(ProgramWorkflowState firstState) {
		this.firstState = firstState;
	}
	
	public ProgramWorkflowState getSecondState() {
		return secondState;
	}

	public void setSecondState(ProgramWorkflowState secondState) {
		this.secondState = secondState;
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

	public void setOffsetUnit(DurationUnit offsetAmountUnit) {
		this.offsetUnit = offsetAmountUnit;
	}
}
