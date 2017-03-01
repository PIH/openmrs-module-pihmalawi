/*
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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.common.TraceCriteria;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.TraceCriteriaPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.TraceDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Handler(supports={TraceDataSetDefinition.class})
public class TraceDataSetEvaluator implements DataSetEvaluator {

	@Autowired
	private DataFactory df;

    @Autowired
    private HivMetadata hivMetadata;

	@Autowired
	private ChronicCareMetadata ccMetadata;

    @Autowired
    private HivPatientDataLibrary hivPatientData;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData;

	@Autowired
	private PatientDataService patientDataService;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData ;

	@Autowired
	private BasePatientDataLibrary basePatientData ;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

	    TraceDataSetDefinition dsd = (TraceDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

        boolean phase1Only = dsd.getTraceType().isPhase1Only();

        TraceCriteriaPatientDataDefinition pdd = new TraceCriteriaPatientDataDefinition();
        pdd.setLocation(dsd.getLocation());
        pdd.setTraceType(dsd.getTraceType());

        PatientData tracePatientData = patientDataService.evaluate(pdd, context);
        Cohort traceCohort = new Cohort(tracePatientData.getData().keySet());

        EvaluationContext newContext = new EvaluationContext(context);
        newContext.setParameterValues(new HashMap<String, Object>());
        newContext.addParameterValue(ReportingConstants.LOCATION_PARAMETER.getName(), dsd.getLocation());
        newContext.setBaseCohort(traceCohort);

        Map<String, PatientDataDefinition> columnData = new LinkedHashMap<String, PatientDataDefinition>();
        columnData.put("ARV_NUMBER", hivPatientData.getArvNumberAtLocation());
        columnData.put("HCC_NUMBER", hivPatientData.getHccNumberAtLocation());
        columnData.put("FIRST_NAME", builtInPatientData.getPreferredGivenName());
        columnData.put("LAST_NAME", builtInPatientData.getPreferredFamilyName());
        columnData.put("VILLAGE", basePatientData.getVillage());
        columnData.put("VHW", basePatientData.getChw());
        columnData.put("LAST_VISIT_DATE", getAppointmentStatusData(phase1Only, "lastEncounterDate"));
        columnData.put("NEXT_APPT_DATE", getAppointmentStatusData(phase1Only, "nextScheduledDate"));
        columnData.put("WEEKS_OUT_OF_CARE", getAppointmentStatusData(phase1Only, "weeksOutOfCare"));
        columnData.put("TRACE_CRITERIA", pdd);
        columnData.put("PRIORITY_PATIENT", basePatientData.getPriorityPatientForTrace());

        if (!phase1Only) {
            columnData.put("NCD_NUMBER", ccPatientData.getChronicCareNumberAtLocation());
            columnData.put("DIAGNOSES", basePatientData.getDiagnosesBasedOnMastercards());
        }

        for (String columnName : columnData.keySet()) {
            DataSetColumn column = new DataSetColumn(columnName, columnName, Object.class);
            PatientDataDefinition patientDataDefinition = columnData.get(columnName);
            EvaluatedPatientData data = Context.getService(PatientDataService.class).evaluate(patientDataDefinition, newContext);
            addColumnData(dataSet, traceCohort, column, data);
        }

        addColumnData(dataSet, traceCohort, new DataSetColumn("TRACE_CRITERIA", "TRACE_CRITERIA", TraceCriteria.class), tracePatientData);

        SortCriteria sortCriteria = new SortCriteria();
        sortCriteria.addSortElement("VHW", SortCriteria.SortDirection.ASC);
        sortCriteria.addSortElement("VILLAGE", SortCriteria.SortDirection.ASC);
        sortCriteria.addSortElement("LAST_NAME", SortCriteria.SortDirection.ASC);
        dataSet.setSortCriteria(sortCriteria);

		return dataSet;
	}

	protected void addColumnData(SimpleDataSet dataSet, Cohort cohort, DataSetColumn column, PatientData data) {
        for (Integer id : cohort.getMemberIds()) {
            Object val = data.getData().get(id);
            dataSet.addColumnValue(id, column, val);
        }
    }

    /**
     * Return a definition representing a property of the patient's appointment status in IC3
     * If the phase1Only flag is set, limit this only to HIV enrolled patients and HIV visits
     */
    public PatientDataDefinition getAppointmentStatusData(boolean phase1Only, String property) {
        PatientDataDefinition pdd = df.getAppointmentStatus(getIC3ActiveStates(), getIC3EncounterTypes());
        if (phase1Only) {
            pdd = df.getAppointmentStatus(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes());
        }
        return df.convert(pdd, new PropertyConverter(AppointmentInfo.class, property));
    }

    /**
     * @return list of all active states a patient could be in to be considered active in IC3
     */
    public List<ProgramWorkflowState> getIC3ActiveStates() {
        List<ProgramWorkflowState> allStates = new ArrayList<ProgramWorkflowState>(hivMetadata.getActiveHivStates());
        allStates.addAll(ccMetadata.getActiveChronicCareStates());
        return allStates;
    }

    /**
     * @return list of all encounter types a patient could make and attend scheduled IC3 visits
     */
    public List<EncounterType> getIC3EncounterTypes() {
        List<EncounterType> allEncounterTypes = new ArrayList<EncounterType>(hivMetadata.getHivEncounterTypes());
        allEncounterTypes.addAll(ccMetadata.getChronicCareScheduledVisitEncounterTypes());
        return allEncounterTypes;
    }
}
