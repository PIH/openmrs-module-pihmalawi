/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.sql;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes a MySQL script using the native process and returns results
 */
public class MysqlRunner {

	private static Log log = LogFactory.getLog(MysqlRunner.class);

    /**
     * Executes a Sql Script located under resources
     */
    public static SqlResult executeSqlResource(String resourceName) {
        return executeSqlResource(resourceName, new HashMap<String, Object>());
    }

    /**
     * Executes a Sql Script located under resources
     */
    public static SqlResult executeSqlResource(String resourceName, Map<String, Object> parameterValues) {
        String sql = ReportUtil.readStringFromResource(resourceName);
        return executeSql(sql, parameterValues);
    }

    /**
     * Executes a Sql Script located under resources
     */
    public static SqlResult executeSql(String sql) {
        return executeSql(sql, new HashMap<String, Object>());
    }

	/**
     * Executes a Sql Script
	 */
	public static SqlResult executeSql(String sql, Map<String, Object> parameterValues) {

        log.info("Executing SQL...");

        File toExecute = null;
        try {
            // Writing SQL to temporary file for execution
            toExecute = File.createTempFile("mysqlrunner", ".sql");

            StringBuilder sqlToWrite = new StringBuilder();

            if (parameterValues != null) {
                for (String paramName : parameterValues.keySet()) {
                    Object paramValue = parameterValues.get(paramName);
                    sqlToWrite.append("set @").append(paramName);
                    sqlToWrite.append("=").append(getParameterAssignmentString(paramValue)).append(";");
                    sqlToWrite.append(System.getProperty("line.separator"));
                }
            }
            sqlToWrite.append(sql);

            FileUtils.writeStringToFile(toExecute, sqlToWrite.toString());
            log.debug("Wrote SQL file for execution: " + toExecute.getAbsolutePath());
            log.debug("Contents:\n" + sqlToWrite);

            // Constructing command line elements to execute
            List<String> commands = new ArrayList<String>();
            commands.add("mysql");
            commands.add("-u" + Context.getRuntimeProperties().getProperty("connection.username"));
            commands.add("-p" + Context.getRuntimeProperties().getProperty("connection.password"));
            commands.add("-esource " + toExecute.getAbsolutePath());

            commands.add(DatabaseUpdater.getConnection().getCatalog()); // Database Name
            log.debug("Constructed command to execute: \n" + OpenmrsUtil.join(commands, " "));

            Process process = Runtime.getRuntime().exec(commands.toArray(new String[]{}));

            SqlResult result = new SqlResult();
            LineIterator successIterator = null;
            try {
                successIterator = IOUtils.lineIterator(process.getInputStream(), "UTF-8");
                while (successIterator.hasNext()) {
                    String line = successIterator.nextLine();
                    String[] elements = StringUtils.splitPreserveAllTokens(line, '\t');
                    if (result.getColumns().isEmpty()) {
                        result.setColumns(Arrays.asList(elements));
                    }
                    else {
                        Map<String, String> row = new LinkedHashMap<String, String>();
                        for (int i=0; i<result.getColumns().size(); i++) {
                            String value = elements[i].trim();
                            if ("NULL".equals(value)) {
                                value = null;
                            }
                            row.put(result.getColumns().get(i), value);
                        }
                    }
                }
            }
            finally {
                successIterator.close();
            }

            LineIterator errorIterator = null;
            try {
                errorIterator = IOUtils.lineIterator(process.getErrorStream(), "UTF-8");
                while (errorIterator.hasNext()) {
                    String line = errorIterator.nextLine();
                    if (!line.toLowerCase().startsWith("warning")) {
                        result.getErrors().add(line);
                    }
                }
            }
            finally {
                errorIterator.close();
            }

            return result;
        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while executing a SQL file", e);
        }
        finally {
            FileUtils.deleteQuietly(toExecute);
        }
	}

    public static String getParameterAssignmentString(Object paramValue) {
        if (paramValue == null) {
            return "'null'";
        }
        else {
            if (paramValue instanceof Date) {
                return "'" + DateUtil.formatDate((Date)paramValue, "yyyy-MM-dd") + "'";
            }
            else if (paramValue instanceof Number) {
                return paramValue.toString();
            }
            else if (paramValue instanceof OpenmrsObject) {
                return ((OpenmrsObject)paramValue).getId().toString();
            }
            else {
                return  "'" + paramValue.toString() + "'";
            }
        }
    }
}
