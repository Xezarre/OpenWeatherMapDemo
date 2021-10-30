package utility.webdrivers;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import utility.TestReporter;
import utility.Utility;

import java.io.IOException;
import java.net.URL;

import static common.GlobalVariables.RUN_ON;

public class GridDriver extends Utility {

    public RemoteWebDriver webDriver;

    public synchronized RemoteWebDriver initialDriver(String browser, ExtentTest logTest) throws IOException {
        try {
            String hub = "http://sel-hub-1.qa.testing.com:4444/wd/hub";
            TestReporter.logInfo(logTest,"Run On Selenium Grid: " + hub);
            DesiredCapabilities caps = new DesiredCapabilities();
            ChromeOptions chrome_options = new ChromeOptions();
            FirefoxOptions firefoxOptions = new FirefoxOptions();

            switch (browser.toLowerCase()) {
                case "chrome":
                    caps.setBrowserName("chrome");
                    chrome_options.addArguments("headless");
                    chrome_options.addArguments("--ignore-certificate-errors");
                    chrome_options.addArguments("--disable-dev-shm-usage");
                    caps.setCapability(ChromeOptions.CAPABILITY, chrome_options);
                    break;
                case "firefox":
                    caps.setBrowserName("firefox");
                    firefoxOptions.setHeadless(true);
                    caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                    break;
                case "safari":
                    caps.setBrowserName("safari");
                    break;
                default:
                    log4j.error("Our framework does not support this platform: " + browser);
                    TestReporter.logFail(logTest, "Our framework does not support this platform: " + browser);
                    break;
            }
            webDriver = new RemoteWebDriver(new URL(hub), caps);
            webDriver.setFileDetector(new LocalFileDetector());

        } catch (Exception e) {
            log4j.error("initialDriver method - Error: " + e);
            logTest.fail("initialDriver method - Error" + e);
        }
        return webDriver;
    }
}
