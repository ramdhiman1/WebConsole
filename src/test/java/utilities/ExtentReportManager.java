package utilities;

import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.testng.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.*;

import base_Classes.Base_Page;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

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
        String url = Base_Page.currentURL; // You already store this from the test method
        String testName = extractServerFromUrl(url); // Get www2, www3 etc.

        if (testName == null || testName.isEmpty()) {
            testName = result.getMethod().getMethodName(); // fallback
        }

        ExtentTest test = getReporter().createTest(testName); // set readable name
        test.assignCategory(result.getMethod().getGroups());
        test.info("🟢 Test started at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        setTest(test); // ThreadLocal for ExtentTest
    }

    private String extractServerFromUrl(String url) {
        if (url == null || url.isEmpty()) return "unknown";
        try {
            URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost(); // www2.faronicsbeta.com
            if (host.contains("www")) {
                return host.substring(0, host.indexOf('.')); // returns "www2"
            }
            return host; // fallback
        } catch (Exception e) {
            return "unknown";
        }
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        log(Status.PASS, "✅ Test Passed: " + result.getName());
        log(Status.INFO, "🕔 Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log(Status.FAIL, "❌ Test Failed: " + result.getName());
        log(Status.FAIL, "📌 Reason: " + result.getThrowable().getMessage());
        log(Status.INFO, "🕔 Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        try {
            String imgPath = new Base_Page().captureScreen(result.getName(), reportDir);
            getTest().addScreenCaptureFromPath(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log(Status.SKIP, "⚠️ Test Skipped: " + result.getName());
        if (result.getThrowable() != null) {
            log(Status.INFO, "ℹ️ Reason: " + result.getThrowable().getMessage());
        }
        log(Status.INFO, "🕔 Test ended at: " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
    }

    @Override
    public void onFinish(ITestContext testContext) {
        flushReport();

        File reportFolder = new File(reportDir);
        System.out.println("📁 Report directory path: " + reportFolder.getAbsolutePath());

        if (reportFolder.exists()) {
            System.out.println("✅ Report folder exists. Opening...");
            try {
                Desktop.getDesktop().open(reportFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String zipPath = reportDir + ".zip";
            try {
                zipFolder(reportFolder.getAbsolutePath(), zipPath);
                sendEmailWithZip(zipPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("❌ Report folder not found.");
        }
    }

    public static void zipFolder(String sourceDirPath, String zipFilePath) throws IOException {
        Path zipPath = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }

    private void sendEmailWithZip(String zipFilePath) {
        final String fromEmail = "sonu2010dhiman@gmail.com"; // ✅ Replace
        final String password = "gycw idcf fuyv cglp";       // ✅ Use app password
        final String toEmail = "ramdhiman2222@gmail.com";     // ✅ Replace

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("📦 Automation Report ZIP - DeepFreeze");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi,\n\nPlease find the attached ZIP of the automation report.\n\nRegards,\nAutomation Team");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(zipFilePath));

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("📧 Email with ZIP sent successfully!");

        } catch (Exception e) {
            System.out.println("❌ Failed to send email with ZIP.");
            e.printStackTrace();
        }
    }

    // ✅ Styled log to console + ExtentReport
    public static void log(Status status, String message) {
        System.out.println("[" + status + "] " + message);

        ExtentTest test = getTest();
        if (test != null) {
            String styled = MarkupHelper.createLabel(message, getColor(status)).getMarkup();
            test.log(status, styled);
        } else {
            System.err.println("⚠️ No ExtentTest found to log to report.");
        }
    }

    // ✅ Plain log (without color styling)
    public static void logToReport(Status status, String message) {
        System.out.println("[LOG] " + message);
        ExtentTest test = getTest();
        if (test != null) {
            test.log(status, message);
        } else {
            System.err.println("⚠️ No test instance found for ExtentReport logging.");
        }
    }

    private static ExtentColor getColor(Status status) {
        switch (status) {
            case PASS:
                return ExtentColor.GREEN;
            case FAIL:
                return ExtentColor.RED;
            case SKIP:
                return ExtentColor.ORANGE;
            case WARNING:
                return ExtentColor.YELLOW;
            default:
                return ExtentColor.BLUE;
        }
    }
}
