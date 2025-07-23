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

public class Production_Server_WWW9 extends Base_Page {
	String randomPolicyName = randomeString().toUpperCase();
	String editedPolicyName = randomeString().toUpperCase();

	List<String[]> loginData = readExcelData("F:\\Automation Work 2024\\2025\\Logindata.xlsx", "Sheet1");

	@Test(priority = 2, groups = { "smoke",
			"Regression" }, description = "Log in to the Cloud Console on 'www9' to verify machine status, install products, and confirm successful installation.")
	public void loginToWww9CloudConsole() throws InterruptedException, IOException {
		String[] secondUser = loginData.get(1);
		String url = secondUser[0];
		String username = secondUser[1];
		String password = secondUser[2];

		getDriver().get(url);
		logToReport(Status.INFO, "Navigated to Production Server www9 URL: " + url);

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
		logToReport(Status.INFO, "Page Title after login: " + actualTitle);
		Assert.assertEquals(actualTitle, expectedTitle, "Login failed or title mismatch");
		logToReport(Status.PASS, "Login successful. Page title verified.");

		logToReport(Status.INFO, "Starting Policy Creation on www9");
		getDriver().get(p.getProperty("policypagewww9"));
		logToReport(Status.INFO, "Navigated to Policy Page");

		Policies_Page_ProductionServer dfPolicy2 = new Policies_Page_ProductionServer();
		dfPolicy2.clickAddPolicyButton();
		logToReport(Status.INFO, "Clicked on Add Policy button");

		dfPolicy2.selectDropdownPolicy();
		dfPolicy2.enterPolicyName(randomPolicyName);
		logToReport(Status.INFO, "Entered random policy name: " + randomPolicyName);

		dfPolicy2.EnableDFService();
		dfPolicy2.ClickonDropdwonDF("Enable (Install and use below settings)");
		logToReport(Status.INFO, "Enabled Deep Freeze settings");

		dfPolicy2.EnableSoftwareUpdater();
		dfPolicy2.ClickonDropdwonSU("Enable (Install and use below settings)");
		logToReport(Status.INFO, "Enabled Software Updater settings");

		dfPolicy2.selectanyapps();
		logToReport(Status.INFO, "Selected random apps");

		dfPolicy2.clickSaveButton();
		logToReport(Status.INFO, "Clicked Save button to create policy");

		driver.get().get(p.getProperty("www2serverdownloadagent"));
		logToReport(Status.INFO, "Navigated to Cloud Agent Download Page");

		dfPolicy2.ClickOnInstallCloudAgentbtn(randomPolicyName);
		logToReport(Status.INFO, "Clicked on Install Cloud Agent button for policy: " + randomPolicyName);

		Thread.sleep(100000);
		logToReport(Status.INFO, "Waited 100 seconds for download");

		Install_CloudPro_Vbox_Production iw2 = new Install_CloudPro_Vbox_Production();

		iw2.renameInstaller();
		logToReport(Status.INFO, "Renamed installer");

		iw2.StartVM();
		logToReport(Status.INFO, "Started Virtual Machine");

		iw2.pingVM();
		logToReport(Status.INFO, "Pinged VM to ensure it's reachable");

		Thread.sleep(10000);
		iw2.copyInstallerUsingPsExec();
		logToReport(Status.INFO, "Copied installer to VM using PsExec");

		Thread.sleep(20000);
		iw2.installCloudAgentandProducts();
		logToReport(Status.INFO, "Started Cloud product installation on VM");

		Map<String, String> expectedProducts = new HashMap<>();
		expectedProducts.put("Cloud Agent", "C:\\Program Files (x86)...FWAService.exe");
		// (same for other products...)

		logToReport(Status.INFO, "Waiting for machine reboot");
		Thread.sleep(130000);
		iw2.pingVM();
		logToReport(Status.INFO, "Pinged VM after reboot");

		Thread.sleep(100000);
		logToReport(Status.INFO, "Waited extra 100 seconds for system load");

		Set<String> installedAfter = new HashSet<>();
		for (Map.Entry<String, String> entry : expectedProducts.entrySet()) {
			String name = entry.getKey();
			String path = entry.getValue();
			boolean found = false;

			for (int i = 1; i <= 2; i++) {
				if (iw2.isFileExists(path, name)) {
					installedAfter.add(name);
					logToReport(Status.PASS, "Verified installed product after reboot: " + name);
					found = true;
					break;
				} else {
					logToReport(Status.INFO, "Retry " + i + ": " + name + " not found yet");
					Thread.sleep(10000);
				}
			}
			if (!found) {
				logToReport(Status.WARNING, "Product not found after reboot: " + name);
			}
		}

		logToReport(Status.INFO, "Installed Products After Reboot: " + installedAfter);

		getDriver().get(p.getProperty("www9computerspage"));
		iw2.SearchMachine();
		iw2.selectOnlineWorkstation();
		iw2.SearchMachine();
		iw2.printAllProductStatuses();
		logToReport(Status.INFO, "Verified product status from Computers Page");

		iw2.selectOnlineWorkstation();
		iw2.dfthawedAction();
		logToReport(Status.INFO, "Performed Deep Freeze thawed action");

		driver.get().get(p.getProperty("www9taskstatuspage"));
		iw2.checkfirestrow1();
		logToReport(Status.INFO, "Checked task status");

		driver.get().get(p.getProperty("www9applicationpage"));
		Application_Page_ProductionServer apps = new Application_Page_ProductionServer();
		apps.setAllComputersFilter();
		logToReport(Status.INFO, "Filtered applications for all computers");

		Applications_Page comapp = new Applications_Page();
		Thread.sleep(5000);
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		logToReport(Status.INFO, "Clicked compress options");

		comapp.ClickonanyApp();
		logToReport(Status.INFO, "Selected application to install");

		comapp.clickonInstallYesbuttons();
		logToReport(Status.INFO, "Confirmed application installation");

		apps.monitorPidginAppStatus();
		logToReport(Status.INFO, "Monitored application installation status");

		driver.get().get(p.getProperty("www9applicationpage"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.clickonunInstallYesbutton22();
		logToReport(Status.INFO, "Confirmed application uninstallation");

		driver.get().get(p.getProperty("www9taskstatuspage"));
		comapp.checkfirestrow1();
		logToReport(Status.INFO, "Checked task row for uninstall status");

		driver.get().get(p.getProperty("www9applicationpage"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		String InstalledAppsVersion = comapp.getInstalledAppVersion();

		if (InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty()) {
			logToReport(Status.PASS, "Verified: Application has been uninstalled successfully");
		} else {
			logToReport(Status.FAIL, "App uninstall failed — Version still present: " + InstalledAppsVersion);
		}

		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty());

		getDriver().get(p.getProperty("www9computerspage"));
		iw2.SearchMachine();
		iw2.printAllProductStatuses();
		logToReport(Status.INFO, "Final product statuses printed");

		iw2.selectOnlineWorkstation();
		iw2.UninstallCloudAgent();
		logToReport(Status.INFO, "Uninstalled Cloud Agent from VM");

		driver.get().get(p.getProperty("www9taskstatuspage"));
		iw2.checkfirestrow1();

		logToReport(Status.INFO, "Waiting 10 minutes for cleanup...");
		Thread.sleep(200000);

		getDriver().get(p.getProperty("signouturlwww9"));
		logToReport(Status.INFO, "Signed out from www9 Cloud Console");
	}
}
