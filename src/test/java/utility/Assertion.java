package utility;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utility.Utility.log4j;

public class Assertion {
    /**
     * verifyExpectedAndActualResults
     */
    public static void verifyExpectedAndActualResults(ExtentTest logTest, String expected, String actual) {
        try {
            if (actual.trim().equalsIgnoreCase(expected)) {
                TestReporter.logPass(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            } else {
                TestReporter.logFail(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResults method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResults method - ERROR", e);
        }
    }

    /**
     * verifyExpectedAndActualResultsBoolean
     */
    public static void verifyExpectedAndActualResultsBoolean(ExtentTest logTest, boolean expected, boolean actual) {
        try {

            if (expected == actual) {
                TestReporter.logPass(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            } else {
                TestReporter.logFail(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }

        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsBoolean method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsBoolean method - ERROR", e);
        }
    }

    public static void verifyExpectedAndActualResultsSubStringSoftAssert(ExtentTest logTest, String expected, String actual) {
        try {
            if (actual.trim().contains(expected.trim())) {
                TestReporter.logPass(logTest, "Expected Result: Object contains '" + expected + "'" + "<br/>Actual Result: " + actual);
            } else {
                TestReporter.logFailSoftAssert(logTest, "Expected Result: Object contains '" + expected + "'" + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSubString method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSubString method - ERROR", e);
        }
    }

    /**
     * verifyExpectedAndActualResults
     */
    public static void verifyExpectedAndActualResults(ExtentTest logTest, double expected, double actual) {
        try {
            if (actual == expected) {
                TestReporter.logPass(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            } else {
                TestReporter.logFail(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResults method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResults method - ERROR", e);
        }
    }

    /**
     * verifyCheckboxStatus
     */
    public static void verifyCheckboxStatus(ExtentTest logTest, WebElement elementName, String objectName, String expected) {
        try {
            if (expected.equalsIgnoreCase("true")) {
                if (elementName.isSelected())
                    TestReporter.logPass(logTest, "Expected Result: " + objectName + " checkbox element displays in 'checked' state <br/>Actual Result: Checkbox element is in checked state");
                else
                    TestReporter.logFail(logTest, "Expected Result: " + objectName + " Checkbox element displays in 'checked' state <br/>Actual Result: Checkbox element is in unchecked state");
            } else {
                if (!elementName.isSelected())
                    TestReporter.logPass(logTest, "Expected Result: " + objectName + " Checkbox element displays in 'unchecked' state <br/>Actual Result: Checkbox element is in unchecked state");
                else
                    TestReporter.logFail(logTest, "Expected Result: " + objectName + " Checkbox element displays in 'unchecked' state <br/>Actual Result: Checkbox element is in checked state");
            }

        } catch (Exception e) {
            log4j.error("verifyCheckboxStatus - ERROR - ", e);
            TestReporter.logException(logTest, "verifyCheckboxStatus method - ERROR", e);
        }
    }

    /**
     * checkControlExist
     */
    public static void checkControlExist(ExtentTest logTest, WebElement elementName, String objectName) {
        try {
            WebDriverUtils.waitForControl(elementName);
            if (!WebDriverUtils.doesControlExist(elementName))
                TestReporter.logFail(logTest, objectName + " does not exist.");
            else TestReporter.logPass(logTest, objectName + " exists.");
        } catch (Exception e) {
            log4j.error("checkControlExist - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlExist method - ERROR", e);
        }
    }

    public static void checkControlExistSoftAssert(ExtentTest logTest, WebElement elementName, String objectName) {
        try {
            WebDriverUtils.waitForControl(elementName);
            if (!WebDriverUtils.doesControlExist(elementName))
                TestReporter.logFailSoftAssert(logTest, objectName + " does not exist.");
            else TestReporter.logPass(logTest, objectName + " exists.");
        } catch (Exception e) {
            log4j.error("checkControlExist - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlExist method - ERROR", e);
        }
    }

    /**
     * checkControlNotLinked
     */
    public static void checkControlNotLinking(ExtentTest logTest, WebElement elementName, String objectName) {
        try {
            WebDriverUtils.waitForControl(elementName);
            if (WebDriverUtils.doesControlPropertyExist(elementName, "href"))
                TestReporter.logFail(logTest, objectName + " is linking.");
            else
                TestReporter.logPass(logTest, objectName + " is not linking");
        } catch (Exception e) {
            log4j.error("checkControlNotLinking - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlNotLinking method - ERROR", e);
        }
    }

    /**
     * checkControlNotExist
     */
    public static void checkControlNotExist(ExtentTest logTest, WebElement elementName, String objectName) {
        try {
            if (WebDriverUtils.doesControlExist(elementName))
                TestReporter.logFail(logTest, objectName + " exist.");
            else
                TestReporter.logPass(logTest, objectName + " does not exists.");
        } catch (Exception e) {
            log4j.error("checkControlNotExist - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlNotExist method - ERROR", e);
        }
    }

    /**
     * Check property of element like text box, label
     */
    public static void checkControlProperty(WebElement elementName, String elementProperty, String propertyValue, ExtentTest logTest) {
        try {
            log4j.debug("checkControlPropertyValue method...Start");

            WebDriverUtils.waitForControl(elementName);
            String actualPropertyValue = elementName.getAttribute(elementProperty);
            verifyExpectedAndActualResults(logTest, propertyValue, actualPropertyValue);

            log4j.info("checkControlPropertyValue method...End");

        } catch (Exception e) {
            log4j.error("checkControlPropertyValue - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlPropertyValue method - ERROR", e);
        }
    }

    /**
     * Check value of element like text box, label
     */
    public static void checkControlValue(WebElement elementName, String value, ExtentTest logTest) {
        try {
            log4j.debug("checkControlValue method...Start");

            WebDriverUtils.waitForControl(elementName, logTest);
            Utility.sleep(2);
            String actualValue = elementName.getAttribute("value");
            if (actualValue == null)
                actualValue = elementName.getText();
            verifyExpectedAndActualResults(logTest, value, actualValue);

            log4j.info("checkControlValue method...End");
        } catch (Exception e) {
            log4j.error("checkControlValue - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlValue method - ERROR", e);
        }
    }

    public static void checkComboBoxValue(WebElement elementName, String value, ExtentTest logTest) {
        try {
            log4j.debug("checkComboboxValue method...Start");

            WebDriverUtils.waitForControl(elementName);
            String actualValue = new Select(elementName).getFirstSelectedOption().getText();
            verifyExpectedAndActualResults(logTest, value, actualValue);

            log4j.info("checkComboboxValue method...End");
        } catch (Exception e) {
            log4j.error("checkComboboxValue - ERROR - ", e);
            TestReporter.logException(logTest, "checkComboboxValue method - ERROR", e);
        }
    }

    public static void assertTrueFalse(ExtentTest logTest, boolean expected, boolean actual) {
        try {
            if (expected == actual) {
                TestReporter.logPass(logTest, "The result is matched, <br/>Expected Result: " + expected + "<br/>Actual Result: " + actual);
            } else {
                TestReporter.logFail(logTest, "The result did not match, <br/>Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("assertTrueFalse method - ERROR - ", e);
            TestReporter.logException(logTest, "assertTrueFalse method - ERROR", e);
        }
    }

    public static void assertEquals(ExtentTest logTest, int expected, int actual) {

        try {
            if (expected == actual)
                TestReporter.logPass(logTest, "The result is matched, <br/>Expected Result: " + expected + "<br/>Actual Result: " + actual);
            else
                TestReporter.logFail(logTest, "The result is matched, <br/>Expected Result: " + expected + "<br/>Actual Result: " + actual);

        } catch (Exception e) {

            log4j.error("assertEquals method - ERROR - ", e);
            TestReporter.logException(logTest, "assertEquals method - ERROR", e);
        }
    }

    /**
     * Check control not displayed
     */
    public static void checkControlNotDisplayed(WebElement controlName, String objectName, ExtentTest logTest) {
        try {
            log4j.debug("checkControlDisplayed method...start");

            if (controlName.isDisplayed())
                TestReporter.logFail(logTest, objectName + " exist.");
            else
                TestReporter.logPass(logTest, objectName + " does not exists.");

            log4j.info("checkControlDisplayed method...end");

        } catch (Exception e) {
            log4j.error("checkControlDisplayed method - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlDisplayed method - ERROR", e);
        }
    }

    /**
     * Check new tab open when clicking link
     */
    public static void checkNewTabOpen(String pageURL, ExtentTest logTest) {
        try {
            log4j.debug("checkNewTabOpen method...start");

            List<String> browserTabs = new ArrayList<>(Utility.getDriver().getWindowHandles());
            Utility.getDriver().switchTo().window(browserTabs.get(1));
            Utility.getDriver().close();
            Utility.getDriver().switchTo().window(browserTabs.get(0));

            log4j.info("checkNewTabOpen method...end");

        } catch (Exception e) {
            log4j.error("checkNewTabOpen method - ERROR - ", e);
            TestReporter.logException(logTest, "checkNewTabOpen method - ERROR", e);
        }
    }

    /**
     * Check if a specific combo box does contain a given value.
     */
    public static void checkComboBoxContainsOption(WebElement element, String value, ExtentTest logTest) {
        try {
            log4j.debug("checkComboBoxContainsOption method...beginning");

            if (value.isEmpty())
                TestReporter.logFail(logTest, "Expected Result: " + value + "is invalid!");
            else {
                //Get all actual option
                List<String> actualOption = new ArrayList<>();
                List<WebElement> dropdownOption = new Select(element).getOptions();

                for (WebElement option : dropdownOption) {
                    actualOption.add(option.getText());
                }

                TestReporter.logInfo(logTest, "Check all options of combobox");
                if (actualOption.contains(value))
                    TestReporter.logPass(logTest, "Expected Result: " + actualOption + " contains " + value
                            + "<br/>Actual Result: " + actualOption + "contains " + value);
                else
                    TestReporter.logFail(logTest, "Expected Result: " + actualOption + " contains " + value
                            + "<br/>Actual Result: " + actualOption + "does not contain " + value);

                log4j.info("checkComboBoxContainsOption method...end");
            }
        } catch (Exception e) {
            log4j.error("checkComboBoxContainsOption method - ERROR - ", e);
            TestReporter.logException(logTest, "checkComboBoxContainsOption method - ERROR", e);
        }
    }

    /**
     * Check if a specific combo box does NOT contain a given value.
     */
    public static void checkComboBoxNotContainsOption(WebElement element, String value, ExtentTest logTest) {
        try {
            log4j.debug("checkComboBoxNotContainsOption method...beginning");

            if (value.isEmpty())
                TestReporter.logFail(logTest, "Expected Result: " + value + "is invalid!");
            else {
                //Get all actual options
                List<String> actualOption = new ArrayList<>();
                List<WebElement> dropdownOption = new Select(element).getOptions();

                for (WebElement option : dropdownOption) {
                    actualOption.add(option.getText());
                }

                TestReporter.logInfo(logTest, "Check all options of combobox");
                if (!actualOption.contains(value))
                    TestReporter.logPass(logTest, "Expected Result: " + actualOption + " does not contains " + value
                            + "<br/>Actual Result: " + actualOption + " does not contains " + value);
                else
                    TestReporter.logFail(logTest, "Expected Result: " + actualOption + " does not contains " + value
                            + "<br/>Actual Result: " + actualOption + " contains " + value);

                log4j.info("checkComboBoxNotContainsOption method...end");
            }
        } catch (Exception e) {
            log4j.error("checkComboBoxNotContainsOption method - ERROR - ", e);
            TestReporter.logException(logTest, "checkComboBoxNotContainsOption method - ERROR", e);
        }
    }

    public static void compareTwoList(List<String> expectList, List<String> actualList, ExtentTest logTest) {
        try {
            Collections.sort(expectList);
            Collections.sort(actualList);

            if (expectList.equals(actualList))
                TestReporter.logPass(logTest, "Expected Result: " + expectList + "<br/>Actual Result: " + actualList);
            else
                TestReporter.logFail(logTest, "Expected Result: " + expectList + "<br/>Actual Result: " + actualList);

        } catch (Exception e) {
            log4j.error("compareTwoList method - ERROR - ", e);
            TestReporter.logException(logTest, "compareTwoList method - ERROR", e);
        }
    }

    public static void verifyExpectedAndActualResultsSoftAssert(ExtentTest logTest, String expected, String actual) {

        try {
            if (actual.trim().equalsIgnoreCase(expected))
                TestReporter.logPass(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            else {
                ITestResult currentTestResult = Reporter.getCurrentTestResult();
                currentTestResult.setStatus(ITestResult.FAILURE);
                TestReporter.logFailSoftAssert(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSoftAssert method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSoftAssert method - ERROR", e);
        }
    }

    public static void verifyExpectedAndActualResultsSoftAssert(ExtentTest logTest, Boolean expected, Boolean actual) {

        try {
            if (actual.equals(expected))
                TestReporter.logPass(logTest, "Element is clickable");
            else
                TestReporter.logFailSoftAssert(logTest, "Element is not clickable");

        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSoftAssert method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSoftAssert method - ERROR", e);
        }
    }

    public static boolean isTimeStampValid(ExtentTest logTest, String pattern, String timeStamp) {

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            format.parse(timeStamp);
            TestReporter.logPass(logTest, "Expected Result: " + pattern + "<br/>Actual Result: " + timeStamp);
            return true;
        } catch (ParseException e) {
            TestReporter.logFail(logTest, "Expected Result: " + pattern + "<br/>Actual Result: " + timeStamp);
            return false;
        }
    }

    public static void verifyExpectedAndActualResultsSubStringSoftAssert(ExtentTest logTest, String expected, String actual, SoftAssert softAssert) {
        try {
            if (actual.trim().contains(expected.trim())) {
                softAssert.assertTrue(true);
                TestReporter.logPass(logTest, "Expected Result: Object contains '" + expected + "'" + "<br/>Actual Result: " + actual);
            } else {
                softAssert.assertTrue(false);
                TestReporter.logFailSoftAssert(logTest, "Expected Result: Object contains '" + expected + "'" + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSubString method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSubString method - ERROR", e);
        }
    }

    public static void checkControlExistSoftAssert(ExtentTest logTest, WebElement elementName, String objectName, SoftAssert softAssert) {

        try {
            WebDriverUtils.waitForControl(elementName);
            if (!WebDriverUtils.doesControlExist(elementName)) {
                softAssert.assertTrue(false);
                TestReporter.logFailSoftAssert(logTest, objectName + " does not exist.");
            } else {
                softAssert.assertTrue(true);
                TestReporter.logPass(logTest, objectName + " exists.");
            }
        } catch (Exception e) {
            log4j.error("checkControlExist - ERROR - ", e);
            TestReporter.logException(logTest, "checkControlExist method - ERROR", e);
        }
    }

    public static void verifyExpectedAndActualResultsSoftAssert(ExtentTest logTest, String expected, String actual, SoftAssert softAssert) {

        try {
            if (actual.trim().equalsIgnoreCase(expected)) {
                softAssert.assertTrue(true);
                TestReporter.logPass(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            } else {
                softAssert.assertTrue(false);
                TestReporter.logFailSoftAssert(logTest, "Expected Result: " + expected + "<br/>Actual Result: " + actual);
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSoftAssert method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSoftAssert method - ERROR", e);
        }
    }

    public static void verifyExpectedAndActualResultsSoftAssert(ExtentTest logTest, Boolean expected, Boolean actual, SoftAssert softAssert) {

        try {
            if (actual.equals(expected)) {
                softAssert.assertTrue(true);
                TestReporter.logPass(logTest, "Element is clickable");
            } else {
                softAssert.assertTrue(false);
                TestReporter.logFailSoftAssert(logTest, "Element is not clickable");
            }
        } catch (Exception e) {
            log4j.error("verifyExpectedAndActualResultsSoftAssert method - ERROR - ", e);
            TestReporter.logException(logTest, "verifyExpectedAndActualResultsSoftAssert method - ERROR", e);
        }
    }



    public static void verifyValueHasChanged(ExtentTest logTest, String oldValue, String newValue) {

        if (newValue.equals(oldValue))
            TestReporter.logFail(logTest, "The value does not change: " + oldValue);
        else
            TestReporter.logPass(logTest, "The value has changed. Old value: " + oldValue + "<br/>New value: " + newValue);
    }
}