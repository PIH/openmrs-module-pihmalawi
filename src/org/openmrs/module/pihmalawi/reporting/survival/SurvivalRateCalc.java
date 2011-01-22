package org.openmrs.module.pihmalawi.reporting.survival;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hsqldb.lib.Iterator;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;

public class SurvivalRateCalc {

	public String[] outcome(Patient p, Location location, int monthsInProgram,
			Program program) {

		String outcomeAndLocation[] = new String[3];
		
		outcomeAndLocation[0] = "Unknown outcome";
		outcomeAndLocation[1] = "Unknown location";
		outcomeAndLocation[2] = "Unknown start date on art";

		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p, program, null, null, null, null, false);
		for (PatientProgram pp : pps) {
			if (!pp.isVoided()) {
				try {
					// todo: check if there are mutliple programs for the same location
					Location enrollmentLocation = getEnrollmentLocation(pp);
					if ((location == null)
							|| (enrollmentLocation != null & enrollmentLocation
									.getId() == location.getId())) {
						PatientState firstOnArtState = getFirstOnArtStateAtLocation(
								p, pp);
						if (firstOnArtState != null) {
							Date enrolledDate = firstOnArtState.getStartDate();
							Calendar c = Calendar.getInstance();
							c.setTime(enrolledDate);
							c.add(Calendar.MONTH, monthsInProgram);
							Date outcomeOn = c.getTime();

							PatientState outcomeAt = null;
							Set<PatientState> states = pp.getStates();

							// would be nice if I could assume that PatientState
							// is
							// ordered by start date, but it is not! makes me
							// use a
							// crowbar to punch it in right now
							HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
							ArrayList<Long> stupidListConverter = new ArrayList<Long>();
							for (PatientState ps : states) {
								if (!ps.isVoided() 
										&& ps.getState().getProgramWorkflow().getId()  == 1 // treatment status
										&& !ps.getState().isRetired() && !ps.getState().getConcept().isRetired()) {
									if (ps.getStartDate().equals(ps.getEndDate())) {
										// special treatment for following started and stopped on same date and on art started
									} else {
									validPatientStates.put(ps.getStartDate()
											.getTime(), ps);
									stupidListConverter.add(ps.getStartDate()
											.getTime());
									}
								}
							}
							Collections.<Long> sort(stupidListConverter);

							for (Long key : stupidListConverter) {
								PatientState state = (PatientState) validPatientStates
										.get(key);
								System.out.println(state.getState()
										.getConcept().getName());
								if (state.getStartDate().before(enrolledDate)) {
									// state too early
								} else {
									boolean before = state.getStartDate()
											.before(outcomeOn);

									if (before) {
										// so far most recent state
										outcomeAt = state;
										outcomeAndLocation[0] = outcomeAt.getState()
												.getConcept().getName()
												.getName();
										outcomeAndLocation[1] = getEnrollmentLocation(outcomeAt.getPatientProgram()).getName();
										outcomeAndLocation[2] = outcomeAt.getStartDate() +  "";
																			System.out
												.println("most recent state at "
														+ outcomeOn
														+ " "
														+ outcomeAt
																.getActive(outcomeOn)
														+ " - "
														+ outcomeAt.getState()
														.getConcept()
														.getName()
												+ " - "
												+ outcomeAt.getVoided()
												+ " - "
												+ outcomeAt.getId()
										+ " - "
														+ outcomeAt
																.getStartDate());
									} else {
										// assume that state are linear in time,
										// stop if
										// more
										// recent one is found
										System.out.println("NOPE");
										break;
									}

								}
							}
						}
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
		return outcomeAndLocation;
	}

	private PatientState getFirstOnArtStateAtLocation(Patient p,
			PatientProgram pp) {

		ProgramWorkflowState onArt = Context.getProgramWorkflowService()
				.getProgramByName("HIV PROGRAM")
				.getWorkflowByName("TREATMENT STATUS")
				.getStateByName("ON ANTIRETROVIRALS");

		HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
		ArrayList<Long> stupidListConverter = new ArrayList<Long>();
		for (PatientState ps : pp.getStates()) {
			if (!ps.isVoided() && ps.getState().getId().equals(onArt.getId())) {
				validPatientStates.put(ps.getStartDate().getTime(), ps);
				stupidListConverter.add(ps.getStartDate().getTime());
			}
		}
		Collections.<Long> sort(stupidListConverter);

		for (Long key : stupidListConverter) {
			PatientState state = (PatientState) validPatientStates.get(key);
			// just take the first one
			return state;
		}
		return null;
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	public Location getEnrollmentLocation(PatientProgram pp) {
		String sql = "select location_id from patient_program where patient_program_id = "
				+ pp.getId();

		Query query = sessionFactory().getCurrentSession().createSQLQuery(
				sql.toString());
		// assume there is only one
		if (!query.list().isEmpty() && query.list().iterator().next() != null) {
			return Context.getLocationService().getLocation(
					((Integer) (query.list().iterator().next())).intValue());
		}
		return null;
	}

}
