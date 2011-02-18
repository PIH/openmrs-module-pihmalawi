package org.openmrs.module.pihmalawi.reporting.duplicateSpotter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;

public class SoundexMatcher {

	public SoundexMatcher() {
	}

	public Collection<Patient> soundexMatches(Patient referencePatient,
			List<EncounterType> encounterTypes, boolean swapFirstLastName) {
		String nameMatch = "";
		if (swapFirstLastName) {
			nameMatch = "p1.family_name_code = p2.given_name_code and p1.given_name_code = p2.family_name_code";
		} else {
			nameMatch = "p1.given_name_code = p2.given_name_code and p1.family_name_code = p2.family_name_code";
		}
		String sql = "select pn2.person_id from person_name_code p1, person_name_code p2, person_name pn1, person_name pn2 "
				+ " where pn1.person_id <> pn2.person_id and " + nameMatch
				+ " and pn1.person_id not in (select user_id from users) and pn2.person_id not in (select user_id from users) "
				+ " and pn1.person_name_id=p1.person_name_id and pn2.person_name_id=p2.person_name_id and pn1.person_id= "
				+ referencePatient.getId() + ";";

		Map<Integer, Patient> potentialDuplicates = new HashMap<Integer, Patient>();
		PatientService ps = Context.getPatientService();
		EncounterService es = Context.getEncounterService();
		Query query = sessionFactory().getCurrentSession().createSQLQuery(
				sql.toString());
		Iterator i = query.list().iterator();
		while (i.hasNext()) {
			try {
				Patient p = ps.getPatient((Integer) i.next());
				if (p != null
						&& (p.getGender() == null
								|| referencePatient.getGender() == null || p
								.getGender().equals(
										referencePatient.getGender()))) {
					List<Encounter> encounters = es.getEncounters(p, null,
							null, null, null, encounterTypes, null, false);
					if (!encounters.isEmpty()) {
						potentialDuplicates.put(p.getId(), p);
					}
				}
			} catch (ObjectNotFoundException onfe) {
				// sic, seems to be a user, maybe better filter them in the first place in sql
			}

		}

		return potentialDuplicates.values();
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

}
