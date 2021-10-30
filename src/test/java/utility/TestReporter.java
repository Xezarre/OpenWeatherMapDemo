package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.SkipException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static common.GlobalVariables.*;

public class TestReporter {
    /**
     * return log steps that contain <span> ticket
     */
    public static void logInfo(ExtentTest logTest, String description) {
        logTest.info(description);
    }

    /**
     * return report failed log that contain <span> ticket
     */
    public static void logFail(ExtentTest logTest, String description) {
        try {
            // Report test fails and capture screenshot
            captureScreenshot("FAILED screenshot: ", "fail-", logTest);

            throw new SkipException(description);
        } catch (SkipException ex) {
            logTest.fail(MarkupHelper.createLabel(description + "</br>" + Utility.getStackTrace(ex.getStackTrace()), ExtentColor.RED));
            Assert.fail(description);
        }
    }

    /**
     * Return report failed log that contain <span> ticket and stop the execution at before suite
     */
    public static void logFailBeforeSuite(ExtentTest logSuite, String description) throws IOException {
        try {
            logSuite.fail(MarkupHelper.createLabel(description + "</br>", ExtentColor.RED));
            TOTAL_TESTCASES = TOTAL_SKIPPED + TOTAL_EXECUTED;
            TOTAL_PASSED = TOTAL_EXECUTED - TOTAL_FAILED;
            Utility.report.flush();
            System.exit(-1);

        } catch (Exception e) {
            Utility.log4j.error("Error when reporting logFail: ", e);
        }
    }

    /**
     * Return report passed log that contain <span> ticket
     */
    public static void logPass(ExtentTest logTest, String description) {
        logTest.pass(MarkupHelper.createLabel(description, ExtentColor.GREEN));
    }

    /**
     * Return report error log that contain <span> ticket
     * @param logTest log
     * @param description description
     * @param exception e
     */
    public static void logException(ExtentTest logTest, String description, Exception exception){
        try {
            // Report test fails and capture screenshot
            captureScreenshot("ERROR screenshot: ", "error-", logTest);

            throw new SkipException(description);
        } catch (SkipException ex) {
            logTest.error(MarkupHelper.createLabel(description + "</br>" + exception.toString() + "</br>" + Utility.getStackTrace(exception.getStackTrace()), ExtentColor.ORANGE));
            Assert.fail(description);
        }
    }

    public static void logSkip(ExtentTest logTest, String description) {
        logTest.skip(MarkupHelper.createLabel(description, ExtentColor.GREY));
    }

    public static ExtentTest logStepInfo(ExtentTest logTest, String description, Object... args){
        return logTest.createNode(description);
    }

    public static ExtentTest createNodeForExtentReport(ExtentTest parentTest, String description) {
        return parentTest.createNode(description);
    }

    // Taking a screenshot
    public static void captureScreenshot(String screenshotName) {
        try {
            TakesScreenshot ts = Utility.getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = Utility.reportLocation + screenshotName + ".png";
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);
        } catch (Exception e) {
            Utility.log4j.info("Exception while taking a screenshot." + e.getMessage());
        }
    }

    public static String captureElementScreenshot(String elementPath, ExtentTest logTest) throws IOException {

        String screenshotFilePath = "";
        try {
            Utility.log4j.debug("captureElementScreenshot...start");

            RemoteWebDriver rwd = Utility.getDriver();
            WebElement element = rwd.findElement(By.xpath(elementPath));

            // Scroll the element to the main view
            WebDriverUtils.scrollIntoView(element);

            // Get the location of element on page
            Point point = element.getLocation();

            // Get width and height of the element
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();

            // Get entire page screenshot
            File screenshot = ((TakesScreenshot)rwd).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);

            // Crop the entire page screenshot to get only element screenshot
            BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY() - eleHeight/4, eleWidth, eleHeight);
            ImageIO.write(eleScreenshot, "png", screenshot);

            // Copy the element screenshot to disk
            String fileName = "elementScreenshot+" + DataFaker.generateTimeStampString("yyyyMMddHHmmss");
            File savedScreenshot = new File(Utility.reportLocation + fileName + ".png");
            FileUtils.copyFile(screenshot, savedScreenshot);

            screenshotFilePath = savedScreenshot.getPath();

            Utility.log4j.debug("captureElementScreenshot...end");
        } catch (Exception e){
            Utility.log4j.error("captureElementScreenshot method - ERROR: ", e);
            logException(logTest, "captureElementScreenshot method - ERROR", e);
        }
        return screenshotFilePath;
    }

    public static void captureScreenshot(String detail, String screenshotName, ExtentTest logTest){
        try {
            Utility.sleep(2);

            // Get screenshot name
            screenshotName = screenshotName + DataFaker.generateTimeStampString("yyyy-MM-dd-HH-mm-ss") + ".png";

            // Capture screenshot (If driver == null, it means there is no window opens => Don't capture screenshot)
            TakesScreenshot ts = (TakesScreenshot) Utility.getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = Utility.reportLocation + screenshotName;
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);

            // Add current URL to report
            if (Utility.getDriver() != null)
                logTest.info("Page URL: " + Utility.getDriver().getCurrentUrl());

            // Add screenshot to report
            String screenshotLink = "<a href=\"" + screenshotName + "\">" + screenshotName + "</a>";
            if (logTest.getStatus() == Status.ERROR)
                logTest.error(detail + screenshotLink).addScreenCaptureFromPath(screenshotName);
            else if (logTest.getStatus() == Status.FAIL)
                logTest.fail(detail + screenshotLink).addScreenCaptureFromPath(screenshotName);
            else
                logTest.pass(detail + screenshotLink).addScreenCaptureFromPath(screenshotName);
        } catch (Exception e) {
            Utility.log4j.info("Exception while taking screenshot: " + e.getMessage());
        }
    }

    public static ExtentTest createTestForExtentReport(ExtentReports report, String description) {
        return report.createTest(description);
    }

    /**
     * return report failed log that contain <span> ticket
     */
    public static void logFailSoftAssert(ExtentTest logTest, String description) throws IOException {
        try {
            // Report test fails and capture screenshot
            captureScreenshot("FAILED screenshot: ", "fail-", logTest);
            throw new SkipException(description);

        } catch (SkipException ex) {
            logTest.fail(MarkupHelper.createLabel(description + "</br>" + Utility.getStackTrace(ex.getStackTrace()), ExtentColor.RED));
        }
    }

    /* Generate report detail section */
    public static String getReportLink() {
        try {
            String buildURL = System.getProperty("buildURL");
            String jobURL = System.getProperty("jobURL");
            if (buildURL != null && jobURL != null) {
                return jobURL + "ws/" + Utility.reportFilePath.substring(Utility.reportFilePath.indexOf("resources"));
            } else {
                return Utility.reportFilePath;
            }
        } catch (Exception ex) {
            Utility.log4j.error("Error while getting report link: " + ex.getMessage());
        }
        return "";
    }
}
