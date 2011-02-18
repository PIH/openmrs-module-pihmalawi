package org.openmrs.module.pihmalawi.reporting.duplicateSpotter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.reporting.cohort.query.db.hibernate.HibernateCohortQueryDAO;

public class DuplicatePatientsSpotter {

	Location nno;
	Location touchscreenNno;
	List<Location> locations;
	List<EncounterType> encounterTypes;
	List<EncounterType> touchscreenEncounterTypes;
	Helper h = new Helper();
	EncounterService es;

	public DuplicatePatientsSpotter() {
		es = Context.getEncounterService();

		touchscreenNno = h
				.location("Neno District Hospital - ART Clinic (NNO)");
		nno = h.location("Neno District Hospital");
		locations = Arrays.asList(nno);
		encounterTypes = Arrays
				.asList(h.encounterType("ART_INITIAL"),
						h.encounterType("ART_FOLLOWUP"),
						h.encounterType("PART_INITIAL"),
						h.encounterType("PART_FOLLOWUP"),
						h.encounterType("EID_INITIAL"),
						h.encounterType("EID_FOLLOWUP"));
		touchscreenEncounterTypes = Arrays.asList(h
				.encounterType("REGISTRATION"));

	}

	public List<Patient> spot(Patient p, Date startDate, Set<Integer> patientIds) {

		// List<Encounter> encounters =
		// Context.getEncounterService().getEncounters(p, location, startDate,
		// null, null, encounterTypes, null, false);

		List<Patient> potentialDuplicates = new ArrayList<Patient>();
		for (Integer id : patientIds) {
			// if (id.equals(53846)) {
			Patient possibleDuplicate = Context.getPatientService().getPatient(
					id);
			// check gender
			if (possibleDuplicate.getGender().equals(p.getGender())) {
				// exclude patients
				if (matchingEncounters(p, possibleDuplicate)) {

					// potential duplicate
					potentialDuplicates.add(possibleDuplicate);
				}
			}
			// }
		}

		return potentialDuplicates;
	}

	private boolean matchingEncounters(Patient p, Patient possibleDuplicate) {
		boolean match = false;
		Date lastThreeTouchscreenEncounters[] = lastTouchscreenEncounters(p, 3);
		for (Date onDate : lastThreeTouchscreenEncounters) {
			if (onDate != null) {
				Date day = new Date(onDate.getYear(), onDate.getMonth(),
						onDate.getDate());
				Date oneDayLater = new Date(onDate.getYear(),
						onDate.getMonth(), onDate.getDate() + 1);
				List<Encounter> encounters = es.getEncounters(
						possibleDuplicate, nno, day, oneDayLater, null,
						encounterTypes, null, false);
				if (encounters.isEmpty()) {
					// one isn't matching, stop here
					match = false;
					break;
				} else {
					encounters = es.getEncounters(possibleDuplicate,
							touchscreenNno, day, oneDayLater, null,
							touchscreenEncounterTypes, null, false);
					if (encounters.isEmpty()) {
						// no touchscreen encounter on this day
						match = true;
					} else {
						match = false;
						break;
					}
				}
			}
		}
		return match;
	}

	private Date[] lastTouchscreenEncounters(Patient p, int mostRecent) {
		Date result[] = new Date[3];
		List<Encounter> encounters = es.getEncounters(p, touchscreenNno, null,
				null, null, touchscreenEncounterTypes, null, false);
		int size = encounters.size();
		if (size > 0) {
			result[0] = encounters.get(size - 1).getEncounterDatetime();
		}
		if (size > 1) {
			result[1] = encounters.get(size - 2).getEncounterDatetime();
		}
		if (size > 2) {
			result[2] = encounters.get(size - 3).getEncounterDatetime();
		}
		return result;
	}
}
