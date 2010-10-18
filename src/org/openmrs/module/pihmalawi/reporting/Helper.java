package org.openmrs.module.pihmalawi.reporting;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
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
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CohortDetailReportRenderer;

public class Helper {
	
	public void purgeDefinition(Class clazz, String name) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context.getService(SerializedDefinitionService.class);
		try {
			// ouch
			if (findDefinition(clazz, name) != null) {
				s.purgeDefinition(findDefinition(clazz, name));
			}
		}
		catch (RuntimeException e) {
			// intentional empty as the author is too long out of business...
		}
	}
	
	public void purgeDimension(String name) {
		DimensionService s = (DimensionService) Context.getService(DimensionService.class);
		List<Dimension> defs = s.getDefinitions(name, true);
		for (Dimension def : defs) {
			s.purgeDefinition(def);
		}
	}
	

	public Definition findDefinition(Class clazz, String name) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context.getService(SerializedDefinitionService.class);
		List<Definition> defs = s.getDefinitions(clazz, name, true);
		for (Definition def : defs) {
			return def;
		}
		throw new RuntimeException("Couldn't find Definition " + name);
	}
	
	public CohortDefinition cohortDefinition(String name) {
		// assuming that the name of the definition is unique across all cohortdefinition types
		SerializedDefinitionService s = (SerializedDefinitionService) Context.getService(SerializedDefinitionService.class);
		List<CohortDefinition> defs = s.getDefinitions(CohortDefinition.class, name, true);
		for (CohortDefinition def : defs) {
			return def;
		}
		throw new RuntimeException("Couldn't find Cohort Definition " + name);
	}
	
	public CohortDefinitionDimension cohortDefinitionDimension(String name) {
		return (CohortDefinitionDimension) findDefinition(CohortDefinitionDimension.class, name);
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
		for (int i = 0; i < e.length; i+=2) {
			dimensionOptions.put(e[i], e[i+1]);
		}
		return dimensionOptions;
	}
	
	public void replaceCohortDefinition(CohortDefinition def) {
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		cds.saveDefinition(def);
	}
	
	public void replaceDefinition(Definition def) {
		SerializedDefinitionService s = (SerializedDefinitionService) Context.getService(SerializedDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		s.saveDefinition(def);
	}
	
	public void newCountIndicator(String name, String cohort, String parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort),
		        ParameterizableUtil.createParameterMappings(parameterMapping)), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
	}
	
	public void newCountIndicatorForLocationWithState(String name, String cohort, String parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort),
		        ParameterizableUtil.createParameterMappings(parameterMapping)), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		i.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		replaceDefinition(i);
	}
	
	public void newCountIndicatorForLocationWithState(String name, String cohort, Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort),
		        parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		i.addParameter(new Parameter("state", "State", ProgramWorkflowState.class));
		replaceDefinition(i);
	}
	
	public CohortIndicator newCountIndicator(String name, String cohort, Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort),
		        parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
		return i;
	}
	
	public void newAppointmentIndicator(String name, String cohort, String endDateValue) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort),
		        ParameterizableUtil.createParameterMappings("value1=" + endDateValue + ",onOrBefore=${endDate}")), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
	}
	
	public ProgramWorkflowState workflowState(String program, String workflow, String state) {
		ProgramWorkflowState s = Context.getProgramWorkflowService().getProgramByName(program).getWorkflowByName(workflow)
		        .getStateByName(state);
		if (s == null) {
			throw new RuntimeException("Couldn't find ProgramWorkflowState " + state);
		}
		return s;
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
		ReportDefinitionService rds = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
		rds.saveDefinition(rd);
    }

	public void replaceDimensionDefinition(CohortDefinitionDimension md) {
		purgeDimension(md.getName());
		DimensionService rds = (DimensionService) Context.getService(DimensionService.class);
		rds.saveDefinition(md);
    }
	
	public void render(final ReportDesign design, ReportDefinition report) throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService().getLocation(2));
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);
		
		CohortDetailReportRenderer renderer = new CohortDetailReportRenderer() {
			
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		
		// We demonstrate here how we can use this renderer to output to HTML
		FileOutputStream fos = new FileOutputStream("/tmp/test.html"); // You will need to change this if you have no /tmp directory
		renderer.render(data, "xxx:html", fos);
		fos.close();
		
		// We demonstrate here how we can use this renderer to output to Excel
		//		fos = new FileOutputStream("/tmp/test.xls"); // You will need to change this if you have no /tmp directory
		//		renderer.render(data, "xxx:xls", fos);
		//		fos.close();
	}

	public Map<String, Object> parameterMap(Object... mappings) {
    	Map<String, Object> m = new HashMap<String, Object>();
    	for (int i = 0; i < mappings.length; i += 2) {
    		m.put((String) mappings[i], (Object) mappings[i + 1]);
    	}
    	return m;
    }
	

}
