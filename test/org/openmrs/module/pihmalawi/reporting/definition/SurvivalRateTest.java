package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.pihmalawi.reporting.survival.SurvivalRateCalc;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class SurvivalRateTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	@Test
	@Rollback(false)
	public void setupHivWeekly() throws Exception {
		Program program = Context.getProgramWorkflowService().getProgramByName(
				"HIV PROGRAM");
		int monthsInProgram = 12;
		Location location = Context.getLocationService().getLocation(2);
		Patient p = Context.getPatientService().getPatient(16462);

		new SurvivalRateCalc().outcome(p, location, monthsInProgram, program);
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
