package pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.TestReporter;
import utility.Utility;
import utility.WebDriverUtils;

public class ResultPage extends BasePage {

    public final String pageName = "Result Page";

    public ResultPage() {
        WebDriverUtils.switchToWindowHandle();
    }

    // region Web Elements

    @FindBy(xpath = "//*[@id='weather-widget']//span[@class='orange-text']")
    private WebElement label_ReturnedDateTime;

    @FindBy(xpath = "//*[@id='weather-widget']//h2")
    private WebElement label_CityName;

    @FindBy(xpath = "//*[@id='weather-widget']//span[@class='heading']")
    private WebElement label_Temperature;

    //endregion Web Elements

    // region Element Methods

    private String getReturnedDateTime() {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(label_ReturnedDateTime);
        return label_ReturnedDateTime.getText();
    }

    private String getReturnedCityName() {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(label_CityName);
        return label_CityName.getText();
    }

    private String getReturnedTemperature() {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(label_Temperature);
        return label_Temperature.getText();
    }

    //endregion Element Methods

    // region Validation Methods

    public void verifyReturnedCityName(String inputCityName, ExtentTest logTest) {

        try {
            log4j.info(pageName + " > verifyReturnedCityName... start");

            boolean result = true;

            WebDriverUtils.waitForPageLoaded();
            TestReporter.logInfo(logTest, "Verify City name");
            if (!this.getReturnedCityName().contains(inputCityName))
                result = false;

            if (result)
                TestReporter.logPass(logTest, "<br>Expected Result: " + inputCityName + "<br>>Actual Result: " + this.getReturnedCityName());
            else
                TestReporter.logFail(logTest, "<br>Expected Result: " + inputCityName + "<br>Actual Result: " + this.getReturnedCityName());
        }
        catch (Exception e) {
            log4j.error("verifyReturnedCityName method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyReturnedCityName method - ERROR", e);
        }

        log4j.info("verifyReturnedData... end");
    }

    public void verifyReturnedDate(ExtentTest logTest) {

        try {
            log4j.info(pageName + " > verifyReturnedDate... start");

            boolean result = true;

            WebDriverUtils.waitForPageLoaded();
            TestReporter.logInfo(logTest, "Verify Returned Date");
            if (!this.getReturnedDateTime().contains(Utility.getCurrentDate("MMM dd")))
                result = false;

            if (result)
                TestReporter.logPass(logTest, "<br>Expected Result: " + Utility.getCurrentDate("MMM dd") + "<br>>Actual Result: " + this.getReturnedDateTime());
            else
                TestReporter.logFail(logTest, "<br>Expected Result: " + Utility.getCurrentDate("MMM dd") + "<br>Actual Result: " + this.getReturnedDateTime());
        }
        catch (Exception e) {
            log4j.error("verifyReturnedDate method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyReturnedDate method - ERROR", e);
        }

        log4j.info("verifyReturnedData... end");
    }

    public void verifyTemperatureLabelExist(ExtentTest logTest) {

        try {
            log4j.info(pageName + " > verifyReturnedDate... start");

            boolean result = true;

            WebDriverUtils.waitForPageLoaded();
            TestReporter.logInfo(logTest, "Verify Temperature label exists");
            if (!WebDriverUtils.doesControlExist(label_Temperature))
                result = false;

            if (result)
                TestReporter.logPass(logTest, "Temperature label does exist in page");
            else
                TestReporter.logFail(logTest, "Temperature label does NOT exist in page");
        }
        catch (Exception e) {
            log4j.error("verifyReturnedDate method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyReturnedDate method - ERROR", e);
        }

        log4j.info("verifyReturnedData... end");
    }
}
