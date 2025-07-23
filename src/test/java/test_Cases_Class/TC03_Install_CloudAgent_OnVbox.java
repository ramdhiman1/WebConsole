package test_Cases_Class;

import org.testng.annotations.Test;
import java.io.IOException;
import org.testng.Assert;

import base_Classes.Base_Page;
import testCasesCode.Install_CloudPro_Vbox_Production;

public class TC03_Install_CloudAgent_OnVbox extends Base_Page {

	@Test
	public void vmStartInstallAgent() throws InterruptedException, IOException {
		Install_CloudPro_Vbox_Production iw = new Install_CloudPro_Vbox_Production();

		String policyname = "\"D:\\DFCloud\\Downloads\\FWAWebInstaller_Faronics Default.exe\"";
		iw.renameInstaller();

		iw.StartVM();
		iw.pingVM();

		Thread.sleep(10000); // Small buffer before copy
		iw.copyInstallerUsingPsExec();
		Thread.sleep(20000); // Ensure files copied
		iw.installCloudAgentandProducts();

		// ✅ Retry: Check product installation (max 20 tries with 20 sec delay)
		boolean allInstalled = false;
		int maxTries = 20;
		for (int i = 1; i <= maxTries; i++) {
			System.out.println("🟡 Checking file using PsExec: Cloud Agent");
			boolean isFWA = iw.isFileExists(
					"C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe",
					"Cloud Agent");

			System.out.println("🟡 Checking file using PsExec: Anti-Virus");
			boolean isAntiVirus = iw.isFileExists("C:\\Program Files\\Faronics\\Faronics Anti-Virus\\FAVEService.exe",
					"Anti-Virus");

			// Add more products if needed here

			if (isFWA && isAntiVirus) {
				allInstalled = true;
				System.out.println("✅ All Cloud products installed.");
				break;
			} else {
				System.out.println("🔁 Cloud Products not fully installed yet... Retry " + i + "/" + maxTries);
				Thread.sleep(20000); // 20 seconds between retries
			}
		}

		Assert.assertTrue(allInstalled, "❌ Cloud Products not installed after retries.");

		// ✅ After installation, wait for reboot and re-ping
		System.out.println("🕒 I am Waiting here for 3 minutes for reboot...");
		Thread.sleep(130000);
		iw.pingVM(); // confirm system came back online
		System.out.println("🕒 I am Waiting here for 1 minute for Load Properly Machine...");
		Thread.sleep(100000);

		// ✅ Final verification after reboot
		System.out.println("🟢 Final installation verification For Cloud Products After reboot The Machine:");

		boolean finalFWA = false;
		boolean finalAV = false;
		boolean finalDF = false;

		for (int i = 1; i <= 5; i++) {
			finalFWA = iw.isFileExists(
					"C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe",
					"Cloud Agent");
			finalAV = iw.isFileExists("C:\\Program Files\\Faronics\\Faronics Anti-Virus\\FAVEService.exe",
					"Anti-Virus");
			finalDF = iw.isFileExists("C:\\Program Files (x86)\\Faronics\\Deep Freeze\\Install C-0\\DFServ.exe",
					"Deep Freeze");

			if (finalFWA && finalAV && finalDF) {
				System.out.println("✅ All Cloud products verified Installation by Checking Installation Path after reboot.");
				break;
			} else {
				System.out.println("⏳ Final check failed. Retry " + i + "/5");
				Thread.sleep(15000);
			}
		}

		// ✅ Final assertions
		Assert.assertTrue(finalFWA, "❌ Cloud Agent installation failed after reboot");
		Assert.assertTrue(finalAV, "❌ Anti-Virus installation failed after reboot");
		Assert.assertTrue(finalDF, "❌ Deep Freeze installation failed after reboot");

		System.out.println("🎉 All Cloud products installed and machine is up!");
	}
}
