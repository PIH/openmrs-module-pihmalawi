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
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.SqlFileIndicatorDataSetDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a SqlFileIndicatorDataSetDefinition and produces results
 */
@Handler(supports={SqlFileIndicatorDataSetDefinition.class}, order = 50)
public class SqlFileIndicatorDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());

        SqlFileIndicatorDataSetDefinition dsd = (SqlFileIndicatorDataSetDefinition) dataSetDefinition;
        SqlFileDataSetEvaluator sqlFileDataSetEvaluator = new SqlFileDataSetEvaluator();
        SimpleDataSet dataSet = (SimpleDataSet)sqlFileDataSetEvaluator.evaluate(dsd, context);

        MapDataSet data = new MapDataSet(dsd, context);
        for (DataSetRow row : dataSet.getRows()) {
            String indicatorName = (String)row.getColumnValue(dsd.getIndicatorNameColumn());
            String indicatorDescription = (String)row.getColumnValue(dsd.getIndicatorDescriptionColumn());
            Object indicatorValue = row.getColumnValue(dsd.getIndicatorValueColumn());
            data.addData(new DataSetColumn(indicatorName, indicatorDescription, Object.class), indicatorValue);
        }

        return data;
	}
}
