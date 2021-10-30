package pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.TestReporter;
import utility.Utility;
import utility.WebDriverUtils;

import static common.GlobalVariables.WAIT_TIME_NULL;

public class SuggestionPage extends BasePage {

    public final String pageName = "Suggestion Page";

    public SuggestionPage() {
        WebDriverUtils.switchToWindowHandle();
    }

    // region Web Elements

    String link_SuggestedLocation = "//a[contains(text(),'%s')]";

    @FindBy(xpath = "//*[@id='forecast_list_ul']//*[text()='Not found']")
    private WebElement label_LocationNotFound;

    // endregion Web Elements

    // region Element Methods

    private void clickLocationLink(String searchString, ExtentTest logTest) {

        String elementPath = String.format(link_SuggestedLocation, searchString);
        WebElement link_SuggestedLocation = Utility.getDriver().findElement(By.xpath(elementPath));
        if (WebDriverUtils.isElementClickable(link_SuggestedLocation, WAIT_TIME_NULL, logTest)) {
            link_SuggestedLocation.click();
        }
    }

    // endregion Element Methods

    // region Common Methods

    public void selectLocationFromResultList(String cityName, ExtentTest logTest) {

        log4j.info(pageName + " > selectLocationFromResultList... start");

        WebDriverUtils.waitForPageLoaded();
        TestReporter.logInfo(logTest, "Select location: " + cityName);
        clickLocationLink(cityName, logTest);

        log4j.info("selectLocationFromResultList... end");
    }

    // endregion

    // region Validation Methods

    public void verifyLocationNotFound(ExtentTest logTest) {

        log4j.info(pageName + " > verifyLocationNotFound... start");

        WebDriverUtils.waitForPageLoaded();
        TestReporter.logInfo(logTest, "Verify input location not found");
        if (WebDriverUtils.doesControlExist(label_LocationNotFound) && WebDriverUtils.isElementClickable(label_LocationNotFound, 5, logTest))
            TestReporter.logPass(logTest, "Location not found!");
        else
            TestReporter.logFail(logTest, "Location found!");

        log4j.info("verifyLocationNotFound... end");
    }

    // endregion
}
