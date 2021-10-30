package common;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class TestNGRetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private int maxRetry = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (result.isSuccess()) {
            result.setStatus(ITestResult.SUCCESS);
        } else {
            if (retryCount < maxRetry) {
                retryCount++;
                result.setStatus(ITestResult.FAILURE);
                return true;
            } else {
                result.setStatus(ITestResult.FAILURE);
                retryCount = 0;
            }
        }
        return false;
    }
}