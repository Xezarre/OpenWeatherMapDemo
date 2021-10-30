package common;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static common.GlobalVariables.THREAD_COUNT;

public class SuiteAlterer implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {

        try {
            // Get XmlSuite
            XmlSuite suite = suites.get(0);

            // Override the thread count
            suite.setThreadCount(Integer.parseInt(THREAD_COUNT));
            System.out.println("threadCount is set to " + THREAD_COUNT);
            // Support running a list of test cases
            if (System.getProperty("testcaseList") != null && !System.getProperty("testcaseList").equalsIgnoreCase("")) {
                String testcaseList = System.getProperty("testcaseList");
                System.out.println("testcaseList: \n" + testcaseList);

                //Create an instance of XML Suite and assign a name for it.
                XmlSuite mySuite = new XmlSuite();
                mySuite.setName("MySuite");

                //Create an instance of XmlTest and assign a name for it.
                XmlTest myTest = new XmlTest(mySuite);
                myTest.setName("MyTest");

                //Create a list which can contain the classes that you want to run.
                List<XmlClass> myClasses = new ArrayList<XmlClass>();
                String[] testcases = testcaseList.trim().split("\\r?\\n");
                for (String tc: testcases) {
                    for (Class<?> clazz : find("com.modules")){
                        if(clazz.getSimpleName().contains(tc.trim())) {
                            myClasses.add(new XmlClass(clazz.getCanonicalName()));
                            System.out.println("Added TC to your suite: " + tc.trim());
                            break;
                        }
                    }
                }

                //Assign that to the XmlTest Object created earlier.
                myTest.setXmlClasses(myClasses);

                //Create a list of XmlTests and add the Xmltest you created earlier to it.
                List<XmlTest> myTests = new ArrayList<XmlTest>();
                myTest.setParallel(XmlSuite.ParallelMode.CLASSES);
                myTests.add(myTest);

                suite.setTests(myTests);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static List<Class<?>> find(String scannedPackage) throws UnsupportedEncodingException {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(URLDecoder.decode(scannedUrl.getFile().toString(), StandardCharsets.UTF_8.toString()));
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }
        return classes;
    }

    private static List<Class<?>> find(File file, String scannedPackage) {

        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

}
