package core.utils;

import io.qameta.allure.testng.AllureTestNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestNG;

public class TestListener implements ISuiteListener, ITestListener {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    static {
        TestNG testng = new TestNG();
        testng.addListener((ITestNGListener) new AllureTestNg());
    }
    @Override
    public void onStart(ISuite suite) {
        log.info("Initializing Allure environment");
        AllureUtils.cleanAllureResultsDirectory();
        AllureUtils.createAllureResultsDirectory();
    }

    @Override
    public void onTestStart(ITestResult result) {
        AllureParams.report(result.getParameters(),
                result.getMethod().getConstructorOrMethod().getMethod());
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("Finalizing Allure reporting");
        AllureUtils.logAllureResultsStatus();
        AllureUtils.generateAndOpenAllureReport();
    }
}