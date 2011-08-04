package org.openmrs.module.pihmalawi.reporting.scripting;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { ScriptingDataSetDefinition.class })
public class ScriptingDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	private Location location = null;

	public ScriptingDataSetEvaluator() {
	}

	public DataSet evaluate(DataSetDefinition dataSetDefinition,
			EvaluationContext context) {

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		ScriptingDataSetDefinition definition = (ScriptingDataSetDefinition) dataSetDefinition;

		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();

		// By default, no nothing
		if (cohort == null) {
			return dataSet;
		}

		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		location = (Location) context.getParameterValue("location");

		// Get a list of patients based on the cohort members
		List<Patient> patients = Context.getPatientSetService().getPatients(
				cohort.getMemberIds());

		for (Patient p : patients) {
			doItForThePatient(p);
		}
		return dataSet;
	}

	private void doItForThePatient(Patient p) {
		// here an eval block from the datasetdefinition would make this more
		// usable for generic things

		log.warn("Patient " + p.getId());
		
		// somehow iterating over these patients caused problems with sync...
		addStatePatientTransferedOutAfterEveryTransferInternallyState(p);
	}

	private void addStatePatientTransferedOutAfterEveryTransferInternallyState(
			Patient p) {
		Program program = Context.getProgramWorkflowService().getProgramByName(
				"HIV PROGRAM");
		ProgramWorkflow programWorkflow = program
				.getWorkflowByName("TREATMENT STATUS");
		ProgramWorkflowState internalTransfer = programWorkflow
				.getState("TRANSFERRED INTERNALLY");
		ProgramWorkflowState transferOut = programWorkflow
				.getState("PATIENT TRANSFERRED OUT");

		List<PatientProgram> patientPrograms = Context
				.getProgramWorkflowService().getPatientPrograms(p, program,
						null, null, null, null, false);
		for (PatientProgram pp : patientPrograms) {
			try {
				log.warn("Check PP " + pp.getId());
				PatientState ps = pp.getCurrentState(programWorkflow);
				if (ps != null && ps.getState() != null && !ps.isVoided()
						&& !ps.getState().isRetired()
						&& ps.getState().getId()
								.equals(internalTransfer.getId())) {
					log.warn("PS matches " + pp.getId());
					Location programLocation = new Helper().getEnrollmentLocation(ps.getPatientProgram(),
							 sessionFactory().getCurrentSession());
					if (programLocation != null && location != null && programLocation.getId().equals(location.getId())) {
						// found a program with last state internal transfer, add
						// transferred out
						log.warn("Adding transferred out to " + p.getId());
						pp.transitionToState(transferOut, ps.getStartDate());
						return;
					}
				}
			} catch (Throwable t) {
				log.error(t);
			}
		}
	}
	
	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
				HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}


}
