package tests;

import common.TestBase;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import pages.MainPage;
import pages.ResultPage;
import pages.SuggestionPage;
import utility.TestReporter;
import utility.Utility;
import utility.WebDriverUtils;

import java.io.IOException;
import java.util.Hashtable;

import static common.GlobalVariables.DEFAULT_PROD;

public class C01_Verify_user_can_search_for_weather_data_of_given_city extends TestBase {

    @Test(dataProvider = "getDataForTest", priority = 1, description = "Verify user can search for weather data of a given city")
    public void TC01(Hashtable<String, String> data) throws IOException {
        if (isTestCaseExecutable && isTestDataExecutable(data, logMethod)) {
            try {
                String searchString = data.get("SearchString");
                Hashtable<String, String> parsedData = Utility.parseSearchString(searchString, logStep);

                logStep = TestReporter.logStepInfo(logMethod, "Pre-condition: Buy phone Warranty and age back");
                WebDriverUtils.navigateToTestSite(logStep, DEFAULT_PROD);

                logStep = TestReporter.logStepInfo(logMethod, "Perform searching by City name");
                MainPage mainPage = PageFactory.initElements(Utility.getDriver(), MainPage.class);
                mainPage.searchByCityName(searchString, logStep);

                logStep = TestReporter.logStepInfo(logMethod, "Select one suggestion from result list");
                SuggestionPage suggestionPage = PageFactory.initElements(Utility.getDriver(), SuggestionPage.class);
                suggestionPage.selectLocationFromResultList(parsedData.get("City"), logStep);

                logStep = TestReporter.logStepInfo(logMethod, "Verify result");
                ResultPage resultPage = PageFactory.initElements(Utility.getDriver(), ResultPage.class);
                resultPage.verifyReturnedCityName(parsedData.get("City"), logStep);
                resultPage.verifyReturnedDate(logStep);
                resultPage.verifyTemperatureLabelExist(logStep);

            } catch (Exception e) {
                log4j.error(getStackTrace(e.getStackTrace()));
                TestReporter.logException(logMethod, testCaseName, e);
            }
        }
    }
}
