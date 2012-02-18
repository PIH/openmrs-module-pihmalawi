package org.openmrs.module.pihmalawi.reports.experimental;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
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

@Handler(supports = { ApzuNumericConceptPatientDataSetDefinition.class })
public class ApzuNumericConceptPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	private final static int MAX_PREVIOUS_WEEKS = 208;
	private final static long MILLISECONDS_PER_WEEK = (long) 7 * 24 * 60 * 60
			* 1000;

	public ApzuNumericConceptPatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ApzuNumericConceptPatientDataSetDefinition definition = (ApzuNumericConceptPatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();
		Concept concept = definition.getNumericConcept();

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Date endDateParameter = (Date) context.getParameterValue("endDate");
		if (endDateParameter == null) {
			// default to today
			endDateParameter = new Date();
		}
		Date startDateParameter = (Date) context.getParameterValue("startDate");
		if (startDateParameter == null) {
			startDateParameter = new Date(endDateParameter.getTime()
					- (MAX_PREVIOUS_WEEKS * MILLISECONDS_PER_WEEK));
		}

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
			List<PatientIdentifier> pis = null;
			if (patientIdentifierType == null) {
				pis = p.getActiveIdentifiers();
			} else {
				pis = p.getPatientIdentifiers(patientIdentifierType);
			}
			for (PatientIdentifier pi : pis) {
				String link = "<a href="
						+ url
						+ "/patientDashboard.form?patientId="
						+ p.getId()
						+ ">"
						+ (pi != null ? formatPatientIdentifier(pi
								.getIdentifier()) : "(none)") + "</a> ";
				patientLink += link;
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, patientLink);
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// dob
			c = new DataSetColumn("Birthdate", "Birthdate", Integer.class);
			row.addColumnValue(c, formatEncounterDate(p.getBirthdate()));
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

			long weeksDelta = (endDateParameter.getTime() - startDateParameter
					.getTime()) / (MILLISECONDS_PER_WEEK);

			for (int i = 0; i < weeksDelta; i++) {
				Date weekStart = new Date(startDateParameter.getTime()
						+ ((MILLISECONDS_PER_WEEK) * i));
				Date weekEnd = new Date(startDateParameter.getTime()
						+ ((MILLISECONDS_PER_WEEK) * (i + 1)));
				c = new DataSetColumn(columnHeader(concept, i), columnHeader(concept, i),
						String.class);
				List<Obs> obses = Context.getObsService().getObservations(
						Arrays.asList((Person) p), null,
						Arrays.asList(concept), null, null, null, null, 1,
						null, weekStart, weekEnd, false);
				if (!obses.isEmpty()) {
					Obs o = obses.get(0);
					row.addColumnValue(c, calcObsValue(o, weekEnd));
				} else {
					row.addColumnValue(c, "&nbsp;");
				}
			}
			dataSet.addRow(row);
		}
		return dataSet;
	}

	private String formatPatientIdentifier(String id) {
		if (id.lastIndexOf(" ") > 0) {
			// for now assume that an id without leading zeros is there when
			// there is a space
			String number = id.substring(id.lastIndexOf(" ") + 1);
			try {
				DecimalFormat f = new java.text.DecimalFormat("0000");
				number = f.format(new Integer(number));
			} catch (Exception e) {
				// error while converting
				return id;
			}
			return id.substring(0, id.lastIndexOf(" ")) + "-" + number;
		}
		return id;
	}

	protected String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	protected String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
	
	protected String calcObsValue(Obs o, Date endDate) {
		return "" + o.getValueNumeric();
	}

	protected String columnHeader(Concept concept, int i) {
		return concept.getName().getName() +"_" + i;
	}
}
