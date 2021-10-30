package common;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static common.GlobalVariables.RETRY_FAILED_TESTS;

public class TestNGAnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        RETRY_FAILED_TESTS = System.getProperty("retryFailedTests") == null ? "No" : System.getProperty("retryFailedTests");
        if (RETRY_FAILED_TESTS.equalsIgnoreCase("Yes")) {
            annotation.setRetryAnalyzer(TestNGRetryAnalyzer.class);
        }
    }
}