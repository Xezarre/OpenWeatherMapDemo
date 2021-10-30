package utility;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static common.GlobalVariables.*;

public class WebDriverUtils {

    /**
     * Navigate to site by URL
     */
    public static void navigateToTestSite(ExtentTest logTest, String url) {
        try {
            TestReporter.logInfo(logTest, "Navigate to site: " + url);
            WebDriverUtils.switchToWindowHandle();
            Utility.getDriver().navigate().to(url);
            WebDriverUtils.waitForPageLoaded();

        } catch (Exception e) {
            Utility.log4j.error("navigateToTestSite method - ERROR - ", e);
            TestReporter.logException(logTest, "navigateToTestSite method - ERROR", e);
        }
    }

    /**
     * wait for a specific control in period time
     */
    public static void waitForControl(WebElement controlName) {
        try {
            new WebDriverWait(Utility.getDriver(), WAIT_TIME).until(ExpectedConditions.visibilityOf(controlName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void waitForControlToBeClickable(WebElement controlName) {

        new WebDriverWait(Utility.getDriver(), WAIT_TIME).until(ExpectedConditions.visibilityOf(controlName));
        new WebDriverWait(Utility.getDriver(), WAIT_TIME).until(ExpectedConditions.elementToBeClickable(controlName));
    }

    /**
     * wait for a specific control disappear
     */
    public static void waitForControlDisappeared(WebElement controlName, ExtentTest logTest) {
        try {
            if (doesControlExist(controlName)) {
                int i = 0;
                while (doesControlExist(controlName)) {
                    Utility.sleep(2);
                }
            }
        } catch (Exception e) {
            Utility.log4j.error("waitForControlDisAppear method - ERROR - ", e);
            TestReporter.logException(logTest, "waitForControlDisAppear method - ERROR", e);
        }
    }

    /**
     * wait for a specific control in period time
     */
    public static void waitForControl(WebElement controlName, ExtentTest logTest) {

        try {
            for (int i = 0; i < WAIT_TIME / 12; i++) {
                if (doesControlExist(controlName)) {
                    break;
                } else Utility.sleep(1);
            }
        } catch (Exception e) {
            Utility.log4j.error("waitForControl method - ERROR - ", e);
            TestReporter.logException(logTest, "waitForControl method - ERROR", e);
        }
    }

    /**
     * waitForPageLoaded
     */
    public static void waitForPageLoaded() {

        Wait<WebDriver> wait = new WebDriverWait(Utility.getDriver(), WAIT_TIME);
        try {
            // Wait for HTML load
            wait.until(driver -> {
                Utility.sleep(1);
                boolean readyState = ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                boolean activeJQuery = ((JavascriptExecutor) driver).executeScript("if (typeof jQuery != 'undefined') { return jQuery.active == 0; } else {  return true; }").equals(true);
                return readyState && activeJQuery;
            });

            // Wait for Angular load
            wait.until(driver -> {
                Utility.sleep(1);
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                return (Boolean) (executor.executeScript("return angular.element(document).injector().get('$http').pendingRequests.length === 0"));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * refresh page
     */
    public static void refreshPage() {
        try {
            Utility.getDriver().navigate().refresh();
            WebDriverUtils.waitForPageLoaded();
        } catch (Exception e) {
            Utility.log4j.error("refreshPage method - ERROR - ", e);
        }
    }

    /**
     * force Click by java script
     */
    public static void forceClick(WebElement controlName) {
        WebDriverUtils.waitForControl(controlName);
        JavascriptExecutor executor = Utility.getDriver();
        executor.executeScript("arguments[0].click();", controlName);
    }

    /**
     * Scroll into view by java script
     */
    public static void scrollIntoView(WebElement controlName) {
        WebDriverUtils.waitForControl(controlName);
        JavascriptExecutor executor = Utility.getDriver();
        executor.executeScript("arguments[0].scrollIntoView(true);", controlName);
    }

    /**
     * return true if control exists/false if control not exists
     */
    public static boolean doesControlExist(WebElement control) {
        try {
            return control.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * return true if there is one or more elements specified by xpath found in the page
     */
    public static boolean doesControlExist(List<WebElement> listOfElements) {
        try {
            return listOfElements.size() != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This returns 'true' if there is one or more element specified by xpath found in the page
     */
    public static boolean doesControlExist(String path) {
        try {
            return Utility.getDriver().findElements(By.xpath(path)).size() != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * doesControlPropertyExist
     */
    public static boolean doesControlPropertyExist(WebElement control, String property) {
        try {
            String value = control.getAttribute(property);
            return value != null;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * getWindowHandle
     */
    public static String getWindowHandle(WebDriver driver) {

        // get all the window handles after the popup window appears
        Set<String> afterPopup = driver.getWindowHandles();
        for (String s : afterPopup) Utility.subWindowHandler = s;

        return Utility.subWindowHandler;
    }

    /**
     * switchToWindowHandle
     */
    public static void switchToWindowHandle() {
        try {
            String popupWidowHandle = getWindowHandle(Utility.getDriver());
            Utility.getDriver().switchTo().window(popupWidowHandle);
            maximizeWindow();
        } catch (Exception e) {
            Utility.log4j.error("switchToWindowHandle method - ERROR - ", e);
        }
    }

    /**
     * switchToFrame
     */
    public static void switchToFrame() {
        try {
            WebElement element = Utility.getDriver().findElement(By.tagName("iframe"));
            Utility.getDriver().switchTo().frame(element);
        } catch (Exception e) {
            Utility.log4j.error("switchToFrame method - ERROR - ", e);
        }
    }

    /**
     * maximizeWindow();
     */
    public static void maximizeWindow() {
        try {
            if (OS_NAME.contains("mac") || OS_NAME.contains("linux"))
                Utility.getDriver().manage().window().setSize(new Dimension(1440, 900));
            else
                Utility.getDriver().manage().window().maximize();
        } catch (Exception e) {
            Utility.log4j.error("maximizeWindow method - ERROR - ", e);
        }
    }

    /**
     * Go back to previous page
     */
    public static void goBackToPreviousPage(ExtentTest logTest) {
        try {
            TestReporter.logInfo(logTest, "Back to previous page");
            Utility.getDriver().navigate().back();
            WebDriverUtils.waitForPageLoaded();
        } catch (Exception e) {
            Utility.log4j.error("goBackToPreviousPage method - ERROR - ", e);
            TestReporter.logException(logTest, "goBackToPreviousPage method - ERROR", e);
        }
    }

    public static WebElement getElementActionLink(List<WebElement> actionLinksList, String description, ExtentTest logTest) {

        WebElement elementLink = null;
        try {
            Utility.log4j.debug("getElementActionLink method...starts");

            if (ENVIRONMENT.equalsIgnoreCase(PRODUCTION)) {
                Utility.log4j.info(actionLinksList.get(0).getText());
                elementLink = actionLinksList.get(0);
            } else {
                for (WebElement webElement : actionLinksList) {
                    Utility.log4j.info(webElement.getText());
                    if (webElement.getText().toLowerCase().contains(ENVIRONMENT.toLowerCase())) {
                        elementLink = webElement;
                        break;
                    }
                }
            }
            if (elementLink == null) {
                Utility.log4j.info("Action link '" + description + "' on '" + ENVIRONMENT + "' environment can not be found...");
                TestReporter.logFail(logTest, "Action link '" + description + "' on '" + ENVIRONMENT + "' environment can not be found...");
            }

            Utility.log4j.info("getElementActionLink method...ends");

        } catch (Exception e) {
            Utility.log4j.error("Action link '" + description + "' on '" + ENVIRONMENT + "' environment can not be found...", e);
            TestReporter.logException(logTest, "Action link '" + description + "' on '" + ENVIRONMENT + "' environment can not be found...", e);
        }
        return elementLink;
    }

    public static void handleAlert(String option, ExtentTest logTest) { // Handle error when leaving page unexpected.

        try {
            Utility.log4j.debug("handleAlertbox...start");
            TestReporter.logInfo(logTest, "handleAlertbox method starts");

            // Switching to Alert
            Alert alert = Utility.getDriver().switchTo().alert();

            // Displaying alert message
            Utility.sleep(2);

            if (option.equals("accept")) {
                // Accepting alert
                alert.accept();
            } else alert.dismiss();

            Utility.getDriver().switchTo().defaultContent();
            TestReporter.logInfo(logTest, "handleAlertbox method ends");
            Utility.log4j.debug("handleAlertbox...end");

        } catch (Exception e) {
            Utility.log4j.error("handleAlertbox method - ERROR - ", e);
            TestReporter.logException(logTest, "handleAlertbox method - ERROR - ", e);
        }
    }

    /**
     * getItemFromLocalStorage
     */
    public static String getItemFromLocalStorage(String key, ExtentTest logTest) {
        try {
            JavascriptExecutor js = Utility.getDriver();
            return (String) js.executeScript(String.format(
                    "return window.localStorage.getItem('%s');", key));

        } catch (Exception e) {
            Utility.log4j.error("getItemFromLocalStorage method - ERROR - ", e);
            TestReporter.logException(logTest, "getItemFromLocalStorage method - ERROR", e);
            return "";
        }
    }

    /**
     * isItemPresentInLocalStorage
     */
    public static boolean isItemPresentInLocalStorage(String key, ExtentTest logTest) {
        try {
            JavascriptExecutor js = Utility.getDriver();
            return !(js.executeScript(String.format(
                    "return window.localStorage.getItem('%s');", key)) == null);

        } catch (Exception e) {
            Utility.log4j.error("isItemPresentInLocalStorage method - ERROR - ", e);
            TestReporter.logException(logTest, "isItemPresentInLocalStorage method - ERROR", e);
            return false;
        }
    }

    /**
     * Clear value in textbox using keyboard: ctrl+a > delete
     */
    public static void clearTextboxByKey(WebElement controlName, ExtentTest logTest) {
        try {
            Utility.log4j.debug("clearTextboxByKey method...start");
            int length = controlName.getAttribute("value").length();
            waitForControlToBeClickable(controlName);
            controlName.click();
            Utility.sleep(1);
            for (int i = 0; i < length; i++) {
                controlName.sendKeys(Keys.ARROW_RIGHT);
            }
            Utility.sleep(2);

            for (int i = 0; i < length; i++) {
                controlName.sendKeys(Keys.BACK_SPACE);
            }
            Utility.sleep(3);

            Utility.log4j.info("clearTextboxByKey method...end");
        } catch (Exception e) {
            Utility.log4j.error("clearTextboxByKey method - ERROR - ", e);
            TestReporter.logException(logTest, "clearTextboxByKey method - ERROR", e);
        }
    }

    /***
     * Change value of the entry element by executing javascript commands: set value, dispatch 'input' and then 'blur' events
     * Use this method if sendKeys methods does not work
     * @param element
     * @param keysToSend
     */
    public static void sendKeysByJS(WebElement element, String keysToSend) {
        try {
            JavascriptExecutor executor = (JavascriptExecutor) Utility.getDriver();
            String script = "function changeValue(ele, value){" +
                    "ele.value=value;" +
                    "ele.dispatchEvent(new Event('input'));" +
                    "ele.dispatchEvent(new Event('blur'));};" +
                    "changeValue(arguments[0],arguments[1]);";

            executor.executeScript(script, element, keysToSend);
        } catch (Exception e) {
            Utility.log4j.error("sendKeysByJS method - ERROR - ", e);
        }
    }

    /***
     * Change value of the entry element by Keyboard
     * Use this method if sendKeys methods does not work
     * @param element
     * @param keysToSend
     */
    public static void sendKeysByKeyboard(WebElement element, String keysToSend) {
        try {
            element.click();
            Keyboard board = ((HasInputDevices) Utility.getDriver()).getKeyboard();
            board.sendKeys(keysToSend);
        } catch (Exception e) {
            Utility.log4j.error("sendKeysByKeyboard method - ERROR - ", e);
        }
    }

    //wait for alert present
    public static void waitForAlertPresent() {
        int i = 0;
        while (i++ < 5) {
            try {
                Alert alert = Utility.getDriver().switchTo().alert();
                break;
            } catch (NoAlertPresentException e) {
                Utility.sleep(1);
                continue;
            }
        }
    }

    /**
     * clear Local Storage
     *
     * @param logTest
     */
    public static void clearLocalStorage(ExtentTest logTest) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) Utility.getDriver();
            js.executeScript(String.format("window.localStorage.clear();"));
        } catch (Exception e) {
            Utility.log4j.error("clearLocalStorage method - ERROR - ", e);
            TestReporter.logException(logTest, "clearLocalStorage method - ERROR", e);
        }
    }

    public static void inputCharactersOneByOne(WebElement controlName, String value) {
        controlName.clear();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            String s = new StringBuilder().append(c).toString();
            controlName.sendKeys(s);
        }
    }

    public static boolean isAttributePresent(WebElement element, String attribute) {
        Boolean result = false;
        try {
            String value = element.getAttribute(attribute);
            if (value != null) {
                result = true;
            }
        } catch (Exception e) {
        }

        return result;
    }

    public static void closeTab() {
        Utility.getDriver().close();
    }

    public static void openNewTab() {
        String newTab = "window.open('about:blank','_blank')";
        ((JavascriptExecutor) Utility.getDriver()).executeScript(newTab);
    }

    public static void switchTab(int num) {
        ArrayList<String> tabs = new ArrayList<String>(Utility.getDriver().getWindowHandles());
        Utility.getDriver().switchTo().window(tabs.get(num));
    }

    public static void switchTab() {
        ArrayList<String> tabs = new ArrayList<String>(Utility.getDriver().getWindowHandles());
        Utility.getDriver().switchTo().window(tabs.get(1));
    }

    public static boolean isElementClickable(WebElement elementName, int waitTime, ExtentTest logStep) {
        try {
            Utility.log4j.debug("isElementClickable method...start");
            new WebDriverWait(Utility.getDriver(), waitTime).until(ExpectedConditions.visibilityOf(elementName));
            new WebDriverWait(Utility.getDriver(), waitTime).until(ExpectedConditions.elementToBeClickable(elementName));
            Utility.log4j.info("isElementClickable method... end");
            return true;
        } catch (Exception e) {
            Utility.log4j.info("Element is not clickable");
        }
        return false;
    }

    /**
     * clickBackArrowBrowser
     *
     * @param logTest
     */
    public static void clickBackArrowBrowser(ExtentTest logTest) {
        try {
            Utility.log4j.debug("Start checking Buyer Center Home page displays");
            Utility.getDriver().navigate().back();
            Utility.log4j.debug("End checking Buyer Center Checkout Page displays");
        } catch (Exception e) {
            Utility.log4j.error("clickBackArrowBrowser method - ERROR - ", e);
            TestReporter.logException(logTest, "clickBackArrowBrowser method - ERROR", e);

        }
    }

    /**
     * @Action name: select item in combo box
     * @Example: selectItem(combobox_ManufacturerForNonPhone, manufacturerForNonPhone)
     * @CreatedBy: tien.tran
     * @On: 12/14/2020
     */
    public static void selectItemIgnoreCaseUsingIndex(WebElement comboBoxElement, String itemName) {
        Select dropDown = new Select(comboBoxElement);
        int index = 0;
        for (WebElement option : dropDown.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(itemName))
                break;
            index++;
        }
        dropDown.selectByIndex(index);
    }

    public static void selectItemIgnoreCaseUsingValue(WebElement comboBoxElement, String itemName) {
        Select dropDown = new Select(comboBoxElement);
        String value = "";
        for (WebElement option : dropDown.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(itemName) || option.getText().trim().contains(itemName)) {
                value = option.getAttribute("value");
                break;
            }
        }
        dropDown.selectByValue(value);
    }

    public static void setItemInLocalStorage(String item, String value, ExtentTest logTest) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) Utility.getDriver();
            js.executeScript(String.format(
                    "window.localStorage.setItem('%s','%s');", item, value));
        } catch (Exception e) {
            Utility.log4j.error("setItemInLocalStorage method - ERROR - ", e);
            TestReporter.logException(logTest, "setItemInLocalStorage method - ERROR", e);
        }
    }

    public static void clickElement(WebElement webElement) {
        WebDriverUtils.scrollIntoView(webElement);
        WebDriverUtils.waitForControlToBeClickable(webElement);
        webElement.click();
        WebDriverUtils.waitForPageLoaded();
    }

    /**
     * Paste text into WebElement.
     *
     * @param element
     * @param text
     * @param logStep
     * @author rtrigueros
     */
    public static void pasteText(ExtentTest logStep, WebElement element, String text) {
        try {
            Utility.log4j.debug("pasteText method...beginning");

            WebDriverUtils.waitForControl(element);
            element.clear();

            StringSelection stringSelection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            element.sendKeys(Keys.SHIFT, Keys.INSERT);

            Utility.log4j.debug("pasteText method...end");
        } catch (WebDriverException wex) {
            Utility.log4j.error("pasteText method - ERROR - " + wex);
            TestReporter.logException(logStep, "pasteText method - ERROR", wex);
        }
    }

    /**
     * Wait for element to be clickable using specific timeout.
     *
     * @param logStep
     * @param controlName
     * @param timeout
     * @author rtrigueros
     */
    public static WebElement waitForElementToBeClickable(ExtentTest logStep, WebElement controlName,
                                                         Integer... timeout) {
        try {
            Utility.log4j.debug("wait for element to be clickable " + controlName);
            return new WebDriverWait(Utility.getDriver(), timeout.length > 0 ? timeout[0] : TIMEOUT_ELEMENT)
                    .until(ExpectedConditions.elementToBeClickable(controlName));
        } catch (NoSuchElementException | TimeoutException | ElementClickInterceptedException ex) {
            Utility.log4j.error("wait for element to be clickable failed", ex);
            TestReporter.logException(logStep, "wait for element to be clickable failed", ex);
            return null;
        }
    }

    /**
     * Wait until element is not visible.
     *
     * @param <T>         You can either pass a WebElement or By
     * @param elementAttr UI locator value
     * @param logStep
     * @param timeOut     You can either pass an expected timeout in seconds or use
     *                    default TIMEOUT_ELEMENT
     * @author rtrigueros
     */
    public static <T> void waitUntilElementNotVisible(ExtentTest logStep, T elementAttr, long... timeOut) {
        try {
            if (elementAttr.getClass().getName().contains("By")) {
                new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.invisibilityOfElementLocated((By) elementAttr));
            } else {
                new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.invisibilityOf((WebElement) elementAttr));
            }
        } catch (TimeoutException ex) {
            Utility.log4j.error("wait until element not visible - time out " + elementAttr, ex);
            TestReporter.logException(logStep, "wait until element not visible - time out", ex);
        } catch (NoSuchElementException nse) {
            Utility.log4j.error("wait until element not visible - element not found " + elementAttr, nse);
            TestReporter.logException(logStep, "wait until element not visible - element not found", nse);
        }
    }

    /**
     * Wait for an element to be displayed.
     *
     * @param <T>         You can either pass a WebElement or By
     * @param elementAttr UI locator value
     * @param timeOut     You can either pass an expected timeout in seconds, or use
     *                    default value 10 seconds
     * @return WebElement to interact with
     * @author rtrigueros
     */
    public static <T> WebElement waitForElementDisplayed(ExtentTest logStep, T elementAttr, long... timeOut) {
        Utility.log4j.debug("wait for element displayed: " + elementAttr);
        try {
            if (elementAttr.getClass().getName().contains("By")) {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOfElementLocated((By) elementAttr));
            } else {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOf((WebElement) elementAttr));
            }
        } catch (NoSuchElementException | TimeoutException ex) {
            Utility.log4j.error("wait for element to be displayed failed: " + elementAttr, ex);
            TestReporter.logException(logStep, "wait for element to be displayed failed: ", ex);
            return null;
        }
    }

    public static <T> boolean isElementDisplayed(ExtentTest logStep, T elementAttr, long... timeOut) {
        Utility.log4j.debug("is element displayed? " + elementAttr);

        if (elementAttr == null) {
            return false;
        }

        try {
            if (elementAttr.getClass().getName().contains("By")) {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOfElementLocated((By) elementAttr)) != null;
            } else {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOf((WebElement) elementAttr)) != null;
            }
        } catch (TimeoutException toe) {
            Utility.log4j.error("wait for element to be displayed - TimeoutException: " + elementAttr, toe);
            TestReporter.logException(logStep, "wait for element to be displayed - TimeoutException: ", toe);
            return false;
        } catch (NoSuchElementException nse) {
            Utility.log4j.error("wait for element to be displayed - NoSuchElementException: " + elementAttr, nse);
            TestReporter.logException(logStep, "wait for element to be displayed - NoSuchElementException: ", nse);
            return false;
        } catch (NullPointerException npe) {
            Utility.log4j.error("wait for element to be displayed - NullPointerException: " + elementAttr, npe);
            TestReporter.logException(logStep, "wait for element to be displayed - NullPointerException: ", npe);
            return false;
        }
    }

    public static <T> void moveToElement(T elementAttr) {
        Utility.log4j.debug("move to element: " + elementAttr);

        try {
            Actions actions = new Actions(Utility.getDriver());
            if (elementAttr.getClass().getName().contains("By")) {
                actions.moveToElement(Utility.getDriver().findElement((By) elementAttr)).build().perform();
            } else {
                actions.moveToElement((WebElement) elementAttr).build().perform();
            }
        } catch (Exception ex) {
            Utility.log4j.error("error moving to element: " + elementAttr, ex);
        }
    }

    public static <T> String getAttribute(T elementAttr, String attr) {
        Utility.log4j.debug(String.format("get attribute: %s in the element: %s", attr, elementAttr));
        if (elementAttr == null) {
            Utility.log4j.debug("element is null, cannot get text");
            return "";
        }

        try {
            if (elementAttr.getClass().getName().contains("By")) {
                return Utility.getDriver().findElement((By) elementAttr).getAttribute(attr);
            } else {
                return ((WebElement) elementAttr).getAttribute(attr);
            }
        } catch (Exception ex) {
            Utility.log4j.error("error getting element text" + elementAttr, ex);
            return "";
        }
    }

    public static <T> void waitForAttribute(T element, String attr, String value, long... timeOut) {
        Utility.log4j.debug(
                String.format("wait for value: %s in the attribute: %s for the element: %s", value, attr, element));
        ExpectedCondition<Boolean> isAttribute;

        if (element.getClass().getName().contains("By")) {
            isAttribute = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) Utility.getDriver()).executeScript(
                            "return arguments[0].value", Utility.getDriver().findElement((By) element)).equals(value);
                }
            };
        } else {
            isAttribute = new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) Utility.getDriver()).executeScript(
                            "return arguments[0].value", ((WebElement) element)).equals(value);
                }
            };
        }

        try {
            new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                    .ignoring(NoSuchElementException.class).until(isAttribute);
            Utility.log4j.debug("current attribute: " + ((JavascriptExecutor) Utility.getDriver()).executeScript(
                    "return arguments[0].value", ((WebElement) element)));
        } catch (TimeoutException ex) {
            Utility.log4j.error("wait for attribute in Web Element failed, expected: " + value, ex);
        }
    }

    public static <T> boolean shouldNotBeDisplayed(ExtentTest logStep, T elementAttr, long... timeOut) {
        Utility.log4j.debug("element should not be displayed " + elementAttr);

        if (elementAttr == null) {
            return true;
        }

        try {
            if (elementAttr.getClass().getName().contains("By")) {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOfElementLocated((By) elementAttr)) != null;
            } else {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOf((WebElement) elementAttr)) != null;
            }
        } catch (TimeoutException | NoSuchElementException | NullPointerException toe) {
            return true;
        }
    }

    public static boolean shouldNotBeDisplayed(ExtentTest logStep, List<WebElement> elements, long... timeOut) {
        if (elements == null) {
            return true;
        }

        try {
            return (new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                    .until(ExpectedConditions.visibilityOfAllElements(elements)) != null);
        } catch (NoSuchElementException | TimeoutException ex) {
            return true;
        }
    }

    /**
     * Find an element inside of parent element and return null if not found.
     *
     * @param parent        to search into
     * @param childSelector to find
     * @return WebElement
     * @author rtrigueros
     */
    public static WebElement findElement(WebElement parent, By childSelector) {
        try {
            if (parent.findElements(childSelector).size() == 0) {
                return null;
            } else {
                return parent.findElement(childSelector);
            }
        } catch (NoSuchElementException ex) {
            Utility.log4j.error("error finding child selector " + childSelector, ex);
            return null;
        }
    }

    public static <T> void moveByOffsetAndClick(T elementAttr, int x, int y) {
        try {
            Actions actions = new Actions(Utility.getDriver());
            if (elementAttr.getClass().getName().contains("By")) {
                actions.moveToElement(Utility.getDriver().findElement((By) elementAttr)).moveByOffset(x, y).click().build().perform();
            } else {
                actions.moveToElement((WebElement) elementAttr).moveByOffset(x, y).click().build().perform();
            }
        } catch (Exception ex) {
            Utility.log4j.error("error moving to element and click: " + elementAttr, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<WebElement> waitForElementsToBeDisplayed(List<T> elements, long... timeOut) {
        try {
            if (elements.getClass().getName().contains("By")) {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By) elements));
            } else {
                return new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT)
                        .until(ExpectedConditions.visibilityOfAllElements((List<WebElement>) elements));
            }
        } catch (NoSuchElementException | TimeoutException ex) {
            Utility.log4j.error("wait for elements to be displayed failed", ex);
            return null;
        }
    }

    public static void waitForUrlContains(String url, long... timeOut) {
        new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_PAGE)
                .until(ExpectedConditions.urlContains(url));
    }

    public static void waitForExactUrl(String url, long... timeOut) {
        new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_PAGE).until(ExpectedConditions.urlToBe(url));
    }

    public static <T> void click(T elementAttr) {
        try {
            Utility.log4j.debug("click on " + elementAttr);
            if (elementAttr.getClass().getName().contains("By")) {
                Utility.getDriver().findElement((By) elementAttr).click();
            } else {
                ((WebElement) elementAttr).click();
            }
        } catch (Exception ex) {
            Utility.log4j.error("error clicking on element " + elementAttr, ex);
        }
    }

    public static <T> void clickWaitNewElement(ExtentTest logStep, T elementAttr, T newElement, long... timeOut) {
        Utility.log4j.debug("click " + elementAttr + " and wait new element " + newElement);

        ExpectedCondition<Boolean> newElementDisplayed = driver -> {
            if (!isElementDisplayed(logStep, newElement, 0)) {
                click(elementAttr);
            }
            return isElementDisplayed(logStep, newElement, 0);
        };

        try {
            new WebDriverWait(Utility.getDriver(), timeOut.length > 0 ? timeOut[0] : TIMEOUT_ELEMENT).until(newElementDisplayed);
        } catch (TimeoutException ex) {
            Utility.log4j.error("wait for new element displayed time out", ex);
        } catch (ClassCastException cc) {
            Utility.log4j.error("wait for new element displayed class cast exception", cc);
        }
    }

}
