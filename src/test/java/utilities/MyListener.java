package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import reporting.PatchDeploymentReport;

public class MyListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        String msg = "🚀 Test Execution Started for Suite: " + context.getSuite().getName();
        System.out.println(msg);

        // Initialize ExtentReports and PatchDeploymentReport
        ExtentReportManager.getReporter();
        PatchDeploymentReport.getReporter();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getTestClass().getName() + " : " + result.getMethod().getMethodName();
        System.out.println("🧪 Test Started: " + testName);

        // Create ExtentTest and PatchDeployment test instance
        ExtentTest test = ExtentReportManager.getReporter().createTest(testName);
        ExtentReportManager.setTest(test);
        PatchDeploymentReport.getReporter(); // Optional: Add test-level tracking if needed

        ExtentReportManager.log(Status.INFO, "🧪 Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("✅ Test Passed: " + testName);

        PatchDeploymentReport.getReporter(); // Optional: If logging success/final summary

        ExtentReportManager.log(Status.PASS, "✅ Test Passed: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "No Exception";

        System.out.println("❌ Test Failed: " + testName);
        System.out.println("   ↳ Reason: " + errorMessage);

        PatchDeploymentReport.getReporter(); // Optional: If capturing screenshot path or HTML block

        ExtentReportManager.log(Status.FAIL, "❌ Test Failed: " + testName);
        ExtentReportManager.log(Status.FAIL, "📌 Exception: " + errorMessage);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("⚠️ Test Skipped: " + testName);

        PatchDeploymentReport.getReporter(); // Optional for skipped tests logging

        ExtentReportManager.log(Status.SKIP, "⚠️ Test Skipped: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        String msg = "🏁 Test Execution Completed for Suite: " + context.getSuite().getName();
        System.out.println(msg);

        PatchDeploymentReport.getReporter(); // Optional: Generate summary or flush data

        ExtentReportManager.log(Status.INFO, msg);
        ExtentReportManager.flushReport(); // Final flush of report
    }
}
