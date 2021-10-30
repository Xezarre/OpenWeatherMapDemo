package utility.database;

import com.aventstack.extentreports.ExtentTest;
import utility.JDBCUtils;
import utility.enums.Database;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class DBManager {

    public static void executeQueryToUpdateDB(ExtentTest logTest, Database dataBase, String query, Object... args) {
        logTest = DBFactory.createConnection(logTest, dataBase)
                .connect()
                .createQuery(query, args)
                .executeQueryToUpdateDB()
                .close()
                .getLogTest();
    }

    public static List<HashMap<String, Object>> executeQueryAndGetResultAsList(ExtentTest logTest, Database dataBase, String query, Object... args) {
        int timeOutInSeconds = 120;
        DBConnection dbConnection = DBFactory.createConnection(logTest, dataBase);

        ResultSet resultSet = dbConnection.connect()
                .createQuery(query, args)
                .tryExecuteQueryUntilDBUpdate(timeOutInSeconds)
                .getResultSet();

        return JDBCUtils.getResultSetAsList(resultSet);
    }

    public static HashMap<String, String> executeQueryTillNotNull(ExtentTest logTest, Database dataBase, String field, String query, Object... args) {
        int timeOutInSeconds = 120;
        DBConnection dbConnection = DBFactory.createConnection(logTest, dataBase);

        HashMap<String, String> firstRecord = dbConnection.connect()
                .createQuery(query, args)
                .tryExecuteQueryUntilValueNotNull(timeOutInSeconds, field)
                .getExpectedRecord();

        return firstRecord;
    }

    public static HashMap<String, String> executeQueryTillExpectedValue(ExtentTest logTest, Database dataBase, String field, String value, String query, Object... args) {
        int timeOutInSeconds = 120;
        DBConnection dbConnection = DBFactory.createConnection(logTest, dataBase);

        HashMap<String, String> record = dbConnection.connect()
                .createQuery(query, args)
                .tryExecuteQueryUntilExpectedValue(timeOutInSeconds, field, value)
                .getExpectedRecord();

        return record;
    }

    public static HashMap<String, String> executeQueryAndGetFirstResultReturnNullIfNoRecord(ExtentTest logTest, Database dataBase, String query, Object... args) {

        int timeOutInSeconds = 120;
        DBConnection dbConnection = DBFactory.createConnection(logTest, dataBase);

        ResultSet resultSet = dbConnection.connect()
                .createQuery(query, args)
                .tryExecuteQueryUntilDBUpdate(timeOutInSeconds)
                .getResultSet();

        return JDBCUtils.getFirstDataFromResultSet(resultSet);
    }

    public static HashMap<String, String> executeQueryAndGetFirstResult(ExtentTest logTest, Database dataBase, String query, Object... args) {

        int timeOutInSeconds = 120;
        DBConnection dbConnection = DBFactory.createConnection(logTest, dataBase);

        ResultSet resultSet = dbConnection.connect()
                .createQuery(query, args)
                .tryExecuteQueryUntilDBUpdate(timeOutInSeconds)
                .getResultSet();

        return JDBCUtils.getFirstDataFromResultSet(resultSet);
    }
}

