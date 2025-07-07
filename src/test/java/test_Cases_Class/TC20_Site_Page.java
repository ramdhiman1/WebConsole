package test_Cases_Class;

import org.testng.annotations.Test;

import base_Classes.Base_Page;
import testCasesCode.Site_Page;

import org.testng.annotations.Listeners;
import utilities.AllureListener;
@Listeners({AllureListener.class})
public class TC20_Site_Page extends Base_Page {
	String randomsiteName = randomeString().toUpperCase(); // Random site name
	
	@Test(priority = 1, description = "Verify Successful Loading of My Sites Page Without Errors.")
	public void OpenSitePage() throws InterruptedException {
		Site_Page sites = new Site_Page();
		Thread.sleep(5000); // Add implicit wait here if needed
		sites.clickOnUserID();
		sites.clickOnMySite();
	}

	@Test(priority = 2, description = "Ensure User Can Successfully Add a Site Under My Site Page.")
	public void SitePage() throws InterruptedException {
		Site_Page site = new Site_Page();
		//Thread.sleep(5000); // Add implicit wait here if needed
		//site.clickOnUserID();
		//site.clickOnMySite();
		Thread.sleep(5000);
		site.clickOnAddSitebtns();
		site.EnterSiteName(randomsiteName); // Use random site name here
		Thread.sleep(5000); // Add implicit wait here if needed
		site.ClickOnOkbtnn(); // Check for success message

	}

	@Test(priority = 3, description = "Ensure User Can Successfully Edit a Site Under My Site Page.")
	public void EditSitePage() throws InterruptedException {
		Site_Page editsite = new Site_Page();
		Thread.sleep(5000); // Add implicit wait here if needed
		editsite.searchbysitename(randomsiteName); // Search for the same random site
		Thread.sleep(8000);
		editsite.clickonEditbutton(); // Click on the edit button for that site
		editsite.UpdateSiteName(randomsiteName + "_EDITED"); // Update the site with a new name
		Thread.sleep(5000);
		editsite.ClickonUpdateButton(); // Check for success message after update

	}

	@Test(priority = 4, description = "Ensure User Can Successfully Delete a Site Under My Site Page.")
	public void DeleteSite() throws InterruptedException {
		Site_Page deletesite = new Site_Page();
		Thread.sleep(5000); // Add implicit wait here if needed
		/*	deletesite.clickOnUserID();
		deletesite.clickOnMySite();
		Thread.sleep(5000);
		deletesite.clickOnAddSitebtns();
		deletesite.EnterSiteName(randomsiteName); // Use random site name here
		Thread.sleep(5000); // Add implicit wait here if needed
		deletesite.ClickOnOkbtnn(); // Check for success message
		Thread.sleep(5000); // Add implicit wait here if needed */
		deletesite.searchbysitename(randomsiteName); // Search for the same random site
		Thread.sleep(5000);
		deletesite.clickondeleteicon();
		deletesite.CheckSiteCheckbox();
		Thread.sleep(5000);
		deletesite.clickonDeletebutton();
		

	}
}
