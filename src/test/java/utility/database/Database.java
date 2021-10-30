package utility.database;

import com.aventstack.extentreports.ExtentTest;
import utility.TestReporter;
import utility.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static common.GlobalVariables.*;

public class Database extends Utility {

    /**
     * Open a connection session to database. Retry maximum 3 times if failing to connect
     */
    public Connection connectDatabase(String databaseName, ExtentTest logTest){
        Connection dbConnection = null;
        String Database_URL;

        // Retry connect database 3 times if it fails
        int count = 1;
        while (dbConnection == null && count < 3) {
            try {
                String user;
                String pwd;

                if (ENVIRONMENT.contains("stage")){
                    user = DB_STAGE_USERNAME;
                    pwd = DB_STAGE_PASSWORD;
                }
                else if(ENVIRONMENT.contains("production")){
                    user = DB_PROD_USERNAME;
                    pwd = DB_PROD_PASSWORD;
                }
                else {
                    user = DB_ADMIN_USERNAME;
                    pwd = DB_ADMIN_PASSWORD;
                }

                Class.forName(JDBC_DRIVER);
                if (ENVIRONMENT.equalsIgnoreCase("production")) {
                    Database_URL = String.format(DB_URL_PRODUCTION, databaseName, databaseName);
                    log4j.info("Connect to Production DB:" + Database_URL);
                    TestReporter.logInfo(logTest, "Open connection for Production database");
                }
                else {
                    Database_URL = String.format(DB_URL, databaseName, ENVIRONMENT, databaseName);
                    log4j.info("Connect to non production DB: " + Database_URL);
                    TestReporter.logInfo(logTest, "Open connection: " + Database_URL);
                }

                Properties props = new Properties();
                props.setProperty("user", user);
                props.setProperty("password",pwd);
                props.setProperty("socketTimeout", "120");
                props.setProperty("tcpKeepAlive", "true");
                dbConnection = DriverManager.getConnection(Database_URL, props);

            } catch (Exception e) {
                count++;
                log4j.info("Connecting failed, retrying...");
                TestReporter.logInfo(logTest, "Connecting failed, retrying...");
                log4j.error("connectDatabase method - ERROR - ", e);
            }
        }
        return dbConnection;
    }

    /**
     * Close connection, statement if they're open
     */
    public void closeDBConnection(Connection conn, PreparedStatement stmt, ExtentTest logTest){
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {

            e.printStackTrace();
            log4j.error("closeDBConnection method - ERROR - " + e);
            TestReporter.logException(logTest, "closeDBConnection method- ERROR ", e);
        }
    }



    /**
     * Input query, query parameters (dynamic), then execute query in indicated database (merchant, warranty,etc.)
     * @return : if empty result after querying, log fail; if non-empty result, return list of records
     * */
    public List<HashMap<String,Object>> executeQueryAndGetListResult(ExtentTest logTest, String dbName, String query, Object... args) {

        Connection conn = null;
        ResultSet results;
        PreparedStatement stmt = null;
        List<HashMap<String,Object>> list = new ArrayList<>();
        String formattedQuery = String.format(query,args);
        log4j.info("Query: " + formattedQuery);
        TestReporter.logInfo(logTest, "Query: " + formattedQuery);

        try {
            conn = connectDatabase(dbName, logTest);
            stmt = conn.prepareStatement(formattedQuery);
            results = stmt.executeQuery();

            //retry execute query in 120s, wait for database update
            int count = 0;
            while (!results.isBeforeFirst() && count < WAIT_TIME *2) {
                results = stmt.executeQuery();
                sleep(2);
                count++;
            }

            if (count == WAIT_TIME * 2) {
                TestReporter.logFail(logTest, "Waited for data from database " + count + " seconds. But record did not get updated.");
            } else {
                ResultSetMetaData md = results.getMetaData();
                int columns = md.getColumnCount();

                while (results.next()){
                    HashMap<String,Object> row = new HashMap<>(columns);
                    for(int i=1; i<=columns; ++i){
                        row.put(md.getColumnName(i),results.getObject(i));
                    }
                    list.add(row);
                }
            }
            results.close();

        } catch (Exception e) {
            log4j.error("executeQueryAndGetListResult method - ERROR - " + e);
            TestReporter.logException(logTest, "executeQueryAndGetListResult method- ERROR ", e);
        }
        finally {
            closeDBConnection(conn, stmt, logTest);
        }
        return list;
    }

    public void executeQueryToUpdateDB(ExtentTest logTest, String dbName, String query, Object... args) {

        Connection conn = null;
        PreparedStatement stmt = null;

        String formattedQuery = String.format(query,args);
        log4j.info("Query: " + formattedQuery);
        TestReporter.logInfo(logTest, "Query: " + formattedQuery);

        try {
            conn = connectDatabase(dbName, logTest);
            stmt = conn.prepareStatement(formattedQuery);
            stmt.executeUpdate();

        } catch (Exception e) {
            log4j.error("executeQueryToUpdateDB method - ERROR - " + e);
            TestReporter.logException(logTest, "executeQueryToUpdateDB method- ERROR ", e);
        }
        finally {
            closeDBConnection(conn, stmt, logTest);
        }
    }
}
