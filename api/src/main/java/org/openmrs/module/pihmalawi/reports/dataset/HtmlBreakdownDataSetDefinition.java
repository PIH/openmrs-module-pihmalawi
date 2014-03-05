package org.openmrs.module.pihmalawi.reports.dataset;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class HtmlBreakdownDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405584324151111487L;
	
	@ConfigurationProperty
	String htmlBreakdownPatientRowClassname = null;
	
	@ConfigurationProperty
	PatientIdentifierType patientIdentifierType = null;
	
	public HtmlBreakdownDataSetDefinition() {
		super();
		addParameter(ReportingConstants.START_DATE_PARAMETER);
		addParameter(ReportingConstants.END_DATE_PARAMETER);
		addParameter(ReportingConstants.LOCATION_PARAMETER);
	}
	
	public HtmlBreakdownDataSetDefinition(String name, String description) {
		super(name, description);
	}

	public String getHtmlBreakdownPatientRowClassname() {
		return htmlBreakdownPatientRowClassname;
	}

	public void setHtmlBreakdownPatientRowClassname(
			String htmlBreakdownPatientRowClassname) {
		this.htmlBreakdownPatientRowClassname = htmlBreakdownPatientRowClassname;
	}

	public PatientIdentifierType getPatientIdentifierType() {
		return patientIdentifierType;
	}

	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
		this.patientIdentifierType = patientIdentifierType;
	}
}
