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

	private Map<String, String> getExpectedProductMap() {
		Map<String, String> map = new HashMap<>();
		map.put("Cloud Agent", "C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe");
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
	}

	@Test(priority = 1, dataProvider = "serverLoginData", description = "Run full flow per server")
	public void LoginOnProductionServer(String url, String username, String password)
			throws InterruptedException, IOException {

		Base_Page.currentURL = url;
		String serverTag = extractSubdomainFromUrl(url);
		Multi_Server_Login_Page loginPage = new Multi_Server_Login_Page();
		String randomPolicyName = randomeString().toUpperCase();

		// ========== STEP 1: LOGIN ==========
		currentTestName = "Login_" + serverTag;
		String result = loginPage.performLogin(url, username, password);
		if (!result.contains("Login Success")) {
		    logMatrixResult("Login on Production Server:", serverTag, "Fail");
		    logToReport(Status.FAIL, "❌ Login failed for server: " + url);
		    Assert.fail("❌ Server is down or unreachable & Login failed for: " + url);
		    return;
		}
		logMatrixResult("Login on Production Server:", serverTag, "Pass");


		// ========== STEP 2: CREATE POLICY ==========
		currentTestName = "CreatePolicy_" + serverTag;
		logToReport(Status.INFO, "🛠 Creating policy: " + randomPolicyName);

		try {
		    loginPage.navigateToPage("policypagewww2");

		    Policies_Page_ProductionServer dfPolicy2 = new Policies_Page_ProductionServer();
		    dfPolicy2.clickAddPolicyButton();  // <-- if this fails, it goes to catch block
		    dfPolicy2.selectDropdownPolicy();
		    dfPolicy2.enterPolicyName(randomPolicyName);
		    dfPolicy2.EnableDFService();
		    dfPolicy2.ClickonDropdwonDF("Enable (Install and use below settings)");
		    dfPolicy2.EnableSoftwareUpdater();
		    dfPolicy2.ClickonDropdwonSU("Enable (Install and use below settings)");
		    dfPolicy2.selectanyapps();

		    boolean isSaved = dfPolicy2.clickSaveButton();

		    if (isSaved) {
		        logMatrixResult("Create Policy", serverTag, "Pass");
		        logToReport(Status.PASS, "💾 Policy created: " + randomPolicyName);
		    } else {
		        logMatrixResult("Create Policy", serverTag, "Fail");
		        logToReport(Status.FAIL, "❌ Policy creation failed: " + randomPolicyName);
		        Assert.fail("Policy creation failed for: " + randomPolicyName);
		    }

		} catch (Exception e) {
		    // ⚠️ Handles all unexpected failures like element not found, click issues, etc.
		    logMatrixResult("Create Policy", serverTag, "Fail");
		    logToReport(Status.FAIL, "❌ Error while creating policy on " + serverTag + ": " + e.getMessage());
		    e.printStackTrace(); // Optional: for console debug
		    Assert.fail("Exception during policy creation for " + serverTag + ": " + e.getMessage());
		}
 
		
				

		// ========== STEP 3: INSTALL AGENT ==========
		currentTestName = "Launch Virtual Machine and InstallAgent&Products_" + serverTag;
		try {
		    Policies_Page_ProductionServer dfPolicy2 = new Policies_Page_ProductionServer();
		    loginPage.navigateToPage("serverdownloadagent");
		    dfPolicy2.ClickOnInstallCloudAgentbtn(randomPolicyName);
		    logMatrixResult("Download The Cloud Agent Bootstraper: ", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Download The Cloud Agent Bootstraper: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional: prints the stack trace for debugging
		}

		Thread.sleep(100000);

		try {
		    Install_CloudPro_Vbox_Production iw2 = new Install_CloudPro_Vbox_Production();
		    iw2.renameInstaller();
		    iw2.StartVM();
		    iw2.pingVM();
		    Thread.sleep(120000);
		    iw2.copyInstallerUsingPsExec();
		    Thread.sleep(20000);
		    iw2.installCloudAgentandProducts();

		    logMatrixResult("Install Cloud Agent on Machine: ", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Install Cloud Agent on Machine: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional for debugging
		}

		logToReport(Status.PASS, "✅ Agent and products installed");

		// ========== STEP 4: VERIFY PRODUCTS ==========
		currentTestName = "VerifyProducts_Status(like Installed or Not on VM)" + serverTag;
		Install_CloudPro_Vbox_Production iw2 = new Install_CloudPro_Vbox_Production();
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
				System.out.println("Product not Installed: " + name);
			} else {
				logToReport(Status.PASS, "✅ Verified Product Installed: " + name);
			}
		}

		// ========== STEP 5: CHECK PRODUCTS STATUS ==========
		currentTestName = "Computers page: Check Product Status_" + serverTag;
		try {
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

		    logMatrixResult("Computers page: Check Product Status :", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Computers page: Check Product Status: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional: for debugging/logs
		}

		

		// ========== STEP 6: INSTALL SU APPS ==========
		try {
		    loginPage.navigateToPage("applicationpath");
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
		    logMatrixResult("Install Any SU Application: ", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Install Any SU Application: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional: helpful for debugging/logs
		}


		// ========== STEP 7: UNINSTALL SU SAME Installed APP ==========
		Applications_Page comapp = new Applications_Page();
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
			logMatrixResult("✅ App uninstalled successfully: ", serverTag, "Pass");
			logToReport(Status.PASS, "✅ App uninstalled successfully");
			
		} else {
			logMatrixResult("❌ App uninstall failed: Version still exists: ", serverTag, "Fail");
			logToReport(Status.FAIL, "❌ App uninstall failed: Version still exists: " + InstalledAppsVersion);
		}
		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty());

		// ========== STEP 8: UNINSTALL AGENT ==========
		try {
		    currentTestName = "Uninstall Cloud Agent & all Products_" + serverTag;
		    loginPage.navigateToPage("computerspage");
		    iw2.SearchMachine();
		    iw2.selectOnlineWorkstation();
		    iw2.UninstallCloudAgent();

		    loginPage.navigateToPage("taskstatuspagenew");
		    iw2.checkfirestrow1();
		    loginPage.navigateToPage("computerspage");

		    logToReport(Status.PASS, "🧹 Agent uninstalled");
		    Thread.sleep(200000); // cleanup wait

		    logMatrixResult("Uninstall Cloud Agent & all Products: ", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Uninstall Cloud Agent & all Products: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional: helpful for debugging
		}


		// ========== STEP 9: Visit on every Cloud Pages ==========
		Map<String, String> pagePathMap = new LinkedHashMap<>();
		pagePathMap.put("computerpages", "/en/Computers/List");
		pagePathMap.put("homepage", "/en/Home/Dashboard");
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
	        String actualUrl = driver.get().getCurrentUrl();
	        String actualPath = new URL(actualUrl).getPath();

	        try {
	            Assert.assertEquals(actualPath, expectedPath, "❌ Path mismatch for page: " + pageKey);
	            logToReport(Status.PASS, "✅ Navigated to " + pageKey + " — Path OK: " + actualPath);
	            logMatrixResult("Navigate: " + pageKey, serverTag, "Pass");
	        } catch (AssertionError e) {
	            logMatrixResult("Navigate: " + pageKey, serverTag, "Fail");
	            throw e;
	        }
	    }

		
		// ========== STEP 10: LOGOUT ==========
		try {
		    currentTestName = "Logged out from server_" + serverTag;
		    loginPage.performLogout();

		    logMatrixResult("Logged out from server: ", serverTag, "Pass");
		} catch (Exception e) {
		    logMatrixResult("Logged out from server: ", serverTag, "Fail");
		    e.printStackTrace(); // Optional for debugging
		}


		
	}
}
