package org.openmrs.module.pihmalawi.reporting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

public class Helper {

	protected static final Log log = LogFactory.getLog(Helper.class);

	public void purgeDefinition(Class clazz, String name) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		try {
			// ouch
			if (findDefinition(clazz, name) != null) {
				s.purgeDefinition(findDefinition(clazz, name));
			}
		} catch (RuntimeException e) {
			// intentional empty as the author is too long out of business...
		}
	}

	public void purgeAll(String tag) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		List<CohortDefinition> defs = s.getAllDefinitions(
				CohortDefinition.class, false);
		for (Definition def : defs) {
			if (def.getName().startsWith(tag)) {
				s.purgeDefinition(def);
			}
		}
		List<CohortIndicator> is = s.getAllDefinitions(CohortIndicator.class,
				false);
		for (CohortIndicator i : is) {
			if (i.getName().startsWith(tag)) {
				s.purgeDefinition(i);
			}
		}
	}

	public void purgeDimension(String name) {
		DimensionService s = (DimensionService) Context
				.getService(DimensionService.class);
		List<Dimension> defs = s.getDefinitions(name, true);
		for (Dimension def : defs) {
			s.purgeDefinition(def);
		}
	}

	public Definition findDefinition(Class clazz, String name) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		List<Definition> defs = s.getDefinitions(clazz, name, true);
		for (Definition def : defs) {
			return def;
		}
		throw new RuntimeException("Couldn't find Definition " + name);
	}

	public CohortDefinition cohortDefinition(String name) {
		// assuming that the name of the definition is unique across all
		// cohortdefinition types
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		List<CohortDefinition> defs = s.getDefinitions(CohortDefinition.class,
				name, true);
		for (CohortDefinition def : defs) {
			return def;
		}
		throw new RuntimeException("Couldn't find Cohort Definition " + name);
	}

	public CohortDefinitionDimension cohortDefinitionDimension(String name) {
		return (CohortDefinitionDimension) findDefinition(
				CohortDefinitionDimension.class, name);
	}

	public CohortIndicator cohortIndicator(String name) {
		return (CohortIndicator) findDefinition(CohortIndicator.class, name);
	}

	public Map<String, String> hashMap(String key, String value) {
		Map<String, String> dimensionOptions = new HashMap<String, String>();
		dimensionOptions.put(key, value);
		return dimensionOptions;
	}

	public Map<String, String> hashMap(String... e) {
		Map<String, String> dimensionOptions = new HashMap<String, String>();
		for (int i = 0; i < e.length; i += 2) {
			dimensionOptions.put(e[i], e[i + 1]);
		}
		return dimensionOptions;
	}

	public void replaceCohortDefinition(CohortDefinition def) {
		CohortDefinitionService cds = Context
				.getService(CohortDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		cds.saveDefinition(def);
	}

	public void replaceDefinition(Definition def) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		s.saveDefinition(def);
	}

	public void replaceDataSetDefinition(DataSetDefinition def) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context
				.getService(SerializedDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		s.saveDefinition(def);
	}

	public void newCountIndicator(String name, String cohort,
			String parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(
				name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
						ParameterizableUtil
								.createParameterMappings(parameterMapping)),
				null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
	}

	public void newCountIndicatorForLocationWithState(String name,
			String cohort, String parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(
				name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
						ParameterizableUtil
								.createParameterMappings(parameterMapping)),
				null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		i.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		replaceDefinition(i);
	}

	public void newCountIndicatorForLocationWithState(String name,
			String cohort, Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
						parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		i.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		replaceDefinition(i);
	}
	
	public CohortIndicator newCountIndicator(String name, String cohort) {
		return newCountIndicator(name, cohort, new HashMap<String, Object>());
	}

	public CohortIndicator newCountIndicator(String name, String cohort,
			Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
						parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
		return i;
	}

	public void newAppointmentIndicator(String name, String cohort,
			String endDateValue) {
		CohortIndicator i = CohortIndicator.newCountIndicator(
				name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
						ParameterizableUtil.createParameterMappings("value1="
								+ endDateValue + ",onOrBefore=${endDate}")),
				null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
	}

	public ProgramWorkflowState workflowState(String program, String workflow,
			String state) {
		ProgramWorkflowState s = Context.getProgramWorkflowService()
				.getProgramByName(program).getWorkflowByName(workflow)
				.getStateByName(state);
		if (s == null) {
			throw new RuntimeException("Couldn't find ProgramWorkflowState "
					+ state);
		}
		return s;
	}

	public Program program(String program) {
		Program s = Context.getProgramWorkflowService()
				.getProgramByName(program);;
		if (s == null) {
			throw new RuntimeException("Couldn't find Program "
					+ s);
		}
		return s;
	}

	public ProgramWorkflow programWorkflow(String program, String workflow) {
		return program(program).getWorkflowByName(workflow);
	}
	
	public Location location(String location) {
		Location s = Context.getLocationService().getLocation(location);
		if (s == null) {
			throw new RuntimeException("Couldn't find Location " + location);
		}
		return s;
	}

	public void addDefaultIndicatorParameter(CohortIndicator i) {
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
	}

	public void replaceReportDefinition(PeriodIndicatorReportDefinition rd) {
		purgeDefinition(PeriodIndicatorReportDefinition.class, rd.getName());
		ReportDefinitionService rds = (ReportDefinitionService) Context
				.getService(ReportDefinitionService.class);
		rds.saveDefinition(rd);
	}

	public void replaceReportDefinition(ReportDefinition rd) {
		purgeDefinition(ReportDefinition.class, rd.getName());
		ReportDefinitionService rds = (ReportDefinitionService) Context
				.getService(ReportDefinitionService.class);
		rds.saveDefinition(rd);
	}

	public void replaceDimensionDefinition(CohortDefinitionDimension md) {
		purgeDimension(md.getName());
		DimensionService rds = (DimensionService) Context
				.getService(DimensionService.class);
		rds.saveDefinition(md);
	}

	public void render(final ReportDesign design, ReportDefinition report)
			throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService()
				.getLocation(2));

		ReportDefinitionService rs = Context
				.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		CohortDetailReportRenderer renderer = new CohortDetailReportRenderer() {

			public ReportDesign getDesign(String argument) {
				return design;
			}
		};

		// We demonstrate here how we can use this renderer to output to HTML
		FileOutputStream fos = new FileOutputStream("/tmp/test.html"); // You
																		// will
																		// need
																		// to
																		// change
																		// this
																		// if
																		// you
																		// have
																		// no
																		// /tmp
																		// directory
		renderer.render(data, "xxx:html", fos);
		fos.close();

		// We demonstrate here how we can use this renderer to output to Excel
		// fos = new FileOutputStream("/tmp/test.xls"); // You will need to
		// change this if you have no /tmp directory
		// renderer.render(data, "xxx:xls", fos);
		// fos.close();
	}

	public Map<String, Object> parameterMap(Object... mappings) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i = 0; i < mappings.length; i += 2) {
			m.put((String) mappings[i], (Object) mappings[i + 1]);
		}
		return m;
	}

	public ReportDesign createHtmlBreakdown(ReportDefinition rd, String name,
			Map<String, Mapped<? extends DataSetDefinition>> map)
			throws IOException, SerializationException {
		ReportingSerializer serializer = new ReportingSerializer();
		String designXml = serializer.serialize(map);

		final ReportDesign design = new ReportDesign();
		design.setName(name);
		design.setReportDefinition(rd);
		design.setRendererType(CohortDetailReportRenderer.class);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile"); // Note: You must name your resource
										// exactly like this for it to work
		resource.setContents(designXml.getBytes());
		design.addResource(resource);
		resource.setReportDesign(design);
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
		return design;
	}

	public void createGenericPatientDesignBreakdown(ReportDefinition rd,
			String name, String rendererResource) throws IOException {
		final ReportDesign design = new ReportDesign();
		design.setName(name);
		design.setReportDefinition(rd);
		design.setRendererType(CohortDetailReportRenderer.class);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
				rendererResource);
		resource.setContents(IOUtils.toByteArray(is));
		design.addResource(resource);
		resource.setReportDesign(design);
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}

	public void createXlsOverview(ReportDefinition rd, String resourceName,
			String name, Map<? extends Object, ? extends Object> properties)
			throws IOException {
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(resourceName);
		resource.setExtension("xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
				resourceName);
		resource.setContents(IOUtils.toByteArray(is));
		final ReportDesign design = new ReportDesign();
		design.setName(name);
		design.setReportDefinition(rd);
		design.setRendererType(ExcelTemplateRenderer.class);
		design.addResource(resource);
		if (properties != null) {
			design.getProperties().putAll(properties);
		}
		resource.setReportDesign(design);

		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}

	public PatientState getFirstTimeInState(Patient p, Program program, ProgramWorkflowState firstTimeInState) {

		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p, program, null, null, null, null, false);
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			// should be refactored to use statesInWorkflow() as an intermediate solution
			if (!pp.isVoided()) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getState().getId().equals(firstTimeInState.getId())
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					PatientState state = (PatientState) validPatientStates
							.get(key);
					// just take the first one
					return state;
				}
			}
		}
		return null;
	}


	public PatientState getMostRecentStateAtLocation(Patient p,
			List<ProgramWorkflowState> programWorkflowStates,
			Location enrollmentLocation, Session hibernateSession) {
		PatientState state = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflowStates.get(0).getProgramWorkflow().getProgram(),
						null, null, null, null, false);
		List<Integer> programWorkflowStateIds = new ArrayList<Integer>();
		for (ProgramWorkflowState pws : programWorkflowStates) {
			programWorkflowStateIds.add(pws.getId());
		}
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			Location location = getEnrollmentLocation(pp, hibernateSession);
			if (!pp.isVoided() && location != null
					&& location.getId().equals(enrollmentLocation.getId())) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& programWorkflowStateIds.contains(ps.getState().getId())
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					// just take the last one and hope it is the most recent one
					state = (PatientState) validPatientStates.get(key);
				}
			}
		}
		return state;
	}

	// sorry, but this a haaack
	public PatientState getMostRecentStateAtLocation_hack(Patient p,
			Program program,
			Location enrollmentLocation, Session hibernateSession) {
		PatientState state = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						program,
						null, null, null, null, false);
		for (PatientProgram pp : pps) {
			// hope that the first found pp is also first in time
			Location location = getEnrollmentLocation(pp, hibernateSession);
			if (!pp.isVoided() && location != null
					&& location.getId().equals(enrollmentLocation.getId())) {
				HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
				ArrayList<Long> stupidListConverter = new ArrayList<Long>();
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						stupidListConverter.add(ps.getStartDate().getTime());
					}
				}
				Collections.<Long> sort(stupidListConverter);

				for (Long key : stupidListConverter) {
					// just take the last one and hope it is the most recent one
					state = (PatientState) validPatientStates.get(key);
				}
			}
		}
		return state;
	}
	
	public PatientState getMostRecentStateAtLocation(Patient p,
			ProgramWorkflow programWorkflow,
			Location enrollmentLocation, Session hibernateSession) {
		List<PatientState> lastStateOfAllPatientPrograms = new ArrayList<PatientState>();
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflow.getProgram(),
						null, null, null, null, false);
		
		// get all last states of patientprograms
		for (PatientProgram pp : pps) {
			Location programLocation = getEnrollmentLocation(pp, hibernateSession);
			if (programLocation != null && enrollmentLocation != null && programLocation.getId().equals(enrollmentLocation.getId())) {
				List<PatientState> states = statesInWorkflow(pp, programWorkflow);
				if (states != null && !states.isEmpty()) {
					lastStateOfAllPatientPrograms.add(states.get(states.size() - 1));
					log.debug("lastStatesOfAllPatientPrograms " + p.getPatientId() + " " + states.get(states.size() - 1).getState().getConcept().getName());
				}
			}
		}
		// figure out which patientprogram is last
		PatientState lastState = null;
		for (PatientState state : lastStateOfAllPatientPrograms) {
			if (state.getPatientProgram().getDateCompleted() == null) {
				// assume only one uncompleted program is possible (although not the case)
				return state; 
			} else {
				// otherwise assume the order of patientprograms is sequentially ordered (although not the case)
				lastState = state;
			}
		}
		return lastState;
	}
	
	// quick hack copied from bugfix for PatientProgram from ProgramLocation module
	// once OpenMRS can handle same-day-transitions this could be removed
	private List<PatientState> statesInWorkflow(PatientProgram patientProgram, ProgramWorkflow programWorkflow) {
		List<PatientState> ret = new ArrayList<PatientState>();
		for (PatientState st : patientProgram.getStates()) {
			if (st.getState().getProgramWorkflow().equals(programWorkflow) && !st.getVoided()) {
				ret.add(st);
			}
		}
		Collections.sort(ret, new Comparator<PatientState>() {
			
			public int compare(PatientState left, PatientState right) {
				// check if one of the states is active 
				if (left.getActive()) {
					return 1;
				}
				if (right.getActive()) {
					return -1;
				}
				return OpenmrsUtil.compareWithNullAsEarliest(left.getStartDate(), right.getStartDate());
			}
		});
		return ret;
	}

	public PatientState getMostRecentStateAtDate(Patient p, ProgramWorkflow programWorkflow,
			Date endDate) {
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflow.getProgram(),
						null, null, null, null, false);
		PatientState lastStateOnDate = null;
		
		try {
			for (PatientProgram pp : pps) {
				if (pp.getActive(endDate)) {
					// assuming there is only on active patientprogram (migh tbe wrong)
					List<PatientState> states = statesInWorkflow(pp, programWorkflow);
					for (PatientState state : states) {
						if (state.getStartDate().getTime() < endDate.getTime()) {
							// assuming the states is ordered
							lastStateOnDate = state;
						}
					}
					
				}
			}
		} catch (Throwable t) {
			// shouldn't happen, but it does...
		}
		return lastStateOnDate;
	}

	public PatientState getMostRecentStateAtDate_hack(Patient p, Program program,
			Date endDate, org.hibernate.classic.Session hibernateSession) {
		// wrong assumption that there is only one programworkflow for a program 
		PatientState state = null;
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						program,
						null, null, null, null, false);
		
		HashMap<Long, PatientState> validPatientStates = new HashMap<Long, PatientState>();
		ArrayList<Long> sortedByStartDate = new ArrayList<Long>();

		for (PatientProgram pp : pps) {
			if (!pp.isVoided() ) {
				for (PatientState ps : pp.getStates()) {
					if (!ps.isVoided()
							&& ps.getStartDate() != null) {
						validPatientStates.put(ps.getStartDate().getTime(), ps);
						sortedByStartDate.add(ps.getStartDate().getTime());
					}
				}
			}
		}
		Collections.<Long> sort(sortedByStartDate);

		for (Long key : sortedByStartDate) {
			// take the one with the start date on or before endDate
			if (key <= endDate.getTime()) {
				state = (PatientState) validPatientStates.get(key);
			}
		}
		return state;
	}

	public List<PatientState> getPatientStatesByWorkflowAtLocation(Patient p,
			ProgramWorkflowState programWorkflowState,
			Location enrollmentLocation, Session hibernateSession) {
		
		Integer programWorkflowStateId = programWorkflowState.getId();
		
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						programWorkflowState.getProgramWorkflow().getProgram(),
						null, null, null, null, false);
		
		// list of patientstates (patient, workflow)
		List<PatientState> patientStateList = new ArrayList<PatientState>();
		// hope that the first found pp is also first in time
		for (PatientProgram pp : pps) {
			if(enrollmentLocation == null) {
				if (!pp.isVoided()) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided()
								&& programWorkflowStateId.equals(ps.getState().getId())
								&& ps.getStartDate() != null) {
							
							patientStateList.add(ps);
						}
					}
				}
			} else {
				Location location = getEnrollmentLocation(pp, hibernateSession);
				if (!pp.isVoided() && location != null
						&& location.getId().equals(enrollmentLocation.getId())) {
					for (PatientState ps : pp.getStates()) {
						if (!ps.isVoided()
								&& programWorkflowStateId.equals(ps.getState().getId())
								&& ps.getStartDate() != null) {
							
							patientStateList.add(ps);
						}
					}
				}
			}
		}
		
		return patientStateList;
	}

	public Location getEnrollmentLocation(PatientProgram pp,
			Session hibernateSession) {
		String sql = "select location_id from patient_program where patient_program_id = "
				+ pp.getId();

		Query query = hibernateSession.createSQLQuery(sql.toString());
		// assume there is only one
		if (!query.list().isEmpty() && query.list().get(0) != null) {
			return Context.getLocationService().getLocation(
					((Integer) (query.list().get(0))).intValue());
		}
		return null;
	}

	public EncounterType encounterType(String string) {
		return Context.getEncounterService().getEncounterType(string);
	}
	
	public CohortIndicator createCompositionIndicator(String name,
			String cohort1, Map<String, Object> map1, String cohort2, Map<String, Object> map2, String composition, Map<String, Object> compositionMap) {
		
		CompositionCohortDefinition ccd = new CompositionCohortDefinition();
		ccd.setName(name);
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		ccd.getSearches().put("1",
				new Mapped(cohortDefinition(cohort1), map1));
		ccd.getSearches().put("2",
				new Mapped(cohortDefinition(cohort2), map2));
		ccd.setCompositionString(composition);
		replaceCohortDefinition(ccd);
				
		CohortIndicator i = newCountIndicator(name, name, compositionMap);
		
		return i;
	}
	
	public CohortIndicator createCompositionIndicator(String name, String operator, Map<String, Object> compositionMap,
			Map<String, Mapped<? extends CohortDefinition>> entries) {
		
		return createCompositionIndicator(name, operator, compositionMap, entries, false);
	}
	
	public CohortIndicator createCompositionIndicator(String name, String operator, Map<String, Object> compositionMap,
			Map<String, Mapped<? extends CohortDefinition>> entries, boolean fullString) {
		
		// create cohort with cohorts and search string
		CompositionCohortDefinition ccd = (CompositionCohortDefinition)getCompositionCohort(entries, operator, fullString);
		ccd.setName(name + " Composition");
		ccd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ccd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ccd.addParameter(new Parameter("location", "Location", Location.class));
		
		replaceCohortDefinition(ccd);
		
		return newCountIndicator(name + " Indicator", name + " Composition", compositionMap);
	}
	
	public static CohortDefinition getCompositionCohort(Map<String, Mapped<? extends CohortDefinition>> entries, String operator, boolean fullString) {
		if (entries.size() == 1) {
			return entries.values().iterator().next().getParameterizable();
		}
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Mapped<? extends CohortDefinition>> cd : entries.entrySet()) {
			d.addSearch(cd.getKey(), cd.getValue().getParameterizable(), cd.getValue().getParameterMappings());
		
			if (s.length() > 0) {
				s.append(" " + operator + " ");
			}
			
			s.append(cd.getKey());
		}
		
		if(fullString) {
			d.setCompositionString(operator);
		} else {
			d.setCompositionString(s.toString());
		}
		return d;
	}
	
	public static CohortDefinition minus(CohortDefinition base, CohortDefinition... toSubtract) {
		CompositionCohortDefinition d = new CompositionCohortDefinition();
		d.addSearch("base", base, null);
		StringBuilder s = new StringBuilder("base AND NOT (");
		int i = 1;
		for (CohortDefinition cd : toSubtract) {
			d.addSearch(""+i, cd, null);
			if (i > 1) {
				s.append(" OR ");
			}
			s.append(i++);
		}
		s.append(")");
		d.setCompositionString(s.toString());
		return d;
	}
	
	public static CodedObsCohortDefinition makeCodedObsCohortDefinition(String name, String question, String value, SetComparator setComparator, TimeModifier timeModifier) {		
		CodedObsCohortDefinition obsCohortDefinition = new CodedObsCohortDefinition();
		obsCohortDefinition.setName(name);
		if (question != null) obsCohortDefinition.setQuestion(Context.getConceptService().getConceptByUuid(question));
		if (setComparator != null) obsCohortDefinition.setOperator(setComparator);
		if (timeModifier != null) obsCohortDefinition.setTimeModifier(timeModifier);
		Concept valueCoded = Context.getConceptService().getConceptByUuid(value);
		List<Concept> valueList = new ArrayList<Concept>();
		if (valueCoded != null) {
			valueList.add(valueCoded);
			obsCohortDefinition.setValueList(valueList);
		}		
		return obsCohortDefinition;
	}

	public Set<PatientState> getMostRecentStates(Patient p,
			Session currentSession) {
		List<PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(p,
						null, null, null, null, null, false);
		Set<PatientState> allStates = new HashSet<PatientState>();
		for (PatientProgram pp : pps) {
			allStates.addAll(pp.getCurrentStates());
		}
		return allStates;
	}

	public Concept concept(String string) {
		return Context.getConceptService().getConcept(string);
	}
}
