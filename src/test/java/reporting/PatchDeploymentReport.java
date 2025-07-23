package reporting;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import base_Classes.Base_Page;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class PatchDeploymentReport extends Base_Page implements ISuiteListener, ITestListener {
	private static ThreadLocal<ExtentReports> extentReports = new ThreadLocal<>();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private String reportDir;

    private final Map<String, Map<String, String>> resultMatrix = new LinkedHashMap<>();
    private final Map<String, String> failureReasons = new HashMap<>();
    private final Map<String, String> failureScreenshots = new HashMap<>();
    private final Map<ITestResult, String> logMap = new HashMap<>();
    private final Set<String> allServers = new TreeSet<>();
    private File reportFolder;

    @Override
    public void onStart(ISuite suite) {
        resultMatrix.clear();
        allServers.clear();
        failureReasons.clear();
        failureScreenshots.clear();
        logMap.clear();
        
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("🛠️ Suite finished, generating custom HTML report...");
        generateHtmlReport();
        File latestReport = getLatestReportFile(reportFolder);
        if (latestReport != null) {
            sendEmailWithReport(latestReport);
            
        }

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        captureStepLogs(result);
        storeResult(result, "Pass", null, null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        captureStepLogs(result);
        String testName = getCurrentTestName(result);
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        String screenshotPath = captureScreenshot(testName);
        storeResult(result, "Fail", errorMessage, screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        storeResult(result, "Skip", null, null);
    }

    private void captureStepLogs(ITestResult result) {
        Base_Page base = new Base_Page();
        List<String> logs = base.getTestLogs();
        StringBuilder stepLogBuilder = new StringBuilder();
        for (String log : logs) {
            stepLogBuilder.append(log).append("<br>");
        }
        logMap.put(result, stepLogBuilder.toString());
        base.clearTestLogs();
    }

    private void storeResult(ITestResult result, String status, String failureReason, String screenshotPath) {
        String testName = getCurrentTestName(result);
        String server = extractServerFromUrl();

        allServers.add(server);
        resultMatrix.putIfAbsent(testName, new LinkedHashMap<>());
        resultMatrix.get(testName).put(server, status);

        if ("Fail".equalsIgnoreCase(status)) {
            failureReasons.put(testName + "_" + server, failureReason);
            failureScreenshots.put(testName + "_" + server, screenshotPath);
        }
    }

    private String getCurrentTestName(ITestResult result) {
        try {
            Object instance = result.getInstance();
            Field field = instance.getClass().getDeclaredField("currentTestName");
            field.setAccessible(true);
            Object value = field.get(instance);
            if (value != null && !value.toString().trim().isEmpty()) {
                return value.toString();
            }
        } catch (Exception e) {
            // Fallback handled below
        }
        return result.getMethod().getMethodName(); // fallback
    }

    private String extractServerFromUrl() {
        try {
            String url = Base_Page.driver.get().getCurrentUrl();
            URL parsedUrl = new URL(url);
            return parsedUrl.getHost().split("\\.")[0];
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String captureScreenshot(String testName) {
        try {
            File src = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if (reportFolder == null) {
                reportFolder = new File("F:\\Automation Work 2024\\2025\\Start_From_Basic\\CustomReport\\Report_" + timestamp);
                reportFolder.mkdirs();
            }
            File screenshotFile = new File(reportFolder, testName + "_" + timestamp + ".png");
            FileUtils.copyFile(src, screenshotFile);
            return screenshotFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void generateHtmlReport() {
        Set<String> testCases = new LinkedHashSet<>(resultMatrix.keySet());

        long passCount = resultMatrix.values().stream()
                .flatMap(m -> m.values().stream())
                .filter("Pass"::equalsIgnoreCase).count();

        long failCount = resultMatrix.values().stream()
                .flatMap(m -> m.values().stream())
                .filter("Fail"::equalsIgnoreCase).count();

        long totalCount = passCount + failCount;
        int successRate = totalCount > 0 ? (int) ((passCount * 100) / totalCount) : 0;

        StringBuilder serverHeaders = new StringBuilder();
        for (String server : allServers) {
            serverHeaders.append("<th>").append(server).append("</th>");
        }

        StringBuilder rows = new StringBuilder();
        StringBuilder modals = new StringBuilder();
        int modalCounter = 0;

        for (String testCase : testCases) {
            rows.append("<tr><td>").append(testCase).append("</td>");
            for (String server : allServers) {
                String result = resultMatrix.getOrDefault(testCase, new HashMap<>())
                        .getOrDefault(server, "-");
                String cssClass = switch (result.toLowerCase()) {
                    case "pass" -> "pass";
                    case "fail" -> "fail";
                    case "skip" -> "skip";
                    default -> "";
                };

                String cellContent = result;
                if ("fail".equalsIgnoreCase(result)) {
                    String key = testCase + "_" + server;
                    String reason = failureReasons.getOrDefault(key, "No Reason Captured");
                    String screenshot = failureScreenshots.get(key);
                    String modalId = "modal" + (++modalCounter);

                    cellContent += "<br><a href='#' onclick=\"showModal('" + modalId + "')\">View Details</a>";

                    modals.append("<div id='").append(modalId).append("' class='modal'>")
                            .append("<div class='modal-content'>")
                            .append("<span class='close' onclick=\"closeModal('").append(modalId).append("')\">&times;</span>")
                            .append("<h3>Error Details</h3>")
                            .append("<div style='max-height:200px; overflow-y:auto; white-space:pre-wrap; background:#f4f4f4; padding:10px; border:1px solid #ccc; font-family:monospace;'>")
                            .append("<strong>Error Log:</strong>\n")
                            .append(reason)
                            .append("</div><br>");
                    if (screenshot != null) {
                        modals.append("<p><strong>Screenshot:</strong></p>")
                                .append("<div style='max-height:400px; overflow:auto; text-align:center;'>")
                                .append("<img src='./").append(screenshot)
                                .append("' style='max-width:100%; height:auto; border-radius:10px;'>")
                                .append("</div>");
                    }
                    modals.append("</div></div>");
                }

                rows.append("<td class='").append(cssClass).append("'>").append(cellContent).append("</td>");
            }
            rows.append("</tr>\n");
        }

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        String html = String.format("""
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                <meta charset=\"UTF-8\" />
                <title>Post Update Patch Deployment</title>
                <style>
                body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; }
                .container { padding: 20px; }
                .header { background-color: #2c3e50; color: white; text-align: center; padding: 25px 0; border-radius: 10px 10px 0 0; }
                .summary { display: flex; justify-content: space-around; background-color: #ffffff; padding: 20px; border-bottom: 2px solid #ecf0f1; }
                .card { background-color: #ecf0f1; padding: 15px 20px; border-radius: 10px; text-align: center; width: 20%%; }
                .card h2 { margin: 0; color: #2c3e50; }
                .card p { margin: 5px 0 0 0; color: #7f8c8d; font-weight: bold; }
                table { width: 100%%; border-collapse: collapse; background-color: white; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
                th { background-color: #2c3e50; color: white; }
                .pass { background-color: #2ecc71; color: white; font-weight: bold; }
                .fail { background-color: #e74c3c; color: white; font-weight: bold; }
                .skip { background-color: orange; color: white; font-weight: bold; }
                .footer { background-color: #2c3e50; color: white; text-align: center; padding: 10px; border-radius: 0 0 10px 10px; }
                .legend { margin-top: 10px; }
                .legend span { display: inline-block; padding: 5px 10px; border-radius: 5px; margin: 0 5px; color: white; font-weight: bold; }
                .legend .pass { background-color: #2ecc71; }
                .legend .fail { background-color: #e74c3c; }
                .legend .skip { background-color: orange; }
                .modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%%; height: 100%%; background-color: rgba(0,0,0,0.4); }
                .modal-content { background-color: #fff; margin: 5%% auto; padding: 20px; border-radius: 10px; width: 90%%; position: relative; }
                .close { position: absolute; right: 15px; top: 10px; font-size: 28px; font-weight: bold; cursor: pointer; }
                </style>
                <script>
                function showModal(id) { document.getElementById(id).style.display = 'block'; }
                function closeModal(id) { document.getElementById(id).style.display = 'none'; }
                window.onclick = function(event) {
                    if (event.target.classList.contains('modal')) {
                        event.target.style.display = 'none';
                    }
                }
                </script>
                </head>
                <body>
                <div class='container'>
                <div class='header'>
                <h1>✅ Automation Report - Deep Freeze Cloud</h1>
                <h3>Production Test Results</h3>
                <p>Test Date: %s | Test Time: %s</p>
                </div>
                <div class='summary'>
                <div class='card'><h2>%d</h2><p>Tests Passed</p></div>
                <div class='card'><h2>%d</h2><p>Tests Failed</p></div>
                <div class='card'><h2>%d</h2><p>Total Tests</p></div>
                <div class='card'><h2>%d%%</h2><p>Success Rate</p></div>
                </div>
                <table><thead><tr><th>Test Case</th>%s</tr></thead><tbody>%s</tbody></table>
                <div class='footer'><p><strong>Test Environment:</strong> Production Environment</p>
                <p><strong>Patch Version:</strong> Latest Update Patch</p>
                <div class='legend'>
                <span class='pass'>Pass</span><span class='fail'>Fail</span><span class='skip'>Skip</span></div></div>
                %s
                </div>
                </body>
                </html>
                """, date, time, passCount, failCount, totalCount, successRate, serverHeaders, rows, modals);

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if (reportFolder == null) {
                reportFolder = new File("F:\\Automation Work 2024\\2025\\Start_From_Basic\\CustomReport\\Report_" + timestamp);
                reportFolder.mkdirs();
            }
            File reportFile = new File(reportFolder, "PostUpdatePatchDeploymentReport_" + timestamp + ".html");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                writer.write(html);
            }
            System.out.println("✅ Report generated at: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onTestFailedWithTimeout(ITestResult result) {}

private void sendEmailWithReport(File reportFile) {
    // SMTP config
    final String from = "sonu2010dhiman@gmail.com";
    final String password = "gycw idcf fuyv cglp"; // App password or normal password
    final String to = "ramdhiman222@gmail.com,rammehard@alohatechnology.com";
    final String subject = "✅ Automation Report - Deep Freeze Cloud";
    final String body = "Hello Team,<br><br>Please find the attached automation report for Patch Deployment.<br><br>Regards,<br>Automation Team";

    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com"); // or your SMTP server
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, password);
        }
    });

    try {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        for (String recipient : to.split(",")) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()));
        }
        message.setSubject(subject);
        message.setContent(body, "text/html");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(reportFile);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("📧 Email sent successfully to: " + to);
    } catch (Exception e) {
        System.out.println("❌ Email sending failed!");
        e.printStackTrace();
    }
}

private File getLatestReportFile(File folder) {
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".html"));
    if (files == null || files.length == 0) return null;
    return Arrays.stream(files).max(Comparator.comparingLong(File::lastModified)).orElse(null);
}
public static PatchDeploymentReport getReporter() {
    return new PatchDeploymentReport(); // or return singleton if needed
}


}

