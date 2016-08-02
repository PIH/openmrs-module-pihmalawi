package org.openmrs.module.pihmalawi.reporting;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.common.AppointmentInfo;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.pihmalawi.reporting.reports.HivDataQualityReport;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuickTest extends StandaloneContextSensitiveTest {

	@Autowired
	DataFactory df;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	ChronicCareMetadata ccMetadata;

	@Autowired
	HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	HivEncounterQueryLibrary hivEncounterQueries;

	@Autowired
	ReportDefinitionService reportDefinitionService;

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

    @Autowired
    PatientDataService patientDataService;

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {
		LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);

		EvaluationContext context = new EvaluationContext();

        Map<String, PatientDataDefinition> l = new LinkedHashMap<String, PatientDataDefinition>();
        l.put("hiv", df.getAppointmentStatus(hivMetadata.getActiveHivStates(), hivMetadata.getHivEncounterTypes()));
        l.put("asthma", df.getAppointmentStatus(ccMetadata.getActiveChronicCareStates(), ccMetadata.getAsthmaEncounterTypes()));
        l.put("htn_dm", df.getAppointmentStatus(ccMetadata.getActiveChronicCareStates(), ccMetadata.getHtnDiabetesEncounterTypes()));
        l.put("epilepsy", df.getAppointmentStatus(ccMetadata.getActiveChronicCareStates(), ccMetadata.getEpilepsyEncounterTypes()));
        l.put("mental_health", df.getAppointmentStatus(ccMetadata.getActiveChronicCareStates(), ccMetadata.getMentalHealthEncounterTypes()));

        ListMap<Integer, Map<String, Object>> patientApptData = new ListMap<Integer, Map<String, Object>>();
        for (String s : l.keySet()) {
            PatientDataDefinition pdd = l.get(s);
            PatientData pd = patientDataService.evaluate(pdd, context);
            for (Integer pId : pd.getData().keySet()) {
                AppointmentInfo info = (AppointmentInfo)pd.getData().get(pId);
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("apptType", s);
                data.put("apptDate", info.getNextScheduledDate());
                patientApptData.putInList(pId, data);
            }
        }

        System.out.println("Found " + patientApptData.size() + " patient with appointment data");
        Date now = new Date();

        for (Iterator<Integer> iter = patientApptData.keySet().iterator(); iter.hasNext();) {
            Integer pId = iter.next();
            List<Map<String, Object>> appts = patientApptData.get(pId);
            if (appts == null || appts.size() == 1) {
                iter.remove();
            }
            else {
                int numInPast = 0;
                Set<Date> dates = new HashSet<Date>();
                for (Map<String, Object> ai : appts) {
                    Date apptDate = (Date)ai.get("apptDate");
                    dates.add(apptDate);
                    if (apptDate != null && apptDate.before(now)) {
                        numInPast++;
                    }
                }
                if (dates.size() == 1 || numInPast == 0) {
                    iter.remove();
                }
            }
        }

        System.out.println("Found " + patientApptData.size() + " patient with discordant data");
        for (Integer pId : patientApptData.keySet()) {
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> ai : patientApptData.get(pId)) {
                sb.append(sb.length() == 0 ? "" : ", ");
                sb.append(ai.get("apptType") + ": " + DateFormatUtils.format((Date)ai.get("apptDate"), "yyyy-MM-dd"));
            }
            System.out.println(pId + ": " + sb);
        }
	}
}
