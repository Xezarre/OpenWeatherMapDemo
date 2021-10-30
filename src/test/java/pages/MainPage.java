package pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.TestReporter;
import utility.WebDriverUtils;

public class MainPage extends BasePage {

    public final String pageName = "Main Page";

    public MainPage() {
        WebDriverUtils.switchToWindowHandle();
    }

    // region Web Elements

    @FindBy(css = "#Temperature")
    private WebElement radio_Temperature;

    @FindBy(css = "#Pressure")
    private WebElement radio_Pressure;

    @FindBy(css = "#Clouds")
    private WebElement radio_Clouds;

    @FindBy(id = "Wind speed")
    private WebElement radio_WindSpeed;

    @FindBy(id = "Global Precipitation")
    private WebElement radio_GlobalPrecipitation;

    //endregion <Web Elements>

    // region <Element Methods>

    public void selectTemperatureRadio() {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(radio_Temperature);
        radio_Temperature.click();
    }

    //endregion <Element Methods>

    // region Common Methods

    public void searchByCityName (String cityName, ExtentTest logTest) {

        log4j.info(pageName + " > searchByCityName... start");

        WebDriverUtils.waitForPageLoaded();
        TestReporter.logInfo(logTest, "Enter search string: " + cityName);
        enterSearchString(cityName, logTest);

        log4j.info("searchByCityName... end");
    }

    // endregion
}
