package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.NutritionHistoryPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + IC3NutritionHistoryRestController.PIHMALAWI + IC3NutritionHistoryRestController.IC3_NUTRITION_HISTORY)
public class IC3NutritionHistoryRestController {


    public static final String PIHMALAWI = "/pihmalawi";
    public static final String IC3_NUTRITION_HISTORY = "/ic3NutritionHistory";

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientDataService patientDataService;

    @Autowired
    private ChronicCareMetadata metadata;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get(HttpServletRequest request, HttpServletResponse response) throws EvaluationException {
        RequestContext requestContext = RestUtil.getRequestContext(request, response);

        String patientUuid = requestContext.getParameter("patient");
        Patient patient = patientService.getPatientByUuid(patientUuid);

        if (patient == null) {
            throw new IllegalArgumentException("Unable to find patient with uuid " + patientUuid);
        }

        Cohort cohort = new Cohort(Collections.singleton(patient.getId()));
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setBaseCohort(cohort);
        PatientDataDefinition pdd = new NutritionHistoryPatientDataDefinition();

        PatientData data = patientDataService.evaluate(pdd, evaluationContext);

        List<SimpleObject> results = new ArrayList<SimpleObject>();

        String bmiConceptUuid = metadata.getBMIConcept().getUuid();

        if (data.getData().containsKey(patient.getId())) {
            for (Object obsOrBmi : (List<Object>) data.getData().get(patient.getId())) {
                if (obsOrBmi instanceof Obs) {
                    results.add((SimpleObject) ConversionUtil.convertToRepresentation(obsOrBmi,
                            new CustomRepresentation("(id,uuid,display,obsDatetime,value:(id,uuid,display,name:(uuid,name)),concept:(uuid),encounter:(id,uuid,encounterDatetime))")));
                }
                else if (obsOrBmi instanceof BMI) {
                    // we coerce the BMI in an object into a kind of "faux" obs
                    BMI bmi = (BMI) obsOrBmi;
                    SimpleObject bmiObject = new SimpleObject();
                    bmiObject.put("uuid", UUID.randomUUID());
                    bmiObject.put("obsDatetime", ConversionUtil.convertToRepresentation(bmi.getWeightObs().getObsDatetime(), Representation.REF));  // date is from the underlying weight concept
                    bmiObject.put("value", bmi.getNumericValue(1));

                    SimpleObject concept = new SimpleObject();
                    concept.put("uuid", bmiConceptUuid);
                    bmiObject.put("concept", concept);

                    if (bmi.getWeightObs().getEncounter() != null) {
                        SimpleObject encounter = new SimpleObject();
                        encounter.put("uuid", bmi.getWeightObs().getEncounter().getUuid());
                        encounter.put("encounterDatetime", ConversionUtil.convertToRepresentation(bmi.getWeightObs().getEncounter().getEncounterDatetime(), Representation.REF));
                        bmiObject.put("encounter", encounter);
                    }

                    results.add(bmiObject);
                }
            }
        }

        return results;
    }

}



