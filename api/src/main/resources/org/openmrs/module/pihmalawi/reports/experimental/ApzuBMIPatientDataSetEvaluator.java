package org.openmrs.module.pihmalawi.reports.experimental;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;

@Handler(supports = { ApzuBMIPatientDataSetDefinition.class }, order=50)
public class ApzuBMIPatientDataSetEvaluator extends ApzuNumericConceptPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public ApzuBMIPatientDataSetEvaluator() {
	}

	@Override
	protected String columnHeader(Concept concept, int i) {
		return "BMI_" + i;
	}

	@Override
	protected String calcObsValue(Obs o, Date endDate) {
		return "" + bmi(o.getValueNumeric(), mostRecentHeight(o.getPerson(), endDate));
	}

	private double bmi(double weightInKG, double heightInCM) {
		if (weightInKG == 0 || heightInCM == 0) 
			return 0;
		return (weightInKG / ((heightInCM/100) * (heightInCM/100)));
	}
	
	private double mostRecentHeight(Person p, Date endDate) {
		final Concept HEIGHT = Context.getConceptService().getConcept(5090);
		List<Obs> obses = Context.getObsService().getObservations(
				Arrays.asList(p), null,
				Arrays.asList(HEIGHT), null, null, null, null, 1,
				null, null, endDate, false);
		if (obses.isEmpty()) {
			return 0;
		} else {
			return obses.get(0).getValueNumeric();
		}
	}
}
