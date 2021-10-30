package common;

import api.selenium_services.SeleniumServices;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import utility.*;
import io.restassured.response.Response;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static common.GlobalVariables.*;

public class TestBase extends Utility {

    public boolean isTestCaseExecutable = false;
    public static boolean isTestSuiteExecutable = false;

    public ExtentTest logStep = null;
    public ExtentTest logClass = null;
    public ExtentTest logMethod = null;
    public static ExtentTest logSuite = null;

    public String testCaseName;
    public String testNameWithStatus;
    public static ArrayList<String> testcaseList = new ArrayList<>();

    @BeforeSuite()
    public synchronized void beforeSuite(ITestContext context) throws IOException {

        // Initiate log4j property system
        log4jConfiguration();
        DOMConfigurator.configure("resources/suites/log4j.xml");

        log4j.info("beforeSuite method - Start");

        // Initiate Test report
        try {
            htmlReporter = new ExtentHtmlReporter(reportFilePath);
            htmlReporter.loadXMLConfig(new File(PROJECT_PATH + "/resources/suites/config.xml"));
            report = new ExtentReports();
            report.attachReporter(htmlReporter);
            logSuite = TestReporter.createTestForExtentReport(report, "Initial Setup");

        } catch (Exception e) {

            log4j.error("ERROR while initializing Extend report: " + e.getStackTrace());
            TestReporter.logException(logSuite, "ERROR while initializing Extent Report", e);
        }

        // Create report folder
        TestReporter.logInfo(logSuite, "Report folder: " + reportLocation);
        File folder = new File(reportLocation);
        folder.mkdirs();

        // Validate toRecipient
        if (System.getProperty("toRecipient") != null) {
            TO_RECIPIENT = System.getProperty("toRecipient");
            IS_SEND_EMAIL = true;
        }

        // Validate environment
        if (ENVIRONMENT == null || ENVIRONMENT.equals(""))
            TestReporter.logFail(logSuite, "Invalid 'environment' parameter: " + ENVIRONMENT);
        else if (ENVIRONMENT.toLowerCase().startsWith("stage")) {
            TestReporter.logInfo(logSuite, "Environment: " + ENVIRONMENT);
            // Configuration
        } else {
            TestReporter.logInfo(logSuite, "Environment: " + ENVIRONMENT);
            // Another configuration
        }

        // Validate runOn
        if (RUN_ON != null && (RUN_ON.equalsIgnoreCase("Local") || RUN_ON.equalsIgnoreCase("Grid"))) {
            TestReporter.logInfo(logSuite, "Run On: " + RUN_ON);
        } else {
            TestReporter.logFail(logSuite, "Invalid 'runOn' parameter: " + RUN_ON);
        }

        //Health check status of selenium hub
        try {
            if (RUN_ON.equalsIgnoreCase("Grid")) {

                Response response = SeleniumServices.gridHealthCheckRequestSpecification(HUB_ENDPOINT, logSuite);
                assert response != null;
                Boolean gridHealthCheckStatus = (Boolean) response.jsonPath().getMap("value").get("ready");
                String gridHealthCheckMessage = (String) response.jsonPath().getMap("value").get("message");
                if (!gridHealthCheckStatus) {
                    log4j.error(gridHealthCheckMessage);
                    TestReporter.logFailBeforeSuite(logSuite, gridHealthCheckMessage);
                } else TestReporter.logInfo(logSuite, gridHealthCheckMessage);
            }
        } catch (Exception e) {
            log4j.error("Error when checking the health status of selenium hub: ", e);
            TestReporter.logFailBeforeSuite(logSuite, "Error when checking the health status of selenium hub:" + e);
        }

        // Validate browserName
        if (BROWSER == null || BROWSER.equals("")) {
            TestReporter.logFail(logSuite, "Invalid 'browserName' parameter: " + BROWSER);
        } else {
            TestReporter.logInfo(logSuite, "Browser name: " + BROWSER);
        }

        // Validate OS name
        if (RUN_ON.equalsIgnoreCase("Local") || RUN_ON.equalsIgnoreCase("Grid")) {

            TestReporter.logInfo(logSuite, "OS name: " + OS_NAME);
        }

        // Override CONSTANT variables
        if (ENVIRONMENT.equalsIgnoreCase(PRODUCTION)) {
            DEFAULT_PROD = "https://openweathermap.org/weathermap";

        } else {
            DEFAULT_STAGING = "";
        }

        report.setSystemInfo("environment", ENVIRONMENT);
        report.setSystemInfo("Browser", BROWSER);

        isTestSuiteExecutable = true;

        log4j.info("beforeSuite method - End");
    }

    @BeforeClass
    public synchronized void beforeClass() {

        log4j.info("beforeClass method - start");

        // Get test case class name
        testCaseName = this.getClass().getSimpleName();

        // Check if TC is executable or not
        if (isTestSuiteExecutable) {
            isTestCaseExecutable = true;
        }

        log4j.info("beforeClass method - End");
    }

    @BeforeMethod
    public synchronized void beforeMethod(Object[] data) throws IOException {

        log4j.info("beforeMethod method - Start");
        logStep = null;
        TOTAL_EXECUTED++;

        if (data != null && data.length > 0) {
            // Get test data for test case
            Hashtable<String, String> dataTest = (Hashtable<String, String>) data[0];

            //Get Retry count
            if (RETRY_FAILED_TESTS.equalsIgnoreCase("Yes")) {
                int retryCount = getRetryCount(testcaseList, testCaseName + ": " + dataTest.get("No."));
                if (retryCount > 0) {
                    testNameWithStatus = testCaseName + ": " + dataTest.get("No.") + ": RETRY" + retryCount;
                } else {
                    testNameWithStatus = testCaseName + ": " + dataTest.get("No.");
                }
            } else {
                testNameWithStatus = testCaseName + ": " + dataTest.get("No.");
            }

            //Initialize logClass
            logClass = TestReporter.createTestForExtentReport(report, testNameWithStatus);

            // Initial logMethod
            logMethod = TestReporter.createNodeForExtentReport(logClass, dataTest.get("TestDataPurpose"));
            log4j.info(dataTest.get("No.") + ": " + dataTest.get("TestDataPurpose"));

            // Assign test category
            logMethod.assignCategory(dataTest.get("TestingType"));

            // Start web driver
            if (isTestDataExecutable(dataTest, logMethod)) {
                initializeDriver(logMethod);
            }

            log4j.info("beforeMethod method - End");
        } else {
            logClass = TestReporter.createTestForExtentReport(report, testCaseName);
            TestReporter.logSkip(logClass, "This test case has no data to run");
        }
    }

    @AfterMethod
    public synchronized void afterMethod() {

        log4j.info("afterMethod method - Start");

        //Update test execution status to the testcaseList
        testcaseList.add(testNameWithStatus + ": " + logMethod.getStatus());

        // Quit
        quit(logMethod);
        logMethod = null;

        log4j.info("afterMethod method - End");
    }

    @AfterClass()
    public synchronized void afterClass() {
        log4j.info("afterClass method - Start");

        // Remove skip test case from report
        if (!isTestCaseExecutable && !SHOW_SKIP) report.removeTest(logClass);

        List statusHierarchy = Arrays.asList(
                Status.FATAL,
                Status.FAIL,
                Status.ERROR,
                Status.WARNING,
                Status.PASS,
                Status.SKIP,
                Status.DEBUG,
                Status.INFO
        );

        report.config().statusConfigurator().setStatusHierarchy(statusHierarchy);

        // Save test result to HTML file after each test class
        report.flush();
        logClass = null;

        log4j.info("afterClass method - End");
    }

    @AfterSuite()
    public synchronized void afterSuite(ITestContext context) throws Exception {
        log4j.info("afterSuite method - Start");

        //Get the total count of TCs passed and failed
        getTestCaseExecutionCount(testcaseList);

        // Send test result on email
        if (IS_SEND_EMAIL) {
            REPORT_STATUS = (TOTAL_PASSED + TOTAL_PASSED_WITH_RETRY == TOTAL_EXECUTED);
            EmailActions.sendEmailReport(ENVIRONMENT, TESTING_TYPE, context.getSuite().getName(), reportFilePath, REPORT_STATUS, logSuite);

            TestReporter.logInfo(logSuite, "Test result email was sent");
        }

        //This code block is used to keep limited number of reports
        if (!RUN_ON.equalsIgnoreCase("Local")) {
            String currentDirectory = System.getProperty("user.dir");
            File dir = new File(currentDirectory + "/resources/output");
            File[] files = dir.listFiles();
            assert files != null;
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < files.length - NUMBER_OF_REPORT; i++) {
                deleteDirectory(files[i]);
            }
        }
        emailActions.closeConnection();

        log4j.info("afterSuite method - End");
    }

    @DataProvider
    public Object[][] getDataForTest() {
        String DataFilePath = TEST_DATA_JSON + this.getClass().getPackage().getName().replace(".", "/") + "/data.json";
        Object[][] data = getData(testCaseName, DataFilePath);
        if (data.length == 0) {
            logClass = TestReporter.createTestForExtentReport(report, testCaseName);
            logClass.fail(testCaseName + " is not present in the data.json file");
            TOTAL_FAILED++;
        }
        return data;
    }

    @DataProvider
    public Object[][] getDataForSeparatingTest(ITestNGMethod context) {
        String testData = testCaseName + "_" + context.getMethodName();
        String DataFilePath = TEST_DATA_JSON + this.getClass().getPackage().getName().replace(".", "/") + "/data.json";
        Object[][] data = getData(testData, DataFilePath);
        if (data.length == 0) {
            logClass = TestReporter.createTestForExtentReport(report, testCaseName);
            logClass.fail(testData + " is not present in the data.json file");
            TOTAL_FAILED++;
        }
        return data;
    }
}
