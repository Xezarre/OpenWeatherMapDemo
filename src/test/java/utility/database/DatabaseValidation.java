package utility.database;

import com.aventstack.extentreports.ExtentTest;
import dataobjects.ObjectInfo;
import utility.Assertion;
import utility.TestReporter;
import utility.Utility;

import java.util.HashMap;

public class DatabaseValidation extends Utility {


    /**
     * A sample method for validating against DB
     */
    public void checkInfo(HashMap claimInfo, ObjectInfo o, ExtentTest logTest) {
        checkInfo(claimInfo, o.getStatus(), o.getStatusDetail(), o.getResolutionType(), o.getResolutionAgent(),
                o.getProcessType(), o.getShippingMethod(), o.getShippingStatus(), o.getShippedTo(), o.getShippedFrom(), o.getResolutionTypeChangeReason(), logTest);
    }

    /**
     * A sample method for validating against DB
     */
    public void checkInfo(HashMap claimInfo, String status, String statusDetail, String resolutionType, String resolutionAgent,
                               String processType, String shippingMethod, String shippingStatus, String shippedTo, String shippedFrom, String resolutionTypeChangeReason, ExtentTest logTest) {
        try {
            log4j.debug("Check Claim Info...starts");
            claimInfo = convertTableColumnNameToCamelCase(claimInfo, logTest);
            System.out.println("Query's result: " + claimInfo);

            if (status != null) {
                TestReporter.logInfo(logTest, "Verify Claim status: " + status);
                Assertion.verifyExpectedAndActualResults(logTest, status, claimInfo.get("status").toString());
            }

            if (statusDetail != null) {
                TestReporter.logInfo(logTest, "Verify Claim status detail: " + statusDetail);
                Assertion.verifyExpectedAndActualResults(logTest, statusDetail, claimInfo.get("statusDetail").toString());
            }

            if (resolutionType != null) {
                TestReporter.logInfo(logTest, "Verify Claim resolution type: " + resolutionType);
                Assertion.verifyExpectedAndActualResults(logTest, resolutionType, claimInfo.get("resolutionType").toString());
            }

            if (resolutionAgent != null) {
                TestReporter.logInfo(logTest, "Verify Claim resolution agent: " + resolutionAgent);
                Assertion.verifyExpectedAndActualResults(logTest, resolutionAgent, claimInfo.get("resolutionAgentName").toString());
            }

            if (processType != null) {
                TestReporter.logInfo(logTest, "Verify Claim process type: " + processType);
                Assertion.verifyExpectedAndActualResults(logTest, processType, claimInfo.get("processType").toString());
            }

            if (shippingMethod != null) {
                TestReporter.logInfo(logTest, "Verify Shipping method:");
                Assertion.verifyExpectedAndActualResults(logTest, shippingMethod, claimInfo.get("shippingMethod").toString());
            }

            if (shippingStatus != null) {
                TestReporter.logInfo(logTest, "Verify Shipping status:");
                Assertion.verifyExpectedAndActualResults(logTest, shippingStatus, claimInfo.get("shippingStatus").toString());
            }

            if (shippedTo != null) {
                TestReporter.logInfo(logTest, "Verify Shipping shipped to:");
                Assertion.verifyExpectedAndActualResults(logTest, shippedTo, claimInfo.get("shippedTo").toString());
            }

            if (shippedFrom != null) {
                TestReporter.logInfo(logTest, "Verify Shipping shipped from:");
                Assertion.verifyExpectedAndActualResults(logTest, shippedFrom, claimInfo.get("shippedFrom").toString());
            }

            if (resolutionTypeChangeReason != null) {
                TestReporter.logInfo(logTest, "Verify Claim resolution type change reason::");
                Assertion.verifyExpectedAndActualResults(logTest, resolutionTypeChangeReason, claimInfo.get("resolutionTypeChangeReason").toString());
            }

            log4j.debug("Check Claim Info...ends");

        } catch (Exception e) {
            log4j.error("checkClaimInfo method - ERROR - ", e);
            TestReporter.logException(logTest, "checkClaimInfo method - ERROR", e);
        }
    }
}
