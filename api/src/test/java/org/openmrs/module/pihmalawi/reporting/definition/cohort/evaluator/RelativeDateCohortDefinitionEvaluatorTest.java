package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.Cohort;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.RelativeDateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivPatientDataLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.data.patient.definition.StaticValuePatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class RelativeDateCohortDefinitionEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	BasePatientDataLibrary baseData;

	@Autowired
	HivPatientDataLibrary hivData;

	@Autowired
	PatientDataService patientDataService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

		test(RangeComparator.EQUAL, 7, DurationUnit.DAYS, true);
		test(RangeComparator.LESS_EQUAL, 7, DurationUnit.DAYS, true);
		test(RangeComparator.GREATER_EQUAL, 7, DurationUnit.DAYS, true);
		test(RangeComparator.LESS_THAN, 7, DurationUnit.DAYS, false);
		test(RangeComparator.GREATER_THAN, 7, DurationUnit.DAYS, false);

		test(RangeComparator.LESS_THAN, 1, DurationUnit.WEEKS, false);
		test(RangeComparator.EQUAL, 1, DurationUnit.WEEKS, true);
		test(RangeComparator.GREATER_THAN, 1, DurationUnit.WEEKS, false);

		test(RangeComparator.LESS_THAN, null, null, true);
		test(RangeComparator.GREATER_THAN, null, null, false);
		test(RangeComparator.EQUAL, null, null, false);
	}

	private void test(RangeComparator operator, Integer number, DurationUnit unit, boolean shouldPass) throws EvaluationException{
		RelativeDateCohortDefinition cd = new RelativeDateCohortDefinition();

		cd.setEarlierDateDefinition(Mapped.mapStraightThrough(new StaticValuePatientDataDefinition(DateUtil.getDateTime(2012, 12, 1))));
		cd.setLaterDateDefinition(Mapped.mapStraightThrough(new StaticValuePatientDataDefinition(DateUtil.getDateTime(2012, 12, 8))));
		cd.setDifferenceOperator(operator);
		cd.setDifferenceNumber(number);
		cd.setDifferenceUnit(unit);

		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("39608"));
		Cohort c = cohortDefinitionService.evaluate(cd, context);
		int expectedSize = (shouldPass ? 1 : 0);
		Assert.assertEquals(expectedSize, c.getSize());
	}
}
