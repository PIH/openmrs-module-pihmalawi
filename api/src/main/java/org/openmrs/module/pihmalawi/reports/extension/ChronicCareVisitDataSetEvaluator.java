/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reports.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reports.PatientDataHelper;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the Chronic Care Visit DataSet
 */
@Handler(supports = { ChronicCareVisitDataSetDefinition.class })
public class ChronicCareVisitDataSetEvaluator implements DataSetEvaluator {

	protected static final Log log = LogFactory.getLog(ChronicCareVisitDataSetEvaluator.class);

	private static final String EMPTY = "";
	private ChronicCareMetadata ccMetadata = new ChronicCareMetadata();

	/**
	 * Public constructor
	 */
	public ChronicCareVisitDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

		ChronicCareVisitDataSetDefinition dsd = (ChronicCareVisitDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);


		List<EncounterType> types = ccMetadata.getChronicCareEncounterTypes();

		EncounterService es = Context.getEncounterService();
		List<Encounter> encounters = es.getEncounters(null, dsd.getLocation(), dsd.getFromDate(), dsd.getToDate(), null, types, null, false);

		Map<Integer, Date> datesToInclude = new HashMap<Integer, Date>();
		if (dsd.getWhich() != null && dsd.getWhich() != TimeQualifier.ANY) {
			for (Encounter e : encounters) {
				Integer pId = e.getPatient().getPatientId();
				Date current = datesToInclude.get(pId);
				if (current == null) {
					datesToInclude.put(pId, e.getEncounterDatetime());
				}
				else {
					if (dsd.getWhich() == TimeQualifier.FIRST) {
						if (current.after(e.getEncounterDatetime())) {
							datesToInclude.put(pId, e.getEncounterDatetime());
						}
					}
					else {
						if (e.getEncounterDatetime().after(current)) {
							datesToInclude.put(pId, e.getEncounterDatetime());
						}
					}
				}
			}
		}

		Cohort patientsToInclude = null;
		if (dsd.getLimitedToPatientsEnrolledAtEnd()) {
			InProgramAtProgramLocationCohortDefinition cd = new InProgramAtProgramLocationCohortDefinition();
			cd.setOnDate(dsd.getToDate());
			cd.setPrograms(Arrays.asList(ccMetadata.getChronicCareProgram()));
			cd.setLocation(dsd.getLocation());
			patientsToInclude = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		}

		for (Encounter e : encounters) {
			Patient p = e.getPatient();
			Date encounterDate = e.getEncounterDatetime();

			Date dateToInclude = datesToInclude.get(p.getPatientId());
			if (dateToInclude == null || dateToInclude.equals(encounterDate)) {
				if (patientsToInclude == null || patientsToInclude.contains(p.getPatientId())) {

					DataSetRow row = new DataSetRow();

					PatientDataHelper pdh = new PatientDataHelper();

					row.addColumnValue(createColumn("ENCOUNTER_ID", Integer.class), e.getEncounterId());
					row.addColumnValue(createColumn("ENCOUNTER_DATETIME", Date.class), e.getEncounterDatetime());
					row.addColumnValue(createColumn("LOCATION", String.class), (e.getLocation() != null ? e.getLocation().getName() : EMPTY));
					row.addColumnValue(createColumn("ENCOUNTER_TYPE", String.class), e.getEncounterType().getName());

					row.addColumnValue(createColumn("NATIONAL ID", String.class), pdh.identifiers(p, "National ID", ", "));
                    row.addColumnValue(createColumn("CHRONIC CARE NUMBER", String.class), pdh.identifiers(p, "Chronic Care Number", ", "));
					row.addColumnValue(createColumn("INTERNAL_PATIENT_ID", Integer.class), p.getPatientId());
					row.addColumnValue(createColumn("FIRST NAME", String.class), pdh.getGivenName(p));
					row.addColumnValue(createColumn("LAST NAME", String.class), pdh.getFamilyName(p));

					row.addColumnValue(createColumn("Birthdate", Date.class), p.getBirthdate());
					row.addColumnValue(createColumn("Age at encounter (yr)", Integer.class), pdh.getAgeOnDate(p, encounterDate));
					row.addColumnValue(createColumn("Age at encounter (mth)", Integer.class), pdh.getAgeInMonthsOnDate(p, encounterDate));
					row.addColumnValue(createColumn("M/F", String.class), pdh.getGender(p));
					row.addColumnValue(createColumn("Village", String.class), pdh.getVillage(p));
					row.addColumnValue(createColumn("TA", String.class), pdh.getTraditionalAuthority(p));

					String[] drugs = {	"Salbutamol", "Beclomethasone",
							"Hydrochlorothiazide", "Captopril", "Amlodipine", "Enalapril",
							"Nifedipine", "Atenolol", "Lisinopril", "Propranolol",
							"Phenobarbital", "Phenytoin", "Carbamazepine",
							"Insulin", "Metformin", "Glibenclamide",
							"Furosemide", "Spironolactone"
					};

					for (String drug : drugs) {
						addObs(row, e, "Taking " + drug, "Current drugs used", drug);
					}
					addObs(row, e, "PREFERRED TREATMENT UNAVAILABLE", "Preferred treatment out of stock", null);
					addObs(row, e, "CHANGE IN TREATMENT", "Change in treatment", null);

					addObs(row, e, "HOSPITALIZED SINCE LAST VISIT", "Patient hospitalized since last visit", null);
					addObs(row, e, "HOSPITALIZED FOR NCD SINCE LAST VISIT", "Hospitalized for non-communicable disease since last visit", null);

					addObs(row, e, "ASTHMA DIAGNOSIS", "Chronic care diagnosis", "Asthma");
					addObs(row, e, "HYPERTENSION DIAGNOSIS", "Chronic care diagnosis", "Hypertension");
					addObs(row, e, "EPILEPSY DIAGNOSIS", "Chronic care diagnosis", "Epilepsy");
					addObs(row, e, "DIABETES DIAGNOSIS", "Chronic care diagnosis", "Diabetes");
					addObs(row, e, "HEART FAILURE DIAGNOSIS", "Chronic care diagnosis", "Heart failure");

					addObs(row, e, "HT", "Height (cm)", null);
					addObs(row, e, "WT", "Weight (kg)", null);
					addObs(row, e, "SBP", "Systolic blood pressure", null);
					addObs(row, e, "DBP", "Diastolic blood pressure", null);
					addObs(row, e, "CHF CLASSIFICATION", "Nyha class", null);
					addObs(row, e, "BLOOD SUGAR", "Serum glucose", null);
					addObs(row, e, "BLOOD SUGAR TEST TYPE", "Blood sugar test type", null);
					addObs(row, e, "NUMBER OF SEIZURES", "NUMBER OF SEIZURES", null);
					addObs(row, e, "PEAK FLOW", "PEAK FLOW", null);
					addObs(row, e, "PEAK FLOW PREDICTED", "PEAK FLOW PREDICTED", null);
					addObs(row, e, "ASTHMA CLASSIFICATION", "Asthma classification", null);
					addObs(row, e, "HIGH RISK PATIENT", "High risk patient", null);
					addObs(row, e, "VISIT FULLY COMPLETED", "Patient visit completed with all services delivered", null);
					addObs(row, e, "DATA CLERK COMMENTS", "Data clerk comments", null);
					addObs(row, e, "NEXT APPOINTMENT DATE", "Appointment date", null);

					dataSet.addRow(row);
				}
			}
		}

		System.out.println("Evaluated " + dataSet.getRows().size() + " rows");

		return dataSet;
		
	}

	public void addObs(DataSetRow row, Encounter e, String columnName, String conceptName, String answerName) {
		List<String> result = new ArrayList<String>();
		PatientDataHelper pdh = new PatientDataHelper();
		Concept question = ccMetadata.getConcept(conceptName);
		Concept answer = (ObjectUtil.isNull(answerName) ? null : ccMetadata.getConcept(answerName));
		for (Obs o : e.getAllObs()) {
			if (o.getConcept().equals(question)) {
				if (answer == null) {
					result.add(pdh.formatValue(o));
				}
				else if (answer.equals(o.getValueCoded())) {
					result.add("true");
				}
			}
		}
		row.addColumnValue(createColumn(columnName, String.class), OpenmrsUtil.join(result, ", "));
	}

	public DataSetColumn createColumn(String name, Class<?> type) {
		return new DataSetColumn(name, name, type);
	}
}