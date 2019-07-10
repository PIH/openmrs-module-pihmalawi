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
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.common.ViralLoad;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrintableSummaryPageController {

    protected static final Log log = LogFactory.getLog(PrintableSummaryPageController.class);

	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           UiUtils ui, PageModel model,
                           @SpringBean("dataFactory") DataFactory df,
                           @SpringBean("hivMetadata") HivMetadata hivMetadata,
                           @SpringBean("chronicCareMetadata") ChronicCareMetadata ccMetadata,
                           @SpringBean("builtInPatientDataLibrary") BuiltInPatientDataLibrary builtInData,
                           @SpringBean("basePatientDataLibrary") BasePatientDataLibrary baseData,
                           @SpringBean("hivPatientDataLibrary") HivPatientDataLibrary hivData,
                           @SpringBean("chronicCarePatientDataLibrary") ChronicCarePatientDataLibrary ccData,
                           @SpringBean("hivCohortDefinitionLibrary") HivCohortDefinitionLibrary hivCohorts,
                           @SpringBean("chronicCareCohortDefinitionLibrary") ChronicCareCohortDefinitionLibrary ccCohorts,
                           @SpringBean("reportingDataSetDefinitionService") DataSetDefinitionService dataSetDefinitionService,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("dateUtil", new DateUtil());

        Date today = new Date();

        try {
            EvaluationContext context = getSinglePatientEvaluationContext(patient.getPatientId());
            model.addAttribute("firstName", getData(builtInData.getPreferredGivenName(), context));
            model.addAttribute("lastName", getData(builtInData.getPreferredFamilyName(), context));
            model.addAttribute("birthDate", getData(baseData.getBirthdate(), context));
            model.addAttribute("ageYears", getData(baseData.getAgeAtEndInYears(), context));
            model.addAttribute("ageMonths", getData(baseData.getAgeAtEndInMonths(), context));
            model.addAttribute("gender", getData(builtInData.getGender(), context));
            model.addAttribute("hccNumber", getData(hivData.getHccNumberAtLocation(), context));
            model.addAttribute("arvNumber", getData(hivData.getArvNumberAtLocation(), context));
            model.addAttribute("ccNumber", getData(ccData.getChronicCareNumberAtLocation(), context));
            model.addAttribute("hivTxStatus", getData(hivData.getMostRecentHivTreatmentStatusStateNameByEndDate(), context));
            model.addAttribute("hivTxStatusDate", getData(hivData.getMostRecentHivTreatmentStatusStateStartDateByEndDate(), context));
            model.addAttribute("ccTxStatus", getData(ccData.getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate(), context));
            model.addAttribute("ccTxStatusDate", getData(ccData.getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate(), context));
            model.addAttribute("artStartDate", getData(hivData.getEarliestOnArvsStateStartDateByEndDate(), context));
            model.addAttribute("artRegimens", getData(hivData.getArvRegimenChangesByEndDate(), context));
            model.addAttribute("cd4s", getData(hivData.getCd4CountObservations(), context));
            model.addAttribute("tbStatus", getData(hivData.getLatestTbStatusObs(), context));

            List<ViralLoad> viralLoads = getData(hivData.getAllViralLoadsByEndDate(), context);
            String lastViralLoadValue = null;
            Date lastViralLoadDate = null;
            Boolean highViralLoad = false;

            if (viralLoads != null) {
                for (ViralLoad vl : viralLoads) {
                    if (vl.getResultLdl() != null || vl.getLessThanResultNumeric() != null || vl.getResultNumeric() != null) {
                        lastViralLoadDate = vl.getResultDate();
                        if (vl.getResultNumeric() != null) {
                            lastViralLoadValue = ObjectUtil.formatNumber(vl.getResultNumeric(), "1", Context.getLocale());
                            highViralLoad = (vl.getResultNumeric() >= 1000);
                        }
                        else if (vl.getLessThanResultNumeric() != null) {
                            lastViralLoadValue = "< " + vl.getLessThanResultNumeric();
                            highViralLoad = false;
                        }
                        else if (vl.getResultLdl()) {
                            lastViralLoadValue = "LDL";
                            highViralLoad = false;
                        }
                    }
                }
            }

            model.addAttribute("viralLoads", viralLoads);
            model.addAttribute("lastViralLoadValue", lastViralLoadValue);
            model.addAttribute("lastViralLoadDate", lastViralLoadDate);
            model.addAttribute("highViralLoad", highViralLoad);

            Obs ht = getData(baseData.getLatestHeightObs(), context);
            model.addAttribute("height", ht);

            List<Obs> weights = getData(baseData.getAllWeightObservations(), context);
            Obs wt = getLastValue(weights);
            Obs oneYearWt = getValueAtLeastXMonthsBeforeLastValue(weights, 12);
            model.addAttribute("weights", weights);
            model.put("lastWeight", wt);
            model.put("oneYearWeight", oneYearWt);

            model.put("bmi", "");
            model.put("bmiValue", null);
            if (ht != null && wt != null) {
                double bmi = wt.getValueNumeric()/Math.pow(ht.getValueNumeric()/100, 2);
                model.put("bmi", ObjectUtil.format(bmi, "1"));
                model.put("bmiValue", bmi);
            }

            Date aid = getData(hivData.getFirstArtInitialEncounterDateByEndDate(), context);
            model.addAttribute("reasonCd4", getObsValueOnDate(hivMetadata.getCd4CountConcept(), aid, context));
            model.addAttribute("reasonCd4Pct", getObsValueOnDate(hivMetadata.getCd4PercentConcept(), aid, context));
            model.addAttribute("reasonCd4Date", getObsValueOnDate(hivMetadata.getCd4DateConcept(), aid, context));
            model.addAttribute("reasonKs", getObsValueOnDate(hivMetadata.getKsSideEffectsWorseningOnArvsConcept(), aid, context));
            model.addAttribute("reasonTb", getObsValueOnDate(hivMetadata.getTbTreatmentStatusConcept(), aid, context));
            model.addAttribute("reasonStage", getObsValueOnDate(hivMetadata.getWhoStageConcept(), aid, context));
            model.addAttribute("reasonPshd", getObsValueOnDate(hivMetadata.getPresumedSevereHivCriteriaPresentConcept(), aid, context));
            model.addAttribute("reasonConditions", getObsValueOnDate(hivMetadata.getWhoClinicalConditionsConcept(), aid, context));
            model.addAttribute("reasonPregnantLactating", getObsValueOnDate(hivMetadata.getPregnantOrLactatingConcept(), aid, context));

            List<Encounter> encounters = getData(baseData.getAllEncounters(), context);
            Collections.reverse(encounters);
            model.addAttribute("encounters", encounters);

            Map<String, List<AppointmentInfo>> appts = new LinkedHashMap<String, List<AppointmentInfo>>();

            AppointmentInfo eidApp = getData(hivData.getEidAppointmentStatus(), context);
            if (eidApp.isCurrentlyEnrolled()) {
                appts.put("EID", Arrays.asList(eidApp));
            }

            AppointmentInfo artApp = getData(hivData.getArtAppointmentStatus(), context);
            if (artApp.isCurrentlyEnrolled()) {
                appts.put("ART", Arrays.asList(artApp));
            }

            List<AppointmentInfo> ncdList = new ArrayList<AppointmentInfo>();

            List<ProgramWorkflowState> activeNcdStates = ccMetadata.getActiveChronicCareStates();

            List<EncounterType> ncdTypes = new ArrayList<EncounterType>();
            ncdTypes.add(ccMetadata.getHtnDiabetesFollowupEncounterType());
            ncdTypes.add(ccMetadata.getEpilepsyFollowupEncounterType());
            ncdTypes.add(ccMetadata.getAsthmaFollowupEncounterType());
            ncdTypes.add(ccMetadata.getCkdFollowupEncounterType());
            ncdTypes.add(ccMetadata.getPalliativeCareFollowupEncounterType());
            ncdTypes.add(ccMetadata.getChfFollowupEncounterType());
            ncdTypes.add(ccMetadata.getNcdOtherFollowupEncounterType());
            ncdTypes.add(ccMetadata.getMentalHealthFollowupEncounterType());
            for (EncounterType et : ncdTypes) {
                AppointmentInfo ncdApp = getData(df.getAppointmentStatus(activeNcdStates, et), context);
                if (ncdApp.isCurrentlyEnrolled() && ncdApp.getLastEncounterDate() != null) {
                    ncdList.add(ncdApp);
                }
            }
            appts.put("CCC", ncdList);

            model.addAttribute("appointmentStatuses", appts);
        }
        catch (Exception e) {
            model.addAttribute("errors", e.getMessage());
            log.error("An error occured while evaluating data for patient " + patient.getId() + " for printable summary", e);
        }
    }

    protected <T> T getLastValue(List<Obs> l) {
        if (l != null && l.size() > 0) {
            return (T)l.get(l.size()-1);
        }
        return null;
    }

    protected Obs getValueAtLeastXMonthsBeforeLastValue(List<Obs> values, int numMonths) {
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

    protected <T> T getObsValueOnDate(Concept question, Date date, EvaluationContext context) {
        ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
        def.setWhich(TimeQualifier.LAST);
        def.setQuestion(question);
        def.setOnOrAfter(date);
        def.setOnOrBefore(date);
        Obs o = getData(new PersonToPatientDataDefinition(def), context);
        ObsValueConverter converter = new ObsValueConverter();
        return (T)converter.convert(o);
    }

    protected boolean isInCohort(CohortDefinition cd, EvaluationContext context) {
        try {
            Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
            return !c.getMemberIds().isEmpty();
        }
        catch (EvaluationException e) {
            throw new IllegalArgumentException("Unable to evaluate definition for patient", e);
        }
    }

    /**
     * @return the result of evaluating the given definition against the given patient, cast to the given type
     */
    protected <T> T getData(PatientDataDefinition definition, EvaluationContext context) {
        Set<Integer> baseCohort = context.getBaseCohort().getMemberIds();
        if (baseCohort == null || baseCohort.size() != 1) {
            throw new IllegalArgumentException("Single patient base cohort expected");
        }
        try {
            PatientData data = Context.getService(PatientDataService.class).evaluate(definition, context);
            return (T)data.getData().get(baseCohort.iterator().next());
        }
        catch (EvaluationException e) {
            throw new IllegalArgumentException("Unable to evaluate definition for patient", e);
        }
    }

    protected void add(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd, DataConverter... converters) {
        dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd), converters);
    }
}
