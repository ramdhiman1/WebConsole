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
import testCasesCode.Install_CloudPro_Vbox_Production;
import testCasesCode.Multiple_Login_ProductionServer;
import testCasesCode.Policies_Page_ProductionServer;

@Listeners(utilities.MyListener.class)

public class TC08_Multiple_Login_ProductionTest extends Base_Page {
	String randomPolicyName = randomeString().toUpperCase();
	String editedPolicyName = randomeString().toUpperCase();

	List<String[]> loginData = readExcelData("F:\\Automation Work 2024\\2025\\Logindata.xlsx", "Sheet1");

	@Test(priority = 1, description = "Log in to the Cloud Console on 'www2' to check machine status, install products, and verify their status.")
	public void loginWithFirstUser() throws InterruptedException, IOException {
		String[] firstUser = loginData.get(0);
		String url = firstUser[0];
		String username = firstUser[1];
		String password = firstUser[2];

		getDriver().get(url);
		logToReport(Status.INFO, "Navigated to Production Server www2 URL: " + url);
		Multiple_Login_ProductionServer login = new Multiple_Login_ProductionServer();
		login.EnterUserName(username);
		login.ClickOnNextBtn();
		login.EnterPassword(password);
		login.ClickOnSignBtn();
		String expectedTitle = "Deep Freeze Cloud";
		String actualTitle = getDriver().getTitle();
		System.out.println("Page Title: " + actualTitle);
		Assert.assertEquals(actualTitle, expectedTitle, "Login failed or title mismatch");

		// ✅ Custom Action for User 1
		System.out.println("Actions for User 1: " + username);
		getDriver().get(p.getProperty("policypagewww2"));
		
		logToReport(Status.INFO, "Navigated to Policy Page");
		// Add user1-specific actions here
		Policies_Page_ProductionServer dfPolicy = new Policies_Page_ProductionServer();
		dfPolicy.clickAddPolicyButton();
		dfPolicy.selectDropdownPolicy();
		dfPolicy.enterPolicyName(randomPolicyName);
		dfPolicy.EnableDFService();
		dfPolicy.ClickonDropdwonDF("Enable (Install and use below settings)");
		dfPolicy.EnableSoftwareUpdater();
		dfPolicy.ClickonDropdwonSU("Enable (Install and use below settings)");
		dfPolicy.selectanyapps();		
		dfPolicy.clickSaveButton();
		driver.get().get(p.getProperty("www2serverdownloadagent"));
		logToReport(Status.INFO, "Navigated to Cloud Agent Download Page");
		dfPolicy.ClickOnInstallCloudAgentbtn(randomPolicyName);
		Thread.sleep(100000);
		
		// here Launch The VM and Install the Cloud Products 
		
		Install_CloudPro_Vbox_Production iw = new Install_CloudPro_Vbox_Production(driver.get());

        iw.renameInstaller();
        iw.StartVM();
        logToReport(Status.INFO, "Now We are going for Start the Virtual Box (Machine)");
        iw.pingVM();

        Thread.sleep(10000); // Small buffer before copy
        iw.copyInstallerUsingPsExec();
        Thread.sleep(20000); // Ensure files copied
        iw.installApplication();

     // ✅ Step 1: Define expected product installation paths
        Map<String, String> expectedProducts = new HashMap<>();
        expectedProducts.put("Cloud Agent", "C:\\Program Files (x86)\\Faronics\\Faronics Cloud\\Faronics Cloud Agent\\FWAService.exe");
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
        System.out.println("\n🕒 Waiting for reboot (5 mins)...");
        Thread.sleep(150000);
        iw.pingVM();
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
                if (iw.isFileExists(path, name)) {
                    System.out.println("✅ Verified [After Reboot]: " + name + " → " + path);
                    installedAfter.add(name);
                    found = true;
                    break;
                } else {
                    System.out.println("⏳ Retry " + i + "/10: " + name + " not found yet...");
                    Thread.sleep(10000); // 10 sec delay
                }
            }

            if (!found) {
                System.out.println("❌ Not Verified [After Reboot]: " + name);
            }
        }

        // ✅ Summary
        System.out.println("\n📦 Summary Report:");
        System.out.println("➡ Products Installed After Reboot: " + installedAfter);

        System.out.println("\n🎉 Product installation verification completed!");
        
        //Now Switch to Computers Page
        driver.get().get(p.getProperty("www2computerspage"));
        iw.SearchMachine();
        iw.selectOnlineWorkstation();
        iw.printAllProductStatuses(10, 23);
        iw.selectOnlineWorkstation();
        iw.dfthawedAction();
        
        
        
        getDriver().get(p.getProperty("signouturlwww2"));
        logToReport(Status.INFO, "Now SignOut the User from Production Server www2");
	}

	@Test(priority = 2, description = "Log in to the Cloud Console on 'www9' to check machine status, install products, and verify their status.")
	public void loginWithSecondUser() {
		String[] secondUser = loginData.get(1);
		String url = secondUser[0];
		String username = secondUser[1];
		String password = secondUser[2];

		getDriver().get(url);
		logToReport(Status.INFO, "Navigated to Production Server www9 URL: " + url);

		Multiple_Login_ProductionServer login = new Multiple_Login_ProductionServer();
		login.EnterUserName(username);
		login.ClickOnNextBtn();
		login.EnterPassword(password);
		login.ClickOnSignBtn();

		String expectedTitle = "Deep Freeze Cloud";
		String actualTitle = getDriver().getTitle();
		System.out.println("Page Title: " + actualTitle);
		Assert.assertEquals(actualTitle, expectedTitle, "Login failed or title mismatch");

		// ✅ Custom Action for User 2
		System.out.println("Actions for User 2: " + username);
		getDriver().get(p.getProperty("policypagewww9"));
		// Add user2-specific actions here
		Policies_Page_ProductionServer dfPolicy = new Policies_Page_ProductionServer();
		dfPolicy.clickAddPolicyButton();
		dfPolicy.selectDropdownPolicy();
		dfPolicy.enterPolicyName(randomPolicyName);
		dfPolicy.EnableDFService();
		dfPolicy.ClickonDropdwonDF("Enable (Install and use below settings)");
		dfPolicy.clickSaveButton();
		
		

		getDriver().get(p.getProperty("signouturlwww9"));
		logToReport(Status.INFO, "Now SignOut the User from Production Server www9");
	}

	@Test(priority = 3, description = "Log in to the Cloud Console on 'www3' to check machine status, install products, and verify their status.")
	public void loginWithThirdUser() {
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

	    Policies_Page_ProductionServer dfPolicy = new Policies_Page_ProductionServer();
	    dfPolicy.clickAddPolicyButton();
	    logToReport(Status.INFO, "Clicked Add Policy button");

	    dfPolicy.selectDropdownPolicy();
	    logToReport(Status.INFO, "Selected dropdown policy");

	    dfPolicy.enterPolicyName(randomPolicyName);
	    logToReport(Status.INFO, "Entered Policy Name: " + randomPolicyName);

	    dfPolicy.EnableDFService();
	    logToReport(Status.INFO, "Enabled DF Service");

	    dfPolicy.ClickonDropdwonDF("Enable (Install and use below settings)");
	    logToReport(Status.INFO, "Selected DF dropdown option");

	    dfPolicy.clickSaveButton();
	    logToReport(Status.PASS, "Policy saved successfully");

	    getDriver().get(p.getProperty("signouturlwww3"));
	    logToReport(Status.INFO, "Now SignOut the User from Production Server www3");
	}

}
