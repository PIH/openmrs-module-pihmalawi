package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("pihmalawi.AppointmentAdherenceCohortDefinition")
public class AppointmentAdherenceCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required = true, group = "adherenceGroup")
	private Integer minimumAdherence;
	
	@ConfigurationProperty(required = true, group = "adherenceGroup")
	private Integer maximumAdherence;
	
	@ConfigurationProperty(required = true, group = "appointmentGroup")
	private List<EncounterType> encounterTypes;
	
	@ConfigurationProperty(required = true, group = "appointmentGroup")
	private Concept appointmentConcept;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date fromDate;
	
	@ConfigurationProperty(group = "dateRangeGroup")
	private Date toDate;
	
	public AppointmentAdherenceCohortDefinition() {
		super();
	}

	public Integer getMinimumAdherence() {
		return minimumAdherence;
	}

	public void setMinimumAdherence(Integer minimumAdherence) {
		this.minimumAdherence = minimumAdherence;
	}

	public Integer getMaximumAdherence() {
		return maximumAdherence;
	}

	public void setMaximumAdherence(Integer maximumAdherence) {
		this.maximumAdherence = maximumAdherence;
	}

	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}

	public Concept getAppointmentConcept() {
		return appointmentConcept;
	}

	public void setAppointmentConcept(Concept appointmentConcept) {
		this.appointmentConcept = appointmentConcept;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
