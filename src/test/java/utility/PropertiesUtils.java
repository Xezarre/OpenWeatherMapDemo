package utility;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtils {

    private static final String TEST_CONFIGURATION = System.getProperty("user.dir").concat("/resources/configuration/Configuration.properties");
    private static Properties prop = null;

    private static Properties getProp(String path) {
        try {
            System.out.printf("Loading properties : %s%n", path);
            FileInputStream fis = new FileInputStream(path);
            Properties prop = new Properties();
            prop.load(fis);
            return prop;
        } catch (Exception e) {
            System.err.printf("%s not found !%n", path);
        }
        return null;
    }

    public static String getPropValue(String key) {
        return getPropValue(key, null);
    }

    public static int getInt(String key) {
        String value = getPropValue(key, null);
        return Integer.parseInt(value);
    }

    public static String getPropValue(String key, String defaultValue) {
        if (System.getProperty(key) != null)
            return System.getProperty(key).trim();

        if (prop == null) {
            prop = getProp(TEST_CONFIGURATION);
        }

        if (prop != null && prop.containsKey(key))
            return prop.getProperty(key).trim();

        return defaultValue;
    }
}
