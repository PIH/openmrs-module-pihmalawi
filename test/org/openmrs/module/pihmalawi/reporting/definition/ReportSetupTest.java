package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class ReportSetupTest extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before
	public void setup() throws Exception {
		authenticate();
	}
	
	@Rollback(false)
	public void fakedTestToSetupReport() throws Exception {
		
		try {
			SerializedDefinitionService s = (SerializedDefinitionService) Context
			        .getService(SerializedDefinitionService.class);
			
			// cohort query
			GenderCohortDefinition mc = new GenderCohortDefinition();
			mc.setName("male");
			mc.setMaleIncluded(true);
			s.saveDefinition(mc);
			GenderCohortDefinition fc = new GenderCohortDefinition();
			fc.setName("female");
			fc.setFemaleIncluded(true);
			s.saveDefinition(fc);
			
			// dimension
			CohortDefinitionDimension md = new CohortDefinitionDimension();
			md.setName("male");
			md.addCohortDefinition("male", mc, null);
			s.saveDefinition(md);
			CohortDefinitionDimension fd = new CohortDefinitionDimension();
			fd.setName("female");
			fd.addCohortDefinition("female", fc, null);
			s.saveDefinition(fd);
			
			// Indicator
			CohortIndicator mi = CohortIndicator.newCountIndicator("male", new Mapped<GenderCohortDefinition>(mc, null),
			    null);
			mi.addParameter(new Parameter("startDate", "Start date", Date.class));
			mi.addParameter(new Parameter("endDate", "End date", Date.class));
			mi.addParameter(new Parameter("location", "Location", Location.class));
			s.saveDefinition(mi);
			CohortIndicator fi = CohortIndicator.newCountIndicator("female", new Mapped<GenderCohortDefinition>(fc, null),
			    null);
			fi.addParameter(new Parameter("startDate", "Start date", Date.class));
			fi.addParameter(new Parameter("endDate", "End date", Date.class));
			fi.addParameter(new Parameter("location", "Location", Location.class));
			s.saveDefinition(fi);
			
			// DataSet
			//		CohortDataSetDefinition cdsd = new CohortDataSetDefinition();
			//		cdsd.setName("chds");
			//			Map<String, Object> periodMappings = new HashMap<String, Object>();
			//			periodMappings.put("startDate", "${startDate}");
			//			periodMappings.put("endDate", "${endDate}");
			//			periodMappings.put("location", "${location}");
			//			CohortIndicatorDataSetDefinition rdds = new CohortIndicatorDataSetDefinition();
			//			rdds.addParameter(new Parameter("startDate", "Start Date", Date.class));
			//			rdds.addParameter(new Parameter("endDate", "End Date", Date.class));
			//			rdds.addParameter(new Parameter("location", "Location", Location.class));
			//			rdds.setName("Gender Data Set");
			//			rdds.addColumn("1", "Male", new Mapped<CohortIndicator>(mi, periodMappings), "");
			//			rdds.addColumn("2", "Female", new Mapped<CohortIndicator>(fi, periodMappings), "");
			//		s.saveDefinition(rdds);
			
			// ReportDefinition
			PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
			rd.setName("Gender");
			rd.addParameter(new Parameter("startDate", "Start date", Date.class));
			rd.addParameter(new Parameter("endDate", "End date", Date.class));
			rd.addParameter(new Parameter("location", "Location", Location.class));
			//		rd.addDataSetDefinition(rdds, periodMappings);
			//		rd.addIndicator("male", "male", mi);
			// parameters
			// base cohort definition
			//		rd.setBaseCohortDefinition(baseCohortDefinition)
			// output designs
			// dataset definitions
			// LocationFilter
			//		PeriodIndicatorReportUtil.addDimension(rd, "male", new Mapped<CohortDefinitionDimension>(md));
			//		PeriodIndicatorReportUtil.addDimension(rd, "female", dimensionOptions);
			//		CohortDefinition locationFilter = Cohorts.getLocationFilter(location);
			//		if (locationFilter != null) {
			//			report.setBaseCohortDefinition(locationFilter, null);
			//		}
			//		rd.addDimension(gender, dimension)
			Map<String, String> dimensionOptions = new HashMap<String, String>();
			PeriodIndicatorReportUtil.addColumn(rd, "1", "Male", mi, dimensionOptions);
			PeriodIndicatorReportUtil.addColumn(rd, "2", "Female", fi, dimensionOptions);
			ReportDefinitionService rds = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
			rds.saveDefinition(rd);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
