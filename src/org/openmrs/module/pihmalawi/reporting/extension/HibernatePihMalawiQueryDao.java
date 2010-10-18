package org.openmrs.module.pihmalawi.reporting.extension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;

public class HibernatePihMalawiQueryDao {
	
	protected static final Log log = LogFactory.getLog(HibernatePihMalawiQueryDao.class);
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Cohort getPatientsInStatesAtLocation(ProgramWorkflowState programWorkflowState, Date onOrAfter, Date onOrBefore,
	                                            Location location) {
		// potential to include multiple states in future
		List<Integer> stateIds = new ArrayList<Integer>();
		stateIds.add(programWorkflowState.getId());
		
		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");
		
		// optional clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (onOrAfter != null)
			sql.append(" and (ps.end_date is null or ps.end_date >= :onOrAfter) ");
		if (onOrBefore != null)
			sql.append(" and (ps.start_date is null or ps.start_date <= :onOrBefore) ");
		if (location != null)
			sql.append(" and pp.location_id = :location ");
		
		sql.append(" group by pp.patient_id ");
		
		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (onOrAfter != null)
			query.setDate("onOrAfter", onOrAfter);
		if (onOrBefore != null)
			query.setDate("onOrBefore", onOrBefore);
		if (location != null)
			query.setInteger("location", location.getId());
		return new Cohort(query.list());
	}
	
	public Cohort getPatientsHavingStatesAtLocation(ProgramWorkflowState programWorkflowState,
	                                      Date startedOnOrAfter, Date startedOnOrBefore,
                                          Date endedOnOrAfter, Date endedOnOrBefore, Location location) {
		// potential to include multiple states in future
		List<Integer> stateIds = new ArrayList<Integer>();
		stateIds.add(programWorkflowState.getId());

		// Create SQL query
		StringBuilder sql = new StringBuilder();
		sql.append("select pp.patient_id ");
		sql.append("from patient_state ps ");
		sql.append("  inner join patient_program pp on ps.patient_program_id = pp.patient_program_id ");
		sql.append("  inner join patient p on pp.patient_id = p.patient_id ");
		sql.append("where ps.voided = false and pp.voided = false and p.voided = false ");
		
		// Create a list of clauses
		if (stateIds != null && !stateIds.isEmpty())
			sql.append(" and ps.state in (:stateIds) ");
		if (startedOnOrAfter != null)
			sql.append(" and ps.start_date >= :startedOnOrAfter ");
		if (startedOnOrBefore != null)
			sql.append(" and ps.start_date <= :startedOnOrBefore ");
		if (endedOnOrAfter != null)
			sql.append(" and ps.end_date >= :endedOnOrAfter ");
		if (endedOnOrBefore != null)
			sql.append(" and ps.end_date <= :endedOnOrBefore ");
		if (location != null)
			sql.append(" and pp.location_id = :location ");

		sql.append(" group by pp.patient_id ");
		log.debug("query: " + sql);

		// Execute query
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		if (stateIds != null && !stateIds.isEmpty())
			query.setParameterList("stateIds", stateIds);
		if (startedOnOrAfter != null)
			query.setDate("startedOnOrAfter", startedOnOrAfter);
		if (startedOnOrBefore != null)
			query.setDate("startedOnOrBefore", startedOnOrBefore);
		if (endedOnOrAfter != null)
			query.setDate("endedOnOrAfter", endedOnOrAfter);
		if (endedOnOrBefore != null)
			query.setDate("endedOnOrBefore", endedOnOrBefore);
		if (location != null)
			query.setInteger("location", location.getId());

		return new Cohort(query.list());
    }

}
