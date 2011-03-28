package org.openmrs.module.pihmalawi.reporting;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Relationship;
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

@Handler(supports = { ApzuPatientDataSetDefinition.class })
public class ApzuPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public ApzuPatientDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		final Concept DEFAULTER_ACTION_TAKEN = Context.getConceptService()
				.getConceptByName("DEFAULTER ACTION TAKEN");
		final Concept CD4_COUNT = Context.getConceptService().getConceptByName(
				"CD4 COUNT");
		final Concept APPOINTMENT_DATE = Context.getConceptService().getConceptByName("APPOINTMENT DATE");
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ApzuPatientDataSetDefinition definition = (ApzuPatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition
				.getPatientIdentifierType();
		Collection<EncounterType> encounterTypes = definition
				.getEncounterTypes();

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
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// age
			c = new DataSetColumn("Age", "Age", Integer.class);
			row.addColumnValue(c, p.getAge());
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			// last visit & loc
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(p, null, null, null, null, encounterTypes,
							null, false);
			c = new DataSetColumn("Last visit", "Last visit", String.class);
			if (!encounters.isEmpty()) {
				c = new DataSetColumn("Last visit", "Last visit", String.class);
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c,
						formatEncounterDate(e.getEncounterDatetime()) + " ("
								+ e.getLocation() + ")");
				if (definition.getIncludeFirstVisit()) {
					c = new DataSetColumn("First visit", "First visit",
							String.class);
					e = encounters.get(0);
					row.addColumnValue(c,
							formatEncounterDate(e.getEncounterDatetime()));
				}
				// rvd from last encounter
				// String rvd = "";
				// Set<Obs> observations = e.getObs();
				// for (Obs o : observations) {
				// if
				// (o.getConcept().equals(Context.getConceptService().getConceptByName("APPOINTMENT DATE")))
				// {
				// rvd = o.getValueAsString(Context.getLocale());
				// c = new DataSetColumn("RVD", "RVD", String.class);
				// row.addColumnValue(c, h(rvd));
				// }
				// }
			} else {
				row.addColumnValue(c, h(""));
			}
			// rvd
			String rvd = "";
			// getting rvd from valid encounters
			encounters = Context.getEncounterService().getEncounters(p, null,
					null, null, null, encounterTypes, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList(Context.getPersonService().getPerson(
							p.getPersonId())), encounters, Arrays.asList(APPOINTMENT_DATE), null, null,
					null, null, 1, null, null, null, false);
			// getting rvd from any concept of person across encounters
			// Context.getObsService().getObservationsByPersonAndConcept(p,
			// Context.getConceptService().getConceptByName("APPOINTMENT DATE"));
			Iterator<Obs> i = obs.iterator();
			if (i.hasNext()) {
				rvd = i.next().getValueAsString(Context.getLocale());
			}
			c = new DataSetColumn("RVD", "RVD", String.class);
			row.addColumnValue(c, h(rvd));
			// vhw given & last
			c = new DataSetColumn("VHW", "VHW", String.class);
			String vhw = "";
			List<Relationship> ships = Context.getPersonService()
					.getRelationshipsByPerson(p);
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByUuid(
								"19124428-a89d-11df-bba5-000c297f1161"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName();
				} else if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByUuid(
								"19124310-a89d-11df-bba5-000c297f1161"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName() + " (Guardian)";
				}
			}
			row.addColumnValue(c, h(vhw));
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));

			// columns for cross-checking
			if (definition.isIncludeMissedappointmentColumns()) {
				// verified
				c = new DataSetColumn("verified", "verified", String.class);
				row.addColumnValue(c, h(""));
				// not verified
				c = new DataSetColumn("unable to verify", "unable to verify",
						String.class);
				row.addColumnValue(c, h(""));
				// missed data entry
				c = new DataSetColumn("missed data entry", "missed data entry",
						String.class);
				row.addColumnValue(c, h(""));
			}
			// comment
			String comment = "";
			if (definition.isIncludeDefaulterActionTaken()) {
				obs = Context.getObsService()
						.getObservationsByPersonAndConcept(p,
								DEFAULTER_ACTION_TAKEN);
				for (Obs o : obs) {
					comment += o.getValueAsString(Context.getLocale());
					comment += ":&nbsp;("
							+ formatEncounterDate(o.getObsDatetime()) + ") ";
				}
				obs = Context.getObsService()
						.getObservationsByPersonAndConcept(p, CD4_COUNT);
				if (obs.iterator().hasNext()) {
					Obs o = obs.iterator().next();
					comment += "CD4:&nbsp;"
							+ o.getValueAsString(Context.getLocale());
					comment += ":&nbsp;("
							+ formatEncounterDate(o.getObsDatetime()) + ") ";
				}
				List<Encounter> encs = Context.getEncounterService()
						.getEncountersByPatient(p);
				if (encs.size() > 0) {
					Encounter e = encs.get(encs.size() - 1);
					comment += e.getEncounterType().getName();
					comment += ":&nbsp;("
							+ formatEncounterDate(e.getEncounterDatetime())
							+ ") ";
				}
			}
			c = new DataSetColumn("comment", "comment", String.class);
			row.addColumnValue(c, h(comment));

			// chronicCare(p, row);

			dataSet.addRow(row);
		}
		return dataSet;
	}

	private void chronicCare(Patient p, DataSetRow row) {
		DataSetColumn c;
		List<Obs> obs;
		Iterator<Obs> i;
		// chronic care additions
		// deceased
		c = new DataSetColumn("Status", "Status", String.class);
		boolean deceased = (p.getDead() || p.getCauseOfDeath() != null || p
				.getDeathDate() != null);
		row.addColumnValue(c, (deceased ? "died" : "&nbsp;"));

		// cc diagnose
		String diag = "";
		obs = Context.getObsService().getObservationsByPersonAndConcept(
				p,
				Context.getConceptService().getConceptByName(
						"CHRONIC CARE DIAGNOSIS"));
		i = obs.iterator();
		while (i.hasNext()) {
			diag += i.next().getValueAsString(Context.getLocale()) + ", ";
		}
		c = new DataSetColumn("Diagnose", "Diagnose", String.class);
		row.addColumnValue(c, h(diag));
	}

	private String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
}
