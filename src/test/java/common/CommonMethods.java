package common;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utility.TestReporter;
import utility.Utility;

public class CommonMethods extends Utility {

    /**
     * Close current open window
     */
    public void closeWindow (ExtentTest logTest) {
        try {
            log4j.info("closeWindow...starting");
            TestReporter.logInfo(logTest, "Close the window");
            Utility.getDriver().close();

            log4j.info("closeWindow...ends");
        } catch (Exception e) {
            log4j.error("closeWindow method - ERROR - ", e);
            TestReporter.logException(logTest, "closeWindow method - ERROR", e);
        }
    }

    /**
     * Mask an email
     */
    public String maskEmail(String email, ExtentTest logTest) {
        String maskedEmail = null;
        try {

            maskedEmail = email.replaceAll("(?<=.{1}).{1,100}(?=[^@]*?.@)", "..");

        } catch (Exception e) {
            log4j.error("maskEmail method - ERROR - ", e);
            TestReporter.logException(logTest, "maskEmail method - ERROR", e);
        }
        return maskedEmail;
    }

    /**
     * Quit browser
     */
    public void quitBrowser() {
        try {
            log4j.info("Quit a current open browser...start");

            Utility.getDriver().quit();

            log4j.info("quitBrowser...end");

        } catch (Exception e) {
            log4j.error("quitBrowser method - ERROR - ", e);
        }
    }

    /**
     * Input special characters into Text Area
     */
    public void inputSpecialCharactersIntoTextArea(WebElement textArea, String specialCharacters) {

        log4j.info("inputSpecialCharactersIntoTextArea...start");
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('change'));", textArea, specialCharacters);

        log4j.info("inputSpecialCharactersIntoTextArea...end");
    }

}
