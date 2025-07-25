package test_Cases_Class;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;

import base_Classes.Base_Page;
import testCasesCode.*;
import utilities.MyListener;

@Listeners(MyListener.class)
public class Multi_Server_Test_Cases_Class extends Base_Page implements ITest {

	private String currentTestName = "";

	@Override
	public String getTestName() {
		return currentTestName;
	}

	@DataProvider(name = "serverLoginData", parallel = false)
	public Object[][] getServerData() {
		List<String[]> loginData = readExcelData("F:\\Automation Work 2024\\2025\\Logindata.xlsx", "Sheet1");
		Object[][] data = new Object[loginData.size()][3];
		for (int i = 0; i < loginData.size(); i++) {
			data[i][0] = loginData.get(i)[0];
			data[i][1] = loginData.get(i)[1];
			data[i][2] = loginData.get(i)[2];
		}
		return data;
	}

	private String extractSubdomainFromUrl(String url) {
		try {
			URL netUrl = new URL(url);
			String host = netUrl.getHost();
			return host.split("\\.")[0];
		} catch (Exception e) {
			return "UnknownServer";
		}
	}
	

	@Test(priority = 1, dataProvider = "serverLoginData", description = "Run full flow per server")
	public void LoginOnProductionServer(String url, String username, String password)
			throws InterruptedException, IOException {	
		Base_Page.currentURL = url;
		String serverTag = extractSubdomainFromUrl(url);
		//String serverTag = extractSubdomainFromUrl(url);
		Multi_Server_Login_Page loginPage = new Multi_Server_Login_Page();
		String randomPolicyName = randomeString().toUpperCase();

		// ========== STEP 1: LOGIN ==========
		currentTestName = "Login_" + serverTag;
	//	logToReport(Status.INFO, "🌐 Logging into: " + url);
		String result = loginPage.performLogin(url, username, password);
		if (!result.contains("Login Success")) {
			logToReport(Status.FAIL, "❌ Login failed for server: " + url);
			Assert.fail("❌ Server is down or unreachable & Login failed for: " + url);
			return;
		}
		logToReport(Status.PASS, "✅ Login successful: " + serverTag);

		// ========== STEP 2: CREATE POLICY ==========
/*		currentTestName = "CreatePolicy_" + serverTag;
		logToReport(Status.INFO, "🛠 Creating policy: " + randomPolicyName);
		loginPage.navigateToPage("policypagewww2");

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
		logToReport(Status.PASS, "💾 Policy created: " + randomPolicyName);

		// ========== STEP 3: INSTALL AGENT ==========
		currentTestName = "Launch Virtual Machine and InstallAgent&Products_" + serverTag;
		loginPage.navigateToPage("serverdownloadagent");
		dfPolicy2.ClickOnInstallCloudAgentbtn(randomPolicyName);
		logToReport(Status.INFO, "📦 Installing Cloud Agent...");
		Thread.sleep(100000);

		Install_CloudPro_Vbox_Production iw2 = new Install_CloudPro_Vbox_Production();
		iw2.renameInstaller();
		iw2.StartVM();
		iw2.pingVM();
		Thread.sleep(120000);
		iw2.copyInstallerUsingPsExec();
		Thread.sleep(20000);
		iw2.installCloudAgentandProducts();
		logToReport(Status.PASS, "✅ Agent and products installed");

		// ========== STEP 4: VERIFY PRODUCTS ==========
		currentTestName = "VerifyProducts_Status(like Installed or Not on VM)" + serverTag;
		Map<String, String> expectedProducts = getExpectedProductMap();
		Thread.sleep(130000);
		iw2.pingVM();
		Thread.sleep(100000);

		Set<String> installedAfter = new HashSet<>();
		for (Map.Entry<String, String> entry : expectedProducts.entrySet()) {
			String name = entry.getKey();
			String path = entry.getValue();
			boolean found = false;
			for (int j = 1; j <= 2; j++) {
				if (iw2.isFileExists(path, name)) {
					installedAfter.add(name);
					found = true;
					break;
				} else {
					Thread.sleep(10000);
				}
			}
			if (!found) {
				// logToReport(Status.WARNING, "⚠️ Product not verified: " + name);
				System.out.println("Product not Installed: " + name);
			} else {
				logToReport(Status.PASS, "✅ Verified Product Installed: " + name);
			}
		}

		// ========== STEP 5: CHECK PRODUCTS STATUS ==========
		currentTestName = "Computers page: Check Product Status_" + serverTag;
		loginPage.navigateToPage("computerspage");
		iw2.SearchMachine();
		iw2.printAllProductStatuses();
		loginPage.navigateToPage("computerspage");
		iw2.SearchMachine();
		iw2.selectOnlineWorkstation();
		iw2.dfthawedAction();
		loginPage.navigateToPage("taskstatuspagenew");
		iw2.checkfirestrow1();
		loginPage.navigateToPage("computerspage");
		Thread.sleep(120000);  
		loginPage.navigateToPage("applicationpath");

		// ========== STEP 6: INSTALL SU APPS ==========
		currentTestName = "Install Any SU Application_" + serverTag;
		Application_Page_ProductionServer apps = new Application_Page_ProductionServer();
		apps.setAllComputersFilter();
		Applications_Page comapp = new Applications_Page();
		Thread.sleep(5000);
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.ClickonanyApp();
		comapp.clickonInstallYesbuttons();
		apps.monitorPidginAppStatus();
		logToReport(Status.PASS, "📦 App installed");

		// ========== STEP 7: UNINSTALL SU SAME Installed APP ==========
		currentTestName = "UNINSTALL SU SAME Installed APP_" + serverTag;
		loginPage.navigateToPage("applicationpath");
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.clickonunInstallYesbutton22();
		loginPage.navigateToPage("taskstatuspagenew");
		comapp.checkfirestrow1();
		loginPage.navigateToPage("applicationpath");
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		String InstalledAppsVersion = comapp.getInstalledAppVersion();
		if (InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty()) {
			logToReport(Status.PASS, "✅ App uninstalled successfully");
		} else {
			logToReport(Status.FAIL, "❌ App uninstall failed: Version still exists: " + InstalledAppsVersion);
		}

		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty());

	// ========== STEP 8: UNINSTALL AGENT ==========
		currentTestName = "Uninstall Cloud Agent & all Products_" + serverTag;
		loginPage.navigateToPage("computerspage");
		iw2.SearchMachine();
		iw2.selectOnlineWorkstation();
		iw2.UninstallCloudAgent();

		loginPage.navigateToPage("taskstatuspagenew");
		iw2.checkfirestrow1();
		logToReport(Status.PASS, "🧹 Agent uninstalled");

		Thread.sleep(200000); // cleanup wait  
		
	
		// ========== STEP 9: Visit on every Cloud Pages ==========
		
	*/	
		
		Map<String, String> pagePathMap = new LinkedHashMap<>();
		pagePathMap.put("computerpages", "/en/Computers/List");
		/*		pagePathMap.put("homepage", "/en/Home/Dashboard");
		pagePathMap.put("grouppage", "/en/Group/List");
		pagePathMap.put("policypage", "/en/Policy/List");
		pagePathMap.put("applicationpage", "/NU/Dashboard/Applications");
		pagePathMap.put("windowsupdatepage", "/NU/Dashboard/WindowsUpdates");
		pagePathMap.put("imagingpage", "/NU/Dashboard/Imaging");
		pagePathMap.put("inventorypage", "/NU/Dashboard/Inventory");
		pagePathMap.put("ticketspage", "/NU/Dashboard/Tickets");
		pagePathMap.put("dfodpage", "/en/DeepFreezeonDemand/List");
		pagePathMap.put("usagestatuspage", "/en/UsageStats/Dashboard");
		pagePathMap.put("mdmpage", "/en/MDM/Dashboard");
		pagePathMap.put("tagsmanagementpage", "/en/Tags/TagsManagement");
		pagePathMap.put("taskstatuspages", "/en/TaskStatus/List");
		pagePathMap.put("usermanagementpages", "/en/User/UserManagement");
		pagePathMap.put("mysitespages", "/en/MySite/MySites");
		pagePathMap.put("myprofilespages", "/en/Account/Profile");   

		for (Map.Entry<String, String> entry : pagePathMap.entrySet()) {
		    String pageKey = entry.getKey();
		    String expectedPath = entry.getValue();

		    loginPage.navigateToPage(pageKey);

		    // 🔽 Actual full URL (e.g., https://www9.deepfreeze.com/en/Users/List)
		    String actualUrl = driver.get().getCurrentUrl();

		    // 🔽 Extract only path from actual URL
		    String actualPath = new URL(actualUrl).getPath();

		    // 🔁 Compare actual path with expected path
		    Assert.assertEquals(actualPath, expectedPath, "❌ Path mismatch for page: " + pageKey);
		    logToReport(Status.PASS, "✅ Navigated to " + pageKey + " — Path OK: " + actualPath);

		 //   System.out.println("✅ Navigated to " + pageKey + " — Path OK: " + actualPath);
		    
		}		*/
				
			
		// ========== STEP 10: LOGOUT ==========
		currentTestName = "Logged out from server_" + serverTag;
		loginPage.performLogout();
		logToReport(Status.INFO, "🔒 Logged out from server: " + serverTag);
	}

	
/*	private Map<String, String> getExpectedProductMap() {
		Map<String, String> map = new HashMap<>();
		map.put("Cloud Agent",
				"C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe");
		map.put("Anti-Virus", "C:\\Program Files\\Faronics\\Faronics Anti-Virus\\FAVEService.exe");
		map.put("Deep Freeze", "C:\\Program Files (x86)\\Faronics\\Deep Freeze\\Install C-0\\DFServ.exe");
		map.put("Anti-Executable", "C:\\Program Files\\Faronics\\AE\\Antiexecutable.exe");
		map.put("Remote", "C:\\Program Files\\Faronics\\FaronicsRemote\\FaronicsRemote.exe");
		map.put("Power Save", "C:\\Program Files\\Faronics\\Power Save Workstation\\PowerSaveService.exe");
		map.put("Software Updater", "C:\\Program Files\\Faronics\\Software Updater\\FWUSvc.exe");
		map.put("Usage Stats", "C:\\Program Files\\Faronics\\UsageStats\\USEngine.exe");
		map.put("WINSelect", "C:\\Program Files\\Faronics\\WINSelect\\WINSelect.exe");
		map.put("Imaging", "C:\\Program Files (x86)\\Faronics\\Imaging\\Imaging.exe");
		return map;
	}  */
}
