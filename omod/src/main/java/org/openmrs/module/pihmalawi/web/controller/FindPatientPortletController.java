package org.openmrs.module.pihmalawi.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.PatientSearchCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.NullValueConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for the findPatient portlet that overrides the one supplied by core
 */
@Controller
@Order(50)
public class FindPatientPortletController {

	public static final String SOUNDEX_PREFIX = "s:";
	public static final String SOUNDEX_SUFFIX = ":s";

	@Autowired
	BuiltInPatientDataLibrary builtInPatientData;

	@Autowired
	BasePatientDataLibrary basePatientData;

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

	//***** CONTROLLER METHODS *****

	@RequestMapping(value = "**/findPatient.portlet")
	public String viewPortlet() {
		return "/module/pihmalawi/portlets/findPatient";
	}

	@RequestMapping(value = "/module/pihmalawi/findMatchingPatients")
	public void findMatchingPatients(ModelMap model,
							   @RequestParam("phrase") String phrase,
							   @RequestParam(value = "soundexEnabled", required = false) String soundexEnabled,
							   HttpServletResponse response) throws Exception {

		List patientRows = findMatchingPatients(phrase, Boolean.parseBoolean(soundexEnabled));
		response.setContentType("text/json");
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), patientRows);
	}

	public List findMatchingPatients(String phrase, boolean soundexEnabled) throws Exception {
		if (phrase != null) {
			phrase = phrase.trim();
			if (phrase.toLowerCase().startsWith(SOUNDEX_PREFIX)) {
				phrase = phrase.substring(SOUNDEX_PREFIX.length());
				soundexEnabled = true;
			}
			else if (phrase.toLowerCase().endsWith(SOUNDEX_SUFFIX)) {
				phrase = phrase.substring(0, phrase.length() - SOUNDEX_SUFFIX.length());
				soundexEnabled = true;
			}
		}

		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientSearchCohortDefinition cd = new PatientSearchCohortDefinition();
		cd.setSearchPhrase(phrase);
		cd.setSoundexEnabled(soundexEnabled);
		cd.setIdentifierMatchMode(PatientSearchCohortDefinition.MatchMode.START);
		dsd.addRowFilter(Mapped.noMappings(cd));

		DataConverter defaultConverter = new NullValueConverter("");

		dsd.addColumn("patientId", builtInPatientData.getPatientId(), "", defaultConverter);
		dsd.addColumn("identifier", builtInPatientData.getPreferredIdentifierIdentifier(), "", defaultConverter);
		dsd.addColumn("givenName", builtInPatientData.getPreferredGivenName(), "", defaultConverter);
		dsd.addColumn("familyName", builtInPatientData.getPreferredFamilyName(), "", defaultConverter);
		dsd.addColumn("familyName2", builtInPatientData.getPreferredFamilyName2(), "", defaultConverter);
		dsd.addColumn("gender", builtInPatientData.getGender(), "", defaultConverter);
		dsd.addColumn("age", basePatientData.getAgeAtEndInYears(), "endDate=${endDate}", defaultConverter);
		dsd.addColumn("birthdateDisplay", basePatientData.getBirthdate(), "", new ObjectFormatter());
		dsd.addColumn("birthdateYmd", basePatientData.getBirthdate(), "", new ObjectFormatter("yyyy-MM-dd"));

		SimpleDataSet dataSet = (SimpleDataSet)dataSetDefinitionService.evaluate(dsd, new EvaluationContext());

		List rows = new ArrayList();
		for (DataSetRow row : dataSet.getRows()) {
			rows.add(row.getColumnValuesByKey());
		}
		return rows;
	}
}
