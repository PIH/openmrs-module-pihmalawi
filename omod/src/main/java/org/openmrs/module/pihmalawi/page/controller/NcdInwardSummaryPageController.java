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

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;
import org.openmrs.module.reporting.data.encounter.EncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NcdInwardSummaryPageController {

    protected static final Log log = LogFactory.getLog(NcdInwardSummaryPageController.class);

	public void controller(@RequestParam(value="patientId", required=false) Patient patient,
                           UiUtils ui, PageModel model,
                           @SpringBean("builtInPatientDataLibrary") BuiltInPatientDataLibrary builtInData,
                           @SpringBean("basePatientDataLibrary") BasePatientDataLibrary baseData,
                           @SpringBean("hivPatientDataLibrary") HivPatientDataLibrary hivData,
                           @SpringBean("chronicCareMetadata") ChronicCareMetadata ccMetadata,
                           @SpringBean("chronicCarePatientDataLibrary") ChronicCarePatientDataLibrary ccData,
                           @SpringBean("dataFactory") DataFactory df,
	                       @InjectBeans PatientDomainWrapper patientDomainWrapper) {

		patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("dateUtil", new DateUtil());

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort(Arrays.asList(patient.getPatientId())));

        try {

            // Demographics, identifiers

            model.addAttribute("firstName", evaluate(builtInData.getPreferredGivenName(), context));
            model.addAttribute( "lastName", evaluate(builtInData.getPreferredFamilyName(), context));
            model.addAttribute( "birthDate", evaluate(baseData.getBirthdate(), context));
            model.addAttribute( "village", evaluate(baseData.getVillage(), context));
            model.addAttribute( "chw", evaluate(baseData.getChw(), context));
            model.addAttribute( "ageYears", evaluate(baseData.getAgeAtEndInYears(), context));
            model.addAttribute( "ageMonths", evaluate(baseData.getAgeAtEndInMonths(), context));
            model.addAttribute( "gender", evaluate(builtInData.getGender(), context));
            model.addAttribute( "hccNumber", evaluate(hivData.getHccNumberAtLocation(), context));
            model.addAttribute( "arvNumber", evaluate(hivData.getArvNumberAtLocation(), context));
            model.addAttribute( "ccNumber", evaluate(ccData.getChronicCareNumberAtLocation(), context));

            // Program enrollment status

            model.addAttribute("hivEnrollmentDate", evaluate(hivData.getEarliestHivProgramEnrollmentDateByEndDate(), context));
            model.addAttribute( "hivTxStatus", evaluate(hivData.getMostRecentHivTreatmentStatusStateNameByEndDate(), context));
            model.addAttribute( "hivTxStatusDate", evaluate(hivData.getMostRecentHivTreatmentStatusStateStartDateByEndDate(), context));
            model.addAttribute( "hivFirstVisitDate", evaluate(hivData.getFirstHivEncounterDateByEndDate(), context));
            model.addAttribute( "hivLastVisitDate", evaluate(hivData.getMostRecentHivEncounterDateByEndDate(), context));
            model.addAttribute( "artAppointmentStatus", evaluate(hivData.getArtAppointmentStatus(), context));
            model.addAttribute( "ccTxStatus", evaluate(ccData.getMostRecentChronicCareTreatmentStatusStateAtLocationByEndDate(), context));
            model.addAttribute( "ccTxStatusDate", evaluate(ccData.getMostRecentChronicCareTreatmentStatusStateStartDateAtLocationByEndDate(), context));
            model.addAttribute( "ccDxObs", evaluate(ccData.getAllChronicCareDiagnosisObsByEndDate(), context));

            // Weight, height, BMI

            List<Obs> weights = (List<Obs>) evaluate(baseData.getAllWeightObservations(), context);
            Obs weight = getLastValue(weights, Obs.class);
            Obs height = (Obs) evaluate(baseData.getLatestHeightObs(), context);
            model.addAttribute("weights", weights);
            model.addAttribute("height", height);
            model.addAttribute("weight", weight);
            model.put("bmi", "");
            if (weight != null && height != null) {
                double bmi = weight.getValueNumeric()/Math.pow(height.getValueNumeric()/100, 2);
                model.put("bmi", ObjectUtil.format(bmi, "1"));
                model.put("bmiValue", bmi);
            }

            // Diagnoses and mastercard dates

            List<Obs> dxObs = (List<Obs>)model.getAttribute("ccDxObs");

            DiagnosisSection htnSection = new DiagnosisSection("htn", "Hypertension", ccMetadata.getHtnDiabetesEncounterTypes());
            DiagnosisSection diabetesSection = new DiagnosisSection("diabetes", "Diabetes", ccMetadata.getHtnDiabetesEncounterTypes());
            DiagnosisSection epilepsySection = new DiagnosisSection("epilepsy", "Epilepsy", ccMetadata.getEpilepsyEncounterTypes());
            DiagnosisSection asthmaSection = new DiagnosisSection("asthma", "Asthma / COPD", ccMetadata.getAsthmaEncounterTypes());
            DiagnosisSection mhSection = new DiagnosisSection("mh", "Mental Health", ccMetadata.getMentalHealthEncounterTypes());

            List<DiagnosisSection> diagnosisSections = Arrays.asList(htnSection, diabetesSection, epilepsySection, asthmaSection, mhSection);
            if (dxObs != null) {
                for (DiagnosisSection section : diagnosisSections) {
                    for (Obs o : dxObs) {
                        if (o.getEncounter() != null && section.getTypes().contains(o.getEncounter().getEncounterType())) {
                            boolean add = true;
                            if ((section.getKey().equals("htn") && !o.getValueCoded().equals(ccMetadata.getHypertensionConcept())) || (section.getKey().equals("diabetes") && o.getValueCoded().equals(ccMetadata.getHypertensionConcept()))) {
                                add = false;
                            }
                            if (add) {
                                DiagnosisRow diagnosisRow = new DiagnosisRow(o);
                                diagnosisRow.setDiagnosisDate(getSibling(o, ccMetadata.getDiagnosisDateConcept()));
                                section.addRow(diagnosisRow);
                            }
                        }
                    }
                }
            }

            for (DiagnosisSection section : diagnosisSections) {
                if (section.getRows().size() > 0) {
                    section.setEarliestEncounterDate((Date)evaluate(ccData.getFirstEncounterDateByEndDate(section.getTypes()), context));
                    Encounter latestEncounter = (Encounter)evaluate(df.getMostRecentEncounterOfTypesByEndDate(section.getTypes(), null), context);
                    if (latestEncounter != null) {
                        section.setLatestEncounterDate(latestEncounter.getEncounterDatetime());
                        Obs nextApptDateObs = (Obs) evaluate(df.getMostRecentObsOnGivenDate(ccMetadata.getAppointmentDateConcept(), latestEncounter.getEncounterDatetime()), context);
                        if (nextApptDateObs != null) {
                            section.setNextAppointmentDate(nextApptDateObs.getValueDatetime());
                        }

                        if (section.getKey().equals("htn") || section.getKey().equals("diabetes")) {
                            String bp = "None recorded";
                            Obs lastSystolicBp = (Obs) evaluate(df.getMostRecentObsByEndDate(ccMetadata.getSystolicBloodPressureConcept(), null, null), context);
                            Obs lastDiastolicBp = (Obs) evaluate(df.getMostRecentObsByEndDate(ccMetadata.getDiastolicBloodPressureConcept(), null, null), context);
                            if (lastSystolicBp != null && lastDiastolicBp != null) {
                                bp = lastSystolicBp.getValueNumeric().intValue() + " / " + lastDiastolicBp.getValueNumeric().intValue();
                            }
                            section.addObsValue("Last Blood Pressure", bp);
                        }
                        if (section.getKey().equals("diabetes")) {
                            Obs lastHba1cResult = (Obs) evaluate(df.getMostRecentObsByEndDate(ccMetadata.getHbA1cConcept(), null, null), context);
                            section.addObsValue("Last HbA1c result", ui.format(lastHba1cResult));

                            Obs bloodSugarResult = (Obs) evaluate(df.getMostRecentObsByEndDate(ccMetadata.getBloodSugarTestResultConcept(), null, null), context);
                            if (bloodSugarResult != null) {
                                Obs bloodSugarTestType = getSibling(bloodSugarResult, ccMetadata.getBloodSugarTestTypeConcept());
                                String resultStr = ui.format(bloodSugarResult) + " on " + ui.format(bloodSugarResult.getObsDatetime());
                                if (bloodSugarTestType != null) {
                                    resultStr += " (" + ui.format(bloodSugarTestType) + ")";
                                }
                                section.addObsValue("Last Blood sugar result", resultStr);
                            }
                        }

                        List<Obs> meds = (List<Obs>)evaluate(df.getAllObsOnGivenDate(ccMetadata.getCurrentDrugsUsedConcept(), latestEncounter.getEncounterDatetime()), context);
                        section.setCurrentMedications(meds);
                    }
                }
            }

            model.addAttribute("diagnosisSections", diagnosisSections);

            List<List<DiagnosisSection>> sectionLayout = new ArrayList<List<DiagnosisSection>>();
            List<DiagnosisSection> sectionRow1 = new ArrayList<DiagnosisSection>();
            for (DiagnosisSection ds : diagnosisSections) {
                model.addAttribute(ds.getKey() + "Section", ds);
                if (ds.getRows().size() > 0) {
                    sectionRow1.add(ds);
                }
            }
            sectionLayout.add(sectionRow1);
            model.addAttribute("sectionLayout", sectionLayout);

            Map<Date, Map<String, Obs>> bpTable = new TreeMap<Date, Map<String, Obs>>(new ReverseComparator());
            addObsToTable(bpTable, "sbp", (List<Obs>)evaluate(df.getAllObsByEndDate(ccMetadata.getSystolicBloodPressureConcept(), null, null), context));
            addObsToTable(bpTable, "dbp", (List<Obs>)evaluate(df.getAllObsByEndDate(ccMetadata.getDiastolicBloodPressureConcept(), null, null), context));
            addObsToTable(bpTable, "bs", (List<Obs>) evaluate(df.getAllObsByEndDate(ccMetadata.getBloodSugarTestResultConcept(), null, null), context));
            addObsToTable(bpTable, "bst", (List<Obs>) evaluate(df.getAllObsByEndDate(ccMetadata.getBloodSugarTestTypeConcept(), null, null), context));
            model.addAttribute("bpTable", bpTable);

            List<Obs> seizures = (List<Obs>) evaluate(df.getAllObsByEndDate(ccMetadata.getNumberOfSeizuresConcept(), null, null), context);
            model.addAttribute("seizures", seizures);
        }
        catch (Exception e) {
            model.addAttribute("errors", e.getMessage());
            log.error("An error occured while evaluating data for patient " + patient.getId() + " for printable summary", e);
        }
    }

    protected <T> T getLastValue(List l, Class<T> type) {
        if (l != null) {
            return (T)l.get(l.size()-1);
        }
        return null;
    }

    protected Object evaluate(PatientDataDefinition pdd, EvaluationContext context) throws EvaluationException {
        PatientDataService service = Context.getService(PatientDataService.class);
        PatientData data = service.evaluate(pdd, context);
        if (data.getData().size() > 0) {
            return data.getData().values().iterator().next();
        }
        return null;
    }

    protected Object evaluate(EncounterDataDefinition edd, EncounterEvaluationContext context) throws EvaluationException {
        EncounterDataService service = Context.getService(EncounterDataService.class);
        EncounterData data = service.evaluate(edd, context);
        if (data.getData().size() > 0) {
            return data.getData().values().iterator().next();
        }
        return null;
    }

    protected Obs getSibling(Obs o, Concept siblingConcept) {
        Obs groupingObs = o.getObsGroup();
        if (groupingObs != null) {
            for (Obs childObs : groupingObs.getGroupMembers()) {
                if (childObs.getConcept().equals(siblingConcept)) {
                    return childObs;
                }
            }
        }
        return null;
    }

    protected void addObsToTable(Map<Date, Map<String, Obs>> table, String key, List<Obs> obsToAdd) {
	    if (obsToAdd != null) {
            for (Obs o : obsToAdd) {
                Date d = DateUtil.getStartOfDay(o.getObsDatetime());
                Map<String, Obs> dateRow = table.get(d);
                if (dateRow == null) {
                    dateRow = new HashMap<String, Obs>();
                    table.put(d, dateRow);
                }
                dateRow.put(key, o);
            }
        }
    }

    class DiagnosisSection {

        private String key;
        private String label;
        private List<EncounterType> types;
        private List<DiagnosisRow> rows;
        private Date earliestEncounterDate;
        private Date latestEncounterDate;
        private Date nextAppointmentDate;
        private Map<String, Object> obsValues;
        private List<Obs> currentMedications;

        @Override
        public String toString() {
            return getKey() + " (" + getLabel() + "): " + getRows();
        }

        public DiagnosisSection(String key, String label, List<EncounterType> types) {
            this.key = key;
            this.label = label;
            this.types = types;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<EncounterType> getTypes() {
            return types;
        }

        public void setTypes(List<EncounterType> types) {
            this.types = types;
        }

        public List<DiagnosisRow> getRows() {
            if (rows == null) {
                rows = new ArrayList<DiagnosisRow>();
            }
            return rows;
        }

        public void setRows(List<DiagnosisRow> rows) {
            this.rows = rows;
        }

        public void addRow(DiagnosisRow row) {
            getRows().add(row);
        }

        public Date getEarliestEncounterDate() {
            return earliestEncounterDate;
        }

        public void setEarliestEncounterDate(Date earliestEncounterDate) {
            this.earliestEncounterDate = earliestEncounterDate;
        }

        public Date getLatestEncounterDate() {
            return latestEncounterDate;
        }

        public void setLatestEncounterDate(Date latestEncounterDate) {
            this.latestEncounterDate = latestEncounterDate;
        }

        public Date getNextAppointmentDate() {
            return nextAppointmentDate;
        }

        public void setNextAppointmentDate(Date nextAppointmentDate) {
            this.nextAppointmentDate = nextAppointmentDate;
        }

        public Map<String, Object> getObsValues() {
            if (obsValues == null) {
                obsValues = new LinkedHashMap<String, Object>();
            }
            return obsValues;
        }

        public void setObsValues(Map<String, Object> obsValues) {
            this.obsValues = obsValues;
        }

        public void addObsValue(String key, Object value) {
            if (value != null) {
                getObsValues().put(key, value);
            }
        }

        public List<Obs> getCurrentMedications() {
            return currentMedications;
        }

        public void setCurrentMedications(List<Obs> currentMedications) {
            this.currentMedications = currentMedications;
        }
    }

    class DiagnosisRow {
        private Obs diagnosis;
        private Obs diagnosisDate;

        @Override
        public String toString() {
            return getDiagnosis().getValueCoded().getDisplayString() + (getDiagnosisDate() == null ? "" : " (" + getDiagnosisDate().getValueDate() + ")");
        }

        public DiagnosisRow(Obs diagnosis) {
            this.diagnosis = diagnosis;
        }

        public Obs getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(Obs diagnosis) {
            this.diagnosis = diagnosis;
        }

        public Obs getDiagnosisDate() {
            return diagnosisDate;
        }

        public void setDiagnosisDate(Obs diagnosisDate) {
            this.diagnosisDate = diagnosisDate;
        }
    }

    class ValueOnDate {

        private Object value;
        private Date date;

        public ValueOnDate(Object value, Date date) {
            this.value = value;
            this.date = date;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
