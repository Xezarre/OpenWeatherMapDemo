package common;

import utility.PropertiesUtils;

public class GlobalVariables {

    // Default wait time
    public static final int WAIT_TIME = 60;
    public static final int WAIT_TIME_30_SEC = 30;
    public static final int WAIT_TIME_60_SEC = 60;
    public static final int WAIT_TIME_90_SEC = 90;
    public static final int WAIT_TIME_180_SEC = 180;
    public static final int WAIT_TIME_NULL = 5;
    public static final int TIMEOUT_ELEMENT = 5;
    public static final int TIMEOUT_PAGE = 10;

    // Execution parameters
    public static String TESTING_TYPE = PropertiesUtils.getPropValue("testingType");
    public static String ENVIRONMENT = PropertiesUtils.getPropValue("environment");
    public static String RUN_ON = PropertiesUtils.getPropValue("runOn");
    public static String BROWSER = PropertiesUtils.getPropValue("browserName");
    public static String THREAD_COUNT = PropertiesUtils.getPropValue("threadCount");
    public static boolean IS_MOBILE = false;
    public static int NUMBER_OF_REPORT = 180;
    public static String HUB_ENDPOINT = "http://localhost:4444/status";

    //Report data
    public static int TOTAL_TESTCASES = 0;
    public static int TOTAL_EXECUTED = 0;
    public static int TOTAL_PASSED = 0;
    public static int TOTAL_FAILED = 0;
    public static int TOTAL_SKIPPED = 0;
    public static boolean REPORT_STATUS = true;
    public static int TOTAL_PASSED_WITH_RETRY = 0;
    public static String RETRY_FAILED_TESTS = PropertiesUtils.getPropValue("retryFailedTests", "No");

    //Project path
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String OUTPUT_PATH = PROJECT_PATH + "/resources/output/";

    //OS name
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    //Test data and configuration
    public static final String TEST_DATA_JSON = PROJECT_PATH + "/src/test/java/";

    //Automation GMAIL variables
    public static final String GMAIL_USERNAME = System.getenv("GMAIL_CRED_USR");
    public static final String GMAIL_PASSWORD = System.getenv("GMAIL_CRED_PSW");
    public static final String ALL_MAIL_FOLDER = "[Gmail]/All Mail";

    // Report email data
    public static final String FROM_RECIPIENT = "vinh@testing.com";
    public static String TO_RECIPIENT = "recipient@testing.com";

    //Test email variables
    public static String EMAIL_ADDRESS = "automation@testing.com";

    //SHOW/HIDE skip test case in report
    public static final boolean SHOW_SKIP = false;

    //Turn off send email when running on local
    public static boolean IS_SEND_EMAIL = false;

    //Environments
    public static final String PRODUCTION = "production";
    public static final String STAGE = "stage";

    //Driver variables
    public static final String CHROME_DRIVER_MAC = PROJECT_PATH + "/resources/drivers/chromedriver";
    public static final String CHROME_DRIVER_WIN = PROJECT_PATH + "/resources/drivers/chromedriver_v79.exe";
    public static final String GECKO_DRIVER_MAC = PROJECT_PATH + "/resources/drivers/geckodriver-v0.23";
    public static final String GECKO_DRIVER_WIN = PROJECT_PATH + "/resources/drivers/geckodriver-v0.23.exe";

    //Database connection urls
    public static final String JDBC_DRIVER = "org.postgresql.Driver";
    public static String DB_URL = "jdbc:postgresql://db-%s.%s.testing.com:5432/%s?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    public static String DB_URL_PRODUCTION = "jdbc:postgresql://db-%s-standby.production.testing.com:5432/%s?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    //Database accounts
    public static final String DB_ADMIN_USERNAME = System.getenv("DB_ADMIN_USR");
    public static final String DB_ADMIN_PASSWORD = System.getenv("DB_ADMIN_PSW");
    public static final String DB_PROD_USERNAME = System.getenv("DB_ADMIN_USR");
    public static final String DB_PROD_PASSWORD = System.getenv("DB_ADMIN_PSW");
    public static final String DB_STAGE_USERNAME = System.getenv("DB_ADMIN_USR");
    public static final String DB_STAGE_PASSWORD = System.getenv("DB_ADMIN_PSW");

    // Sites
    public static String DEFAULT_PROD = "";
    public static String DEFAULT_STAGING = "";

}
