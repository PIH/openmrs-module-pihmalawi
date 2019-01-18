package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.common.BMI;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.NutritionPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Handler(supports = NutritionPatientDataDefinition.class, order = 50)
public class NutritionPatientDataEvaluator implements PatientDataEvaluator {

    // TODO: change concept id sorting as ASC or DESC based on comparing concept IDs
    // TODO confirm that this works all the way through (ie build out the required functionality to return as REST request and to parse as part of Obs History)
    // TODO include MUAC;
    // TODO: factor in age and pregnancy when calculating BMI;
    // TODO: do we need to collapse datetime to date (so that if weight is technically captured earlier in day, can still do BMI?)

    @Autowired
    private ChronicCareMetadata metadata;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        NutritionPatientDataDefinition def = (NutritionPatientDataDefinition) definition;
        EvaluatedPatientData c = new EvaluatedPatientData(def, context);

        if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
            return c;
        }

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("o.personId", "o");
        q.from(Obs.class, "o");
        q.wherePersonIn("o.personId", context);
        q.whereInAny("o.concept", metadata.getHeightConcept(), metadata.getWeightConcept());
        q.whereLessOrEqualTo("o.obsDatetime", def.getEndDate());
        q.orderAsc("o.obsDatetime");
        q.orderDesc("o.concept.id");

        List<Object[]> results = evaluationService.evaluateToList(q, context);
        for (Object[] row : results) {
            Integer pId = (Integer)row[0];
            Obs o = (Obs)row[1];
            List<Object> obsForPatient = (List<Object>)c.getData().get(pId);
            if (obsForPatient == null) {
                obsForPatient = new ArrayList<Object>();
                c.getData().put(pId, obsForPatient);
            }
            obsForPatient.add(o);
        }

        // add in the BMI values
        for (Object obsForPatient : c.getData().values()) {

            List<Object> obs = (List<Object>) obsForPatient;
            ListIterator<Object> i = obs.listIterator();

            Obs height = null;
            while (i.hasNext()) {
                Obs nextObs = (Obs) i.next();

                if (nextObs.getConcept().equals(metadata.getHeightConcept())) {
                    height = nextObs;
                }

                if (nextObs.getConcept().equals(metadata.getWeightConcept()) && height != null) {
                    i.add(new BMI(nextObs, height));
                }
            }
        }

        return c;
    }
}
