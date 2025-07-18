package reporting;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import org.testng.xml.XmlSuite;

import base_Classes.Base_Page;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomReportListener implements IReporter {

    private String reportDirPath;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        StringBuilder html = new StringBuilder();
        int totalTests = 0, passed = 0, failed = 0, skipped = 0;

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportDirPath = System.getProperty("user.dir") + File.separator + "reports"
                + File.separator + "CustomReports" + File.separator + "Report_" + timeStamp;

        new File(reportDirPath).mkdirs();

        // ✅ Count results before rendering summary
        for (ISuite suite : suites) {
            for (ISuiteResult result : suite.getResults().values()) {
                ITestContext context = result.getTestContext();
                totalTests += context.getPassedTests().size()
                        + context.getFailedTests().size()
                        + context.getSkippedTests().size();
                passed += context.getPassedTests().size();
                failed += context.getFailedTests().size();
                skipped += context.getSkippedTests().size();
            }
        }

        // ✅ Start building HTML
        html.append("<html><head><title>Test Report</title><style>")
                .append("body { font-family: Arial; background: #f4f7fa; margin: 0; padding: 20px; }")
                .append(".summary { display: flex; justify-content: space-around; margin-bottom: 20px; margin-top: 20px; }")
                .append(".card { padding: 20px; border-radius: 8px; color: white; font-weight: bold; width: 20%; text-align: center; }")
                .append(".pass { background-color: #4CAF50; }")
                .append(".fail { background-color: #f44336; }")
                .append(".skip { background-color: #ff9800; }")
                .append(".total { background-color: #607d8b; }")
                .append("table { border-collapse: collapse; width: 100%; background: #fff; }")
                .append("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }")
                .append("th { background-color: #3f51b5; color: white; position: sticky; top: 0; }")
                .append("tr:hover { background-color: #f1f1f1; }")
                .append(".status-pass { color: green; font-weight: bold; }")
                .append(".status-fail { color: red; font-weight: bold; }")
                .append(".status-skip { color: orange; font-weight: bold; }")
                .append(".modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4); }")
                .append(".modal-content { background-color: #fefefe; margin: 10% auto; padding: 20px; border: 1px solid #888; width: 80%; border-radius: 10px; }")
                .append(".close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }")
                .append(".close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }")
                .append(".logo { height: 50px; }")
                .append("</style></head><body>");

        html.append("<div style='display:flex;align-items:center;gap:20px;'>")
                .append("<img class='logo' src='logo.png' alt='Logo'>")
                .append("<h2>DEEP FREEZE CLOUD - Automation Test Report</h2>")
                .append("</div>");

        // ✅ Summary Cards on TOP
        html.append("<div class='summary'>")
                .append("<div class='card total'>Total: <br>").append(totalTests).append("</div>")
                .append("<div class='card pass'>Passed: <br>").append(passed).append("</div>")
                .append("<div class='card fail'>Failed: <br>").append(failed).append("</div>")
                .append("<div class='card skip'>Skipped: <br>").append(skipped).append("</div>")
                .append("</div>");

        // ✅ Table Header
        html.append("<table><tr><th>Test Case</th><th>Browser</th><th>Task Execution Time (s)</th><th>Status</th><th>Action</th></tr>");

        // ✅ Table Rows
        for (ISuite suite : suites) {
            for (ISuiteResult result : suite.getResults().values()) {
                ITestContext context = result.getTestContext();
                List<ITestResult> allResults = new ArrayList<>();
                allResults.addAll(context.getPassedTests().getAllResults());
                allResults.addAll(context.getFailedTests().getAllResults());
                allResults.addAll(context.getSkippedTests().getAllResults());

                for (ITestResult testResult : allResults) {
                    String statusClass = "status-pass";
                    String statusText = "PASS";

                    if (testResult.getStatus() == ITestResult.FAILURE) {
                        statusClass = "status-fail";
                        statusText = "FAIL";
                    } else if (testResult.getStatus() == ITestResult.SKIP) {
                        statusClass = "status-skip";
                        statusText = "SKIPPED";
                    }

                    String methodName = testResult.getMethod().getMethodName();
                    long duration = testResult.getEndMillis() - testResult.getStartMillis();
                    double durationInSeconds = duration / 1000.0;

                    String browser = getBrowserInfo(testResult);
                    String screenshot = "";
                    String errorMsg = "";

                    if (testResult.getStatus() == ITestResult.FAILURE) {
                        screenshot = captureScreenshot(testResult);
                        errorMsg = testResult.getThrowable() != null ? testResult.getThrowable().toString() : "No exception";
                    }

                    html.append("<tr><td>").append(methodName).append("</td>")
                            .append("<td>").append(browser).append("</td>")
                            .append("<td>").append(String.format("%.2f", durationInSeconds)).append("</td>")
                            .append("<td class='").append(statusClass).append("'>").append(statusText).append("</td>");

                    if (testResult.getStatus() == ITestResult.FAILURE) {
                        String modalId = methodName + "Modal";
                        html.append("<td><button onclick=\"document.getElementById('" + modalId + "').style.display='block'\">View Details</button></td></tr>");
                        html.append("<div id='").append(modalId).append("' class='modal'>")
                                .append("<div class='modal-content'>")
                                .append("<span class='close' onclick=\"document.getElementById('").append(modalId).append("').style.display='none'\">&times;</span>")
                                .append("<h3>Failure Reason</h3><p>").append(errorMsg).append("</p>")
                                .append("<img src='").append(screenshot).append("' style='width:100%;border:1px solid #ccc;border-radius:10px;'>")
                                .append("</div></div>");
                    } else {
                        html.append("<td>-</td></tr>");
                    }
                }
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

            sendEmailWithAttachment(
                    Arrays.asList("recipient1@gmail.com", "recipient2@yahoo.com"),
                    reportDirPath + File.separator + "Custom_Report.html"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBrowserInfo(ITestResult result) {
        Object testClass = result.getInstance();
        try {
            return (String) testClass.getClass().getMethod("getBrowserInfo").invoke(testClass);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String captureScreenshot(ITestResult result) {
        Object testClass = result.getInstance();
        try {
            WebDriver driver = (WebDriver) testClass.getClass().getMethod("getDriver").invoke(testClass);
            if (driver != null) {
                String methodName = result.getMethod().getMethodName();
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String fileName = methodName + "_screenshot_" + timeStamp + ".png";

                // ✅ Pass reportDirPath to Base_Page().captureScreen()
                Base_Page basePage = new Base_Page();
                String fullPath = basePage.captureScreen(methodName, reportDirPath);

                // ✅ Return only the filename, not full path, for embedding in HTML
                return new File(fullPath).getName(); // just the file name
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
            message.setSubject("✅ Automation Report - Deep Freeze Cloud");

            BodyPart textPart = new MimeBodyPart();
            textPart.setText("Attached is the latest Automation Test Report.");

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
            e.printStackTrace();
        }
    }
}
