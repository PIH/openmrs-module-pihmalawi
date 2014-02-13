package org.openmrs.module.pihmalawi.reports;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportHelper {

	protected static final Log log = LogFactory.getLog(ReportHelper.class);

	public void purgeDefinition(Class<? extends Definition> clazz, String name) {
		SerializedDefinitionService s =  Context.getService(SerializedDefinitionService.class);
		try {
			Definition d = findDefinition(clazz, name);
			if (d != null) {
				s.purgeDefinition(d);
			}
		} catch (RuntimeException e) {
			// intentional empty as the author is too long out of business...
		}
	}

	public void purgeAll(String tag) {
		SerializedDefinitionService s = Context.getService(SerializedDefinitionService.class);
		List<CohortDefinition> defs = s.getAllDefinitions(CohortDefinition.class, false);
		for (Definition def : defs) {
			if (def.getName().startsWith(tag)) {
				s.purgeDefinition(def);
			}
		}
		List<CohortIndicator> is = s.getAllDefinitions(CohortIndicator.class, false);
		for (CohortIndicator i : is) {
			if (i.getName().startsWith(tag)) {
				s.purgeDefinition(i);
			}
		}
	}

	public void purgeDimension(String name) {
		DimensionService s = Context.getService(DimensionService.class);
		List<Dimension> defs = s.getDefinitions(name, true);
		for (Dimension def : defs) {
			s.purgeDefinition(def);
		}
	}

	public <T extends Definition> T findDefinition(Class<T> clazz, String name) {
		SerializedDefinitionService s = Context.getService(SerializedDefinitionService.class);
		List<T> defs = s.getDefinitions(clazz, name, true);
		if (defs.isEmpty()) {
			throw new RuntimeException("Couldn't find Definition " + name);
		}
		return defs.get(0);
	}

	public CohortDefinition cohortDefinition(String name) {
		return findDefinition(CohortDefinition.class, name);
	}

	public CohortDefinitionDimension cohortDefinitionDimension(String name) {
		return findDefinition(CohortDefinitionDimension.class, name);
	}

	public CohortIndicator cohortIndicator(String name) {
		return findDefinition(CohortIndicator.class, name);
	}

	public Map<String, String> hashMap(String key, String value) {
		Map<String, String> dimensionOptions = new HashMap<String, String>();
		dimensionOptions.put(key, value);
		return dimensionOptions;
	}

	public void replaceCohortDefinition(CohortDefinition def) {
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		cds.saveDefinition(def);
	}

	public void replaceDefinition(Definition def) {
		SerializedDefinitionService s = Context.getService(SerializedDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		s.saveDefinition(def);
	}

	public void replaceDataSetDefinition(DataSetDefinition def) {
		SerializedDefinitionService s = Context.getService(SerializedDefinitionService.class);
		purgeDefinition(def.getClass(), def.getName());
		s.saveDefinition(def);
	}

	public void newCountIndicator(String name, String cohort, String parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name,
				new Mapped<CohortDefinition>(cohortDefinition(cohort),
				ParameterizableUtil.createParameterMappings(parameterMapping)), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
	}
	
	public CohortIndicator newCountIndicator(String name, String cohort) {
		return newCountIndicator(name, cohort, new HashMap<String, Object>());
	}

	public CohortIndicator newCountIndicator(String name, String cohort, Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohortDefinition(cohort), parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
		return i;
	}

	public CohortIndicator newCountIndicator(String name, CohortDefinition cohort, Map<String, Object> parameterMapping) {
		CohortIndicator i = CohortIndicator.newCountIndicator(name, new Mapped<CohortDefinition>(cohort, parameterMapping), null);
		i.addParameter(new Parameter("startDate", "Start date", Date.class));
		i.addParameter(new Parameter("endDate", "End date", Date.class));
		i.addParameter(new Parameter("location", "Location", Location.class));
		replaceDefinition(i);
		return i;
	}

	public void replaceReportDefinition(PeriodIndicatorReportDefinition rd) {
		purgeDefinition(PeriodIndicatorReportDefinition.class, rd.getName());
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		rds.saveDefinition(rd);
	}

	public void replaceReportDefinition(ReportDefinition rd) {
		purgeDefinition(ReportDefinition.class, rd.getName());
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		rds.saveDefinition(rd);
	}

	public void replaceDimensionDefinition(CohortDefinitionDimension md) {
		purgeDimension(md.getName());
		DimensionService rds = Context.getService(DimensionService.class);
		rds.saveDefinition(md);
	}

	public Map<String, Object> parameterMap(Object... mappings) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i = 0; i < mappings.length; i += 2) {
			m.put((String) mappings[i], mappings[i + 1]);
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

	public void createXlsOverview(ReportDefinition rd, String resourceName, String name, Map<?, ?> properties) throws IOException {
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(resourceName);
		resource.setExtension("xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName);
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
				s.append(" ").append(operator).append(" ");
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


	public CohortIndicator newCountIndicator(String name, CohortDefinition cohort) {
		// i know, i know. not really efficient...
		return newCountIndicator(name, cohort.getName());
	}
}
