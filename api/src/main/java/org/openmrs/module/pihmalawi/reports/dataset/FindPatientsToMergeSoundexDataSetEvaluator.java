package org.openmrs.module.pihmalawi.reports.dataset;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.query.db.hibernate.HibernateCohortQueryDAO;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { FindPatientsToMergeSoundexDataSetDefinition.class })
public class FindPatientsToMergeSoundexDataSetEvaluator implements
		DataSetEvaluator {

	private final static String OPENMRS_SERVER = "http://emr:8080";

	protected Log log = LogFactory.getLog(this.getClass());

	public FindPatientsToMergeSoundexDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		FindPatientsToMergeSoundexDataSetDefinition dsds = (FindPatientsToMergeSoundexDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();

		// By default, get all patients
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}

		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		Query query = prepareQueryStatement(dsds.isSwapFirstLastName());

		Set<Integer> memberIds = cohort.getMemberIds();
		if (dsds.getEncounterTypesToLookForDuplicates() != null) {
			HibernateCohortQueryDAO dao = (HibernateCohortQueryDAO) Context
					.getRegisteredComponents(HibernateCohortQueryDAO.class)
					.get(0);
			memberIds = dao.getPatientsHavingEncounters(null,
					null, null, dsds.getEncounterTypesToLookForDuplicates(),
					null, null, null).getMemberIds();
			Set<Integer> memberIdsWithoutIdentifierType = new HashSet<Integer>();
			
			if (dsds.getPatientIdentifierTypeRequiredToLookForDuplicates() != null) {
				for (Integer id : memberIds) {
					if (Context.getPatientService().getPatient(id).getPatientIdentifier(dsds.getPatientIdentifierTypeRequiredToLookForDuplicates()) == null) {
						memberIdsWithoutIdentifierType.add(id);
					}
				}
				// mkae sure to exclude all patients without any hcc number
				memberIds.removeAll(memberIdsWithoutIdentifierType);
			}
			// make sure the sets are disjunct
			memberIds.removeAll(cohort.getMemberIds());
		}

		List<Patient> patients = Context.getPatientSetService().getPatients(
				cohort.getMemberIds());

		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn col = null;

			try {
				Collection<Patient> ps = soundexMatches(query, p, memberIds,
						dsds.isSwapFirstLastName());
				if (ps != null && !ps.isEmpty()) {
					col = new DataSetColumn("#", "#", String.class);
					row.addColumnValue(col, linkifyId(p, dsds.getEncounterTypesForSummary(), dsds.getProgramWorkflowForSummary()));
					int i = 1;
					for (Patient potential : ps) {
						col = new DataSetColumn("potential match_" + i,
								"potential match_" + i, String.class);
						row.addColumnValue(col, linkifyMerge(p, potential, dsds.getEncounterTypesForSummary(), dsds.getProgramWorkflowForSummary()));
						i++;
					}
					dataSet.addRow(row);
				}
			} catch (Throwable t) {
				col = new DataSetColumn("Error",
						"Error", String.class);
				row.addColumnValue(col, "Error while loading patient " + p.getId());
				dataSet.addRow(row);
			}
		}

		return dataSet;
	}

	private Query prepareQueryStatement(boolean swapFirstLastName) {
		String nameMatch = "";
		if (swapFirstLastName) {
			nameMatch = "p1.family_name_code = p2.given_name_code and p1.given_name_code = p2.family_name_code";
		} else {
			nameMatch = "p1.given_name_code = p2.given_name_code and p1.family_name_code = p2.family_name_code";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select pn2.person_id from person_name_code p1, person_name_code p2, person_name pn1, person_name pn2 ");
		sql.append(" where pn1.person_id <> pn2.person_id and ").append(
				nameMatch);
		sql.append(" and pn1.person_id not in (select user_id from users) and pn2.person_id not in (select user_id from users) ");
		sql.append(" and pn2.person_id in (:personIds)");
		sql.append(" and pn1.person_name_id=p1.person_name_id and pn2.person_name_id=p2.person_name_id and pn1.person_id=:referenceId ;");
		Query query = sessionFactory().getCurrentSession().createSQLQuery(
				sql.toString());
		return query;
	}

	private Collection<Patient> soundexMatches(Query query,
			Patient referencePatient, Set<Integer> cohortOfPatients,
			boolean swapFirstLastName) {
		Map<Integer, Patient> potentialDuplicates = new HashMap<Integer, Patient>();
		PatientService ps = Context.getPatientService();

		query.setInteger("referenceId", referencePatient.getId())
				.setParameterList("personIds", cohortOfPatients);
		Iterator i = query.list().iterator();
		while (i.hasNext()) {
			try {
				Patient p = ps.getPatient((Integer) i.next());
				if (p != null
						&& (p.getGender() == null
								|| referencePatient.getGender() == null || p
								.getGender().equals(
										referencePatient.getGender()))) {
					potentialDuplicates.put(p.getId(), p);
				}
			} catch (ObjectNotFoundException onfe) {
				// sic, seems to be a user, maybe better filter them in the
				// first place in sql
			}
		}

		return potentialDuplicates.values();
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}

	private String linkifyId(Patient p, List<EncounterType> encounterTypes, ProgramWorkflow pw) {
		return "<a href=" + OPENMRS_SERVER
				+ "/openmrs/patientDashboard.form?patientId=" + p.getId() + ">"
				+ p.getGivenName() + " " + p.getFamilyName() + "</a>"
				+ "<br/>" + p.getGender() + ", " + p.getAge() + ", " + currentVillage(p) 
				+ "<br/>" + firstLastEncounter(p, encounterTypes)
				+ "<br/>" + currentOutcome(p, pw);
	}

	private String currentVillage(Patient p) {
		return p.getAddresses().iterator().next().getCityVillage();
	}

	private String linkifyMerge(Patient p, Patient p2, List<EncounterType> encounterTypes, ProgramWorkflow pw) {
		return "<a href=" + OPENMRS_SERVER
				+ "/openmrs/admin/patients/mergePatients.form?patientId="
				+ p2.getId() + "&patientId=" + p.getId() + ">"
				+ p2.getGivenName() + " " + p2.getFamilyName() + "</a>"
				+ "<br/>" + p2.getGender() + ", " + p2.getAge() + ", " + currentVillage(p2) 
				+ "<br/>" + firstLastEncounter(p2, encounterTypes)
				+ "<br/>" + currentOutcome(p2, pw);
	}

	private String currentOutcome(Patient p, ProgramWorkflow pw) {
		PatientState ps = new ProgramHelper().getMostRecentStateAtDate(p, pw, new Date());
		if (ps != null) {
			return ps.getState().getConcept().getName() + "@" + (ps.getEndDate() == null ? formatDate(ps.getStartDate()) : formatDate(ps.getEndDate())); 
		}
		return null;
	}

	private String firstLastEncounter(Patient p, List<EncounterType> encounterTypes) {
		String e = "";
		List<Encounter> encounters = Context.getEncounterService().getEncounters(p, null, null, null, null, encounterTypes, null, false);
		if (!encounters.isEmpty()) {
			Encounter firstEncounter = encounters.get(0);
			e = firstEncounter.getEncounterType().getName() + "@" + formatDate(firstEncounter.getEncounterDatetime());
			Encounter lastEncounter = encounters.get(encounters.size() - 1);
			e += " - " + lastEncounter.getEncounterType().getName() + "@" + formatDate(lastEncounter.getEncounterDatetime());			
		}
		return e;
	}

	private String formatDate(Date encounterDatetime) {
		return new SimpleDateFormat("dd-MMM-yyyy").format(encounterDatetime);
	}
}