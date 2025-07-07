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

public class ExtentReportManager implements ITestListener {
    private static ThreadLocal<ExtentReports> extentReports = new ThreadLocal<>();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private String reportDir;

    // Define the base report directory
    private static final String BASE_REPORT_DIR = ".\\reports\\DeepFreezeTestReports";

    @Override
    public void onStart(ITestContext testContext) {
        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String randomUUID = UUID.randomUUID().toString();

        // Create the base report directory if it doesn't exist
        new File(BASE_REPORT_DIR).mkdirs();

        // Create a unique report folder within the base directory
        reportDir = BASE_REPORT_DIR + "\\" + "Report-" + browser + "-" + timestamp + "-" + randomUUID;
        new File(reportDir).mkdirs();

        // Keep the report name without the UUID
        String repName = "Test-Report-" + browser + "-" + timestamp + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir + "\\" + repName);
        
        // Basic report configuration
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
        
        // Store the ExtentReports instance in ThreadLocal
        extentReports.set(extent);

        // Adding system information to the report
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
        ExtentTest test = extentReports.get().createTest(result.getTestClass().getName() + " - " + result.getMethod().getMethodName());
        test.assignCategory(result.getMethod().getGroups());
        test.info(result.getMethod().getDescription());
        test.info("Test started at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, result.getName() + " executed successfully.");
        extentTest.get().info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        test.log(Status.FAIL, result.getName() + " failed");
        test.log(Status.INFO, result.getThrowable().getMessage());
        test.info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        try {
            String imgPath = new base_Classes.Base_Page().captureScreen(result.getName(), reportDir);
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, result.getName() + " got skipped");
        extentTest.get().log(Status.INFO, result.getThrowable().getMessage());
        extentTest.get().info("Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onFinish(ITestContext testContext) {
        // Flush the reports for each thread
        extentReports.get().flush();

        // Construct the report file name and path
        String repName = "Test-Report-" + testContext.getCurrentXmlTest().getParameter("browser") +
                         "-" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".html";
        File reportFile = new File(reportDir + "\\" + repName);

        // Log the report file path
        System.out.println("Report file path: " + reportFile.getAbsolutePath());

        // Check if the report file exists
        if (reportFile.exists()) {
            System.out.println("Report file exists. Attempting to open...");
            try {
                Desktop.getDesktop().browse(reportFile.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Report file does not exist. Please check the path.");
        }
    }
}
