package org.openmrs.module.pihmalawi.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

// order is necessary to overwrite default from inherited datasetdefinition
@Handler(supports = { ApzuKsPatientDataSetDefinition.class }, order = 50)
public class ApzuKsPatientDataSetEvaluator extends ApzuPatientDataSetEvaluator
		implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	private final static int MAX_EVALUATIONS = 6;
	private final static int MAX_CHEMOTHERAPIES = 40;

	private ArrayList<Concept> EVALUATION_CONCEPTS;
	private ArrayList<Concept> CHEMOTHERAPY_CONCEPTS;

	public ApzuKsPatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		init();

		return super.evaluate(dataSetDefinition, context);
	}

	private void init() {
		// set up all concepts for all encountertypes to be included
		EVALUATION_CONCEPTS = new ArrayList<Concept>();
		EVALUATION_CONCEPTS.add(h.concept("Weight (kg)"));
		EVALUATION_CONCEPTS.add(h.concept("Height (cm)"));
		EVALUATION_CONCEPTS.add(h.concept("BODY SURFACE AREA CALCULATED"));
		EVALUATION_CONCEPTS.add(h.concept("BODY MASS INDEX, MEASURED"));
		EVALUATION_CONCEPTS.add(h
				.concept("KAPOSIS SARCOMA METHOD OF DIAGNOSIS"));
		EVALUATION_CONCEPTS.add(h.concept("CHEMOTHERAPY CYCLE NUMBER"));
		EVALUATION_CONCEPTS.add(h.concept("TAKING ANTIRETROVIRAL DRUGS"));
		EVALUATION_CONCEPTS
				.add(h.concept("REASON ANTIRETROVIRALS STARTED"));
		EVALUATION_CONCEPTS.add(h.concept("DATE OF HIV DIAGNOSIS"));
		EVALUATION_CONCEPTS.add(h.concept("STATUS OF ANTIRETROVIRAL REGIMEN"));
		EVALUATION_CONCEPTS
				.add(h.concept("KAPOSIS SARCOMA SIDE EFFECTS WORSENING WHILE ON ARVS?"));
		EVALUATION_CONCEPTS.add(h
				.concept("HISTORY OF OPPORTUNISTIC INFECTIONS?"));
		EVALUATION_CONCEPTS.add(h
				.concept("ARE YOU PRESENTLY PHYSICALLY ABLE TO WORK"));
		EVALUATION_CONCEPTS.add(h.concept("IS PATIENT ABLE TO EAT?"));
		EVALUATION_CONCEPTS.add(h.concept("IS PATIENT ABLE TO WALK?"));
		EVALUATION_CONCEPTS.add(h.concept("PATIENT COMPLAINS OF COUGH"));
		EVALUATION_CONCEPTS.add(h.concept("PAIN SCALE OF 0 TO 10"));
		// laboratory examinations construct
		EVALUATION_CONCEPTS.add(h
				.concept("SERUM GLUTAMIC-OXALOACETIC TRANSAMINASE"));
		EVALUATION_CONCEPTS.add(h
				.concept("STOOL GUAIAC (OCCULT BLOOD TEST) RESULT"));
		EVALUATION_CONCEPTS.add(h.concept("WHITE BLOOD CELLS"));
		EVALUATION_CONCEPTS.add(h.concept("CHEST XRAY COMMENTS"));
		EVALUATION_CONCEPTS.add(h.concept("HEMOGLOBIN"));
		EVALUATION_CONCEPTS.add(h.concept("LYMPHOCYTES"));
		EVALUATION_CONCEPTS.add(h
				.concept("ALANINE AMINOTRANSFERASE")); // aka SERUM GLUTAMIC-PYRUVIC TRANSAMINASE
		EVALUATION_CONCEPTS.add(h.concept("NEUTROPHILS"));
		EVALUATION_CONCEPTS.add(h.concept("CD4 count"));
		EVALUATION_CONCEPTS.add(h.concept("PLATELETS"));
		// laboratory examinations construct
		EVALUATION_CONCEPTS.add(h.concept("NUMBER OF LESIONS"));
		EVALUATION_CONCEPTS.add(h.concept("KS CLINICAL EXAM FINDINGS"));
		EVALUATION_CONCEPTS.add(h.concept("LYMPHADENOPATHY COMMENTS"));
		EVALUATION_CONCEPTS.add(h.concept("TUMOUR PROGNOSIS FOR KS"));
		EVALUATION_CONCEPTS.add(h.concept("SYSTEMIC ILLNESS PROGNOSIS FOR KS"));
		EVALUATION_CONCEPTS.add(h.concept("DATE ANTIRETROVIRALS STARTED"));
//		jEVALUATION_CONCEPTS.add(h.concept("CD4 count")); // not sure how to deal with the construct
		
		CHEMOTHERAPY_CONCEPTS = new ArrayList<Concept>();
		CHEMOTHERAPY_CONCEPTS.add(h.concept("Height (cm)"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("CHEMOTHERAPY CYCLE NUMBER"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("Weight (kg)"));
		CHEMOTHERAPY_CONCEPTS.add(h
				.concept("VINCRISTINE SULPHATE DOSE RECEIVED"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("BLEOMYCIN DOSE RECEIVED"));
		CHEMOTHERAPY_CONCEPTS
				.add(h.concept("PACLITAXEL (TAXOL) DOSE RECEIVED"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("HEMOGLOBIN"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("PLATELETS"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("NEUTROPHILS"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("WHITE BLOOD CELLS"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("Appointment date"));
		CHEMOTHERAPY_CONCEPTS.add(h.concept("SERUM ALBUMIN"));
	}

	public void additionalColumns(Patient p, DataSetRow row, Location location,
			Date endDate) {
		exportAllEncountersAndObs(p, row, location, endDate,
				h.encounterType("PATIENT EVALUATION"), MAX_EVALUATIONS,
				EVALUATION_CONCEPTS);
		exportAllEncountersAndObs(p, row, location, endDate,
				h.encounterType("CHEMOTHERAPY"), MAX_CHEMOTHERAPIES,
				CHEMOTHERAPY_CONCEPTS);
	}

	private void exportAllEncountersAndObs(Patient p, DataSetRow row,
			Location location, Date endDate, final EncounterType encounterType,
			int maxEncounters, ArrayList<Concept> encounterConcepts) {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				location, null, endDate, null, Arrays.asList(encounterType),
				null, false);
		for (int encounterCount = 1; encounterCount <= maxEncounters; encounterCount++) {
			Encounter e = null;
			if (encounterCount <= es.size()) {
				e = es.get(encounterCount - 1);
			}
			DataSetColumn col = new DataSetColumn("count_" + encounterCount + "_" + encounterType.getName(),
					"count_" + encounterCount + "_" + encounterType.getName(), Integer.class);
			row.addColumnValue(col, encounterCount);
			
			col = new DataSetColumn("encounterDate_" + encounterCount + "_" + encounterType.getName(),
					"encounterDate_" + encounterCount + "_" + encounterType.getName(), String.class);
			String value = (e != null ? h(formatEncounterDate(e.getEncounterDatetime())) : "");
			row.addColumnValue(col, value);
			
			col = new DataSetColumn("encounterId_" + encounterCount + "_" + encounterType.getName(),
					"encounterId_" + encounterCount + "_" + encounterType.getName(), String.class);
			value = (e != null ? e.getId() + "" : "");
			row.addColumnValue(col, value);

			for (Concept c : encounterConcepts) {
					value = "<empty>";
					String label = c.getName().getName() + "_" + encounterCount + "_" + encounterType.getName();
					col = new DataSetColumn(label, label, String.class);
				try {
					if (e != null) {
						List<Obs> os = obs(e, c, location, endDate);
						for (Obs o : os) {
							value += o.getValueAsString(Context.getLocale()) + " ";
						}
					}
				} catch (Throwable t) {
					log.error(t);
				}
				row.addColumnValue(col, h(value));
			}
		}
	}

	private List<Obs> obs(Encounter e, Concept concept, Location location, Date endDate) {
		List<Obs> obs = Context.getObsService().getObservations(null,
				Arrays.asList(e), Arrays.asList(concept), null, null, null,
				null, null, null, null, endDate, false);
		return obs;
	}
}
