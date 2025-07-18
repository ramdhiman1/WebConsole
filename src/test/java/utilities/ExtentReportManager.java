package utilities;

import java.awt.Desktop;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.testng.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.*;

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

        File reportFolder = new File(reportDir);
        System.out.println("📁 Report directory path: " + reportFolder.getAbsolutePath());

        if (reportFolder.exists()) {
            System.out.println("✅ Report folder exists. Opening...");
            try {
                Desktop.getDesktop().open(reportFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // ✅ Zip and email the folder
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

    // ✅ Zip folder utility
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

    // ✅ Email sender for zip
    private void sendEmailWithZip(String zipFilePath) {
        final String fromEmail = "sonu2010dhiman@gmail.com"; // ✅ replace
        final String password = "gycw idcf fuyv cglp";     // ✅ use Gmail App Password
        final String toEmail = "ramdhiman222@gmail.com"; // ✅ replace

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

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi,\n\nPlease find the attached ZIP of the automation report.\n\nRegards,\nAutomation Team");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(zipFilePath));

            Multipart multipart = new MimeMultipart();
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
}
