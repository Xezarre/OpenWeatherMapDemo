package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.gson.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.RemoteWebDriver;
import utility.webdrivers.DriverFactory;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static common.GlobalVariables.*;

public class Utility {
    public static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();

    // Add initialization of drivers
    public static String subWindowHandler = null;
    public static ExtentReports report = null;
    public static ExtentHtmlReporter htmlReporter = null;

    //Initiate variable for log4j
    public static Log log4j;

    //Initiate local variables for generating time stamp

    public static String timeStampString = DataFaker.generateTimeStampString("yyyy-MM-dd-HH-mm-ss");

    //Initiate local variables for sending email
    public static String reportLocation = OUTPUT_PATH + "report-" + timeStampString + "/";
    public static String reportFilePath = reportLocation + "report-" + timeStampString + ".html";
    public static String reportCancelWarrantyCSV = null;
    public static String reportRefundClaimCSV = null;

    //Variable for generate random string
    static Calendar now = Calendar.getInstance();

    public static EmailActions emailActions = new EmailActions();

    public static RemoteWebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(RemoteWebDriver webDriver) {
        driver.set(webDriver);
    }

    public static void initializeDriver(ExtentTest logTest) throws IOException {
        try {
            switch (RUN_ON.toLowerCase()) {
                case "grid":
                    Utility.setDriver(DriverFactory.createInstanceGrid(BROWSER, logTest));
                    WebDriverUtils.maximizeWindow();
                    break;
                default:
                    Utility.setDriver(DriverFactory.createInstance(BROWSER, logTest));
                    WebDriverUtils.maximizeWindow();
                    break;
            }

            // Check if running on Mobile or Desktop
            Platform platForm = ((RemoteWebDriver) Utility.getDriver()).getCapabilities().getPlatform();
            IS_MOBILE = (platForm == Platform.ANDROID || platForm == Platform.IOS);

            Utility.getDriver().manage().deleteAllCookies();
            Utility.getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            if (!(BROWSER.toLowerCase().startsWith("internet") || BROWSER.equalsIgnoreCase("IE"))) {
                Utility.getDriver().manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            TOTAL_FAILED++;
            log4j.error("initializeDriver method - ERROR - ", e);
            TestReporter.logException(logTest, "initializeDriver method - ERROR", e);
        }
    }

    public static void log4jConfiguration() {

        try {
            log4j = LogFactory.getLog(new Object().getClass());
        } catch (Exception e) {
            log4j.error("log4jConfiguration method - ERROR: ", e);
        }
    }

    public static String getStackTrace(StackTraceElement[] stackTradeElements) {

        try {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : stackTradeElements) {
                stackTrace.append(element.toString()).append("</br>");
                // Get stack trace from java.module level only
                if (element.toString().startsWith("java.modules"))
                    break;
            }
            return stackTrace.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * wait for a specific time
     */
    public static void sleep(long timeout) {
        try {
            Thread.sleep(timeout * 1000);
        } catch (Exception e) {
            log4j.warn("Exception is sleep method - ERROR: " + e);
        }
    }

    /**
     * Close browser and release device.
     */
    public static void quit(ExtentTest logTest) {
        try {
            Utility.getDriver().quit();
            TestReporter.logInfo(logTest, "Closed browser and released device");
        } catch (Exception e) {
            log4j.error("Unable to close browser/release device: ", e);
        }
    }

    /**
     * Reading from data.json file for which test data to be executed methods
     */
    public static boolean isTestDataExecutable(Hashtable<String, String> data, ExtentTest logTest) {
        boolean testDataRun = false;
        boolean testingType = false;

        try {
            if (TESTING_TYPE.equalsIgnoreCase("Regression"))
                testingType = true;

            if (data.get("RunMode").equalsIgnoreCase("Y") && testingType)
                testDataRun = true;
            else {
                if (!data.get("RunMode").equalsIgnoreCase("Y"))
                    TestReporter.logSkip(logTest, "Skipping test as RunMode was set to NO");
                else
                    TestReporter.logSkip(logTest, "Skipping test as TestingType is not appropriated");
            }

        } catch (Exception e) {
            log4j.error("isTestDataExecutable method - ERROR - ", e);
            TestReporter.logException(logTest, "isTestDataExecutable method - ERROR", e);
        }
        return testDataRun;
    }

    public static String getTestCaseID() {

        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement item : stackTraceElements) {
                if (item.getClassName().startsWith("com.modules"))
                    return item.getFileName().split("_")[0].toLowerCase();
            }
            return "noname";

        } catch (Exception e) {
            return "noname";
        }
    }

    public static void countTestCaseRan(Status result) {
        try {
            if (result.equals(Status.PASS))
                TOTAL_PASSED++;
            else if ((result.equals(Status.FAIL)) || result.equals(Status.ERROR) || result.equals(Status.WARNING))
                TOTAL_FAILED++;
        } catch (Exception e) {
            log4j.error("countTestCaseRan method - ERROR - ", e);
        }
    }

    public static String readPayloadDataFromJsonFile(String filePath) throws IOException {
        String data = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(filePath));
            data = obj.toString();

        } catch (Exception e) {
            log4j.error("clickButtonExit method - ERROR - ", e);
        }
        return data;
    }

    /**
     * convert a string in snake case(e.g. this_is_example) to camel case (e. thisIsExample)
     */
    public String convertSnakeCaseToCamelCase(String name, ExtentTest logTest) throws IOException {
        String newName = null;
        try {
            newName = String.format(name.replaceAll("\\_(.)", "%S"), (Object[]) name.replaceAll("[^_]*_(.)[^_]*", "$1_").split("_"));
        } catch (Exception e) {
            log4j.error("convertSnakeCaseToCamelCase method - ERROR - ", e);
            TestReporter.logException(logTest, "convertSnakeCaseToCamelCase method - ERROR", e);
        }
        return newName;
    }

    /**
     * We have 2 ways to fetch value from database for validation: via API and via query DB
     * The API response return columns' name in camel case.
     * The query DB's result return columns' name in snake case.
     * We don't want to have 2 separated methods to validate the same thing.
     * This method is to convert columns' name to the same format so that we can use the same validation method regardless of input is API's response or query DB's result
     * @return if the input Hashmap has keys(columns) name in snake case, convert those column name to camel case, then return new hashmap
     * if the input Hashmap has keys(columns) name in camel case, no need to convert, return original hashmap
     */
    public HashMap convertTableColumnNameToCamelCase(HashMap table, ExtentTest logTest) throws IOException {

        HashMap<String, String> tableClone = table;
        HashMap<String, String> newTable = new HashMap();
        try {
            //check if table's column name contain snake case or not. If yes, convert it to camel case. If no, return original Hashmap
            Set keys = table.keySet();
            if (keys.toString().contains("_") && !keys.toString().contains("_links")) {
                System.out.println("Result is getting from database");
                for (Map.Entry<String, String> entry : tableClone.entrySet()) {
                    String oldColumnName = entry.getKey();
                    String value = entry.getValue();
                    String newColumnName = convertSnakeCaseToCamelCase(oldColumnName, logTest);
                    newTable.put(newColumnName, value);
                }
            } else {
                System.out.println("Result is getting from API's response");
                newTable = table;
            }
        } catch (Exception e) {
            log4j.error("convertTableColumnNameToCamelCase method - ERROR - ", e);
            TestReporter.logException(logTest, "convertTableColumnNameToCamelCase method - ERROR", e);
        }
        return newTable;
    }

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDayOfMonth() {
        return now.get(Calendar.DATE);
    }

    public static int getMonth() {
        return now.get(Calendar.MONTH);
    }

    public static int getyear() {
        return now.get(Calendar.YEAR);
    }

    public static String right(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }

    public boolean isThisDateValid(String dateToValidate, String dateFormat) {
        if (dateToValidate == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            sdf.parse(dateToValidate);

        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * @param logTest
     * @param args
     * @ActionName: createCSVFile(logTest, Object... args)
     * @purpose: Create CSV file in the output folder and add the headers
     * @Author: Rooban
     */
    public void createCSVFile(ExtentTest logTest, String... args) throws IOException {
        try {
            log4j.debug("Create a CSV file in output folder...start");

            String buildNumber = System.getenv("BUILD_NUMBER");
            String fileName = null;

            if (args[0].equalsIgnoreCase("Warranty Id")) {
                if (buildNumber != null) {
                    //String buildNumber = buildURL.split("/")[buildURL.split("/").length-1];
                    reportCancelWarrantyCSV = reportLocation + "CancelWarranty-Build-" + buildNumber + ".csv";
                } else {
                    reportCancelWarrantyCSV = reportLocation + "CancelWarranty-report-" + timeStampString + ".csv";
                }
                fileName = reportCancelWarrantyCSV;
            } else {
                if (buildNumber != null) {
                    reportRefundClaimCSV = reportLocation + "RefundClaim-Build-" + buildNumber + ".csv";
                } else {
                    reportRefundClaimCSV = reportLocation + "RefundClaim-report-" + timeStampString + ".csv";
                }
                fileName = reportRefundClaimCSV;
            }
            TestReporter.logInfo(logTest, "Create a CSV file: " + fileName);

            FileWriter csvWriter = new FileWriter(fileName, true);
            for (int i = 0; i <= args.length - 1; i++) {
                if (i == args.length - 1) {
                    csvWriter.append(args[i]);
                    csvWriter.append("\n");
                } else {
                    csvWriter.append(args[i] + ",");
                }
            }
            csvWriter.flush();
            csvWriter.close();

            log4j.info("Create a CSV file in output folder...end");
        } catch (Exception e) {
            log4j.error("Create a CSV file in output folder - ERROR - ", e);
            TestReporter.logException(logTest, "Create a CSV file in output folder - ERROR - ", e);
        }
    }

    /**
     * @param logTest
     * @param hashMap
     * @param args
     * @ActionName: writeDataToCSV(logTest, String csvFileName, HashMap < String, Object > hashMap, String... args)
     * @purpose: Write data into CSV file with append mode
     * @Author: Rooban
     */
    public void writeDataToCSV(ExtentTest logTest, String csvFileName, HashMap<String, Object> hashMap, String... args) throws IOException {
        try {
            log4j.debug("Append the data in CSV file...start");

            FileWriter csvWriter = new FileWriter(csvFileName, true);
            for (int i = 0; i <= args.length - 1; i++) {
                String data = "";
                if (hashMap.get(args[i]) == null) {
                    data = "";
                } else {
                    data = hashMap.get(args[i]).toString();
                }

                if (i == args.length - 1) {
                    csvWriter.append(data);
                    csvWriter.append("\n");
                } else {
                    csvWriter.append(data + ",");
                }
            }

            csvWriter.flush();
            csvWriter.close();

            log4j.debug("Append the data in CSV file...end");
        } catch (Exception e) {
            log4j.error("Append the data in CSV file - ERROR - ", e);
            TestReporter.logException(logTest, "Append the data in CSV file - ERROR - ", e);
        }
    }

    public String readTxtFileToString(String path, ExtentTest logTest) {

        byte[] content = null;

        try {
            log4j.debug("readTxtFileToString...start");
            TestReporter.logInfo(logTest, "readTxtFileToString file");

            content = Files.readAllBytes(Paths.get(path));

            log4j.info("readTxtFileToString...end");

        } catch (Exception e) {
            log4j.error("readTxtFileToString - ERROR - ", e);
            TestReporter.logException(logTest, "readTxtFileToString - ERROR - ", e);
        }
        return new String(content);
    }

    /**
     * Reading test data from json for a specific test case
     *
     * @param testName
     * @param dataFilePath
     * @return Test Data Object in Key Value pair.
     * <p>
     * Operations in Sequence: Get the data from json file
     * Check if the test case is present in the json file
     * Get the test data in json array format
     * Deserialize the json read into an object of Hashtable type
     * Put the data into object array and return to data provider
     * The action name is the same as other action. But it use override method when running.
     */
    public static Object[][] getData(String testName, String dataFilePath) {

        Object[][] data = new Object[0][1];

        //Read json file data using Gson library
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(dataFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonElement jsonElement = new JsonParser().parse(br);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        //Check for the test name in the json file
        boolean blnTCExist = jsonObject.has(testName);
        if (!blnTCExist) {
            log4j.error(testName + " is not present in the data.json file - " + dataFilePath);
            return data;
        }

        //Get test data for the specific test case
        JsonArray jsonArray = jsonObject.getAsJsonArray(testName);
        data = jsonArrayToObjectArray(jsonArray);
        return data;
    }

    public static Object[][] jsonArrayToObjectArray(JsonArray jsonArray) {

        Object[][] data = new Object[0][1];
        int index = 0;
        Gson gson = new Gson();

        if (jsonArray.size() > 0) {
            data = new Object[jsonArray.size()][1];
            for (Object obj : jsonArray) {
                Hashtable<String, String> hashTable = new Hashtable<String, String>();
                data[index][0] = gson.fromJson((JsonElement) obj, hashTable.getClass());
                index++;
            }
        }
        return data;
    }

    public static boolean deleteDirectory(File dir) {

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                boolean success = deleteDirectory(file);
                if (!success) {
                    return false;
                }
            }
        }
        System.out.println("removing file or directory : " + dir.getName());
        return dir.delete();
    }

    /**
     Get retry count if the test case is failed
     */
    public int getRetryCount(ArrayList<String> testCaseList, String testName) {
        int count = 0;

        for (int i = 0; i < testCaseList.size(); i++) {
            if (testCaseList.get(i).contains(testName)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get Test execution count and update the global variables
     */
    public void getTestCaseExecutionCount(ArrayList<String> testCaseList) {

        for (String s : testCaseList) {
            if (s.contains(": pass")) {
                if (s.contains(": RETRY")) {
                    TOTAL_PASSED_WITH_RETRY++;
                } else {
                    TOTAL_PASSED++;
                }
            } else if (s.contains(": skip")) {
                TOTAL_SKIPPED++;
            } else {
                if (RETRY_FAILED_TESTS.equalsIgnoreCase("Yes")) {
                    if (s.contains(": RETRY2")) {
                        TOTAL_FAILED++;
                    }
                } else {
                    TOTAL_FAILED++;
                }
            }
        }

        TOTAL_TESTCASES = TOTAL_PASSED + TOTAL_PASSED_WITH_RETRY + TOTAL_FAILED + TOTAL_SKIPPED;
    }

    public static String subtractDate(String date, Integer dayToSubstract) {
        String[] inputDate = date.split(" ");
        return LocalDate.parse(inputDate[0]).minusDays(dayToSubstract) + " " + inputDate[1];
    }

    public static String plusDate(int daysToPlus, String dateString) {
        return LocalDate.parse(dateString).plusDays(daysToPlus).toString();
    }

    public static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern).format(Calendar.getInstance().getTime());
    }

    public String getCreateResetPasswordButtonNameByLocale(Hashtable<String, String> data) throws IOException {

        Properties prop = new Properties();
        FileInputStream input = new FileInputStream("resources/languages/buttonName.properties");
        prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        input.close();
        return prop.getProperty(data.get("Locale"));
    }

    public static void turnOffWaits() {
        getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    public static void turnOnWaits() {
        getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    /**
     * This function will wait until {@code waitCondition} return true with default timeOut
     * @param waitCondition function will wait util this function return true
     * @param logForWait log debug to console what you waiting for
     * @link #waitFor(Supplier < T > , Predicate < T > ,long , long , TimeUnit , String )
     */
    public static void waitFor(BooleanSupplier waitCondition, String logForWait) {
        waitFor(waitCondition, WAIT_TIME_180_SEC, 3, TimeUnit.SECONDS, logForWait);
    }

    /**
     * This function will get result from {@code getFunction} util the checkResultCondition return true or throw exception if timeOut (timeOut is Default)
     * @param getFunction function that you want to get result
     * @param checkResultCondition condition to check result from function
     * @param logForWait log debug to console what you getting/waiting for
     * @param <T> Type of data you want to get
     * @link #waitAndGet(Supplier < T > , Predicate < T > , long , long , TimeUnit , String )
     */
    public static <T> T waitAndGet(Supplier<T> getFunction, Predicate<T> checkResultCondition, String logForWait) {
        return waitAndGet(getFunction, checkResultCondition, WAIT_TIME_180_SEC, 3, TimeUnit.SECONDS, logForWait);
    }

    /**
     * This function will wait until {@code waitCondition} return true or throw exception if timeout
     * @param waitCondition function will wait util this function return true
     * @param timeOut if this function run long than than timeOut the function will throw exception
     * @param intervalTime time between each call getFunction and checkResultCondition
     * @param timeUnit timeUnit of timeOut & intervalTime
     * @param logForWait log debug to console what you waiting for
     * Example
     * {@code
     *      waitFor(()->{return true},
     *      3, 3, TimeUnit.SECONDS, "wait for function return true")
     * }
     */
    public static void waitFor(BooleanSupplier waitCondition, long timeOut, long intervalTime, TimeUnit timeUnit, String logForWait) {

        log4j.debug("Waiting " + logForWait + " TimeOut " + timeOut + timeUnit.toString());
        StopWatch stopWatch = StopWatch.createStarted();
        timeOut = TimeUnit.NANOSECONDS.convert(timeOut, timeUnit);
        intervalTime = TimeUnit.NANOSECONDS.convert(intervalTime, timeUnit);
        boolean resultAsExpected = false;
        long count = 0;
        do {
            try {
                resultAsExpected = waitCondition.getAsBoolean();
                if (resultAsExpected) return;
            } catch (Throwable throwable) {}
            long waitTime = ++count * intervalTime - stopWatch.getNanoTime();
            long actualIntervalTime = Math.max(Math.min(waitTime, timeOut - stopWatch.getNanoTime()),0);
            try {
                Thread.sleep(actualIntervalTime/1000000, (int) (actualIntervalTime%1000000));
            } catch (InterruptedException e) {
            }
        } while (stopWatch.getNanoTime() < timeOut);
        throw new RuntimeException("Time Out: Waiting for " + logForWait + " total " + stopWatch.getTime(TimeUnit.SECONDS) + "second(s). ");
    }

    /**
     * This function will get result from {@code getFunction} util the checkResultCondition return true or throw exception if timeOut (timeOut is Default)
     * Example
     * {@code
     *      waitAndGet(()->{return 1},
     *      (int result)->{return result == 1;},
     *      3, 3, TimeUnit.SECONDS, "wait for function return 1")
     * }
     */
    public static <T> T waitAndGet(Supplier<T> getFunction, Predicate<T> checkResultCondition,
                                   long timeOut, long intervalTime, TimeUnit timeUnit, String logForWait) {
        log4j.debug("Waiting " + logForWait + " TimeOut " + timeOut + timeUnit.toString());
        StopWatch stopWatch = StopWatch.createStarted();
        timeOut = TimeUnit.NANOSECONDS.convert(timeOut, timeUnit);
        intervalTime = TimeUnit.NANOSECONDS.convert(intervalTime, timeUnit);
        boolean resultAsExpected;
        long count = 0;
        T result;
        do {
            try {
                result = getFunction.get();
                resultAsExpected = checkResultCondition.test(result);
                if (resultAsExpected) return result;
            } catch (Throwable throwable) {}
            long waitTime = ++count * intervalTime - stopWatch.getNanoTime();
            long actualIntervalTime = Math.max(Math.min(waitTime, timeOut - stopWatch.getNanoTime()),0);
            try {
                Thread.sleep(actualIntervalTime/1000000, (int) (actualIntervalTime % 1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (stopWatch.getNanoTime() < timeOut);
        throw new RuntimeException("Time Out: Waiting for " + logForWait + " total " + stopWatch.getTime(TimeUnit.SECONDS) + "second(s). ");
    }

    public static Hashtable<String, String> parseSearchString(String searchString, ExtentTest logTest) {

        Hashtable<String, String> data = new Hashtable<>();
        if (searchString.contains(",")) {
            String[] result = searchString.trim().split(",");
            data.put("City", result[0]);
            data.put("Country", result[1]);
        }
        else {
            data.put("City", searchString);
        }
        return data;
    }
}
