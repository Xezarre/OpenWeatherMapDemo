package utility;

import com.aventstack.extentreports.ExtentTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader extends Utility {

    private Properties prop = null;

    //Constructor for ConfigFileReader class
    public ConfigFileReader(String path, ExtentTest logTestForTestBase) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(path);
            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            log4j.error("ConfigFileReader method - ERROR - " + e);
            TestReporter.logException(logTestForTestBase, "ConfigFileReader method - ERROR", e);
        }
    }

    public ConfigFileReader(String path) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(path);
            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            log4j.error("ConfigFileReader method - ERROR - " + e);
        }
    }

    /*
     *  This method is used to get config values from configuration.properties file
     *  Input: config parameter name
     *  Output: config parameter value
     */
    public String getDataFromConfigurationFile(String configParameter) {
        String configValue = null;
        try {
            configValue = prop.getProperty(configParameter).trim();

        } catch (Exception e) {
            log4j.error("getDataFromConfigurationFile method - ERROR - " + e);
        }
        return configValue;
    }
}
