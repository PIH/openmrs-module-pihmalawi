
package org.openmrs.module.pihmalawi.reporting.reports;

import org.junit.Ignore;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Test the Regimen Dispensation Report
 */
@Ignore
public class RegimenDispensationReportTest extends ReportManagerTest {


    @Autowired
    RegimenDispensationReport report;

    @Override
    public ReportManager getReportManager() {
        return report;
    }

    @Override
    public void performTest() throws Exception {
        String url = "jdbc:mysql://localhost:3306/openmrs_neno_warehouse?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "zxqw1234@@##";

        File propertiesFile = new File(OpenmrsUtil.getApplicationDataDirectory(), PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
        Properties properties = new Properties();
        properties.put("connection.url", url);
        properties.put("connection.username", user);
        properties.put("connection.password", password);
        properties.store(new FileOutputStream(propertiesFile), null);

        super.performTest();
    }

    @Override
    public EvaluationContext getEvaluationContext() {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("endDate", DateUtil.getDateTime(2020, 3, 16));
        context.addParameterValue("location", "Neno District Hospital");
        return context;
    }

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    public boolean enableReportOutput() {
        return true;
    }
}
