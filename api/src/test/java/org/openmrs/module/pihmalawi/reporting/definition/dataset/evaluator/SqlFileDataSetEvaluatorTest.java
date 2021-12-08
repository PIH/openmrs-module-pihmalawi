package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.junit.Ignore;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Ignore
public class SqlFileDataSetEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
    DataSetDefinitionService dataSetDefinitionService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

		SqlFileDataSetDefinition patientsBornAfterDate = new SqlFileDataSetDefinition();
        patientsBornAfterDate.addParameter(new Parameter("gender", "Gender", Date.class));
        patientsBornAfterDate.setSqlResource("org/openmrs/module/pihmalawi/sql/simpleScript.sql");

		EvaluationContext context = new EvaluationContext();
        context.addParameterValue("gender", "M");

		DataSet dsd = dataSetDefinitionService.evaluate(patientsBornAfterDate, context);
        DataSetUtil.printDataSet(dsd, System.out);
	}
}
