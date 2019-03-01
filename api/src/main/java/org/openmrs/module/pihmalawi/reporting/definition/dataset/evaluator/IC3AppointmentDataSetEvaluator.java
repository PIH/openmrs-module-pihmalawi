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
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.alert.AlertNotification;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.data.IC3ScreeningData;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.IC3AppoinmentDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCareCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates an IC3AppoinmentDataSetDefinition and produces results
 */
@Handler(supports={IC3AppoinmentDataSetDefinition.class})
public class IC3AppointmentDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	IC3ScreeningData ic3ScreeningData;

	@Autowired
	ChronicCareCohortDefinitionLibrary ccCohorts;

	@Autowired
	CohortDefinitionService cdService;
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort baseCohort = context.getBaseCohort() == null ? new Cohort() : context.getBaseCohort();
		IC3AppoinmentDataSetDefinition dsd = (IC3AppoinmentDataSetDefinition)dataSetDefinition;

		Cohort patients = ic3ScreeningData.getPatientsWithAppointmentsAtLocation(dsd.getEndDate(), dsd.getLocation());

		// Limit to patients in advanced care if appropriate
		if (dsd.getAdvancedCare() != null && dsd.getAdvancedCare().booleanValue()) {
			Cohort inAdvancedCare = cdService.evaluate(ccCohorts.getPatientsInAdvancedCareStateAtLocationOnEndDate(), context);
			patients = PatientIdSet.intersect(patients, inAdvancedCare);
		}

		Map<Integer, JsonObject> data = ic3ScreeningData.getDataForCohort(patients, dsd.getEndDate(), dsd.getLocation(), false);

		DataSetColumn alertColumn = new DataSetColumn("alert", "alert", String.class);
		DataSetColumn actionsColumn = new DataSetColumn("actions", "actions", String.class);

		SimpleDataSet ret = new SimpleDataSet(dsd, context);
		for (Integer pId : data.keySet()) {
			if (baseCohort.isEmpty() || baseCohort.contains(pId)) {
				JsonObject patData = data.get(pId);
				addColumnValue(ret, pId, "patient_uuid", patData);
				addColumnValue(ret, pId, "last_name", patData);
				addColumnValue(ret, pId, "first_name", patData);
				addColumnValue(ret, pId, "gender", patData);
				addColumnValue(ret, pId, "age", patData, "age_years");
				addColumnValue(ret, pId, "village", patData);
				addColumnValue(ret, pId, "traditional_authority", patData);
				addColumnValue(ret, pId, "district", patData);
				addColumnValue(ret, pId, "phone_number", patData);
				addColumnValue(ret, pId, "vhw", patData);
				addColumnValue(ret, pId, "eid_number", patData, "hcc_number");
				addColumnValue(ret, pId, "art_number", patData);
				addColumnValue(ret, pId, "ncd_number", patData);
				addColumnValue(ret, pId, "last_visit_date", patData);
				addColumnValue(ret, pId, "last_appt_date", patData);

				Set<String> alerts = new LinkedHashSet<String>();
				Set<String> actions = new LinkedHashSet<String>();
				List<AlertNotification> alertNotifications = (List<AlertNotification>) data.get("alerts");
				if (alertNotifications != null) {
					for (AlertNotification an : alertNotifications) {
						if (ObjectUtil.notNull(an.getAlert())) {
							alerts.add(an.getAlert());
						}
						if (ObjectUtil.notNull(an.getAction())) {
							actions.add(an.getAction());
						}
					}
				}

				ret.addColumnValue(pId, alertColumn, OpenmrsUtil.join(alerts, ", "));
				ret.addColumnValue(pId, actionsColumn, OpenmrsUtil.join(actions, ", "));
			}
		}

		return ret;
	}

	private void addColumnValue(SimpleDataSet dsd, Integer pId, String key, JsonObject sourceObj) {
		DataSetColumn c = new DataSetColumn(key, key, Object.class);
		dsd.addColumnValue(pId, c, sourceObj.get(key));
	}

	private void addColumnValue(SimpleDataSet dsd, Integer pId, String key, JsonObject sourceObj, String keyInSourceObj) {
		DataSetColumn c = new DataSetColumn(key, key, Object.class);
		dsd.addColumnValue(pId, c, sourceObj.get(keyInSourceObj));
	}
}
