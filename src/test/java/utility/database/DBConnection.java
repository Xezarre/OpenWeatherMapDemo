package utility.database;

import com.aventstack.extentreports.ExtentTest;
import utility.JDBCUtils;
import utility.TestReporter;
import utility.Utility;
import static utility.Utility.log4j;
import utility.enums.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import dataobjects.Account;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.StopWatch;

@Getter
public class DBConnection {

    ExtentTest logTest;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;
    private String query;
    private HashMap<String, String> expectedRecord;

    private Account account;
    private String urlConnection;
    private String driverName;

    public DBConnection(ExtentTest logTest, String urlConnection, Account account, String driverName) {
        this.logTest = logTest;
        this.urlConnection = urlConnection;
        this.account = account;
        this.driverName = driverName;
        this.expectedRecord = new HashMap<>();
    }

    @SneakyThrows
    public DBConnection connect() {
        close();
        loadDriver();
        Properties info = getDBConnectionInfo();

        Database type = Database.fromURL(urlConnection);
        logInfo(String.format("Connect to %s DB: %s", type.name(), urlConnection));

        connection = DriverManager.getConnection(urlConnection, info);
        return this;
    }

    public DBConnection tryConnecting(int tryIn) {

        while (connection == null && tryIn-- > 0) {
            this.connect();
        }
        return this;
    }

    @SneakyThrows
    public DBConnection executeQuery() {

        statement = connection.prepareStatement(query);
        resultSet = statement.executeQuery();
        return this;
    }

    @SneakyThrows
    public DBConnection tryExecuteQueryUntilDBUpdate(int timeOutInSeconds) {

        statement = connection.prepareStatement(query);
        tryExecuteQueryIn(timeOutInSeconds);
        return this;
    }

    @SneakyThrows
    public DBConnection tryExecuteQueryUntilDBUpdate(int timeOutInSeconds, int resultSetType, int resultSetConcurrency) {

        statement = connection.prepareStatement(query, resultSetType, resultSetConcurrency);
        tryExecuteQueryIn(timeOutInSeconds);
        return this;
    }

    @SneakyThrows
    private void tryExecuteQueryIn(int timeOutInSeconds) {
        int timeInterval = 2;
        StopWatch stopWatch = StopWatch.createStarted();

        while (!hasResultSet() && stopWatch.getTime(TimeUnit.SECONDS) < timeOutInSeconds) {
            resultSet = statement.executeQuery();
            Utility.sleep(timeInterval);
        }

        stopWatch.stop();

        if (stopWatch.getTime(TimeUnit.SECONDS) > timeOutInSeconds) {
            logInfo(String.format("Try executing query for %s seconds. ", stopWatch.getTime(TimeUnit.SECONDS)));
        }
    }

    @SneakyThrows
    public DBConnection tryExecuteQueryUntilExpectedValue(int timeOutInSeconds, String field, String value) {
        int timeInterval = 2;
        this.expectedRecord = new HashMap<>();

        statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        StopWatch stopWatch = StopWatch.createStarted();

        while (expectedRecord.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < timeOutInSeconds) {
            resultSet = statement.executeQuery();
            expectedRecord = JDBCUtils.getFirstDataWithExpectedValueFromResultSet(resultSet, field, value);
            Utility.sleep(timeInterval);
        }

        if (stopWatch.getTime(TimeUnit.SECONDS) > timeOutInSeconds) {
            logInfo(String.format("Try executing query to find data for %s seconds. But value of column %s does not meet as expected.",
                    stopWatch.getTime(TimeUnit.SECONDS), field));
        }

        stopWatch.stop();
        return this;
    }

    @SneakyThrows
    public DBConnection tryExecuteQueryUntilValueNotNull(int timeOutInSeconds, String field) {
        int timeInterval = 2;
        this.expectedRecord = new HashMap<>();

        statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        StopWatch stopWatch = StopWatch.createStarted();

        while (expectedRecord.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < timeOutInSeconds) {
            resultSet = statement.executeQuery();
            expectedRecord = JDBCUtils.getFirstDataWithValueNotNullFromResultSet(resultSet, field);
            Utility.sleep(timeInterval);
        }

        if (stopWatch.getTime(TimeUnit.SECONDS) > timeOutInSeconds) {
            logInfo(String.format("Try executing query for %s seconds, but value of column %s still null.",
                    stopWatch.getTime(TimeUnit.SECONDS), field));
        }
        stopWatch.stop();
        return this;
    }

    @SneakyThrows
    public DBConnection executeQueryToUpdateDB() {
        statement = connection.prepareStatement(query);
        statement.executeUpdate();
        return this;
    }

    @SneakyThrows
    public DBConnection close() {
        if (resultSet != null) {
            resultSet.close();
        }

        if (statement != null) {
            statement.close();
        }

        if (connection != null) {
            connection.close();
        }
        return this;
    }

    public DBConnection createQuery(String query, Object... args) {
        logInfo(createLogFromQuery(query, args));
        this.query = String.format(query, args);
        return this;
    }

    @SneakyThrows
    private void loadDriver() {
        Class.forName(driverName);
    }

    private Properties getDBConnectionInfo() {

        Properties connectionInfo = new Properties();
        connectionInfo.setProperty("user", account.getUsername());
        connectionInfo.setProperty("password", account.getPassword());
        connectionInfo.setProperty("socketTimeout", "0");
        connectionInfo.setProperty("tcpKeepAlive", "true");
        return connectionInfo;
    }

    @SneakyThrows
    private boolean hasResultSet() {
        return resultSet != null && resultSet.isBeforeFirst();
    }

    private static String createLogFromQuery(String query, Object... args) {
        String log = "Query: ".concat(query);
        return String.format(log, args);
    }

    private void logInfo(String log) {
        log4j.info(log);
        TestReporter.logInfo(this.logTest, log);
    }

    private void logFail(String log) {
        log4j.error(log);
        TestReporter.logFail(this.logTest, log);
    }
}

