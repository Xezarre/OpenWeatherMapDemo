package utility.webdrivers;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;

public class DriverFactory {

    public static RemoteWebDriver createInstance(String browser, ExtentTest logTest) {

        LocalDriver driver = new LocalDriver();
        try {
            return driver.initialDriver(browser, logTest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RemoteWebDriver createInstanceGrid(String browser, ExtentTest logTest) {
        GridDriver driver = new GridDriver();
        try {
            return driver.initialDriver(browser, logTest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
