package test_Cases_Class;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.*;
import org.testng.Assert;

import base_Classes.Base_Page;
import testCasesCode.Install_CloudPro_Vbox_Production;

public class TC09_Install_Products_VM_ProductionsServer extends Base_Page {

    @Test
    public void vmStartInstallAgent() throws InterruptedException, IOException {
        Install_CloudPro_Vbox_Production iw = new Install_CloudPro_Vbox_Production(driver.get());
        iw.renameInstaller();

        iw.StartVM();
        iw.pingVM();

        Thread.sleep(10000); // Small buffer before copy
        iw.copyInstallerUsingPsExec();
        Thread.sleep(20000); // Ensure files copied
        iw.installApplication();

        // ✅ Define expected products and their installation paths
        Map<String, String> expectedProducts = new HashMap<>();
        expectedProducts.put("Cloud Agent", "C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe");
        expectedProducts.put("Anti-Virus", "C:\\Program Files\\Faronics\\Faronics Anti-Virus\\FAVEService.exe");
        expectedProducts.put("Deep Freeze", "C:\\Program Files (x86)\\Faronics\\Deep Freeze\\Install C-0\\DFServ.exe");

        // ➕ Optional: Add more products as needed
        expectedProducts.put("Anti-Executable", "C:\\Program Files\\Faronics\\AE\\Antiexecutable.exe");
        expectedProducts.put("Remote Control", "C:\\Program Files\\Faronics\\FaronicsRemote\\FaronicsRemote.exe");
        expectedProducts.put("Power Save", "C:\\Program Files\\Faronics\\Power Save Workstation\\PowerSaveService.exe");
        expectedProducts.put("Software Updater", "C:\\Program Files\\Faronics\\Software Updater\\FWUSvc.exe");
        expectedProducts.put("Usage Stats", "C:\\Program Files\\Faronics\\UsageStats\\USEngine.exe");
        expectedProducts.put("WINSelect", "C:\\Program Files\\Faronics\\WINSelect\\WINSelect.exe");
        expectedProducts.put("Imaging", "C:\\Program Files (x86)\\Faronics\\Imaging\\Imaging.exe");

        // ✅ Retry loop to verify installation
        Set<String> successfullyInstalled = new HashSet<>();
        int maxTries = 20;

        for (int i = 1; i <= maxTries; i++) {
            System.out.println("🔄 Checking product installations - Attempt " + i);
            successfullyInstalled.clear();

            for (Map.Entry<String, String> entry : expectedProducts.entrySet()) {
                String productName = entry.getKey();
                String productPath = entry.getValue();

                boolean isInstalled = iw.isFileExists(productPath, productName);
                if (isInstalled) {
                    successfullyInstalled.add(productName);
                }
            }

            if (successfullyInstalled.containsAll(expectedProducts.keySet())) {
                System.out.println("✅ All expected products installed.");
                break;
            } else {
                System.out.println("🔁 Not all expected products are installed. Retrying in 20 seconds...");
                Thread.sleep(20000);
            }
        }

        // ✅ Final assertion before reboot wait
        for (String product : expectedProducts.keySet()) {
            Assert.assertTrue(successfullyInstalled.contains(product), "❌ " + product + " not installed.");
        }

        // ✅ Reboot wait and ping
        System.out.println("🕒 Waiting 3 minutes for system reboot...");
        Thread.sleep(130000);
        iw.pingVM();
        System.out.println("🕒 Waiting 1 more minute for machine to load...");
        Thread.sleep(100000);

        // ✅ Final verification after reboot
        System.out.println("🟢 Final product verification after reboot...");
        Set<String> finalInstalled = new HashSet<>();

        for (int i = 1; i <= 5; i++) {
            finalInstalled.clear();

            for (Map.Entry<String, String> entry : expectedProducts.entrySet()) {
                String productName = entry.getKey();
                String productPath = entry.getValue();

                boolean isInstalled = iw.isFileExists(productPath, productName);
                if (isInstalled) {
                    finalInstalled.add(productName);
                }
            }

            if (finalInstalled.containsAll(expectedProducts.keySet())) {
                System.out.println("✅ All expected products verified after reboot.");
                break;
            } else {
                System.out.println("⏳ Retry " + i + "/5: Some products not verified after reboot.");
                Thread.sleep(15000);
            }
        }

        // ✅ Final assertion
        for (String product : expectedProducts.keySet()) {
            Assert.assertTrue(finalInstalled.contains(product), "❌ " + product + " not installed after reboot.");
        }

        System.out.println("🎉 All expected products are installed and verified after reboot!");
    }
}
