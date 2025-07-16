package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import base_Classes.Base_Page;

public class ExtentReportManager implements ITestListener {

    private static ThreadLocal<ExtentReports> extentReports = new ThreadLocal<>();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private String reportDir;

    private static final String BASE_REPORT_DIR = ".\\reports\\DeepFreezeTestReports";

    public static ExtentReports getReporter() {
        return extentReports.get();
    }

    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flushReport() {
        if (extentReports.get() != null) {
            extentReports.get().flush();
        }
    }

    @Override
    public void onStart(ITestContext testContext) {
        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String randomUUID = UUID.randomUUID().toString();

        new File(BASE_REPORT_DIR).mkdirs();
        reportDir = BASE_REPORT_DIR + "\\" + "Report-" + browser + "-" + timestamp + "-" + randomUUID;
        new File(reportDir).mkdirs();

        String repName = "Test-Report-" + browser + "-" + timestamp + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir + "\\" + repName);

        sparkReporter.config().setDocumentTitle(browser + " Automation Report");
        sparkReporter.config().setReportName("Functional Testing on " + browser);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setCss(".badge-primary{background-color:#fd3259}");
        sparkReporter.config().setJs("document.getElementsByClassName('logo')[0].style.display='none';");
        sparkReporter.viewConfigurer().viewOrder().as(new ViewName[]{
                ViewName.DASHBOARD,
                ViewName.CATEGORY,
                ViewName.EXCEPTION,
                ViewName.TEST,
                ViewName.DEVICE,
                ViewName.AUTHOR,
        }).apply();

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extentReports.set(extent);

        extent.setSystemInfo("Browser", browser);
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "RamQA");

        String os = testContext.getCurrentXmlTest().getParameter("os");
        extent.setSystemInfo("Operating System", os);

        List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
        if (!includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = getReporter()
                .createTest(result.getTestClass().getName() + " - " + result.getMethod().getMethodName());
        test.assignCategory(result.getMethod().getGroups());
        test.info(result.getMethod().getDescription());
        test.info("Test started at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getTest().log(Status.PASS, result.getName() + " executed successfully.");
        getTest().info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = getTest();
        test.log(Status.FAIL, result.getName() + " failed");
        test.log(Status.INFO, result.getThrowable().getMessage());
        test.info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        try {
            String imgPath = new Base_Page().captureScreen(result.getName(), reportDir);
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        getTest().log(Status.SKIP, result.getName() + " got skipped");
        getTest().log(Status.INFO, result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason");
        getTest().info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onFinish(ITestContext testContext) {
        flushReport();

        String repName = "Test-Report-" + testContext.getCurrentXmlTest().getParameter("browser") +
                "-" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".html";
        File reportFile = new File(reportDir + "\\" + repName);

        System.out.println("📄 Report file path: " + reportFile.getAbsolutePath());

        if (reportFile.exists()) {
            System.out.println("✅ Report file exists. Opening...");
            try {
                Desktop.getDesktop().browse(reportFile.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Report file not found.");
        }
    }
}
