package org.openmrs.module.pihmalawi;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Utils {

    /**
     * @return the connection properties to use
     */
    public static Properties getConnectionProperties(String connectionPropertyFile) throws EvaluationException {
        Properties properties = Context.getRuntimeProperties();
        if (StringUtils.isNotBlank(connectionPropertyFile)) {
            properties = new Properties();
            InputStream is = null;
            try {
                File file = new File(OpenmrsUtil.getApplicationDataDirectory(), connectionPropertyFile);
                is = new FileInputStream(file);
                properties.load(is);
            }
            catch (Exception e) {
                throw new EvaluationException("Unable to load connection properties from file <" + connectionPropertyFile + ">", e);
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }
    /**
     * @return a new connection given a set of connection properties
     */
    public static Connection createConnection(Properties connectionProperties) throws EvaluationException {
        try {
            String driver = connectionProperties.getProperty("connection.driver_class", "com.mysql.jdbc.Driver");
            String url = connectionProperties.getProperty("connection.url");
            String user = connectionProperties.getProperty("connection.username");
            String password = connectionProperties.getProperty("connection.password");
            Context.loadClass(driver);
            return DriverManager.getConnection(url, user, password);
        }
        catch (Exception e) {
            throw new EvaluationException("Unable to create a new connection to the database", e);
        }
    }
}
