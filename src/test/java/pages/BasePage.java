package pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.TestReporter;
import utility.Utility;
import utility.WebDriverUtils;

public class BasePage extends Utility {

    // region Web Elements

    @FindBy(xpath = "//input[@name='q']")
    protected WebElement textBox_Search;

    @FindBy(css = "div a[href*='guide']")
    protected WebElement link_Guide;

    @FindBy(css = "div a[href*='api']")
    protected WebElement link_API;

    @FindBy(css = "div a[href*='price']")
    protected WebElement link_Pricing;

    @FindBy(css = "div a[href*=sign_in]")
    protected WebElement link_SignIn;

    // endregion <Web Elements>

    // region <Action Methods>

    protected void enterSearchString (String searchString, ExtentTest logTest) {

        WebDriverUtils.scrollIntoView(textBox_Search);
        WebDriverUtils.waitForControlToBeClickable(textBox_Search);

        TestReporter.logInfo(logTest, "Search string: " + searchString);
        textBox_Search.clear();
        textBox_Search.sendKeys(searchString.trim());

        TestReporter.logInfo(logTest, "Hit ENTER key");
        textBox_Search.sendKeys(Keys.ENTER);
    }

    // end region <Action Methods>
}
