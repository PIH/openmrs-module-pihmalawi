package org.openmrs.module.pihmalawi.reporting;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.web.WebConstants;

@Handler(supports = { ArcGisPatientDataSetDefinition.class })
public class ArcGisPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public ArcGisPatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ArcGisPatientDataSetDefinition definition = (ArcGisPatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();

		// By default, get all patients
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}

		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(
				cohort.getMemberIds());

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;

			// todo, get current id and/or preferred one first
			String patientLink = "";
			// todo, don't hardcode server
				String url = "http://emr:8080/" + WebConstants.WEBAPP_NAME;
			for (PatientIdentifier pi : p
					.getPatientIdentifiers(patientIdentifierType)) {
				patientLink += "<a href=" + url + "/patientDashboard.form?patientId=" + p.getId() + ">" + (pi != null ? pi.getIdentifier() : "(none)") + "</a> ";
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, patientLink);
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));
			// indicator
			c = new DataSetColumn("Indicator", "Indicator", String.class);
			row.addColumnValue(c, "tbd - e.g. patient missed for x weeks");
			
			dataSet.addRow(row);
		}
		return dataSet;
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
}
