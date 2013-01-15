package org.openmrs.module.pihmalawi.reports.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.reports.PatientDataHelper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.*;

// todo, should/could be migrated to the HtmlBreakdownDataSet
@Handler(supports = { AppointmentAdherencePatientDataSetDefinition.class })
public class AppointmentAdherencePatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	private final static long MILLISECONDS_PER_WEEK = (long) 7 * 24 * 60 * 60 * 1000;

	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		AppointmentAdherencePatientDataSetDefinition definition = (AppointmentAdherencePatientDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition.getPatientIdentifierType();
		List<EncounterType> ets = definition.getEncounterTypes();
		Session session = sessionFactory().getCurrentSession();

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		Date endDateParameter = (Date) context.getParameterValue("endDate");
		if (endDateParameter == null) {
			// default to today
			endDateParameter = new Date();
		}
		Date startDateParameter = (Date) context.getParameterValue("startDate");
		if (startDateParameter == null) {
			startDateParameter = new Date(0);
		}
		Location location = (Location) context.getParameterValue("location");

		// By default, get all patients
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}

		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());

		PatientDataHelper pdh = new PatientDataHelper();

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();

			pdh.addCol(row, "#", p.getPatientId());
			pdh.addCol(row, "Facility", (location == null ? "" : location.getName()));
			pdh.addCol(row, "Identifier", pdh.preferredIdentifierAtLocation(p, patientIdentifierType, location));
			pdh.addCol(row, "Given", pdh.getGivenName(p));
			pdh.addCol(row, "Last", pdh.getFamilyName(p));
			pdh.addCol(row, "Birthdate", pdh.getBirthdate(p));
			pdh.addCol(row, "M/F", pdh.getGender(p));
			pdh.addCol(row, "Village", pdh.getVillage(p));
			pdh.addCol(row, "VHW", pdh.vhwName(p, false));

			Obs mostRecentDxDate = pdh.getLatestObs(p, "DATE OF HIV DIAGNOSIS", null, endDateParameter);
			pdh.addCol(row, "HIV dx date", pdh.formatValueDatetime(mostRecentDxDate));

			Map<String, String> reasonsForStartingArvs = pdh.getReasonStartingArvs(p, endDateParameter);
			for (String reasonKey : reasonsForStartingArvs.keySet()) {
				pdh.addCol(row, "ARV Reason " + reasonKey, reasonsForStartingArvs.get(reasonKey));
			}

			Program hivProgram = MetadataLookup.program("HIV program");
			ProgramWorkflow hivTreatmentStatus = MetadataLookup.programWorkflow("HIV program", "Treatment status");
			ProgramWorkflowState onArvState = MetadataLookup.workflowState("HIV program", "Treatment status", "On antiretrovirals");

			PatientProgram latestHivProgramEnrollment = new ProgramHelper().getMostRecentProgramEnrollmentAtLocation(p, hivProgram, location, session);
			PatientState latestOnArvsState = new ProgramHelper().getMostRecentStateAtLocation(p, Arrays.asList(onArvState), location, session);
			PatientState latestTxStatusState = new ProgramHelper().getMostRecentStateAtLocation(p, hivTreatmentStatus, location, session);
			Date arvStartDate = (latestOnArvsState == null ? null : latestOnArvsState.getStartDate());

			pdh.addCol(row, "ARV Start Date", pdh.formatStateStartDate(latestOnArvsState));
			pdh.addCol(row, "HIV Tx Status", pdh.formatStateName(latestTxStatusState));
			pdh.addCol(row, "HIV Tx Status Date", pdh.formatStateStartDate(latestTxStatusState));

			Obs nextApptDate = pdh.getLatestObs(p, "Appointment date", ets, null); // This is as of the report generation date
			pdh.addCol(row, "Next Appt Date", pdh.formatValueDatetime(nextApptDate));

			Obs weightAtArtStart = pdh.getLatestObs(p, "Weight (kg)", null, arvStartDate);
			pdh.addCol(row, "ART START WT", pdh.formatValue(weightAtArtStart));

			Obs weight = pdh.getLatestObs(p, "Weight (kg)", null, endDateParameter);
			pdh.addCol(row, "LAST WT", pdh.formatValue(weight));
			pdh.addCol(row, "LAST WT DATE", pdh.formatObsDatetime(weight));

			Obs heightAtArtStart = pdh.getLatestObs(p, "Height (cm)", null, arvStartDate);
			pdh.addCol(row, "ART START HT", pdh.formatValue(heightAtArtStart));

			Obs height = pdh.getLatestObs(p, "Height (cm)", null, endDateParameter);
			pdh.addCol(row, "LAST HT", pdh.formatValue(height));
			pdh.addCol(row, "LAST HT DATE", pdh.formatObsDatetime(height));

			// app adherence

			Map<Date, Integer> daysMissedByScheduledVisitDate = new TreeMap<Date, Integer>();

			// consider adherence between the patient's arv start date and program completion date, as well as report parameters
			Date adherencePeriodStart = startDateParameter;
			if (arvStartDate != null && (adherencePeriodStart == null || arvStartDate.after(adherencePeriodStart))) {
				adherencePeriodStart = arvStartDate;
			}

			Date adherencePeriodEnd = endDateParameter;
			if (latestHivProgramEnrollment != null && latestHivProgramEnrollment.getDateCompleted() != null) {
				if (adherencePeriodEnd == null || latestHivProgramEnrollment.getDateCompleted().before(adherencePeriodEnd)) {
					adherencePeriodEnd = latestHivProgramEnrollment.getDateCompleted();
				}
			}

			pdh.addCol(row, "Adherence Period Start", pdh.formatYmd(adherencePeriodStart));
			pdh.addCol(row, "Adherence Period End", pdh.formatYmd(adherencePeriodEnd));

			// Get all scheduled and actual encounters during this period
			Set<Date> scheduledVisits = new TreeSet<Date>();
			Set<Date> actualVisits = new TreeSet<Date>();

			List<Encounter> es = Context.getEncounterService().getEncounters(p, location, adherencePeriodStart, adherencePeriodEnd, null, ets, null, false);
			for (Encounter e : es) {
				actualVisits.add(e.getEncounterDatetime());
			}
			if (es.size() > 0) {
				Concept apptDateConcept = MetadataLookup.concept("Appointment date");
				for (Obs o : Context.getObsService().getObservations(Arrays.asList((Person) p), es, Arrays.asList(apptDateConcept), null, null, null, null, null, null, null, null, false)) {
					if (o.getValueDatetime() != null) {
						scheduledVisits.add(o.getValueDatetime());
					}
				}
			}

			// Remove the first actual visit in the list since it does not correspond to a scheduled visit date from a previous visit
			if (actualVisits.size() > 0) {
				Iterator<Date> i = actualVisits.iterator();
				i.next();
				i.remove();
			}

			pdh.addCol(row, "Num Scheduled Visits", scheduledVisits.size());
			pdh.addCol(row, "Num Actual Visits", actualVisits.size());

			// Iterate across all of the scheduled visit dates that fall within the period
			for (Date scheduledVisitDate : scheduledVisits) {

				log.debug("Looking at scheduled visit for: " + scheduledVisitDate);

				// Find the actual visit date that is is closest to the scheduled visit date
				Date closestVisitDate = null;
				for (Iterator<Date> i = actualVisits.iterator(); i.hasNext();) {
					Date actualVisitDate = i.next();
					if (actualVisitDate.compareTo(scheduledVisitDate) <= 0 || closestVisitDate == null) {
						closestVisitDate = actualVisitDate;
						i.remove();
					}
				}
				if (closestVisitDate == null) {
					closestVisitDate = adherencePeriodEnd;
				}

				log.debug("Found closest matching visit of: " + closestVisitDate);

				int daysBetweenScheduledAndActualVisit = daysBetween(scheduledVisitDate, closestVisitDate);
				daysMissedByScheduledVisitDate.put(scheduledVisitDate, daysBetweenScheduledAndActualVisit);

				log.debug("Days missed for this scheduled date: " + daysBetweenScheduledAndActualVisit);
			}

			Integer adherencePeriodDays = daysBetween(adherencePeriodStart, adherencePeriodEnd);

			List<Integer> intervals = new ArrayList<Integer>();
			intervals.add(null);  // Represents the overall total interval
			for (int i=90; i<=360; i+=90) {
				intervals.add(i);  // Every 90 days for first year
			}
			for (int i=180; i<=3600; i+=180) { // Every 180 days for 10 more years
				intervals.add(i);
			}

			for (Integer interval : intervals) {

				if (interval == null || adherencePeriodDays >= interval) {

					int missedDays = 0;
					int overallDays = (interval != null ? interval : adherencePeriodDays);
					int lateAppts = 0;
					int overallAppts = 0;

					for (Date d : daysMissedByScheduledVisitDate.keySet()) {
						int daysFromStart = daysBetween(adherencePeriodStart, d);
						if (interval == null || daysFromStart <= interval) {
							Integer daysFromDate = daysMissedByScheduledVisitDate.get(d);
							if (daysFromDate > 2) {
								missedDays += daysFromDate;
								lateAppts++;
							}
							overallAppts++;
						}
					}

					String prefix = (interval == null ? "" : interval + "d");
					pdh.addCol(row, prefix + "DaysMissed", missedDays);
					pdh.addCol(row, prefix + "DaysOverall", overallDays);
					pdh.addCol(row, prefix + "OntimeAppts", (overallAppts-lateAppts));
					pdh.addCol(row, prefix + "OverallAppts", overallAppts);
				}
			}

			dataSet.addRow(row);
		}
		return dataSet;
	}

	private int daysBetween(Date from, Date to) {
		return (int)((to.getTime() - from.getTime())/1000/60/60/24);

	}

	private SessionFactory sessionFactory() {
		return Context.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(0).getSessionFactory();
	}
}
