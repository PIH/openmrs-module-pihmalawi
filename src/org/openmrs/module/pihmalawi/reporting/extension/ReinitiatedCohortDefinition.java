package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class ReinitiatedCohortDefinition  extends BaseCohortDefinition {

private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrBefore;
	
	@ConfigurationProperty(group="otherGroup")
	private List<Location> locationList;
	
	@ConfigurationProperty(group="which")
	private List<EncounterType> encounterTypeList;

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
}
