package testCasesCode;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import base_Classes.Base_Page;

public class Applications_Page extends Base_Page {

	public Applications_Page() {
		super();
		initializeElements(this);

		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(20));

	}

	// Locators
	@FindBy(xpath = "//a[normalize-space()='APPLICATIONS']")
	WebElement application_page; // Applications page link

	public void ApplicationPage() {

		wait.until(ExpectedConditions.elementToBeClickable(application_page));

		application_page.click();
	}

	// Install any Apps code
	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//div[@class='dx-datagrid-headers dx-datagrid-nowrap']//tr//td[11]")
	WebElement AppsCompclick;

	public void clickonCommpress() {

		wait.until(ExpectedConditions.visibilityOf(AppsCompclick)).click();

	}

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-11')]")
	WebElement clickcomps;

	public void clickonCommpress1() {

		wait.until(ExpectedConditions.elementToBeClickable(clickcomps)).click();

	}

	@FindBy(xpath = "//button[@id='btnInstallApplication']")
	WebElement clickonInstallbtns;

	public void ClickonanyApp() {

		wait.until(ExpectedConditions.elementToBeClickable(clickonInstallbtns)).click();
	}

	@FindBy(xpath = "//button[@id='btnUninstallApplication']")
	WebElement clickonUnInstallbtns;

	@FindBy(xpath = "//div[@id='dvConfirmCommonPopup']//div[@class='modal-header p-2 popupTitle']")
	WebElement confirmfrozenpopup;

	@FindBy(xpath = "//div[@role='alert']")
	WebElement appaleartmessage;

	public boolean clickonunInstallYesbutton22() {
		boolean isclickonYesbtn = false;

		wait.until(ExpectedConditions.visibilityOf(clickonUnInstallbtns)).click();
		wait.until(ExpectedConditions.visibilityOf(confirmfrozenpopup));

		wait.until(ExpectedConditions.elementToBeClickable(clickonInstallyesbtn1)).click();
		wait.until(ExpectedConditions.visibilityOf(appaleartmessage));

		if (appaleartmessage.isDisplayed()) {
			String alerts = appaleartmessage.getText();
			System.out.println("appaleartmessage: " + alerts);

			if (alerts.contains("uninstallation is initiated.")) {
				isclickonYesbtn = true;
				wait.until(ExpectedConditions.invisibilityOf(appaleartmessage));
			}

		}
		return isclickonYesbtn;
	}

	@FindBy(id = "btnInstallAll")
	WebElement clickonInstallbtn;

	public void clickonInstallbutton() {

		wait.until(ExpectedConditions.elementToBeClickable(clickonInstallbtn));
		wait.until(ExpectedConditions.visibilityOf(clickonInstallbtn));
		clickonInstallbtn.click();
	}

	@FindBy(xpath = "//input[@id='btnConfirmCommonYes']")
	WebElement clickonInstallyesbtn1;

	public boolean clickonInstallYesbuttons() {
		boolean isclickonuninstallbtn = false;
		wait.until(ExpectedConditions.visibilityOf(confirmfrozenpopup));

		wait.until(ExpectedConditions.elementToBeClickable(clickonInstallyesbtn1)).click();
		wait.until(ExpectedConditions.visibilityOf(appaleartmessage));

		if (appaleartmessage.isDisplayed()) {
			String alerts = appaleartmessage.getText();
			System.out.println("appaleartmessage: " + alerts);

			if (alerts.contains("uninstallation is initiated.")) {
				isclickonuninstallbtn = true;
				wait.until(ExpectedConditions.invisibilityOf(appaleartmessage));
			}

		}
		return isclickonuninstallbtn;

	}

	// Get Installed Version Number
	public String getInstalledAppVersion() {

		try {
			WebElement versionCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-11')]")));

			String versionText = versionCell.getText();
			System.out.println("Application Version: " + versionText);
			return versionText;

		} catch (TimeoutException e) {

			System.out.println("No Installed Versin Found - Apps may be Uninstalled");
			return "";

		}

	}

	@FindBy(xpath = "//input[@id='btnUninstallAll']")
	WebElement clickonappsUnbtn;

	public void clickonunInstallbutton() {

		wait.until(ExpectedConditions.elementToBeClickable(clickonappsUnbtn));
		wait.until(ExpectedConditions.visibilityOf(clickonappsUnbtn));
		clickonappsUnbtn.click();

	}

//Custom Apps Installation

	@FindBy(xpath = "//button[normalize-space()='CUSTOM APP']")
	WebElement clickdropdowncuapp;

	public void ClickonCustomApp() {
		clickdropdowncuapp.click();
	}

	@FindBy(xpath = "//a[normalize-space()='Create Custom App']")
	WebElement clickonCreateCUAPP;

	public void clickonCreateApps() {
		clickonCreateCUAPP.click();

	}

	@FindBy(xpath = "//input[@id='txtPackageName']")
	WebElement enterCUAPPName;

	public void EnterPackageName(String cuname) {
		enterCUAPPName.sendKeys(cuname);
	}

	@FindBy(xpath = "//input[@id='txtLocation']")
	WebElement EneteURL;

	public void enterCUAppUrl(String url) {
		EneteURL.sendKeys(url);
	}

	@FindBy(xpath = "//input[@id='txtInstallCommandline']")
	WebElement eneteInstallcmd;

	public void clickEneteInstallcmd(String cmd) {

		wait.until(ExpectedConditions.elementToBeClickable(eneteInstallcmd));
		eneteInstallcmd.sendKeys(cmd);
	}

	@FindBy(xpath = "//input[@id='txtUninstallCommandline']")
	WebElement eneteUnInstallcmd;

	public void clickEneteUnInstallcmd(String cmdun) {
		eneteUnInstallcmd.sendKeys(cmdun);
	}

	@FindBy(xpath = "//button[@id='btnNextSCA']")
	WebElement clickonNextbtn;

	public void clickonNextbtn() {
		clickonNextbtn.click();
	}

	@FindBy(xpath = "//input[@id='txtInstallCommandline']")
	WebElement selectMachine;

	@FindBy(xpath = "//button[@id='btnNextSCA']")
	WebElement clickNextbtn2;

	@FindBy(xpath = "/html/body/div[9]/div/div/div[2]/div/div[1]/div/div[2]/div[2]/div[2]/div/div[2]/div/div/div/div[6]/div[2]/table/tbody/tr[1]/td[1]/span")
	WebElement selectwrks;

	public void Selectwks() {
		selectwrks.click();
	}

	@FindBy(xpath = "//button[@id='btnCustomAppInstall']")
	WebElement clickonInstallbtn1;

	public void clickonInstallbtn11() {
		clickonInstallbtn1.click();
	}

	@FindBy(xpath = "//input[@id='btnConfirmCommonYes']")
	WebElement cliconYesbutton;

	@FindBy(xpath = "//div[@id='dvSelectedCustomAppName']")
	WebElement waitforappversion;

	public boolean clickonYesCUappbtn() {
		boolean isInstalled = false;

		// Click on the Yes button to start the app installation
		wait.until(ExpectedConditions.elementToBeClickable(cliconYesbutton)).click();

		// Wait for the version number to be visible, retrying if it is not immediately
		// available
		long startTime = System.currentTimeMillis();
		long timeout = 130000; // Wait for 30 seconds max

		while (System.currentTimeMillis() - startTime < timeout) {
			try {
				// Wait until the app version element is visible
				if (wait.until(ExpectedConditions.visibilityOf(waitforappversion)).isDisplayed()) {
					// If the version is visible, get the text (version number)
					String versionNumber = waitforappversion.getText();
					System.out.println("App Version: " + versionNumber);
					isInstalled = true;
					break;
				}
			} catch (Exception e) {
				// If the element is not visible, continue waiting
				System.out.println("Waiting for app version to become visible...");
			}

			// Optionally, you could add a small sleep to avoid a tight loop
			try {
				Thread.sleep(2000); // Sleep for 1 second before checking again
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// If the version was not displayed after waiting for the timeout, handle the
		// failure case
		if (!isInstalled) {
			System.out.println("App installation or version not found in the given timeout.");
		}

		return isInstalled;
	}

	// For check Reboot Thawed or not

	@FindBy(xpath = "//button[contains(@class,'btn btnWhite disabledActions customAppButton')]")
	WebElement ClickonActionbtn;

	@FindBy(xpath = "//div[@class='notranslate self-dd-item-label locale-text-ellipsis width-160'][normalize-space()='Open Action Toolbar']")
	WebElement ClickonOpenActionToolbar;

	@FindBy(xpath = "//input[@id='btnDFRebootThawedANDLockedAction']")
	WebElement RebootThawed;

	@FindBy(xpath = "//input[@id='btnRebootThawedOk']")
	WebElement ClickOnOkbtn;

	@FindBy(xpath = "//div[@id='dvToastNotification']")
	WebElement messageNotification;

	@FindBy(xpath = "//div[@class='dx-datagrid-content']//table//tbody//tr[1]//td[5]")
	WebElement clickonfirstrow;
	@FindBy(xpath = "//div[@class='dx-datagrid-content']//table//tbody//tr[1]//td[4]")
	WebElement clicktaskname;

	public boolean CheckDFStatus() throws InterruptedException {
		boolean isActionExecuted = false;

		try {
			// DF frozen status check karne ke liye element dhund rahe hain
			WebElement frozenRow = driver.get().findElement(By.xpath(
					"//tr[contains(@class,'dx-data-row')]//div[contains(@class,'CompId') and @data-isdffrozen='true']/ancestor::tr"));

			// Machine name get karo from 2nd column
			WebElement machineNameElement = frozenRow.findElement(By.xpath(
					"//div[@class='dx-datagrid-content dx-datagrid-content-fixed dx-pointer-events-target']//table[1]//tr[1]/td[2]"));
			String frozenMachineName = machineNameElement.getText().trim();
			System.out.println("✅ Frozen Machine Name: " + frozenMachineName);

			String isFrozen = frozenRow.findElement(By.xpath(".//div[contains(@class,'CompId')]"))
					.getAttribute("data-isdffrozen");
			System.out.println("Current DF Status: " + isFrozen);

			if (isFrozen != null && isFrozen.equalsIgnoreCase("true")) {
				// Agar DF frozen hai, toh action perform karo
				WebElement clickableElement = driver.get().findElement(By.xpath(
						"(//div[contains(@class, 'dx-select-checkbox')]//span[contains(@class, 'dx-checkbox-icon')])[2]"));
				clickableElement.click();

				ClickonActionbtn.click();
				ClickonOpenActionToolbar.click();
				RebootThawed.click();

				wait.until(ExpectedConditions.visibilityOf(ClickOnOkbtn)).click();

				wait.until(ExpectedConditions.visibilityOf(messageNotification));
				if (messageNotification.isDisplayed()) {
					wait.until(ExpectedConditions.invisibilityOf(messageNotification));
					isActionExecuted = true;
				}

				System.out.println("data-isdffrozen = true, so clicked on the modal header and performed actions.");

				driver.get().get(p.getProperty("taskstatusURL"));

				for (int r = 1; r <= 30; r++) {
					driver.get().navigate().refresh();
					Thread.sleep(15000);

					WebElement element = clickonfirstrow;
					WebElement element2 = clicktaskname;

					String statusText = element.getText();
					String taskName = element2.getText();

					System.out.println("Task Name: " + taskName + ", Status for row " + r + ": " + statusText);

					if (statusText.contains("Execute")) {
						System.out.println("✅ Task executed successfully");
						break;
					} else if (statusText.contains("Failed")) {
						System.out.println("❌ Task execution failed");
						break;
					}
				}

				// Reopen application page
				driver.get().get(p.getProperty("applicationspageURL"));

				// Wait & verify that same machine is thawed now
				boolean isNowThawed = false;
				for (int i = 1; i <= 10; i++) {
					Thread.sleep(5000);
					driver.get().navigate().refresh();

					List<WebElement> rows = driver.get().findElements(By.xpath("//tr[contains(@class,'dx-data-row')]"));

					for (WebElement row : rows) {
						String currentName = row.findElement(By.xpath(".//td[2]//span")).getText().trim();

						if (currentName.equalsIgnoreCase(frozenMachineName)) {
							String thawedStatus = row.findElement(By.xpath(".//div[contains(@class,'CompId')]"))
									.getAttribute("data-isdffrozen");

							if (thawedStatus != null && thawedStatus.equalsIgnoreCase("false")) {
								System.out.println("✅ Machine '" + frozenMachineName + "' is now thawed.");
								isNowThawed = true;
								break;
							} else {
								System.out.println(
										"⏳ Machine '" + frozenMachineName + "' still appears frozen. Retrying...");
							}
						}
					}
					if (isNowThawed)
						break;
				}
			} else {
				System.out.println("data-isdffrozen is not true. Continuing without clicking.");
			}

		} catch (NoSuchElementException e) {
			System.out.println("❌ DF is Not in Frozen Mode. Skipping this step and continuing the test case.");
		}

		return isActionExecuted;
	}

	
	
	// Uninstall Custom Apps

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//div[@class='dx-datagrid-headers dx-datagrid-nowrap']//tr//td[9]")
	WebElement CustomAppColumn;

	public void CustomAppsColumn() {

		wait.until(ExpectedConditions.visibilityOf(CustomAppColumn)).click();

	}

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-10')]")
	WebElement ClickCustomApp;

	public void clickOnCustomApp() {

		wait.until(ExpectedConditions.elementToBeClickable(ClickCustomApp)).click();

	}

	public boolean CustomAppUninstallation() {
		boolean isclickonYesbtn = false;

		wait.until(ExpectedConditions.visibilityOf(clickonUnInstallbtns)).click();
		wait.until(ExpectedConditions.visibilityOf(confirmfrozenpopup));

		wait.until(ExpectedConditions.elementToBeClickable(clickonInstallyesbtn1)).click();
		wait.until(ExpectedConditions.visibilityOf(appaleartmessage));

		if (appaleartmessage.isDisplayed()) {
			String alerts = appaleartmessage.getText();
			System.out.println("appaleartmessage: " + alerts);

			if (alerts.contains("uninstallation is initiated.")) {
				isclickonYesbtn = true;
				wait.until(ExpectedConditions.invisibilityOf(appaleartmessage));
			}

		}
		return isclickonYesbtn;
	}

	// Deleting custom app

	@FindBy(xpath = "//button[normalize-space()='CUSTOM APP']")
	WebElement customappdropdown;

	@FindBy(xpath = "//a[normalize-space()='View Custom Apps']")
	WebElement viewcustomappoption;

	@FindBy(xpath = "//div[@id='dvBSpopup']//div[@class='modal-header p-2 popupTitle']")
	WebElement addscriptpopup;

	@FindBy(xpath = "//div[@id='dvBSpopup']//div[@class='modal-header p-2 popupTitle']")
	WebElement addCustomAppspopup;

	@FindBy(xpath = "//input[@id='btnDeleteVCustomApps']")
	WebElement deletecustomappbutton;

	@FindBy(xpath = "//b[@id='bPopupConfirmCommonTitle']")
	WebElement waitDeleteconfirmpopup;
	@FindBy(xpath = "//input[@id='btnConfirmCommonYes']")
	WebElement btnconfirmyes;
	@FindBy(xpath = "//div[@id='dvViewCustomScriptsContainer']//input[@value='Done']")
	WebElement btndone;
	@FindBy(xpath = "//div[@class='dx-toast-message']")
	WebElement CAdeleteNotificationMsg;

	@FindBy(xpath = "//div[@id='dvViewCustomAppsContainer']//input[@value='Done']")
	WebElement ClickonDoneBtn;

	@FindBy(xpath = "//div[@id='dvViewCustomAppsGrid']//*[@class='dx-datagrid-content']//table//tr//td[1]")
	List<WebElement> customapptabledata;

	@FindBy(xpath = "//div[@id='dvViewCustomAppsContainer']//input[@value='Done']")
	WebElement btndoneappswindow;

	public boolean deleteCustomApplication() throws InterruptedException {
		boolean isDeleted = false;

		Thread.sleep(2000);
		customappdropdown.click();
		viewcustomappoption.click();
		wait.until(ExpectedConditions.visibilityOf(addscriptpopup));

		try {
			if (customapptabledata.isEmpty()) {
				System.out.println("⚠️ No custom apps found to delete.");
				return false;
			}

			for (WebElement row : customapptabledata) {
				row.click();
				deletecustomappbutton.click();

				wait.until(ExpectedConditions.visibilityOf(waitDeleteconfirmpopup));
				btnconfirmyes.click();

				wait.until(ExpectedConditions.invisibilityOf(waitDeleteconfirmpopup));
				wait.until(ExpectedConditions.visibilityOf(CAdeleteNotificationMsg));
				isDeleted = true;

				wait.until(ExpectedConditions.invisibilityOf(CAdeleteNotificationMsg));
				btndoneappswindow.click();
				break;
			}
		} catch (Exception e) {
			System.out.println("❌ Error while deleting app: " + e.getMessage());
		}

		if (isDeleted) {
			System.out.println("✅ Custom application deleted successfully.");
		}

		return isDeleted;
	}

	public void ClickonDonebutton() {
		wait.until(ExpectedConditions.visibilityOf(ClickonDoneBtn)).click();
	}

	// Add Custom Script

	@FindBy(xpath = "//button[normalize-space()='SCRIPTS']")
	WebElement ClickonCMScript;

	@FindBy(xpath = "//a[normalize-space()='Create Custom Script']")
	WebElement ClickonCreateScript;

	@FindBy(xpath = "//input[@id='txtCustScriptAppName']")
	WebElement EnterScriptName;

	@FindBy(xpath = "//input[@id='txtUrlNetworkLocation']")
	WebElement EnterNetworkLocation;

	@FindBy(xpath = "//select[@id='ddCustScriptTypes']")
	WebElement TypeDropDown;

	@FindBy(xpath = "//div[@class='row']//input[@id='btnCustScriptSaveToGrid']")
	WebElement ClickonSaveToGridbtn;

	@FindBy(xpath = "//a[normalize-space()='View Custom Scripts']")
	WebElement ClickOnViewCustomS;

	public void ViewCustomScript() {
		wait.until(ExpectedConditions.visibilityOf(ClickonCMScript)).click();

		wait.until(ExpectedConditions.visibilityOf(ClickOnViewCustomS)).click();
	}

	public void clickonCMScripts() {

		wait.until(ExpectedConditions.visibilityOf(ClickonCMScript)).click();
		wait.until(ExpectedConditions.visibilityOf(ClickonCreateScript)).click();
	}

	public void EnterScriptDetail(String name) {
		wait.until(ExpectedConditions.visibilityOf(EnterScriptName)).sendKeys(name);

	}

	public void EnterNetworkLocations(String url) {

		wait.until(ExpectedConditions.visibilityOf(EnterNetworkLocation)).sendKeys(url);

	}

	public void ClickonDropDown(String Type) {

		Select se = new Select(TypeDropDown);
		se.selectByContainsVisibleText(Type);
		wait.until(ExpectedConditions.visibilityOf(ClickonSaveToGridbtn)).click();

	}

//Get Added Scripts Details.
	public String CheckCustomScriptAddedorNot() {

		WebElement CustomScriptCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@id='dvViewCustomScriptsGrid']//*[@class='dx-datagrid-content']//table//tr[1]")));

		String CustomScriptText = CustomScriptCell.getText();
		System.out.println("Deatils of Added Script: " + CustomScriptText);

		return CustomScriptText;

	}

	// install Custom Scripts

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//div[@class='dx-datagrid-headers dx-datagrid-nowrap']//tr//td[9]")
	WebElement CustomScriptColumn;

	public void CustomScriptsColumn() {

		wait.until(ExpectedConditions.visibilityOf(CustomScriptColumn)).click();

	}

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-10')]")
	WebElement ClickCustomScApp;

	public void clickOnCustomScriptApp() {

		wait.until(ExpectedConditions.elementToBeClickable(ClickCustomScApp)).click();

	}

	@FindBy(xpath = "//button[@id='btnRunCustomScript']")

	WebElement ClickonRunBtn;

	@FindBy(xpath = "//input[@id='btnConfirmCommonYes']")

	WebElement ClickRunbtnConfirm;

	public boolean CustomScriptsinstallation() {
		boolean isclickonYesbtn = false;

		wait.until(ExpectedConditions.visibilityOf(ClickonRunBtn)).click();
		wait.until(ExpectedConditions.visibilityOf(confirmfrozenpopup));

		wait.until(ExpectedConditions.elementToBeClickable(ClickRunbtnConfirm)).click();
		wait.until(ExpectedConditions.visibilityOf(appaleartmessage));

		if (appaleartmessage.isDisplayed()) {
			String alerts = appaleartmessage.getText();
			System.out.println("appaleartmessage: " + alerts);

			if (alerts.contains("installation is initiated.")) {
				isclickonYesbtn = true;
				wait.until(ExpectedConditions.invisibilityOf(appaleartmessage));
			}

		}
		return isclickonYesbtn;
	}

	// Delete script
	@FindBy(xpath = "//input[@id='btnDeleteVCustomScripts']")
	WebElement deletescriptbutton;
	@FindBy(xpath = "//b[@id='bPopupConfirmCommonTitle']")
	WebElement confirmdeletescriptpopup;
	@FindBy(xpath = "//div[@class='dx-toast-message']")
	WebElement toastmessage;
	@FindBy(xpath = "//div[@id='dvViewCustomScriptsGrid']//*[@class='dx-datagrid-content']//table//tr")
	List<WebElement> tableData;

	public boolean deleteScript() {
		boolean isDeleted = false;

		ClickonCMScript.click();
		ClickOnViewCustomS.click();
		wait.until(ExpectedConditions.visibilityOf(addscriptpopup));

		try {
			for (WebElement row : tableData) {
				WebElement scriptname = row.findElement(By.xpath(".//td[1]"));
				if (scriptname.getText().equalsIgnoreCase("ScriptName1")) {
					wait.until(ExpectedConditions.visibilityOf(row));
					row.click();
					deletescriptbutton.click();
					wait.until(ExpectedConditions.visibilityOf(confirmdeletescriptpopup));
					btnconfirmyes.click();
					wait.until(ExpectedConditions.invisibilityOf(confirmdeletescriptpopup));
					wait.until(ExpectedConditions.visibilityOf(toastmessage));
					System.out.println("✅ Script deleted: " + toastmessage.getText());
					isDeleted = true;
					wait.until(ExpectedConditions.invisibilityOf(toastmessage));
					btndone.click();
					break;
				}
			}

			if (!isDeleted) {
				System.out.println("Script 'ScriptName1' not found in grid.");
			}

		} catch (Exception e) {
			System.out.println("Exception while deleting script: " + e.getMessage());
		}

		return isDeleted;
	}
}
