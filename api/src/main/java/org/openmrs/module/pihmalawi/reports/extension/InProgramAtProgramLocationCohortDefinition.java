package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("pihmalawi.InProgramAtProgramLocationCohortDefinition")
public class InProgramAtProgramLocationCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required = true, group = "programsGroup")
	private List<Program> programs;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrAfter;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date onOrBefore;
	
	@ConfigurationProperty(group = "onDateGroup")
	private Date onDate;
	
	@ConfigurationProperty(required = true, group = "locationGroup")
	private Location location;
	
	public InProgramAtProgramLocationCohortDefinition() {
		super();
	}
	
	public List<Program> getPrograms() {
		return programs;
	}
	
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
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
