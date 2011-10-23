package org.openmrs.module.pihmalawi.reporting;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Relationship;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
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

@Handler(supports = { ApzuTbRegisterDataSetDefinition.class })
public class ApzuTbRegisterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	protected Helper h = new Helper();

	public ApzuTbRegisterDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {

		final Concept TREATMENT_UNIT = Context.getConceptService()
				.getConceptByName("Clinic location other");
		final Concept DOT_OPTION = Context.getConceptService()
				.getConceptByName("Site of TB disease");
		final Concept DURATION_COUGH = Context.getConceptService()
				.getConceptByName("Duration of current cough");
		final Concept TB_CLASSIFICATION = Context.getConceptService()
				.getConceptByName("Tuberculosis case type");
		final Concept HIV_TEST_STATUS = Context.getConceptService()
				.getConceptByName("HIV test history at registration");
		final Concept TIME_OF_HIV_TEST = Context.getConceptService()
				.getConceptByName("");
		final Concept ARV_STATUS = Context.getConceptService()
				.getConceptByName("Taking antiretroviral drugs");
		final Concept CPT_START = Context.getConceptService().getConceptByName(
				"Taking co-trimoxazole preventive therapy");
		final Concept REMARKS = Context.getConceptService().getConceptByName(
				"Comments at conclusion of examination");

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ApzuTbRegisterDataSetDefinition definition = (ApzuTbRegisterDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByName("District TB Number");
		
		Collection<EncounterType> encounterTypes = definition
				.getEncounterTypes();
		final Program tbProgram = h.program("TB program");
		final ProgramWorkflow programWorkflow = h.programWorkflow("TB program", "Tuberculosis treatment status");
		final ProgramWorkflowState onTreatment = h.workflowState("TB program", "Tuberculosis treatment status", "Currently in treatment");

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Location locationParameter = (Location) context
				.getParameterValue("location");
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
		List<Patient> patients = Context.getPatientSetService().getPatients(
				cohort.getMemberIds());

		// sorting like this is expensive and should be somehow provided by the
		// framework!
		sortByIdentifier(patients, patientIdentifierType, locationParameter);

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;

			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDateParameter, null, encounterTypes, null,
					false);
			// take first one for now
			Encounter e = es.isEmpty() ? null : es.get(0);

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
				if (pi != null
						&& pi.getLocation() != null
						&& locationParameter != null
						&& pi.getLocation().getId() == locationParameter
								.getId()) {
					// move preferred prefixed id to front if location was
					// specified
					patientLink = link + patientLink;
				} else {
					patientLink += link;
				}
			}
			// number of tb treatments (only first one taken here)
			c = new DataSetColumn("TB Treatment count", "TB Treatment count",
					String.class);
			row.addColumnValue(c, es.size());
			// reg date
			c = new DataSetColumn("Registration date", "Registration date",
					String.class);
			row.addColumnValue(c,
					formatEncounterDate(startedOn(p, tbProgram, onTreatment)));
			// tb numbers
			c = new DataSetColumn("District TB Number", "#", String.class);
			row.addColumnValue(c, patientLink);

			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			
			// sex
			c = new DataSetColumn("Sex", "Sex", String.class);
			row.addColumnValue(c, p.getGender());
			
			// age
			c = new DataSetColumn("Age (yr)", "Age (yr)", Integer.class);
			row.addColumnValue(c, p.getAge(endDateParameter));
			
			// dob
			c = new DataSetColumn("Birthdate", "Birthdate", Integer.class);
			row.addColumnValue(c, formatEncounterDate(p.getBirthdate()));
			
			// place of residence
			c = new DataSetColumn("Place of residence", "Place of residence",
					String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));
			
			c = new DataSetColumn("Treatment Unit", "Treatment Unit",
					String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, TREATMENT_UNIT));
			
			c = new DataSetColumn("DOT Option", "DOT Option", String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, DOT_OPTION));
			
			c = new DataSetColumn("Duration current cough",
					"Duration current cough", String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, DURATION_COUGH));
			
			c = new DataSetColumn("TB Classification", "TB Classification",
					String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, TB_CLASSIFICATION));
			
			c = new DataSetColumn("Sputum Examination...",
					"Sputum Examination...", String.class);
			
			c = new DataSetColumn("Treatment outcome", "Treatment outcome",
					String.class);
			PatientState ps = null;
			if (locationParameter == null) {
				// outcome from endDate, hopefully the one you are interested in
				ps = h.getMostRecentStateAtDate(p,
						programWorkflow,
						endDateParameter);
			} else {
				// enrollment outcome from location
				ps = h.getMostRecentStateAtLocation(p,
						programWorkflow,
						locationParameter, sessionFactory().getCurrentSession());
			}
		
			c = new DataSetColumn("Outcome", "Outcome", String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
			}
			
			c = new DataSetColumn("Outcome Date", "Outcome Date", String.class);
			if (ps != null) {
				row.addColumnValue(c, formatEncounterDate(ps.getStartDate()));
			}
			
			c = new DataSetColumn("HIV Test Status", "HIV Test Status",
					String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, HIV_TEST_STATUS));
			
			c = new DataSetColumn("Time of HIV Test", "Time of HIV Test",
					String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, TIME_OF_HIV_TEST));
			
			c = new DataSetColumn("ARV Status", "ARV Status", String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, ARV_STATUS));
			
			c = new DataSetColumn("CPT Start date", "CPT Start date",
					String.class);
			row.addColumnValue(c, obsDate(CPT_START, p, e));
			
			c = new DataSetColumn("Remarks", "Remarks", String.class);
			row.addColumnValue(c, obsFromEncounter(p, e, REMARKS));
			
			c = new DataSetColumn("Initial Phase Regimen",
					"Initial Phase Regimen", String.class);
			// row.addColumnValue(c, obsFromEncounter(p, e, ""));
			
			c = new DataSetColumn("Continuation Phase Regimen",
					"Continuation Phase Regimen", String.class);
			// row.addColumnValue(c, obsFromEncounter(p, e, ""));
			
			// vhw given & last
			c = new DataSetColumn("VHW", "VHW", String.class);
			String vhw = "";
			List<Relationship> ships = Context.getPersonService()
					.getRelationshipsByPerson(p);
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByName(
								"Patient/Village Health Worker"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName();
				} else if (r.getRelationshipType().equals(
						Context.getPersonService().getRelationshipTypeByName(
								"Patient/Guardian"))) {
					vhw = r.getPersonB().getGivenName() + " "
							+ r.getPersonB().getFamilyName() + " (Guardian)";
				}
			}
			row.addColumnValue(c, h(vhw));

			programEnrollments(p, row);

			dataSet.addRow(row);
		}
		return dataSet;
	}

	private String obsDate(final Concept CPT_START, Patient p, Encounter e) {
		Obs o = obsFromEncounterAsObs(p, e, CPT_START);
		if (o != null) {
			return formatEncounterDate(o.getObsDatetime());
		} 
		return "&nbsp;";
	}

	private Obs obsFromEncounterAsObs(Patient p, Encounter e, Concept concept) {
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), Arrays.asList(e),
				Arrays.asList(concept), null, null, null, null, 1, null, null,
				null, false);
		if (obs.iterator().hasNext()) {
			return obs.iterator().next();
		}
		return null;
	}

	private String obsFromEncounter(Patient p, Encounter e, Concept concept) {
		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), Arrays.asList(e),
				Arrays.asList(concept), null, null, null, null, 1, null, null,
				null, false);
		if (obs.iterator().hasNext()) {
			return obs.iterator().next().getValueAsString(Context.getLocale());
		}
		return "&nbsp";
	}

	private void sortByIdentifier(List<Patient> patients,
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

	private void tbNumbers(Patient p, PatientIdentifierType piType,
			DataSetRow row) {
		String arvs = "";
		if (piType != null) {
			for (PatientIdentifier pi : p.getPatientIdentifiers(piType)) {
				if (pi != null && pi.getLocation() != null) {
					arvs += pi.getIdentifier() + " ";
				}
			}
			DataSetColumn c = new DataSetColumn("TB #", "TB #", String.class);
			row.addColumnValue(c, arvs);
		}
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

	private void programEnrollments(Patient p, DataSetRow row) {
		DataSetColumn c;
		c = new DataSetColumn("Enrollments", "Enrollments", String.class);
		String programs = "";

		// just collect everything latest program enrollment you can find
		Set<PatientState> pss = h.getMostRecentStates(p, sessionFactory()
				.getCurrentSession());
		if (pss != null) {
			Iterator<PatientState> i = pss.iterator();
			while (i.hasNext()) {
				PatientState ps = i.next();
				programs += ps.getPatientProgram().getProgram().getName()
						+ ":&nbsp;" + ps.getState().getConcept().getName()
						+ "&nbsp;(since&nbsp;"
						+ formatEncounterDate(ps.getStartDate()) + ") ";
			}
		}
		row.addColumnValue(c, h(programs));
	}

	protected String formatEncounterDate(Date encounterDatetime) {
		if (encounterDatetime == null) {
			return "<Unknown>";
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(encounterDatetime);
	}

	protected String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private Date startedOn(Patient p, Program program,
			ProgramWorkflowState firstTimeInState) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState);
		if (ps != null) {
			return ps.getStartDate();
		}
		return null;
	}

}
