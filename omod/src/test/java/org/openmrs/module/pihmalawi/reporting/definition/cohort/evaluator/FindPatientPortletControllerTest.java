package org.openmrs.module.pihmalawi.reporting.definition.cohort.evaluator;

import org.openmrs.module.pihmalawi.web.controller.FindPatientPortletController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FindPatientPortletControllerTest extends StandaloneWebContextSensitiveTest {

	@Autowired
	FindPatientPortletController controller;

	@Override
	protected void performTest() throws Exception {
		List patients = controller.findMatchingPatients("Mike", false);
		System.out.println(patients);
	}

	@Override
	protected boolean isEnabled() {
		return false;
	}
}
