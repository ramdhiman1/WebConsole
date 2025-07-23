package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class MyListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        String msg = "🚀 Test Execution Started for Suite: " + context.getSuite().getName();
        System.out.println(msg);
        ExtentReportManager.getReporter(); // Initialize ExtentReports
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getTestClass().getName() + " : " + result.getMethod().getMethodName();
        System.out.println("🧪 Test Started: " + testName);

        ExtentTest test = ExtentReportManager.getReporter().createTest(testName);
        ExtentReportManager.setTest(test);
        ExtentReportManager.log(Status.INFO, "🧪 Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("✅ Test Passed: " + testName);
        ExtentReportManager.log(Status.PASS, "✅ Test Passed: " + testName);
    }

    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "No Exception";

        System.out.println("❌ Test Failed: " + testName);
        System.out.println("   ↳ Reason: " + errorMessage);

        ExtentReportManager.log(Status.FAIL, "❌ Test Failed: " + testName);
        ExtentReportManager.log(Status.FAIL, "📌 Exception: " + errorMessage);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("⚠️ Test Skipped: " + testName);
        ExtentReportManager.log(Status.SKIP, "⚠️ Test Skipped: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        String msg = "🏁 Test Execution Completed for Suite: " + context.getSuite().getName();
        System.out.println(msg);
        ExtentReportManager.log(Status.INFO, msg);
        ExtentReportManager.flushReport(); // Final flush of report
    }
}
