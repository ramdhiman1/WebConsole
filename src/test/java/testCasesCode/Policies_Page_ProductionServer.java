package testCasesCode;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Policies_Page_ProductionServer extends Base_Page {

	public Policies_Page_ProductionServer() {
		super();
		initializeElements(this);
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));

	}

	// Locators
	@FindBy(xpath = "//a[normalize-space()='POLICIES']")
	WebElement clickPoliciesPage; // Policies page link

	@FindBy(xpath = "//a[@id='aAdd_Policy']")
	WebElement clickAddPolicyBtn;

	@FindBy(xpath = "//a[@id='P1']")
	WebElement clickDropdownPolicy;

	@FindBy(xpath = "//input[@id='Name']")
	WebElement enterPolicyNameField;

	@FindBy(xpath = "//input[@id='btnSave']")
	WebElement clickSaveBtn;

	@FindBy(xpath = "//span[@id='SpanServiceMsgbox']")
	WebElement popupMessage;

	public void clickAddPolicyButton() {
		wait.until(ExpectedConditions.visibilityOf(clickAddPolicyBtn)).click();

	}

	public void selectDropdownPolicy() {
		wait.until(ExpectedConditions.visibilityOf(clickDropdownPolicy)).click();

	}

	public void enterPolicyName(String policyName) {
		enterPolicyNameField.clear();
		wait.until(ExpectedConditions.visibilityOf(enterPolicyNameField)).sendKeys(policyName);

	}

	public boolean clickSaveButton() {
		boolean isPolicyAdded = false;
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));

		wait.until(ExpectedConditions.elementToBeClickable(clickSaveBtn)).click();
		wait.until(ExpectedConditions.visibilityOf(popupMessage));

		if (popupMessage.isDisplayed()) {
			String message = popupMessage.getText();
			System.out.println("Popup message: " + message);

			if (message.contains("policy has been saved")) {
				isPolicyAdded = true;
				wait.until(ExpectedConditions.invisibilityOf(popupMessage));
			}
		}

		return isPolicyAdded;
	}

	// Enable Some products in Policy
	@FindBy(xpath = "//a[@id='DEEP_FREEZE_a']")
	WebElement DFService;

	@FindBy(xpath = "//select[@id='ddPolicySettingsDeepFreeze']")
	WebElement selectkdropdownDF;

	public void EnableDFService() {
		wait.until(ExpectedConditions.visibilityOf(DFService)).click();

	}

	public void ClickonDropdwonDF(String Enable) {
		Select dropdownenable = new Select(selectkdropdownDF);
		dropdownenable.selectByVisibleText(Enable);

	}

	// Download the Cloud Agent with same Policy
	@FindBy(xpath = "//span[normalize-space()='Install Cloud Agent']")
	WebElement ClickonCLoudAgentbtn;

	@FindBy(xpath = "//input[@id='Install']")
	WebElement ClickonDownloadbtn;

	public void ClickOnInstallCloudAgentbtn(String randomPolicyName) {
		WebElement ClickonPolicyDropDown = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='policyId']")));

		// ✅ Wait until the dropdown contains the policy name
		wait.until(ExpectedConditions.textToBePresentInElement(ClickonPolicyDropDown, randomPolicyName));

		// ✅ Select the random policy by visible text
		Select select = new Select(ClickonPolicyDropDown);
		select.selectByVisibleText(randomPolicyName);

		// ✅ Click the download button
		wait.until(ExpectedConditions.visibilityOf(ClickonDownloadbtn)).click();
	}

	// Enable SU product in Policy
	@FindBy(xpath = "//a[@id='SOFTWARE_UPDATER_a']")
	WebElement ClickOnSUService;

	@FindBy(xpath = "//select[@id='ddSoftwareUpdaterSettings']")
	WebElement selectkdropdowhnSU;

	@FindBy(xpath = "//label[@for='chk_5']")
	WebElement selectSUapplication;

	
	public void EnableSoftwareUpdater() {

		wait.until(ExpectedConditions.visibilityOf(ClickOnSUService)).click();
	}

	public void ClickonDropdwonSU(String Enable) {
		Select sudropdownenable = new Select(selectkdropdowhnSU);
		sudropdownenable.selectByVisibleText(Enable);
	}
	
	public void selectanyapps() {
		
		wait.until(ExpectedConditions.visibilityOf(selectSUapplication)).click();
		
	}
	
	
}
