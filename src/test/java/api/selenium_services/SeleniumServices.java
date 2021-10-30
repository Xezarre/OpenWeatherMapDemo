package api.selenium_services;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import utility.TestReporter;
import utility.Utility;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class SeleniumServices extends Utility {

    /**
     * Selenium Grid health check
     */
    public static Response gridHealthCheckRequestSpecification(String endpoint, ExtentTest logSuite) throws IOException {
        try {
            log4j.info("Get health check status");

            RestAssured.defaultParser = Parser.JSON;
            return given().
                    headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                    when().get(endpoint).
                    then().contentType(ContentType.JSON).extract().response();
        } catch (Exception e) {
            log4j.error("gridHealthCheckStatus method - ERROR - ", e);
            TestReporter.logFailBeforeSuite(logSuite, "gridHealthCheckStatus method - ERROR: " + e);
        }
        return null;
    }
}

