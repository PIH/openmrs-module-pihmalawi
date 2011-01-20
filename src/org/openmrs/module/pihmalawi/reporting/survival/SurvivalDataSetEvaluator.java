package org.openmrs.module.pihmalawi.reporting.survival;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.Relationship;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { SurvivalDataSetDefinition.class })
public class SurvivalDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public SurvivalDataSetEvaluator() {
	}
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		final Concept CD4_COUNT = Context.getConceptService().getConceptByName("CD4 COUNT");
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		SurvivalDataSetDefinition definition = (SurvivalDataSetDefinition) dataSetDefinition;
		PatientIdentifierType patientIdentifierType = definition.getPatientIdentifierType();
		Collection<EncounterType> encounterTypes = definition.getEncounterTypes();
		
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
		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());
		
		for (Patient p : patients) {
			DataSetRow row = new DataSetRow();
			DataSetColumn c = null;
			
			// todo, get current id and/or preferred
			String id = "";
			for (PatientIdentifier pi : p.getPatientIdentifiers(patientIdentifierType)) {
				if (pi.getIdentifier().startsWith("NNO-")) {
					id = pi.getIdentifier() + " " + id;
				} else {
					id += pi.getIdentifier() + " ";
				}
			}
			c = new DataSetColumn("#", "#", String.class);
			row.addColumnValue(c, id);
			// given
			c = new DataSetColumn("Given", "Given", String.class);
			row.addColumnValue(c, p.getGivenName());
			// family
			c = new DataSetColumn("Last", "Last", String.class);
			row.addColumnValue(c, p.getFamilyName());
			// age
			c = new DataSetColumn("Age", "Age", Integer.class);
			row.addColumnValue(c, p.getAge());
			// sex
			c = new DataSetColumn("M/F", "M/F", String.class);
			row.addColumnValue(c, p.getGender());
			// last visit & loc
			List<Encounter> encounters = Context.getEncounterService().getEncounters(p, null, null, null, null,
			    encounterTypes, null, false);
			c = new DataSetColumn("Last visit", "Last visit", String.class);
			if (!encounters.isEmpty()) {
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c, formatEncounterDate(e.getEncounterDatetime()) + " (" + e.getLocation() + ")");
				// rvd from last encounter
				//				String rvd = "";
				//				Set<Obs> observations = e.getObs();
				//				for (Obs o : observations) {
				//					if (o.getConcept().equals(Context.getConceptService().getConceptByName("APPOINTMENT DATE"))) {
				//						rvd = o.getValueAsString(Context.getLocale());
				//						c = new DataSetColumn("RVD", "RVD", String.class);
				//						row.addColumnValue(c, h(rvd));
				//					}
				//				}
			} else {
				row.addColumnValue(c, h(""));
			}
			// vhw given & last			
			c = new DataSetColumn("VHW", "VHW", String.class);
			String vhw = "";
			List<Relationship> ships = Context.getPersonService().getRelationshipsByPerson(p);
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(
				    Context.getPersonService().getRelationshipTypeByUuid("19124428-a89d-11df-bba5-000c297f1161"))) {
					vhw = r.getPersonB().getGivenName() + " " + r.getPersonB().getFamilyName();
					break;
				}
			}
			row.addColumnValue(c, h(vhw));
			// village
			c = new DataSetColumn("Village", "Village", String.class);
			row.addColumnValue(c, h(p.getPersonAddress().getCityVillage()));
			
			// survival analysis
			c = new DataSetColumn("12 months outcome at location", "12 months outcome at location", String.class);
			Program program = Context.getProgramWorkflowService().getProgramByName(
			"HIV PROGRAM");
			int monthsInProgram = 12;
//			Location location = (Location) context.getParameterValue("location"); 
			Location location = (Location) Context.getLocationService().getLocation(2);
//			Patient p = Context.getPatientService().getPatient(17208);

			row.addColumnValue(c, new SurvivalRateCalc().outcome(p, location, monthsInProgram, program));
			
			dataSet.addRow(row);
		}
		return dataSet;
	}
	
	private String formatEncounterDate(Date encounterDatetime) {
		return new SimpleDateFormat("dd-MMM-yyyy").format(encounterDatetime);
	}
	
	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}
}
