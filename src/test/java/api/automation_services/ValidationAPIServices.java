package api.automation_services;

import api.common.RestAssuredConfiguration;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static common.GlobalVariables.*;

public class ValidationAPIServices extends RestAssuredConfiguration {

    public RequestSpecification automationServiceRequestSpecification(String domain, ExtentTest logTest) throws IOException {

        String accessToken = getAuth2Token("username", "password", logTest);
        return given().
                baseUri("https://" + domain + "." + ENVIRONMENT + ".testing.com").
                port(8443).
                basePath("/automationservice/1").
                relaxedHTTPSValidation().
                header("Accept", "application/json").
                header("X-ST-AuthType", "OAUTH2").
                contentType("application/json").
                auth().oauth2(accessToken);
    }
}
