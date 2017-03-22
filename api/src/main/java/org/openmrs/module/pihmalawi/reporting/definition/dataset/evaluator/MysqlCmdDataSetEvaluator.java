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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MysqlCmdDataSetDefinition;
import org.openmrs.module.pihmalawi.sql.MysqlRunner;
import org.openmrs.module.pihmalawi.sql.SqlResult;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSetMetaData;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.util.Map;

/**
 * Evaluates an MysqlCmdDataSetDefinition and produces results
 */
@Handler(supports={MysqlCmdDataSetDefinition.class})
public class MysqlCmdDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, context);

        MysqlCmdDataSetDefinition dsd = (MysqlCmdDataSetDefinition) dataSetDefinition;
        SqlResult resultData = MysqlRunner.executeSql(dsd.getSql(), context.getParameterValues());

        if (resultData.getData().isEmpty() && !resultData.getErrors().isEmpty()) {
            throw new EvaluationException("Errors occurred during mysql execution: " + OpenmrsUtil.join(resultData.getErrors(), "; "));
        }

        SimpleDataSetMetaData metaData = new SimpleDataSetMetaData();
        for (String column : resultData.getColumns()) {
            metaData.addColumn(new DataSetColumn(column, column, String.class));
        }
        data.setMetaData(metaData);

        for (Map<String, Object> rowData : resultData.getData()) {
            DataSetRow row = new DataSetRow();
            for (DataSetColumn column : metaData.getColumns()) {
                row.addColumnValue(column, rowData.get(column.getName()));
            }
            data.addRow(row);
        }

		return data;
	}
}
