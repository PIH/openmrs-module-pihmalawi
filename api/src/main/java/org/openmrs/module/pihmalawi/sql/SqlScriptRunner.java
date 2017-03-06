package org.openmrs.module.pihmalawi.sql;

/*
 * Slightly modified version of ScriptRunner
 * https://github.com/BenoitDuffez/ScriptRunner
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlScriptRunner {

    private static Log log = LogFactory.getLog(SqlScriptRunner.class);

    private static final String DEFAULT_DELIMITER = ";";
    /**
     * regex to detect delimiter.
     * ignores spaces, allows delimiter in comment, allows an equals-sign
     */
    public static final Pattern delimP = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);
    private final Connection connection;

    private final boolean stopOnError;
    private final boolean autoCommit;

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;

    public SqlScriptRunner(Connection connection, boolean stopOnError, boolean autoCommit) {
        this.connection = connection;
        this.stopOnError = stopOnError;
        this.autoCommit = autoCommit;
    }

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }
    private String getDelimiter() {
        return delimiter;
    }
    /**
     * Runs an SQL script (read in using the Reader parameter)
     *
     * @param reader - the source of the script
     */
    public SqlResult runScript(Reader reader) throws IOException, SQLException {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                return runScript(connection, reader);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
        catch (IOException e) {
            throw e;
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in
     *
     * @param conn - the connection to use for the script
     * @param reader - the source of the script
     * @throws SQLException if any SQL errors occur
     * @throws IOException if there is an error reading from the Reader
     */
    private SqlResult runScript(Connection conn, Reader reader) throws IOException,
            SQLException {
        SqlResult result = null;
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                final Matcher delimMatch = delimP.matcher(trimmedLine);
                if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("//")) {
                    // Do nothing
                } else if (delimMatch.matches()) {
                    setDelimiter(delimMatch.group(2), false);
                } else if (trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {
                    command.append(line.substring(0, line
                            .lastIndexOf(getDelimiter())));
                    command.append(" ");
                    result = this.execCommand(conn, command, lineReader);
                    command = null;
                } else {
                    command.append(line);
                    command.append("\n");
                }
            }
            if (command != null) {
                result = this.execCommand(conn, command, lineReader);
            }
            if (!autoCommit) {
                conn.commit();
            }
        }
        catch (IOException e) {
            throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
        } finally {
            conn.rollback();
        }

        return result;
    }

    private SqlResult execCommand(Connection conn, StringBuffer command,
                                  LineNumberReader lineReader) throws SQLException {
        Statement statement = conn.createStatement();

        SqlResult result = new SqlResult();

        boolean hasResults = false;
        try {
            hasResults = statement.execute(command.toString());
        }
        catch (SQLException e) {
            final String errText = String.format("Error executing '%s' (line %d): %s",
                    command, lineReader.getLineNumber(), e.getMessage());
            log.error(errText);
            result.getErrors().add(errText);
            if (stopOnError) {
                throw new SQLException(errText, e);
            }
        }

        if (autoCommit && !conn.getAutoCommit()) {
            conn.commit();
        }

        ResultSet rs = statement.getResultSet();
        if (hasResults && rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            List<String> columns = new ArrayList<String>();
            for (int i = 1; i <= cols; i++) {
                String name = md.getColumnLabel(i);
                columns.add(name);
            }
            if ( columns!=null && columns.size() > 0) {
                result.setColumns(columns);
            }
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                Map<String, String> row = new HashMap<String, String>();
                for (int i = 1; i <= cols; i++) {
                    String value = rs.getString(i);
                    if ("NULL".equals(value)) {
                        value = null;
                    }
                    String column = columns.get(i-1);
                    row.put(column, value);
                }
                data.add(row);
            }
            if (data != null && data.size() > 0) {
                result.setData(data);
            }
        }

        try {
            statement.close();
        }
        catch (Exception e) {
            // Ignore to workaround a bug in Jakarta DBCP
        }

        return result;
    }
}
