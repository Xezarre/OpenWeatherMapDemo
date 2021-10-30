package utility.database;

import com.aventstack.extentreports.ExtentTest;
import dataobjects.Account;
import lombok.NoArgsConstructor;
import utility.AccountUtils;
import utility.enums.Database;

@NoArgsConstructor
public class DBFactory {

    public static DBConnection createConnection(ExtentTest logTest, Database dataBase) {
        String urlConnection = dataBase.getURL();
        Account account = AccountUtils.getDbAccount(dataBase);
        String driverName = dataBase.getType().getJdbcDriver();

        return new DBConnection(logTest, urlConnection, account, driverName);
    }
}