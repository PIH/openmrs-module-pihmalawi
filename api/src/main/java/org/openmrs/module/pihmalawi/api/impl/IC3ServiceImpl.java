package org.openmrs.module.pihmalawi.api.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.Utils;
import org.openmrs.module.pihmalawi.api.IC3Service;
import org.openmrs.module.pihmalawi.reporting.reports.IC3AppointmentReport;
import org.openmrs.module.pihmalawi.sql.SqlResult;
import org.openmrs.module.pihmalawi.sql.SqlRunner;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.util.OpenmrsUtil;

import java.sql.Connection;
import java.util.*;

public class IC3ServiceImpl extends BaseOpenmrsService implements IC3Service{

    protected Log log = LogFactory.getLog(this.getClass());

    @Override
    public SimpleObject getIC3AppointmentData(String locationUuid, String endDate) {
        Properties connectionProperties = null;
        Connection connection = null;
        List<SimpleObject> patients = null;

        if (StringUtils.isNotBlank(locationUuid) && StringUtils.isNotBlank(endDate)) {
            Location clinicLocation = Context.getLocationService().getLocationByUuid(locationUuid);
            if (clinicLocation != null) {
                try {
                    connectionProperties = Utils.getConnectionProperties(PihMalawiConstants.OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME);
                    connection = Utils.createConnection(connectionProperties);
                    SqlRunner runner = new SqlRunner(connection);

                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("location", clinicLocation.getName());
                    parameters.put("endDate", endDate);

                    SqlResult resultData = runner.executeSqlResource(IC3AppointmentReport.SQL_DATA_SET_RESOURCE, parameters);
                    if (!resultData.getErrors().isEmpty()) {
                        log.error("Failed to retrieve appointment data: " + OpenmrsUtil.join(resultData.getErrors(), "; "));
                    } else {
                        patients = new ArrayList<SimpleObject>();
                        for (Map<String, Object> rowData : resultData.getData()) {
                            SimpleObject patient = new SimpleObject();
                            for (String column : resultData.getColumns()) {
                                patient.add(column, rowData.get(column));
                            }

                            Map<String, Object> labParameters = new HashMap<String, Object>();
                            labParameters.put("patientUuid", rowData.get("patient_uuid"));
                            SqlResult labData = runner.executeSqlResource(PihMalawiConstants.PATIENT_LAB_TESTS_SQL_DATA_SET, labParameters);
                            if (!labData.getErrors().isEmpty()){
                                log.error("Failed to retrieve lab tests data: " + OpenmrsUtil.join(labData.getErrors(), "; "));
                            } else {
                                List<SimpleObject> labTests = new ArrayList<SimpleObject>();
                                for (Map<String, Object> rowLabTest : labData.getData()) {
                                    SimpleObject test = new SimpleObject();
                                    for (String column : labData.getColumns()) {
                                        test.add(column, rowLabTest.get(column));
                                    }
                                    labTests.add(test);
                                }
                                patient.add("labTests", labTests);
                            }
                            patients.add(patient);
                        }
                    }
                } catch (EvaluationException e) {
                    log.error("failed to get db connection");
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (Exception e) {
                        log.error("Error while closing db connection", e);
                    }
                }
            }
        }

        return new SimpleObject()
                .add("patients", patients);
    }


}
