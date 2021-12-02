package org.openmrs.module.pihmalawi.reporting;

import org.junit.Ignore;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.pihmalawi.sql.SqlResult;
import org.openmrs.module.pihmalawi.sql.SqlRunner;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class IC3RegisterTest extends StandaloneContextSensitiveTest {

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	public void performTest() throws Exception {

	    SqlRunner functionRunner = new SqlRunner(getConnection(), "#");
	    functionRunner.executeSqlResource("sqlReportingHelperFunctions/ic3RegisterSqlProcedures.sql", null);
        functionRunner.executeSqlResource("sqlReportingHelperFunctions/reportingSqlProcedures.sql", null);

	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("reportEndDate", new Date());
        String resource = "org/openmrs/module/pihmalawi/reporting/reports/sql/ic3_register.sql";

        SqlRunner runner = new SqlRunner(getConnection());
        SqlResult result = runner.executeSqlResource(resource, parameters);

        System.out.println(result.getData().size() + " results found");
        System.out.println("------ FIRST 10 RESULTS -----");
        System.out.println(OpenmrsUtil.join(result.getColumns(), "\t\t"));
        int i=0;
        for (Map<String, Object> row : result.getData()) {
            if (i++ <= 10) {
                System.out.println(OpenmrsUtil.join(row.values(), "\t\t"));
            }
        }
        System.out.println("------ ERRORS -----");
        for (String error : result.getErrors()) {
            System.out.println(error);
        }
    }
}
