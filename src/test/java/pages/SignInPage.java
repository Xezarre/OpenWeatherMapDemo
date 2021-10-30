package pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.TestReporter;
import utility.WebDriverUtils;

public class SignInPage extends BasePage {

    public final String pageName = "Sign In Page";

    public SignInPage() {
        WebDriverUtils.switchToWindowHandle();
    }

    // region Web Elements

    @FindBy(id = "user_email")
    private WebElement textBox_Email;

    @FindBy(id = "user_password")
    private WebElement textBox_Password;

    @FindBy(id = "user_remember_me")
    private WebElement checkBox_RememberMe;

    @FindBy(xpath = "//input[@value='Submit']")
    private WebElement button_Submit;

    // endregion Web Elements

    // region Element Methods

    private void enterEmail (String email) {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(textBox_Email);
        textBox_Email.clear();
        textBox_Email.sendKeys(email);
    }

    private void enterPassword (String password) {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(textBox_Password);
        textBox_Password.clear();
        textBox_Password.sendKeys(password);
    }

    private void clickSubmitButton () {

        WebDriverUtils.waitForPageLoaded();
        WebDriverUtils.waitForControlToBeClickable(button_Submit);
        button_Submit.click();
    }

    // endregion Element Methods

    // region Common Methods

    public void login(String email, String password, ExtentTest logTest) {

        try {
            log4j.info(pageName + " > login...start");

            WebDriverUtils.waitForPageLoaded();

            TestReporter.logInfo(logTest, "Enter email address: " + email);
            this.enterEmail(email);

            TestReporter.logInfo(logTest, "Enter password: " + password);
            this.enterPassword(password);

            TestReporter.logInfo(logTest, "Click on 'Submit' button");
            this.clickSubmitButton();

            WebDriverUtils.waitForPageLoaded();

            log4j.info("login...end");

        } catch (Exception e) {
            log4j.error("login method - ERROR - ", e);
            TestReporter.logException(logTest, "login method - ERROR", e);
        }
    }

    // endregion Common Methods
}
