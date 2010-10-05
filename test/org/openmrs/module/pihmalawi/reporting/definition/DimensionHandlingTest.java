package org.openmrs.module.pihmalawi.reporting.definition;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.SetupArtWeeklyVisit;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.Rollback;

public class DimensionHandlingTest extends BaseModuleContextSensitiveTest {
	
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

		CohortDefinitionDimension md = new CohortDefinitionDimension();
		md.setName("HIV program location_");
		md.addParameter(new Parameter("endDate", "End Date", Date.class));
		// todo, why are location and startdate for me mandatory?
		md.addParameter(new Parameter("startDate", "Start Date", Date.class));
		md.addParameter(new Parameter("location", "Location", Location.class));
		
		DimensionService s = (DimensionService) Context.getService(DimensionService.class);
		List<Dimension> defs = s.getAllDefinitions(false); 
		for (Dimension def : defs) {
			s.purgeDefinition(def);
		}
		s.saveDefinition(md);
		
	}
}
