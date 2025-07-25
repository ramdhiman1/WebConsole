package reporting;

import base_Classes.Base_Page;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Multipart;


import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomReportListener implements IReporter, ITestListener {

    private String reportDirPath;
    private final Map<ITestResult, String> screenshotMap = new HashMap<>();
    private final Map<ITestResult, String> errorMap = new HashMap<>();
    private final Map<ITestResult, String> browserMap = new HashMap<>();
    private final Map<ITestResult, String> osMap = new HashMap<>();

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        StringBuilder html = new StringBuilder();
        int totalTests = 0, passed = 0, failed = 0, skipped = 0;

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportDirPath = System.getProperty("user.dir") + File.separator + "reports"
                + File.separator + "CustomReports" + File.separator + "Report_" + timeStamp;
        new File(reportDirPath).mkdirs();

        for (ISuite suite : suites) {
            for (ISuiteResult result : suite.getResults().values()) {
                ITestContext context = result.getTestContext();
                totalTests += context.getPassedTests().size() + context.getFailedTests().size() + context.getSkippedTests().size();
                passed += context.getPassedTests().size();
                failed += context.getFailedTests().size();
                skipped += context.getSkippedTests().size();
            }
        }

        // HTML Header & Style
        html.append("<html><head><title>Test Report</title><style>")
                .append("body { font-family: Arial; background: #f4f7fa; padding: 20px; }")
                .append(".summary { display: flex; justify-content: space-around; margin-bottom: 20px; }")
                .append(".card { padding: 20px; border-radius: 8px; color: white; font-weight: bold; width: 20%; text-align: center; }")
                .append(".pass { background-color: #4CAF50; }")
                .append(".fail { background-color: #f44336; }")
                .append(".skip { background-color: #ff9800; }")
                .append(".total { background-color: #607d8b; }")
                .append("table { border-collapse: collapse; width: 100%; background: #fff; }")
                .append("th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }")
                .append("th { background-color: #3f51b5; color: white; }")
                .append("tr:hover { background-color: #f1f1f1; }")
                .append(".status-pass { color: green; font-weight: bold; }")
                .append(".status-fail { color: red; font-weight: bold; }")
                .append(".status-skip { color: orange; font-weight: bold; }")
                .append(".modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4); }")
                .append(".modal-content { background-color: #fefefe; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 90%; border-radius: 10px; }")
                .append(".close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }")
                .append(".close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }")
                .append("pre { background-color: #eee; padding: 10px; border-radius: 6px; color: #333; overflow-x: auto; max-height: 200px; }")
                .append("img { width: 100%; border-radius: 10px; border: 1px solid #ccc; margin-top: 15px; }")
                .append("</style></head><body>");

        html.append("<img src='logo.png' alt='Logo' style='max-width:200px;margin-bottom:20px;'>");
        html.append("<h2>🧪 DEEP FREEZE CLOUD - Automation Test Report</h2>");

        html.append("<div class='summary'>")
                .append("<div class='card total'>Total<br>").append(totalTests).append("</div>")
                .append("<div class='card pass'>Passed<br>").append(passed).append("</div>")
                .append("<div class='card fail'>Failed<br>").append(failed).append("</div>")
                .append("<div class='card skip'>Skipped<br>").append(skipped).append("</div>")
                .append("</div>");

        // Table headers with Serial Number
        html.append("<table><tr><th>#</th><th>Test Case</th><th>Test Class</th><th>Browser</th><th>OS</th><th>Status</th><th>Execution Time (s)</th><th>Action</th></tr>");

        int counter = 1;
        for (ITestResult result : screenshotMap.keySet()) {
            String methodName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();
            String browser = browserMap.getOrDefault(result, "N/A");
            String os = osMap.getOrDefault(result, System.getProperty("os.name"));
            long duration = result.getEndMillis() - result.getStartMillis();
            long seconds = (duration / 1000) % 60;
            long minutes = (duration / (1000 * 60)) % 60;
            String timeFormatted = String.format("%02d:%02d", minutes, seconds);
            String status = "PASS";
            String statusClass = "status-pass";

            if (result.getStatus() == ITestResult.FAILURE) {
                status = "FAIL";
                statusClass = "status-fail";
            } else if (result.getStatus() == ITestResult.SKIP) {
                status = "SKIPPED";
                statusClass = "status-skip";
            }

            html.append("<tr><td>").append(counter++).append("</td>")
                    .append("<td>").append(methodName).append("</td>")
                    .append("<td>").append(className).append("</td>")
                    .append("<td>").append(browser).append("</td>")
                    .append("<td>").append(os).append("</td>")
                    .append("<td class='").append(statusClass).append("'>").append(status).append("</td>")
                    .append("<td>").append(String.format("%.2f", seconds)).append("</td>");

            if (result.getStatus() == ITestResult.FAILURE) {
                String modalId = methodName + "Modal";
                html.append("<td><button onclick=\"document.getElementById('")
                        .append(modalId).append("').style.display='block'\">View</button></td></tr>");
                html.append("<div id='").append(modalId).append("' class='modal'>")
                        .append("<div class='modal-content'>")
                        .append("<span class='close' onclick=\"document.getElementById('")
                        .append(modalId).append("').style.display='none'\">&times;</span>")
                        .append("<h3 style='color:red;'>❌ Failure Reason</h3><pre>")
                        .append(errorMap.get(result))
                        .append("</pre><h3>📸 Screenshot</h3><img src='")
                        .append(screenshotMap.get(result))
                        .append("' /></div></div>");
            } else {
                html.append("<td>-</td></tr>");
            }
        }

        html.append("</table></body></html>");

        try {
            FileWriter writer = new FileWriter(reportDirPath + File.separator + "Custom_Report.html");
            writer.write(html.toString());
            writer.close();

            InputStream in = getClass().getClassLoader().getResourceAsStream("logo.png");
            if (in != null) {
                Files.copy(in, new File(reportDirPath + File.separator + "logo.png").toPath());
            }

            System.out.println("✅ Report saved at: " + reportDirPath);

            // 📧 Send the email with the generated report
            List<String> recipients = Arrays.asList(
                    "qa_team@example.com",
                    "manager@example.com"
            );
            sendEmailWithAttachment(recipients, reportDirPath + File.separator + "Custom_Report.html");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void onTestStart(ITestResult result) {
        try {
            String browserName = Base_Page.driverName.get();
            browserMap.put(result, browserName);
            osMap.put(result, System.getProperty("os.name"));
        } catch (Exception e) {
            browserMap.put(result, "Unknown");
            osMap.put(result, System.getProperty("os.name"));
        }
    }
    
    @Override public void onTestSuccess(ITestResult result) { screenshotMap.put(result, ""); }    

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String screenshotPath = new Base_Page().captureScreen(result.getName(), getReportDirPath());
            screenshotMap.put(result, screenshotPath);
            Throwable throwable = result.getThrowable();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            if (throwable != null) {
                throwable.printStackTrace(pw);
                errorMap.put(result, sw.toString());
            } else {
                errorMap.put(result, "No exception available.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onTestSkipped(ITestResult result) { screenshotMap.put(result, ""); }
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}

    private String getReportDirPath() {
        if (reportDirPath == null) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportDirPath = System.getProperty("user.dir") + File.separator + "reports"
                    + File.separator + "CustomReports" + File.separator + "Report_" + timeStamp;
            new File(reportDirPath).mkdirs();
        }
        return reportDirPath;
    }

    private void sendEmailWithAttachment(List<String> recipients, String filePath) {
        final String from = "sonu2010dhiman@gmail.com";
        final String password = "gycw idcf fuyv cglp"; // App password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            Address[] toAddresses = new Address[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                toAddresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            message.setSubject("✅ Automation Report - Deep Freeze Cloud ");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Hello,\n\nPlease find attached the latest automation test report.\n\nRegards,\nQA Team");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(filePath)));
            attachmentPart.setFileName("Custom_Report.html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("📧 Email sent to: " + String.join(", ", recipients));

        } catch (Exception e) {
            System.err.println("❌ Email sending failed!");
            e.printStackTrace();
        }
    }
}
