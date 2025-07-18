package test_Cases_Class;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import base_Classes.Base_Page;
import testCasesCode.Application_Page_ProductionServer;
import testCasesCode.Applications_Page;
import testCasesCode.Install_CloudPro_Vbox_Production;
import testCasesCode.Multiple_Login_ProductionServer;
import testCasesCode.Policies_Page_ProductionServer;

@Listeners(utilities.MyListener.class)

public class Production_Server_WWW3 extends Base_Page {
	String randomPolicyName = randomeString().toUpperCase();
	String editedPolicyName = randomeString().toUpperCase();

	List<String[]> loginData = readExcelData("F:\\Automation Work 2024\\2025\\Logindata.xlsx", "Sheet1");

	@Test(priority = 1, groups = { "smoke",
			"Regression" }, description = "Log in to the Cloud Console on 'www3' to check machine status, install products, and verify their status.")
	public void Login_on_Cloud_Server_www3() throws InterruptedException, IOException {
		String[] thirdUser = loginData.get(2);
		String url = thirdUser[0];
		String username = thirdUser[1];
		String password = thirdUser[2];

		getDriver().get(url);
		logToReport(Status.INFO, "Navigated to Production Server www3 URL: " + url);

		Multiple_Login_ProductionServer login = new Multiple_Login_ProductionServer();
		login.EnterUserName(username);
		logToReport(Status.INFO, "Entered username: " + username);

		login.ClickOnNextBtn();
		logToReport(Status.INFO, "Clicked Next button");

		login.EnterPassword(password);
		logToReport(Status.INFO, "Entered password");

		login.ClickOnSignBtn();
		logToReport(Status.INFO, "Clicked Sign In button");

		String expectedTitle = "Deep Freeze Cloud";
		String actualTitle = getDriver().getTitle();
		logToReport(Status.INFO, "Page Title: " + actualTitle);

		Assert.assertEquals(actualTitle, expectedTitle, "Login failed or title mismatch");
		logToReport(Status.PASS, "Login successful. Page title verified.");

		logToReport(Status.INFO, "Actions for User 3: " + username);

		getDriver().get(p.getProperty("policypagewww3"));
		logToReport(Status.INFO, "Navigated to Policy Page");		
		// Add user1-specific actions here
		Policies_Page_ProductionServer dfPolicy2 = new Policies_Page_ProductionServer();
		dfPolicy2.clickAddPolicyButton();
		dfPolicy2.selectDropdownPolicy();
		dfPolicy2.enterPolicyName(randomPolicyName);
		dfPolicy2.EnableDFService();
		dfPolicy2.ClickonDropdwonDF("Enable (Install and use below settings)");
		dfPolicy2.EnableSoftwareUpdater();
		dfPolicy2.ClickonDropdwonSU("Enable (Install and use below settings)");
		dfPolicy2.selectanyapps();
		dfPolicy2.clickSaveButton();
		driver.get().get(p.getProperty("www3serverdownloadagent"));
		logToReport(Status.INFO, "Navigated to Cloud Agent Download Page");
		dfPolicy2.ClickOnInstallCloudAgentbtn(randomPolicyName);
		Thread.sleep(100000);

		// here Launch The VM and Install the Cloud Products

		Install_CloudPro_Vbox_Production iw2 = new Install_CloudPro_Vbox_Production();

		iw2.renameInstaller();
		iw2.StartVM();
		logToReport(Status.INFO, "Now We are going for Start the Virtual Box (Machine)");
		iw2.pingVM();

		Thread.sleep(10000); // Small buffer before copy
		iw2.copyInstallerUsingPsExec();
		Thread.sleep(20000); // Ensure files copied
		iw2.installApplication();

		// ✅ Step 1: Define expected product installation paths
		Map<String, String> expectedProducts = new HashMap<>();
		expectedProducts.put("Cloud Agent",
				"C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe");
		expectedProducts.put("Anti-Virus", "C:\\Program Files\\Faronics\\Faronics Anti-Virus\\FAVEService.exe");
		expectedProducts.put("Deep Freeze", "C:\\Program Files (x86)\\Faronics\\Deep Freeze\\Install C-0\\DFServ.exe");
		expectedProducts.put("Anti-Executable", "C:\\Program Files\\Faronics\\AE\\Antiexecutable.exe");
		expectedProducts.put("Remote Control", "C:\\Program Files\\Faronics\\FaronicsRemote\\FaronicsRemote.exe");
		expectedProducts.put("Power Save", "C:\\Program Files\\Faronics\\Power Save Workstation\\PowerSaveService.exe");
		expectedProducts.put("Software Updater", "C:\\Program Files\\Faronics\\Software Updater\\FWUSvc.exe");
		expectedProducts.put("Usage Stats", "C:\\Program Files\\Faronics\\UsageStats\\USEngine.exe");
		expectedProducts.put("WINSelect", "C:\\Program Files\\Faronics\\WINSelect\\WINSelect.exe");
		expectedProducts.put("Imaging", "C:\\Program Files (x86)\\Faronics\\Imaging\\Imaging.exe");

		// ✅ Step 2: Wait and Ping after Reboot
		System.out.println("\n🕒 Waiting for reboot (3 mins)...");
		Thread.sleep(130000);
		iw2.pingVM();
		System.out.println("🕒 Waiting 1 more minute for system load...");
		Thread.sleep(100000);

		// ✅ Step 3: AFTER reboot retry check
		System.out.println("\n🔍 [After Reboot] Retrying 2 times every 2 seconds:");
		Set<String> installedAfter = new HashSet<>();
		for (Map.Entry<String, String> entry : expectedProducts.entrySet()) {
			String name = entry.getKey();
			String path = entry.getValue();
			boolean found = false;

			for (int i = 1; i <= 2; i++) {
				if (iw2.isFileExists(path, name)) {
					System.out.println("✅ Verified [After Reboot]: " + name + " → " + path);
					installedAfter.add(name);
					found = true;
					break;
				} else {
					System.out.println("⏳ Retry " + i + "/2: " + name + " not found yet...");
					Thread.sleep(10000); // 10 sec delay
				}
			}

			if (!found) {
				System.out.println("❌ Not Verified [After Reboot]: " + name);
			}
		}

		// ✅ Summary.
		System.out.println("\n📦 Summary Report:");
		System.out.println("➡ Total Products Installed on Machine: " + installedAfter);

		System.out.println("\n🎉 Product installation verification completed!");

		// Now Switch to Computers Page
		driver.get().get(p.getProperty("www2computerspage"));
		iw2.SearchMachine();
		iw2.selectOnlineWorkstation();
		iw2.SearchMachine();
		iw2.printAllProductStatuses();
		iw2.selectOnlineWorkstation();
		iw2.dfthawedAction();
		driver.get().get(p.getProperty("www2taskstatuspage"));
		iw2.checkfirestrow1();

		// Application Page Actions
		driver.get().get(p.getProperty("www2applicationpage"));
		Application_Page_ProductionServer apps = new Application_Page_ProductionServer();
		apps.setAllComputersFilter();
		// apps.InstallAnySUApps(randomPolicyName);
		Applications_Page comapp = new Applications_Page();
		Thread.sleep(5000);
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.ClickonanyApp();
		comapp.clickonInstallYesbuttons();
		// apps.clickonInstallYesbuttons();
		apps.monitorPidginAppStatus();
		driver.get().get(p.getProperty("www2applicationpage"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		// comapp.ClickonanyApp();
		comapp.clickonunInstallYesbutton22();
		driver.get().get(p.getProperty("www2taskstatuspage"));
		comapp.checkfirestrow1();
		driver.get().get(p.getProperty("www2applicationpage"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		// Get the version after uninstall
		String InstalledAppsVersion = comapp.getInstalledAppVersion();

		// ASSERT: It should be empty after uninstall
		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty(),
				"App uninstall failed — Version still present: " + InstalledAppsVersion);
		System.out.println("Verified: Application has been uninstalled successfully.");

		getDriver().get(p.getProperty("www2computerspage"));
		iw2.SearchMachine();
		iw2.printAllProductStatuses();
		getDriver().get(p.getProperty("www2computerspage"));
		iw2.selectOnlineWorkstation();
		iw2.UninstallCloudAgent();
		driver.get().get(p.getProperty("www2taskstatuspage"));
		iw2.checkfirestrow1();

		// wait for 10 mints untill all Cloud Products Uninstall
		System.out.println("🕒 Waiting 10 more minute untill Cloud Agent and Products Uninstalled From Machine ...");
		Thread.sleep(200000);

		getDriver().get(p.getProperty("signouturlwww2"));
		logToReport(Status.INFO, "Now SignOut the User from Production Server www2");
	}

}
