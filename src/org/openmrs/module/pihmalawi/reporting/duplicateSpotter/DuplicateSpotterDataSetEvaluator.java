package org.openmrs.module.pihmalawi.reporting.duplicateSpotter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
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

/**
 * Try to spot potential duplicates based on matching touchscreen and manual
 * data entry encounters
 * 
 * @author xian
 * 
 */
@Handler(supports = { DuplicateSpotterDataSetDefinition.class })
public class DuplicateSpotterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	public DuplicateSpotterDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {
		DuplicateSpotterDataSetDefinition dsds = (DuplicateSpotterDataSetDefinition) dataSetDefinition;
		Date onOrAfter = dsds.getOnOrAfter();
		DuplicatePatientsSpotter s = new DuplicatePatientsSpotter();
		SoundexMatcher sm = new SoundexMatcher();
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		Helper h = new Helper();
		Location nno = h.location("Neno District Hospital");
		List<Location> locations = Arrays.asList(nno);
		List<EncounterType> encounterTypes = Arrays
				.asList(h.encounterType("ART_INITIAL"),
						h.encounterType("ART_FOLLOWUP"),
						h.encounterType("PART_INITIAL"),
						h.encounterType("PART_FOLLOWUP"),
						h.encounterType("EID_INITIAL"),
						h.encounterType("EID_FOLLOWUP"));

		HibernateCohortQueryDAO dao = (HibernateCohortQueryDAO) Context
				.getRegisteredComponents(HibernateCohortQueryDAO.class).get(0);
		Set<Integer> patientIds = dao.getPatientsHavingEncounters(onOrAfter,
				null, locations, encounterTypes, null, null, null)
				.getMemberIds();

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
			DataSetColumn col = null;

			Collection<Patient> ps = null;
			if (dsds.isNnoEncounterMatching()) {
				ps = s.spot(p, onOrAfter, patientIds);
			} else if (dsds.isSoundexCheck()) {
				ps = sm.soundexMatches(p, encounterTypes, dsds.isSoundexSwapFirstLastName());
			}
			if (!dsds.isShowSingleRecords() && ps != null && !ps.isEmpty()) {
				col = new DataSetColumn("#", "#", String.class);
				row.addColumnValue(col, linkifyId(p));
				int i = 1;
				for (Patient potential : ps) {
					col = new DataSetColumn("potential match_" + i,
							"potential match_" + i, String.class);
					row.addColumnValue(col, linkifyMerge(p, potential));
					i++;
				}
				dataSet.addRow(row);
			}
		}

		return dataSet;
	}

//	private final static String OPENMRS_SERVER = "http://172.16.1.4:8080";
	private final static String OPENMRS_SERVER = "http://192.168.1.7:8180";

	private String linkifyId(Patient p) {
		return "<a href=" + OPENMRS_SERVER
				+ "/openmrs/patientDashboard.form?patientId=" + p.getId() + ">"
				+ p.getGivenName() + " " + p.getFamilyName() + " ("
				+ p.getGender() + ", " + p.getAge() + ")</a>";
	}

	private String linkifyMerge(Patient p, Patient p2) {
		return "<a href=" + OPENMRS_SERVER
				+ "/openmrs/admin/patients/mergePatients.form?patientId="
				+ p2.getId() + "&patientId=" + p.getId() + ">"
				+ p2.getGivenName() + " " + p2.getFamilyName() + " ("
				+ p2.getGender() + ", " + p2.getAge() + ")</a>";
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
}
