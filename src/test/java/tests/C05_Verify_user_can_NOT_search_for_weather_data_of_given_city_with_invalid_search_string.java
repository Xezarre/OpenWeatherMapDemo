package tests;

import common.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import pages.MainPage;
import pages.SuggestionPage;
import utility.TestReporter;
import utility.Utility;
import utility.WebDriverUtils;

import java.io.IOException;
import java.util.Hashtable;

import static common.GlobalVariables.DEFAULT_PROD;

public class C05_Verify_user_can_NOT_search_for_weather_data_of_given_city_with_invalid_search_string extends TestBase {

    @Test(dataProvider = "getDataForTest", priority = 1, description = "Verify user can search for weather data of a given city")
    public void TC01(Hashtable<String, String> data) throws IOException {
        if (isTestCaseExecutable && isTestDataExecutable(data, logMethod)) {
            try {
                String searchString = data.get("SearchString");

                logStep = TestReporter.logStepInfo(logMethod, "Pre-condition: Buy phone Warranty and age back");
                WebDriverUtils.navigateToTestSite(logStep, DEFAULT_PROD);

                logStep = TestReporter.logStepInfo(logMethod, "Perform searching by City name");
                MainPage mainPage = PageFactory.initElements(Utility.getDriver(), MainPage.class);
                mainPage.searchByCityName(searchString, logStep);

                logStep = TestReporter.logStepInfo(logMethod, "Verify result");
                SuggestionPage suggestionPage = PageFactory.initElements(Utility.getDriver(), SuggestionPage.class);
                suggestionPage.verifyLocationNotFound(logStep);

            } catch (Exception e) {
                log4j.error(getStackTrace(e.getStackTrace()));
                TestReporter.logException(logMethod, testCaseName, e);
            }
        }
    }
}
