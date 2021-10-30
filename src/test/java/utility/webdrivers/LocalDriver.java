package utility.webdrivers;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import utility.TestReporter;
import utility.Utility;

import java.io.IOException;

import static common.GlobalVariables.*;

public class LocalDriver extends Utility {

    public RemoteWebDriver webDriver;

    public synchronized RemoteWebDriver initialDriver(String browser, ExtentTest logTest) throws IOException {

        try {
            // Set chrome driver path
            if (OS_NAME.contains("mac")) {
                System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_MAC);
                System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_MAC);
            } else if (OS_NAME.contains("windows")) {
                System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_WIN);
                System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_WIN);
            }

            ChromeOptions chrome_options = new ChromeOptions();
            switch (browser.toLowerCase()) {
                case "chrome":
                    chrome_options.addArguments("--ignore-certificate-errors");
                    chrome_options.addArguments("--disable-notifications");
                    webDriver = new ChromeDriver(chrome_options);
                    break;
                case "chromeheadless":
                    chrome_options.addArguments("headless");
                    chrome_options.addArguments("--ignore-certificate-errors");
                    chrome_options.addArguments("--disable-dev-shm-usage");
                    chrome_options.addArguments("--disable-notifications");
                    chrome_options.addArguments("window-size=1280,1024");
                    webDriver = new ChromeDriver(chrome_options);
                    break;
                case "firefox":
                    webDriver = new FirefoxDriver();
                    break;
                case "safari":
                    webDriver = new SafariDriver();
                    break;
                default:
                    log4j.error("Our framework does not support this platform: " + browser);
                    TestReporter.logFail(logTest, "Our framework does not support this platform: " + browser);
                    break;
            }
        } catch (Exception e) {
            log4j.error("initialDriver method - ERROR: " + e);
            TestReporter.logException(logTest, "initialDriver method - ERROR: ", e);
        }

        return webDriver;
    }
}
