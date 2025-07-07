package testCasesCode;

import org.openqa.selenium.WebDriver;
import base_Classes.Base_Page;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VBox_Launched_Code extends Base_Page {

    public VBox_Launched_Code(WebDriver driver) {
        super();
    }

    String vmName = "1234-W-10-64-EN";

    String vboxManagePath = "C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe";

    // Step 1: Start the VirtualBox VM using VBoxManage
    public void StartVM() {
        try {
            
            String[] command = { vboxManagePath, "startvm", vmName, "--type", "gui" };

        
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true); // error and output merge
            Process process = builder.start();

            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[VBox] " + line);
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ VM '" + vmName + "' started successfully.");
            } else {
                System.out.println("❌ Failed to start VM. Exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.out.println("❌ Exception while starting VM:");
            e.printStackTrace();
        }
    }
}
