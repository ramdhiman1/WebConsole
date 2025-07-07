package utilities;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Attachment;

public class AllureListener implements ITestListener {

    /**
     * Retrieves the test method name from the ITestResult.
     */
    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    /**
     * Attaches a screenshot to Allure reports for failed or skipped tests.
     */
    @Attachment(value = "Error Screenshot", type = "image/png")
    public byte[] saveFailureScreenshot(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attaches a text log to Allure reports.
     */
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("Test Suite started: " + iTestContext.getName());
        saveTextLog("Test Suite started: " + iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("Test Suite finished: " + iTestContext.getName());
        saveTextLog("Test Suite finished: " + iTestContext.getName());
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("Starting test: " + getTestMethodName(iTestResult));
        saveTextLog("Test started: " + getTestMethodName(iTestResult));
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("Test passed: " + getTestMethodName(iTestResult));
        saveTextLog("Test passed: " + getTestMethodName(iTestResult));
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("Test failed: " + getTestMethodName(iTestResult));

        // Retrieve the WebDriver instance from Base_Page
        WebDriver driver = base_Classes.Base_Page.getDriver();

        // Attach screenshot and logs
        if (driver != null) {
            System.out.println("Capturing screenshot for failed test: " + getTestMethodName(iTestResult));
            saveFailureScreenshot(driver);
        } else {
            System.err.println("WebDriver instance is null. Cannot capture screenshot.");
        }

        saveTextLog("Test failed: " + getTestMethodName(iTestResult) + "\nException: " +
                (iTestResult.getThrowable() != null ? iTestResult.getThrowable().getMessage() : "No exception provided."));
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("Test skipped: " + getTestMethodName(iTestResult));

        // Retrieve the WebDriver instance from Base_Page
        WebDriver driver = base_Classes.Base_Page.getDriver();

        // Attach screenshot and logs for skipped tests
        if (driver != null) {
            System.out.println("Capturing screenshot for skipped test: " + getTestMethodName(iTestResult));
            saveFailureScreenshot(driver);
        } else {
            System.err.println("WebDriver instance is null. Cannot capture screenshot.");
        }

        saveTextLog("Test skipped: " + getTestMethodName(iTestResult) + "\nReason: " +
                (iTestResult.getThrowable() != null ? iTestResult.getThrowable().getMessage() : "No reason provided."));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test failed but within success percentage: " + getTestMethodName(iTestResult));
        saveTextLog("Test failed but within success percentage: " + getTestMethodName(iTestResult));
    }
}
