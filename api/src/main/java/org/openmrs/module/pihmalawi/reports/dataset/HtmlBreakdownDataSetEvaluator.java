package org.openmrs.module.pihmalawi.reports.dataset;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reports.renderer.BreakdownRowRenderer;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { HtmlBreakdownDataSetDefinition.class })
public class HtmlBreakdownDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public HtmlBreakdownDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

		PatientIdentifierType patientIdentifierType = ((HtmlBreakdownDataSetDefinition) dataSetDefinition).getPatientIdentifierType();
		String breakdownRowRendererClassname = ((HtmlBreakdownDataSetDefinition) dataSetDefinition).getHtmlBreakdownPatientRowClassname();
		BreakdownRowRenderer renderer = null;
		try {
			Class myClass = Class.forName(breakdownRowRendererClassname);
			Object newInstance = myClass.newInstance();
			renderer = (BreakdownRowRenderer) newInstance;
		} catch (ClassNotFoundException e) {
			log.error(e);
		} catch (InstantiationException e) {
			log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		}
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Location locationParameter = (Location) context.getParameterValue("location");
		Date startDateParameter = (Date) context.getParameterValue("startDate");
		Date endDateParameter = (Date) context.getParameterValue("endDate");
		if (endDateParameter == null) {
			// default to today
			endDateParameter = new Date();
		}
		// By default, get all patients
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}
		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());

		// sorting like this is expensive and should be somehow provided by the
		// framework!
		sortByIdentifier(patients, patientIdentifierType, locationParameter);

		for (Patient p : patients) {
			try {
				dataSet.addRow(renderer.renderRow(p, patientIdentifierType, locationParameter, startDateParameter, endDateParameter));
			} catch (Throwable t) {
				log.error(t);
			}
		}
		return dataSet;
	}

	protected void sortByIdentifier(List<Patient> patients,
			final PatientIdentifierType patientIdentifierType,
			final Location locationParameter) {
		Collections.sort(patients, new Comparator<Patient>() {
			@Override
			public int compare(Patient p1, Patient p2) {

				String p1ids = allIds(patientIdentifierType, locationParameter,
						p1);
				String p2ids = allIds(patientIdentifierType, locationParameter,
						p2);

				return p1ids.compareToIgnoreCase(p2ids);
			}

			private String allIds(
					final PatientIdentifierType patientIdentifierType,
					final Location locationParameter, Patient p) {
				String allIds = "";
				List<PatientIdentifier> pis = null;
				if (patientIdentifierType == null) {
					pis = p.getActiveIdentifiers();
				} else {
					pis = p.getPatientIdentifiers(patientIdentifierType);
				}
				for (PatientIdentifier pi : pis) {
					String id = (pi != null ? formatPatientIdentifier(pi
							.getIdentifier()) : "(none) ");
					if (pi != null
							&& pi.getLocation() != null
							&& locationParameter != null
							&& pi.getLocation().getId() == locationParameter
									.getId()) {
						// move preferred prefixed id to front if location was
						// specified
						allIds = id + allIds;
					} else {
						allIds += id;
					}
				}
				return allIds;
			}
		});
	}
	
	private String formatPatientIdentifier(String id) {
		if (id.endsWith(" HCC")) {
			try {
				int firstSpace = id.indexOf(" ");
				int lastSpace = id.lastIndexOf(" ");
				String number = id.substring(firstSpace + 1, lastSpace);
				DecimalFormat f = new java.text.DecimalFormat("0000");
				number = f.format(new Integer(number));
				return id.substring(0, firstSpace) + "-" + number + "-HCC";
			} catch (Exception e) {
				// error while converting
				return id;
			}
		} else {
			if (id.lastIndexOf(" ") > 0) {
				try {
					// for now assume that an id without leading zeros is there when
					// there is a space
					String number = id.substring(id.lastIndexOf(" ") + 1);
					DecimalFormat f = new java.text.DecimalFormat("0000");
					number = f.format(new Integer(number));
					return id.substring(0, id.lastIndexOf(" ")) + "-" + number;
				} catch (Exception e) {
					// error while converting
					return id;
				}
			}
			return id;
		}
	}
}
