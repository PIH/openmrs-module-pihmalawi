package org.openmrs.module.pihmalawi.alert;

import org.junit.Test;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;

public class ViralLoadAlertsTest {

    AlertEngine alertEngine = new AlertEngine();

    long today = DateUtil.getDateTime(2018, 10, 15).getTime();
    String onArvs = HivMetadata.HIV_PROGRAM_STATUS_ON_ARVS;
    String onEid = HivMetadata.HIV_PROGRAM_STATUS_ON_ARVS;

	@Test
	public void shouldTestAgeCondition() throws Exception {

    }

    protected JsonObject getData(int age, String hivStatus, Long vlDate, Double vlNum, Boolean vlLdl, Long artDate, String adhNum, Long adhDate) {
        JsonObject data = new JsonObject();
        data.put("today", today);
        data.put("age_years", age);
        data.put("hiv_treatment_status", hivStatus);
        data.put("last_viral_load_date", vlDate);
        data.put("last_viral_load_numeric", vlNum);
        data.put("last_viral_load_ldl", vlLdl);
        data.put("last_art_regimen_change_date", artDate);
        data.put("last_adherence_counselling_session_number", adhNum);
        data.put("last_adherence_counselling_session_date", adhDate);
        return data;
    }

    protected void testAlert(String alert, boolean pass, JsonObject data) {

    }

    protected long daysAgo(long days) {
        return today - days*24*60*60*1000;
    }
}
