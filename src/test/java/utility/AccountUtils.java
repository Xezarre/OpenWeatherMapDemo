package utility;

import dataobjects.Account;
import utility.enums.Database;
import utility.enums.Environments;
import static common.GlobalVariables.*;

public class AccountUtils {

    //Database accounts
    public static final Account DB_ADMIN_ACCOUNT = new Account(
            System.getenv("DB_ADMIN_CRED_USR"),
            System.getenv("DB_ADMIN_CRED_PSW")
    );

    public static final Account DB_STAGE_ACCOUNT = new Account(
            System.getenv("DB_STAGE_CRED_USR"),
            System.getenv("DB_STAGE_CRED_PSW")
    );

    public static final Account DB_PROD_ACCOUNT = new Account(
            System.getenv("DB_PROD_CRED_USR"),
            System.getenv("DB_PROD_CRED_PSW")
    );

    private static final Environments ENV = Environments.fromName(ENVIRONMENT);

    public static Account getDbAccount(Database dataBase) {
        if (ENV == Environments.PRODUCTION) {
            return DB_PROD_ACCOUNT;
        } else if (ENV.startWithStageOrQA()) {
            return DB_STAGE_ACCOUNT;
        } else {
            return DB_ADMIN_ACCOUNT;
        }
    }
}
