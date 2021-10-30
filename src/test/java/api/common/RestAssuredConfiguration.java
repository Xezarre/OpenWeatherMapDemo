package api.common;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.response.Response;
import utility.TestReporter;
import utility.Utility;

import java.io.IOException;

import static common.GlobalVariables.ENVIRONMENT;
import static io.restassured.RestAssured.given;

public class RestAssuredConfiguration extends Utility {

    /**
     * Get the Auth2 Bearer token to use in automation service API
     */
    public String getAuth2Token(String username, String password, ExtentTest logTest) throws IOException {

        TestReporter.logInfo(logTest, "Call the oauth2 API to get Bearer token - start");
        Response response =
                given().
                        formParam("username", username).
                        formParam("password", password).
                        when().
                        relaxedHTTPSValidation().
                        post("https://www-" + ENVIRONMENT + ".testing.com/rest/oauth/2/token").
                        then().
                        extract().
                        response();

        handleResponseStatusCode(response, 200, logTest);
        String accessToken = response.path("access_token");
        TestReporter.logInfo(logTest, "Access Token: " + accessToken);
        TestReporter.logInfo(logTest, "Call the oauth2 API to get Bearer token - end");
        return accessToken;
    }

    /**
     * Check status code response matches with expectation or not
     */
    public static synchronized void handleResponseStatusCode(Response res, int statusCode, ExtentTest logTest) {
        try {
            if (res.getStatusCode() == statusCode)
                TestReporter.logInfo(logTest, "API calling successful. Status code response is " + res.getStatusCode());
            else
                TestReporter.logFail(logTest, "API calling was unsuccessful: Status code response is " + res.getStatusCode() + " instead of " + statusCode);
        } catch (Exception e) {
            log4j.error(" handleResponseStatusCode - ERROR - ", e);
            TestReporter.logException(logTest, " handleResponseStatusCode - ERROR", e);
        }
    }
}
