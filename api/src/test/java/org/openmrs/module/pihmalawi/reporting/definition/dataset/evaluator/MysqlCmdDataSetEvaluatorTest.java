package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MysqlCmdDataSetDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MysqlCmdDataSetEvaluatorTest extends StandaloneContextSensitiveTest {

	@Autowired
    DataSetDefinitionService dataSetDefinitionService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

		MysqlCmdDataSetDefinition patientsBornAfterDate = new MysqlCmdDataSetDefinition();
        patientsBornAfterDate.addParameter(new Parameter("date", "Date", Date.class));
        patientsBornAfterDate.setSql("select person_id, gender, birthdate from person where birthdate > @date limit 10");

		EvaluationContext context = new EvaluationContext();
        context.addParameterValue("date", DateUtil.getDateTime(2015, 1, 1));

		DataSet dsd = dataSetDefinitionService.evaluate(patientsBornAfterDate, context);
        DataSetUtil.printDataSet(dsd, System.out);
	}
}
