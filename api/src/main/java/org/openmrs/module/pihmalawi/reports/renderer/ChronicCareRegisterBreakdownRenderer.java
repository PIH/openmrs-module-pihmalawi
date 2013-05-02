package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.*;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class ChronicCareRegisterBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,PatientIdentifierType patientIdentifierType,Location locationParameter, Date startDateParameter,Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		// exception handling looks really ugly, but its necessary...
		try {
			addCol(row, "National ID", patientLink(p, patientIdentifierType, locationParameter));
		}
		catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All National IDs", identifiers(p, patientIdentifierType));
		}
		catch (Exception e) {
			log.error(e);
		}

		addFirstEncounterCols(row, p,lookupEncounterType("CHRONIC_CARE_INITIAL"),"Chronic Care initial", endDateParameter);
		addDemographicCols(row, p, endDateParameter);
		addDemographicTaDistrictCols(row, p, endDateParameter);
		addOutcomeCols(row,p,locationParameter,endDateParameter,lookupProgramWorkflow("Chronic care program","Chronic care treatment status"));

		String[] diagnoses = {"Asthma","Diabetes","Epilepsy","Heart failure","Hypertension"};
		for (int i=0; i<diagnoses.length; i++) {
			String d = diagnoses[i];
			String columnName = d + " Diagnosis from Initial Visit";
			DataSetColumn column = new DataSetColumn(columnName, columnName, Boolean.class);
			Object value = getFirstObsValue(p, "CHRONIC CARE DIAGNOSIS", d, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}

		String[] diagnosisAges = {"Age of asthma diagnosis","Age of diabetes diagnosis","Age of epilepsy diagnosis","Age of heart failure diagnosis","Age of hypertension diagnosis"};
		for (int i=0; i<diagnosisAges.length; i++) {
			String question = diagnosisAges[i];
			DataSetColumn column = new DataSetColumn(question + " from Initial Visit", question, Boolean.class);
			Object value = getFirstObsValue(p, question, null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}

		addVhwCol(row, p);
		addLastVisitCols(row, p, Arrays.asList(lookupEncounterType("CHRONIC_CARE_FOLLOWUP")), "");
		addAllEnrollmentsCol(row, p);
		addMostRecentVitalsCols(row, p, endDateParameter);

		{
			DataSetColumn column = new DataSetColumn("HIV Status at Initial Visit", "HIV Status at Initial Visit", String.class);
			Object value = getFirstObsValue(p, "HIV status", null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("On ART at Initial Visit", "On ART at Initial Visit", String.class);
			Object value = getFirstObsValue(p, "On ART", null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("TB Status at Initial Visit", "TB Status at Initial Visit", String.class);
			Object value = getFirstObsValue(p, "Previous or current tuberculosis treatment", null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Clinic travel time in hours", "Clinic travel time in hours", Integer.class);
			Object value = getFirstObsValue(p, "Clinic travel time in hours", null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Clinic travel time in minutes", "Clinic travel time in minutes", Integer.class);
			Object value = getFirstObsValue(p, "Clinic travel time in minutes", null, "CHRONIC_CARE_INITIAL");
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("High risk", "High risk", String.class);
			Object value = getFirstObsValue(p, "High risk patient", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Building type", "Building type", String.class);
			Object value = getFirstObsValue(p, "Wall material", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Roof type", "Roof type", String.class);
			Object value = getFirstObsValue(p, "Roof material", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Electricity", "Electricity", String.class);
			Object value = getFirstObsValue(p, "Home electricity", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Radio", "Radio", String.class);
			Object value = getFirstObsValue(p, "Patient owns radio", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Bicycle", "Bicycle", String.class);
			Object value = getFirstObsValue(p, "Access to bicycle", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Cooking source", "Cooking source", String.class);
			Object value = getFirstObsValue(p, "Location of cooking", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Fuel source", "Fuel source", String.class);
			Object value = getFirstObsValue(p, "Fuel source", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Num fruit veg per day", "Num fruit veg per day", String.class);
			Object value = getFirstObsValue(p, "Number of servings of fruits and vegetables consumed per day", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Smoking status", "Smoking status", String.class);
			Object value = getFirstObsValue(p, "Smoking history", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Num cigarrettes per day", "Num cigarrettes per day", String.class);
			Object value = getFirstObsValue(p, "Number of cigarettes smoked per day", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("History of alcohol use", "History of alcohol use", String.class);
			Object value = getFirstObsValue(p, "History of alcohol use", null, null);
			row.addColumnValue(column, value);
		}
		{
			DataSetColumn column = new DataSetColumn("Liters alcohol per day", "Liters alcohol per day", String.class);
			Object value = getFirstObsValue(p, "Liters per day", null, null);
			row.addColumnValue(column, value);
		}

		return row;
	}

	public Object getFirstObsValue(Person p, String conceptName, String answerConceptName, String encounterTypeName) {
		Concept question = lookupConcept(conceptName);
		List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(p, question);
		if (!obs.isEmpty()) {
			Concept a = (answerConceptName != null ? lookupConcept(answerConceptName) : null);
			EncounterType et = (encounterTypeName != null ? lookupEncounterType(encounterTypeName) : null);
			for (Obs o : obs) {
				if (a == null || a.equals(o.getValueCoded())) {
					if (et == null || et.equals(o.getEncounter().getEncounterType())) {
						if (!"BIT".equals(question.getDatatype().getHl7Abbreviation()) && o.getValueNumeric() != null) {
							return o.getValueNumeric();
						}
						else if (a != null && a.equals(o.getValueCoded())) {
							return Boolean.TRUE;
						}
						else {
							return o.getValueAsString(Locale.ENGLISH);
						}
					}
				}
			}
		}
		return null;
	}

}
