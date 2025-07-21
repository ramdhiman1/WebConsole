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
import testCasesCode.Multi_Server_Login_Page;
import testCasesCode.Multiple_Login_ProductionServer;
import testCasesCode.Policies_Page_ProductionServer;
import utilities.MyListener;

@Listeners(MyListener.class)
public class Multi_Server_Test_Cases_Class extends Base_Page {
	String randomPolicyName = randomeString().toUpperCase();
	String editedPolicyName = randomeString().toUpperCase();

	List<String[]> loginData = readExcelData("F:\\Automation Work 2024\\2025\\Logindata.xlsx", "Sheet1");;

	@Test(priority = 1, groups = { "smoke",
			"regression" }, description = "Login to multiple production servers from Excel")
	public void loginToAllProductionServers() throws InterruptedException, IOException {
		Multi_Server_Login_Page loginPage = new Multi_Server_Login_Page();

		for (int i = 0; i < loginData.size(); i++) {
			String[] serverData = loginData.get(i);
			String url = serverData[0];
			String username = serverData[1];
			String password = serverData[2];

			logToReport(Status.INFO, "🌐 Logging in to server: " + url);
			String result = loginPage.performLogin(url, username, password);
			logToReport(Status.INFO, "🔁 Login attempt result: " + result);

			if (result.contains("Login Success")) {
				logToReport(Status.PASS, "✅ Logged in successfully to: " + url);

				// ✅ Navigate to any pages dynamically using config keys
			/*	loginPage.navigateToPage("applicationpath"); // 🔁 Go to Application Page
				logToReport(Status.INFO, "🎯 Navigate to Application Page");
				loginPage.navigateToPage("computerspage"); // 🔁 Go to Computers Page
				logToReport(Status.INFO, "🎯 Navigate to Computers Page");
				loginPage.navigateToPage("taskstatuspagenew");
				logToReport(Status.INFO, "🎯 Navigate to Task Status Page");
				loginPage.navigateToPage("policypagewww2"); // 🔁 Go to Policy Page
				logToReport(Status.INFO, "🎯 Navigate to Policy Page"); */

				Multiple_Login_ProductionServer login = new Multiple_Login_ProductionServer();

				logToReport(Status.INFO, "Starting Policy Creation");
				loginPage.navigateToPage("policypagewww2"); // 🔁 Go to Policy Page
				logToReport(Status.INFO, "🎯 Navigate to Policy Page");

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

				loginPage.navigateToPage("serverdownloadagent");
				logToReport(Status.INFO, "🎯 Navigate to Task Status Page");
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
				iw2.installApplication();
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

					for (int j = 1; j <= 2; j++) {
						if (iw2.isFileExists(path, name)) {
							installedAfter.add(name);
							logToReport(Status.PASS, "Verified installed product after reboot: " + name);
							found = true;
							break;
						} else {
							logToReport(Status.INFO, "Retry " + j + ": " + name + " not found yet");
							Thread.sleep(10000);
						}
					}
					if (!found) {
						logToReport(Status.WARNING, "Product not found after reboot: " + name);
					}
				}

				logToReport(Status.INFO, "Installed Products After Reboot: " + installedAfter);

				loginPage.navigateToPage("computerspage"); // 🔁 Go to Computers Page
				logToReport(Status.INFO, "🎯 Navigate to Computers Page");
				//iw2.SearchMachine();
				//iw2.selectOnlineWorkstation();
				iw2.SearchMachine();
				iw2.printAllProductStatuses();
				logToReport(Status.INFO, "Verified product status from Computers Page");
				loginPage.navigateToPage("computerspage"); // 🔁 Go to Computers Page
				logToReport(Status.INFO, "🎯 Navigate to Computers Page");
				iw2.SearchMachine();
				Thread.sleep(5000);

				iw2.selectOnlineWorkstation();
				iw2.dfthawedAction();
				logToReport(Status.INFO, "Performed Deep Freeze thawed action");

				loginPage.navigateToPage("taskstatuspagenew");
				logToReport(Status.INFO, "🎯 Navigate to Task Status Page");
				iw2.checkfirestrow1();
				logToReport(Status.INFO, "Checked task status");
				
				
				loginPage.navigateToPage("computerspage"); // 🔁 Go to Computers Page
				logToReport(Status.INFO, "🎯 Navigate to Computers Page");
				logToReport(Status.INFO, "Final product statuses printed");
				Thread.sleep(130000);
				iw2.selectOnlineWorkstation();

				loginPage.navigateToPage("applicationpath"); // 🔁 Go to Application Page
				logToReport(Status.INFO, "🎯 Navigate to Application Page");
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

				loginPage.navigateToPage("applicationpath"); // 🔁 Go to Application Page
				logToReport(Status.INFO, "🎯 Navigate to Application Page");
				comapp.clickonCommpress();
				comapp.clickonCommpress1();
				comapp.clickonunInstallYesbutton22();
				logToReport(Status.INFO, "Confirmed application uninstallation");

				loginPage.navigateToPage("taskstatuspagenew");
				logToReport(Status.INFO, "🎯 Navigate to Task Status Page");
				comapp.checkfirestrow1();
				logToReport(Status.INFO, "Checked task row for uninstall status");

				loginPage.navigateToPage("applicationpath"); // 🔁 Go to Application Page
				logToReport(Status.INFO, "🎯 Navigate to Application Page");
				comapp.clickonCommpress();
				comapp.clickonCommpress1();
				String InstalledAppsVersion = comapp.getInstalledAppVersion();

				if (InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty()) {
					logToReport(Status.PASS, "Verified: Application has been uninstalled successfully");
				} else {
					logToReport(Status.FAIL, "App uninstall failed — Version still present: " + InstalledAppsVersion);
				}

				Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty());

				loginPage.navigateToPage("computerspage"); // 🔁 Go to Computers Page
				logToReport(Status.INFO, "🎯 Navigate to Computers Page");
				//iw2.SearchMachine();
				//iw2.printAllProductStatuses();
				logToReport(Status.INFO, "Final product statuses printed");
				iw2.selectOnlineWorkstation();
				iw2.UninstallCloudAgent();
				logToReport(Status.INFO, "Uninstalled Cloud Agent from VM");

				loginPage.navigateToPage("taskstatuspagenew");
				logToReport(Status.INFO, "🎯 Navigate to Task Status Page");
				iw2.checkfirestrow1();

				logToReport(Status.INFO, "Waiting 10 minutes for cleanup...");
				Thread.sleep(200000);

				// Perform final logout
				loginPage.performLogout();
				logToReport(Status.INFO, "🔒 Performed logout successfully");
		}
	}
	}
}
