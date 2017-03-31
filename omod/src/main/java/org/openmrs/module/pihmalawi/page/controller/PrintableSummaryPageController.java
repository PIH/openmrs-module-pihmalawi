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
package org.openmrs.module.pihmalawi.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrintableSummaryPageController {

    protected static final Log log = LogFactory.getLog(PrintableSummaryPageController.class);

	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           UiUtils ui, PageModel model,
                           @SpringBean("builtInPatientDataLibrary") BuiltInPatientDataLibrary builtInData,
                           @SpringBean("basePatientDataLibrary") BasePatientDataLibrary baseData,
                           @SpringBean("hivPatientDataLibrary") HivPatientDataLibrary hivData,
                           @SpringBean("chronicCarePatientDataLibrary") ChronicCarePatientDataLibrary ccData,
                           @SpringBean("reportingDataSetDefinitionService") DataSetDefinitionService dataSetDefinitionService,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();

        add(dsd, "firstName", builtInData.getPreferredGivenName());
        add(dsd, "lastName", builtInData.getPreferredFamilyName());
        add(dsd, "birthDate", baseData.getBirthdate());
        add(dsd, "ageYears", baseData.getAgeAtEndInYears());
        add(dsd, "ageMonths", baseData.getAgeAtEndInMonths());
        add(dsd, "gender", builtInData.getGender());
        add(dsd, "hccNumber", hivData.getHccNumberAtLocation());
        add(dsd, "arvNumber", hivData.getArvNumberAtLocation());
        add(dsd, "ccNumber", ccData.getChronicCareNumberAtLocation());
        add(dsd, "hivTxStatus", hivData.getMostRecentHivTreatmentStatusStateByEndDate());
        add(dsd, "hivTxStatusDate", hivData.getMostRecentHivTreatmentStatusStateStartDateByEndDate());
        add(dsd, "ccTxStatus", ccData.getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate());
        add(dsd, "ccTxStatusDate", ccData.getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate());
        add(dsd, "artStartDate", hivData.getEarliestOnArvsStateStartDateByEndDate());
        add(dsd, "artRegimens", hivData.getArvRegimenChanges());
        add(dsd, "encounters", baseData.getAllEncounters());
        add(dsd, "hccAppointmentStatus", hivData.getHccAppointmentStatus());
        add(dsd, "artAppointmentStatus", hivData.getArtAppointmentStatus());
        add(dsd, "ccAppointmentStatus", ccData.getChronicCareAppointmentStatus());
        add(dsd, "cd4s", hivData.getCd4CountObservations());
        add(dsd, "viralLoads", hivData.getViralLoadObservations());
        add(dsd, "viralLdl", hivData.getLDLObservations());
        add(dsd, "tbStatus", hivData.getLatestTbStatusObs());
        add(dsd, "height", baseData.getLatestHeightObs());
        add(dsd, "weights", baseData.getAllWeightObservations());
        add(dsd, "reasonCd4", hivData.getFirstArtInitialCd4Count());
        add(dsd, "reasonCd4Pct", hivData.getFirstArtInitialCd4Percent());
        add(dsd, "reasonCd4Date", hivData.getFirstArtInitialCd4Date());
        add(dsd, "reasonKs", hivData.getFirstArtInitialKsSideEffectsWorsening());
        add(dsd, "reasonTb", hivData.getFirstArtInitialTbTreatmentStatus());
        add(dsd, "reasonStage", hivData.getFirstArtInitialWhoStage());
        add(dsd, "reasonPshd", hivData.getFirstArtInitialPresumedSevereHivPresent());
        add(dsd, "reasonConditions", hivData.getFirstArtInitialWhoClinicalConditions());
        add(dsd, "reasonPregnantLactating", hivData.getFirstArtInitialPregnantLactating());

        try {
            EvaluationContext context = getSinglePatientEvaluationContext(patient.getPatientId());
            SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, context);
            Map<String, Object> values = dataSet.getRows().get(0).getColumnValuesByKey();
            model.putAll(values);

            Obs ht = getValue(dataSet, "height", Obs.class);
            Obs wt = getLastValue(dataSet, "weights", Obs.class);
            model.put("lastWeight", wt);

            Obs oneYearWt = getValueAtLeastXMonthsBeforeLastValue(dataSet, "weights", 12);
            model.put("oneYearWeight", oneYearWt);

            model.put("bmi", "");
            model.put("bmiValue", null);
            if (ht != null && wt != null) {
                double bmi = wt.getValueNumeric()/Math.pow(ht.getValueNumeric()/100, 2);
                model.put("bmi", ObjectUtil.format(bmi, "1"));
                model.put("bmiValue", bmi);
            }

            String lastViralLoadValue = null;
            Date lastViralLoadDate = null;
            Boolean highViralLoad = false;

            Obs lastViralLoad = getLastValue(dataSet, "viralLoads", Obs.class);
            Obs lastViralLdl = getLastValue(dataSet, "viralLdl", Obs.class);

            if (lastViralLoad != null) {
                lastViralLoadValue = ObjectUtil.formatNumber(lastViralLoad.getValueNumeric(), "1", Context.getLocale());
                lastViralLoadDate = lastViralLoad.getObsDatetime();
                if (lastViralLoad.getValueNumeric() >= 1000) {
                    highViralLoad = true;
                }
            }
            if (lastViralLdl != null) {
                if (lastViralLoadDate == null || lastViralLoadDate.before(lastViralLdl.getObsDatetime())) {
                    lastViralLoadValue = "LDL";
                    lastViralLoadDate = lastViralLdl.getObsDatetime();
                }
            }
            model.put("lastViralLoadValue", lastViralLoadValue);
            model.put("lastViralLoadDate", lastViralLoadDate);
            model.put("highViralLoad", highViralLoad);

            Map<String, AppointmentInfo> appointmentStatuses = new LinkedHashMap<String, AppointmentInfo>();
            appointmentStatuses.put("HCC", getValue(dataSet, "hccAppointmentStatus", AppointmentInfo.class));
            appointmentStatuses.put("ART", getValue(dataSet,"artAppointmentStatus", AppointmentInfo.class));
            appointmentStatuses.put("CCC", getValue(dataSet,"ccAppointmentStatus", AppointmentInfo.class));
            model.put("appointmentStatuses", appointmentStatuses);
        }
        catch (Exception e) {
            model.addAttribute("errors", e.getMessage());
            log.error("An error occured while evaluating data for patient " + patient.getId() + " for printable summary", e);
        }
    }

    protected <T> T getValue(SimpleDataSet dataSet, String column, Class<T> type) {
        return (T)dataSet.getRows().get(0).getColumnValue(column);
    }

    protected <T> T getLastValue(SimpleDataSet dataSet, String column, Class<T> type) {
        List l = (List)dataSet.getRows().get(0).getColumnValue(column);
        if (l != null) {
            return (T)l.get(l.size()-1);
        }
        return null;
    }

    protected Obs getValueAtLeastXMonthsBeforeLastValue(SimpleDataSet dataSet, String column, int numMonths) {
        List<Obs> values = (List<Obs>)dataSet.getRows().get(0).getColumnValue(column);
        if (values != null && values.size() > 1) {
            Obs latestValue = values.get(values.size()-1);
            for (int i=values.size()-2; i>=0; i--) {
                Obs previousValue = values.get(i);
                if (DateUtil.monthsBetween(previousValue.getObsDatetime(), latestValue.getObsDatetime()) >= numMonths) {
                    return previousValue;
                }
            }
        }
        return null;
    }

    protected EvaluationContext getSinglePatientEvaluationContext(Integer patientId) {
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());
        context.getBaseCohort().addMember(patientId);
        return context;
    }

    protected void add(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd, DataConverter... converters) {
        dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd), converters);
    }
}
