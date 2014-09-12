package org.openmrs.module.pihmalawi.reports.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reports.PatientDataHelper;
import org.openmrs.module.pihmalawi.reports.renderer.ArtRegisterBreakdownRenderer;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
        ArtRegisterBreakdownRenderer brr = new ArtRegisterBreakdownRenderer();
		HivMetadata hivMetadata = new HivMetadata();
		CommonMetadata commonMetadata = new CommonMetadata();

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();

			pdh.addCol(row, "#", p.getPatientId());
            pdh.addCol(row, "Given Name", pdh.getGivenName(p));
            pdh.addCol(row, "Family Name", pdh.getFamilyName(p));
			pdh.addCol(row, "Facility", (location == null ? "" : location.getName()));
			pdh.addCol(row, "Identifier", pdh.preferredIdentifierAtLocation(p, patientIdentifierType, location));
			pdh.addCol(row, "Birthdate", p.getBirthdate());
			pdh.addCol(row, "M/F", pdh.getGender(p));
			pdh.addCol(row, "TA", pdh.getTraditionalAuthority(p));
			pdh.addCol(row, "Village", pdh.getVillage(p));
			pdh.addCol(row, "VHW", pdh.vhwName(p, false));

			Obs mostRecentDxDate = pdh.getLatestObs(p, "DATE OF HIV DIAGNOSIS", null, endDateParameter);
			pdh.addCol(row, "HIV dx date", pdh.getValueDatetime(mostRecentDxDate));

			Map<String, String> reasonsForStartingArvs = pdh.getReasonStartingArvs(p, endDateParameter);
			for (String reasonKey : reasonsForStartingArvs.keySet()) {
				pdh.addCol(row, "ARV Reason " + reasonKey, reasonsForStartingArvs.get(reasonKey));
			}

			Program hivProgram = hivMetadata.getHivProgram();
			ProgramWorkflow hivTreatmentStatus = hivMetadata.getTreatmentStatusWorkfow();
			ProgramWorkflowState onArvState = hivMetadata.getOnArvsState();

			PatientState earliestOnArvsState = new ProgramHelper().getFirstTimeInState(p, hivProgram, onArvState, endDateParameter);
			Date arvStartDate = (earliestOnArvsState == null ? null : earliestOnArvsState.getStartDate());

			pdh.addCol(row, "First On ARVs State Start Date", arvStartDate);

			PatientState earliestOnArvsStateAtLocation = new ProgramHelper().getFirstTimeInStateAtLocation(p, hivProgram, onArvState, endDateParameter, location);
			Date arvStartDateAtLocation = (earliestOnArvsStateAtLocation == null ? null : earliestOnArvsStateAtLocation.getStartDate());
			pdh.addCol(row, "First On ARVs State Start Date At Location", arvStartDateAtLocation);

			List<EncounterType> artEncounterTypes = hivMetadata.getArtEncounterTypes();
			Encounter firstArtEncounter = pdh.getFirstEncounterOfType(p, artEncounterTypes, endDateParameter);

            pdh.addCol(row, "First ART Encounter Date", (firstArtEncounter == null ? null : firstArtEncounter.getEncounterDatetime()));

            Encounter firstArtEncounterAtLocation = pdh.getFirstEncounterAtLocationOfType(p, artEncounterTypes, endDateParameter, location);
            pdh.addCol(row, "First ART Encounter Date at Location", firstArtEncounterAtLocation == null ? null : firstArtEncounterAtLocation.getEncounterDatetime());

			PatientState latestTxStatusStateAtLocation = new ProgramHelper().getMostRecentStateAtLocationAndDate(p, hivTreatmentStatus, location, endDateParameter);

			pdh.addCol(row, "HIV Tx Status at Location", pdh.formatStateName(latestTxStatusStateAtLocation));
			pdh.addCol(row, "HIV Tx Status at Location Date", pdh.getStateStartDate(latestTxStatusStateAtLocation));

			PatientState latestTxStatusStateOverall = new ProgramHelper().getMostRecentStateAtDate(p, hivTreatmentStatus, endDateParameter);
			pdh.addCol(row, "HIV Tx Status Overall", pdh.formatStateName(latestTxStatusStateOverall));
			pdh.addCol(row, "HIV Tx Status Overall Date", pdh.getStateStartDate(latestTxStatusStateOverall));

			Obs nextApptDate = pdh.getLatestObs(p, "Appointment date", ets, null); // This is as of the report generation date
			pdh.addCol(row, "Next Appt Date", pdh.getValueDatetime(nextApptDate));

			Obs weightAtArtStart = pdh.getLatestObs(p, "Weight (kg)", null, arvStartDate);
			pdh.addCol(row, "ART START WT", pdh.formatValue(weightAtArtStart));

			Obs weight = pdh.getLatestObs(p, "Weight (kg)", null, endDateParameter);
			pdh.addCol(row, "LAST WT", pdh.formatValue(weight));
			pdh.addCol(row, "LAST WT DATE", pdh.getObsDatetime(weight));

			Obs heightAtArtStart = pdh.getLatestObs(p, "Height (cm)", null, arvStartDate);
			pdh.addCol(row, "ART START HT", pdh.formatValue(heightAtArtStart));

			Obs height = pdh.getLatestObs(p, "Height (cm)", null, endDateParameter);
			pdh.addCol(row, "LAST HT", pdh.formatValue(height));
			pdh.addCol(row, "LAST HT DATE", pdh.getObsDatetime(height));

			// app adherence

			Map<Date, Integer> daysMissedByScheduledVisitDate = new TreeMap<Date, Integer>();

			// consider adherence between the patient's arv start date and program completion date, as well as report parameters
			Date adherencePeriodStart = startDateParameter;
			if (arvStartDate != null && (adherencePeriodStart == null || arvStartDate.after(adherencePeriodStart))) {
				adherencePeriodStart = arvStartDate;
			}

			PatientProgram latestHivProgramEnrollment = new ProgramHelper().getMostRecentProgramEnrollment(p, hivProgram, endDateParameter);

			Date adherencePeriodEnd = endDateParameter;
			if (latestHivProgramEnrollment != null && latestHivProgramEnrollment.getDateCompleted() != null) {
				if (adherencePeriodEnd == null || latestHivProgramEnrollment.getDateCompleted().before(adherencePeriodEnd)) {
					adherencePeriodEnd = latestHivProgramEnrollment.getDateCompleted();
				}
			}

			pdh.addCol(row, "Adherence Period Start", adherencePeriodStart);
			pdh.addCol(row, "Adherence Period End", adherencePeriodEnd);

			// Get all scheduled and actual encounters during this period
			Set<Date> scheduledVisits = new TreeSet<Date>();
			Map<Date, Boolean> actualVisits = new TreeMap<Date, Boolean>(); // Dates of actual visits, and true if they are at the passed location

			List<Encounter> es = Context.getEncounterService().getEncounters(p, null, adherencePeriodStart, adherencePeriodEnd, null, ets, null, false);
			for (Encounter e : es) {
				actualVisits.put(e.getEncounterDatetime(), e.getLocation().equals(location));
			}
			if (es.size() > 0) {
				Concept apptDateConcept = commonMetadata.getAppointmentDateConcept();
				for (Obs o : Context.getObsService().getObservations(Arrays.asList((Person) p), es, Arrays.asList(apptDateConcept), null, null, null, null, null, null, null, null, false)) {
					if (o.getValueDatetime() != null && o.getValueDatetime().compareTo(adherencePeriodEnd) <= 0) {
						scheduledVisits.add(o.getValueDatetime());
					}
				}
			}

			// Remove the first actual visit in the list since it does not correspond to a scheduled visit date from a previous visit
			if (actualVisits.size() > 0) {
				Iterator<Date> i = actualVisits.keySet().iterator();
				i.next();
				i.remove();
			}

			pdh.addCol(row, "Num Scheduled Visits", scheduledVisits.size());
			pdh.addCol(row, "Num Actual Visits", actualVisits.size());

			// Determine what dates the patient was "enrolled" in the passed in Location, based on encounter data

			Map<Date, Date> datesAtLocation = new LinkedHashMap<Date, Date>();
			Date currentPeriodStart = null;
			boolean lastVisitAtLocation = false;
			for (Date d1 : actualVisits.keySet()) {
				if (currentPeriodStart == null) {
					currentPeriodStart = adherencePeriodStart;
				}
				lastVisitAtLocation = actualVisits.get(d1);

				if (lastVisitAtLocation) {
					datesAtLocation.put(currentPeriodStart, d1);
				}
				currentPeriodStart = addDays(d1, 1);
			}
			if (lastVisitAtLocation) {
				datesAtLocation.put(currentPeriodStart, adherencePeriodEnd);
			}

			int overallDaysAtLocation = 0;
			int missedDaysAtLocation = 0;
			int lateApptsAtLocation = 0;
			int overallApptsAtLocation = 0;

			for (Map.Entry<Date, Date> e : datesAtLocation.entrySet()) {
				overallDaysAtLocation += numDaysInRange(e.getKey(), e.getValue());
			}

			// Iterate across all of the scheduled visit dates that fall within the period
			for (Date scheduledVisitDate : scheduledVisits) {

				log.debug("Looking at scheduled visit for: " + scheduledVisitDate);

				// Find the actual visit date that is is closest to the scheduled visit date
				Date closestVisitDate = null;
				for (Iterator<Date> i = actualVisits.keySet().iterator(); i.hasNext();) {
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

				// If the patient is "enrolled" at the current location at the visit date, include them for the location
				for (Map.Entry<Date, Date> e : datesAtLocation.entrySet()) {
					if (isDateInRange(closestVisitDate, e.getKey(), e.getValue())) {
						int daysFromDate = daysBetweenScheduledAndActualVisit - 2;
						if (daysFromDate > 0) {
							missedDaysAtLocation += daysFromDate;
							lateApptsAtLocation++;
						}
						overallApptsAtLocation++;
					}
				}

				log.debug("Days missed for this scheduled date: " + daysBetweenScheduledAndActualVisit);
			}

			// First report on overall adherence at the report Location
			pdh.addCol(row, "LocationDaysMissed", missedDaysAtLocation);
			pdh.addCol(row, "LocationDaysOverall", overallDaysAtLocation);
			pdh.addCol(row, "LocationOntimeAppts", (overallApptsAtLocation-lateApptsAtLocation));
			pdh.addCol(row, "LocationOverallAppts", overallApptsAtLocation);

			// Now look at adherence overall for the patient across all Locations

			Integer adherencePeriodDays = numDaysInRange(adherencePeriodStart, adherencePeriodEnd);

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
							Integer daysFromDate = daysMissedByScheduledVisitDate.get(d) - 2;
							if (interval != null && (daysFromStart + daysFromDate > interval)) {
								daysFromDate = interval - daysFromStart;
							}
							if (daysFromDate > 0) {
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

	private int numDaysInRange(Date from, Date to) {
		int d = daysBetween(DateUtil.getStartOfDay(from), DateUtil.getStartOfDay(to));
		return d < 0 ? (d-1) : (d+1);
	}

	private Date addDays(Date d, int increment) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	private boolean isDateInRange(Date dateToCheck, Date lowerBound, Date upperBound) {
		if (dateToCheck.getTime() >= lowerBound.getTime()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if (df.format(dateToCheck).compareTo(df.format(upperBound)) <= 0) {
				return true;
			}
		}
		return false;
	}

	private SessionFactory sessionFactory() {
		return Context.getRegisteredComponents(SessionFactory.class).get(0);
	}
}
