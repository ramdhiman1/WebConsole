package utilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomHtmlReportGenerator {

    public static void generate(Map<String, Map<String, String>> testMatrix) throws IOException {
        // Step 1: Collect all server names
        Set<String> servers = new TreeSet<>();
        for (Map<String, String> result : testMatrix.values()) {
            servers.addAll(result.keySet());
        }

        // Step 2: Normalize matrix so that every test case includes all servers
        testMatrix = normalizeMatrix(testMatrix, servers);

        // Step 3: Count pass/fail
        int passCount = 0, failCount = 0;
        for (Map<String, String> map : testMatrix.values()) {
            for (String value : map.values()) {
                if ("Pass".equalsIgnoreCase(value)) passCount++;
                else if ("Fail".equalsIgnoreCase(value)) failCount++;
            }
        }

        int totalTests = testMatrix.size();
        int successRate = (int)(((double) passCount / (passCount + failCount)) * 100);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String reportName = "Matrix_Report_" + timestamp + ".html";

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Arial;}table{border-collapse:collapse;width:100%;}")
            .append("th,td{border:1px solid #ccc;padding:8px;text-align:center;}")

            .append(".pass{background-color:#4CAF50;color:white;}")
            .append(".fail{background-color:#F44336;color:white;}")

            .append(".summary-box{display:flex;justify-content:space-around;padding:10px;}")
            .append(".summary{border:1px solid #ccc;padding:10px;border-radius:5px;text-align:center;width:20%;}")

            .append("</style></head><body>");

        // Header section
        html.append("<div style='background-color:#2c3e50;color:white;padding:30px 20px 10px 20px;border-top-left-radius:10px;border-top-right-radius:10px;text-align:center;'>")
            .append("<h1 style='margin:0;font-size:28px;'>Deep Freeze Cloud Automation</h1>")
            .append("<p style='margin:5px 0 20px;font-size:16px;'>Production Test Results</p>")
            .append("</div>");

        // Test date/time
        String[] datetime = new SimpleDateFormat("dd/MM/yyyy|HH:mm:ss").format(new Date()).split("\\|");
        html.append("<div style='background-color:#f1f4f6;padding:10px;text-align:center;font-size:14px;border-bottom:1px solid #ccc;'>")
            .append("<strong>Test Date:</strong> ").append(datetime[0])
            .append(" | <strong>Test Time:</strong> ").append(datetime[1])
            .append("</div>");

        // Summary cards
        html.append("<div style='background-color:#f8f9fa;padding:20px;border-radius:10px;margin-bottom:20px;'>")
            .append("<h3 style='text-align:center;margin-bottom:20px;'>Test Summary</h3>")
            .append("<div style='display:flex;justify-content:space-around;flex-wrap:wrap;'>")

            // Total
            .append("<div style='background-color:#e7f3fe;padding:20px 30px;border-radius:8px;text-align:center;width:200px;margin:10px;'>")
            .append("<h2 style='color:#007bff;margin:0;'>").append(totalTests).append("</h2>")
            .append("<p style='margin:5px 0 0;'>Total Tests</p>")
            .append("</div>")

            // Passed
            .append("<div style='background-color:#e9f7ef;padding:20px 30px;border-radius:8px;text-align:center;width:200px;margin:10px;'>")
            .append("<h2 style='color:#28a745;margin:0;'>").append(passCount).append("</h2>")
            .append("<p style='margin:5px 0 0;'>Tests Passed</p>")
            .append("</div>")

            // Failed
            .append("<div style='background-color:#fdecea;padding:20px 30px;border-radius:8px;text-align:center;width:200px;margin:10px;'>")
            .append("<h2 style='color:#dc3545;margin:0;'>").append(failCount).append("</h2>")
            .append("<p style='margin:5px 0 0;'>Tests Failed</p>")
            .append("</div>")

            // Success Rate
            .append("<div style='background-color:#fff3cd;padding:20px 30px;border-radius:8px;text-align:center;width:200px;margin:10px;'>")
            .append("<h2 style='color:#212529;margin:0;'>").append(successRate).append("%</h2>")
            .append("<p style='margin:5px 0 0;'>Success Rate</p>")
            .append("</div>")

            .append("</div></div>");

        // Table Header
        html.append("<table><tr><th style='text-align:left;'>Test Case</th>");
        for (String server : servers) {
            html.append("<th>").append(server).append("</th>");
        }
        html.append("</tr>");

        // Table Rows
        int count = 1;
        for (Map.Entry<String, Map<String, String>> entry : testMatrix.entrySet()) {
            html.append("<tr><td style='text-align:left;padding-left:10px;'>")
                .append(count).append(". ").append(entry.getKey())
                .append("</td>");
            Map<String, String> row = entry.getValue();
            for (String server : servers) {
                String status = row.getOrDefault(server, "-");
                String css = status.equalsIgnoreCase("Pass") ? "pass" :
                             status.equalsIgnoreCase("Fail") ? "fail" : "";
                html.append("<td class='").append(css).append("'>").append(status).append("</td>");
            }
            html.append("</tr>");
            count++;
        }

        html.append("</table>");

        // Footer Section
        html.append("<div style='background-color:#2c3e50;padding:20px;border-radius:10px;margin-top:30px;margin-bottom:30px;'>")
            .append("<p style='color:white;text-align:center;margin:0;'>")
            .append("<strong>Test Environment:</strong> Production Environment<br>")
            .append("<strong>Patch Version:</strong> Latest Update Patch")
            .append("</p>")
            .append("<div style='text-align:center;margin-top:10px;'>")
            .append("<button style='background-color:#28a745;color:white;padding:5px 15px;border:none;border-radius:4px;margin:0 10px;'>Pass</button>")
            .append("<button style='background-color:#dc3545;color:white;padding:5px 15px;border:none;border-radius:4px;margin:0 10px;'>Fail</button>")
            .append("</div>")
            .append("</div>");

        // === Create timestamped folder ===
        String baseDir = "F:\\Automation Work 2024\\2025\\Start_From_Basic\\CustomReport\\";
        String folderTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File folder = new File(baseDir + folderTimestamp);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, reportName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(html.toString());
        }

        System.out.println("✅ Matrix HTML report generated at: " + file.getAbsolutePath());
    }

    /**
     * Normalize the test matrix by ensuring every test case has all servers as keys.
     * If a server result is missing, default it to "-"
     */
    private static Map<String, Map<String, String>> normalizeMatrix(Map<String, Map<String, String>> original, Set<String> servers) {
        Map<String, Map<String, String>> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : original.entrySet()) {
            String testName = entry.getKey();
            Map<String, String> originalRow = entry.getValue();
            Map<String, String> normalizedRow = new LinkedHashMap<>();
            for (String server : servers) {
                normalizedRow.put(server, originalRow.getOrDefault(server, "-"));
            }
            normalized.put(testName, normalizedRow);
        }
        return normalized;
    }
}
