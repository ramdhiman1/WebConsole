package test_Cases_Class;

import org.testng.Assert;
import org.testng.annotations.Test;
import base_Classes.Base_Page;
import testCasesCode.Applications_Page;

public class TC06_Application_Page extends Base_Page {

	String randomUserName = randomeString().toUpperCase(); // Random User name generation

	@Test(priority = 1, groups= {"smoke", "Regression"}, description = "Verified that the Application page loads successfully without any errors.")
	public void applicationpage() {
		Applications_Page lp = new Applications_Page();
		lp.ApplicationPage();
	}

	@Test(priority = 2, groups= {"smoke", "Regression"}, description = " Installed a randomly selected SU application and verified that it was successfully installed on the workstation.")
	public void installapps() throws InterruptedException {
		Applications_Page comapp = new Applications_Page();
		comapp.CheckDFStatus();
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.ClickonanyApp();
		comapp.clickonInstallYesbuttons();
		driver.get().get(p.getProperty("taskstatusURL"));
		comapp.checkfirestrow1();
		driver.get().get(p.getProperty("applicationspageURL"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		comapp.getInstalledAppVersion();

		// Get The Installed Apps Version

		String InstalledAppsVersion = comapp.getInstalledAppVersion();

		Assert.assertNotNull(InstalledAppsVersion, "Installed App Version should not be Null");
		Assert.assertFalse(InstalledAppsVersion.isEmpty(), "Installed App Version should not Empty");

		System.out.println("Verified: Application is installed successfully and App Installed Version is: "
				+ InstalledAppsVersion);

	}

	@Test(priority = 3, groups= {"smoke", "Regression"},  description = " Uninstalled the same randomly installed SU application and verified that it was successfully removed from the workstation.")
	public void uninstallsameapps() throws InterruptedException {
		Applications_Page comapp = new Applications_Page();
		driver.get().get(p.getProperty("applicationspageURL"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();
		// comapp.ClickonanyApp();
		comapp.clickonunInstallYesbutton22();
		driver.get().get(p.getProperty("taskstatusURL"));
		comapp.checkfirestrow1();
		driver.get().get(p.getProperty("applicationspageURL"));
		comapp.clickonCommpress();
		comapp.clickonCommpress1();

		// Get the version after uninstall
		String InstalledAppsVersion = comapp.getInstalledAppVersion();

		// ASSERT: It should be empty after uninstall
		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty(),
				"App uninstall failed — Version still present: " + InstalledAppsVersion);

		System.out.println("Verified: Application has been uninstalled successfully.");
	}

	@Test(priority = 4, groups= {"smoke", "Regression"}, description = "Installed the custom application and verified that it was successfully installed on the workstation.")
	public void CustomApp() throws InterruptedException {
		Applications_Page cuapps = new Applications_Page();
		cuapps.ClickonCustomApp();
		cuapps.clickonCreateApps();
		cuapps.EnterPackageName("CustomName1");
		cuapps.enterCUAppUrl(p.getProperty("customappslink"));
		cuapps.clickEneteInstallcmd("/VERYSILENT");
		cuapps.clickEneteUnInstallcmd("/SILENT");
		cuapps.clickonNextbtn();
		cuapps.Selectwks();
		cuapps.clickonNextbtn();
		cuapps.clickonInstallbtn11();

		boolean isInstalled = cuapps.clickonYesCUappbtn();

		Assert.assertTrue(isInstalled, "Custom App Installation Failed or Version not found");

		System.out.println("Custom App Installed successfully and version is visible.");

		cuapps.clickonNextbtn();
		
		driver.get().get(p.getProperty("taskstatusURL"));
		cuapps.checkfirestrow1();
		driver.get().get(p.getProperty("applicationspageURL"));
	}

	@Test(priority = 5, groups= {"smoke", "Regression"}, description = " Uninstalled the custom application and verified that it was successfully removed from the workstation.")
	public void CustomAppUninstallation() throws InterruptedException {
		Applications_Page cuapps = new Applications_Page();
		cuapps.CustomAppsColumn();
		cuapps.clickOnCustomApp();
		cuapps.CustomAppUninstallation();
		driver.get().get(p.getProperty("taskstatusURL"));
		cuapps.checkfirestrow1();
		driver.get().get(p.getProperty("applicationspageURL"));
		cuapps.CustomAppsColumn();
		cuapps.clickOnCustomApp();

		// Get the version after uninstall
		String InstalledAppsVersion = cuapps.getInstalledAppVersion();

		// ASSERT: It should be empty after uninstall
		Assert.assertTrue(InstalledAppsVersion == null || InstalledAppsVersion.trim().isEmpty(),
				"App uninstall failed — Version still present: " + InstalledAppsVersion);

		System.out.println("✅ Verified:Custom Application has been uninstalled successfully.");
	}

	@Test(priority = 6, groups= {"smoke" , "Regression"}, description = "Deleted the custom application and verified that it was successfully removed from the grid.")

	public void deleteCustomApp() throws InterruptedException {

		Applications_Page dcuapps = new Applications_Page();
		driver.get().get(p.getProperty("applicationspageURL"));
		boolean result = dcuapps.deleteCustomApplication();
		Assert.assertTrue(result, "Custom application was not deleted.");
		// dcuapps.ClickonDonebutton();
	}

	@Test(priority = 7, groups = {"smoke", "Regression"} ,description = "Created a custom script under the Applications section and verified that the script was added successfully.")
	public void CreateCustomScript() throws InterruptedException {

		Applications_Page sc = new Applications_Page();

		driver.get().get(p.getProperty("applicationspageURL"));
		sc.clickonCMScripts();
		sc.EnterScriptDetail("ScriptName1");
		sc.EnterNetworkLocations(p.getProperty("customscriptlink"));
		sc.ClickonDropDown("Batch Script");
		sc.ViewCustomScript();

		String scriptInstalled = sc.CheckCustomScriptAddedorNot();

		// Assert the script was actually added
		Assert.assertNotNull(scriptInstalled, "Script not found in the grid.");
		Assert.assertFalse(scriptInstalled.trim().isEmpty(), "Script text is empty. Script might not have been added.");

		System.out.println("Custom script added successfully: " + scriptInstalled);
	}

	@Test(priority = 8, groups = {"smoke", "Regression"}, description = "Installed a custom script and verified that the script was successfully installed and executed as expected.")
	public void InstallCustomScript() throws InterruptedException {

		Applications_Page sc = new Applications_Page();

		driver.get().get(p.getProperty("applicationspageURL"));
		sc.CustomScriptsColumn();
		sc.clickOnCustomScriptApp();
		// Call method and store result
		boolean isScriptInstalled = sc.CustomScriptsinstallation();
		// Assert that script was installed successfully
		Assert.assertTrue(isScriptInstalled, "Custom script installation failed or alert message not found.");

		System.out.println("Custom script installed successfully.");	
		driver.get().get(p.getProperty("taskstatusURL"));
		sc.checkfirestrow1();

	}  

	@Test(priority = 9, groups = { "smoke", "Regression" }, description = "Delete custom script from the grid and verify script should be deleted")
	public void deleteCustomScript() {

		Applications_Page ap = new Applications_Page();
		driver.get().get(p.getProperty("applicationspageURL"));
		boolean result = ap.deleteScript();
		Assert.assertTrue(result, "Script failed to delete");
		System.out.println("Script deletion test completed.");
	}

}
