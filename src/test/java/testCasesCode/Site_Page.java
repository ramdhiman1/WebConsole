package testCasesCode;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Site_Page extends Base_Page {

	public Site_Page() {
		super();
		initializeElements(this);
	}

	// Locators
	@FindBy(xpath = "//a[@id='logg_main' and contains(@class, 'dropdown-toggle')]")
	WebElement clickEmail;

	@FindBy(xpath = "//a[@id='aLogin_MySite']")
	WebElement clickOnSitepage;

	@FindBy(xpath = "//span[@class='imgTextHome1']")
	WebElement clickOnAddSitebtn;

	@FindBy(xpath = "//input[@id='SiteName']")
	WebElement enterSiteName;

	@FindBy(xpath = "//input[@id='btn_OK']")
	WebElement clickOnOkbuttonn;

	@FindBy(xpath = "//span[@id='SpanServiceMsgbox']")
	WebElement NotificationMessage;

	// Edit Site locators
	@FindBy(xpath = "//input[@id='SearchMySites']")
	WebElement search_box1;

	@FindBy(xpath = "//div[@class='dx-treelist-content']//table//tr")
	List<WebElement> rowData; // Rows containing group data

	@FindBy(xpath = "//a[contains(@id,'aMSGVEditMySite')]/img[@alt='Edit']")
	WebElement clickoneditbtn;

	@FindBy(xpath = "//input[@id='SiteName']")
	WebElement updatesitename;

	@FindBy(xpath = "//input[@id='btn_Update']")
	WebElement clickonUpdatebtn;

	@FindBy(xpath = "//span[@id='SpanServiceMsgbox']")
	WebElement NotificationmessageUpdate;

	// Delete Site

	@FindBy(xpath = "//a[contains(@id,'aMSGVDeleteMySite')]/img[@alt='Delete']")
	WebElement clickonDeleteBtn;

	@FindBy(xpath = "//label[@for='chkdeletemsg']")
	WebElement checkbox;

	@FindBy(xpath ="//input[@id='btnDelConfirmOK']")
	WebElement clickondeletebtn;

	@FindBy(xpath = "//span[@id='SpanServiceMsgbox']")
	WebElement DeleteMessage;

	// Actions
	public void clickOnUserID(){
		
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(35));
		wait.until(ExpectedConditions.elementToBeClickable(clickEmail)).click();
	}

	public void clickOnMySite() {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickOnSitepage)).click();
	}

	public void clickOnAddSitebtns() {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickOnAddSitebtn)).click();
	}

	public void EnterSiteName(String siteName) {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(enterSiteName)).sendKeys(siteName);
	}

	public boolean ClickOnOkbtnn() {
		boolean isclickonokbtn = false;
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickOnOkbuttonn)).click();
		wait.until(ExpectedConditions.visibilityOf(NotificationMessage));

		if (NotificationMessage.isDisplayed()) {
			String message = NotificationMessage.getText();
			System.out.println("NotificationMessage: " + message);

			if (message.contains("has been added")) {
				isclickonokbtn = true;
				wait.until(ExpectedConditions.invisibilityOf(NotificationMessage));
			}
		}
		return isclickonokbtn;
	}

	// Edit Site methods
	public void searchbysitename(String siteName) {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.visibilityOf(search_box1));
		search_box1.clear();
		search_box1.sendKeys(siteName);
	}

	public void clickonEditbutton() {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickoneditbtn)).click();
	}

	public void UpdateSiteName(String newSiteName) {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(updatesitename));

		updatesitename.sendKeys(newSiteName); // Enter the new site name
	}

	public boolean ClickonUpdateButton() {
		boolean isupdated = false;
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(20));
		wait.until(ExpectedConditions.elementToBeClickable(clickonUpdatebtn)).click();
		wait.until(ExpectedConditions.visibilityOf(NotificationmessageUpdate));

		if (NotificationmessageUpdate.isDisplayed()) {
			String message = NotificationmessageUpdate.getText();
			System.out.println("NotificationmessageUpdate: " + message);

			if (message.contains("has been updated")) {
				isupdated = true;
				wait.until(ExpectedConditions.invisibilityOf(NotificationmessageUpdate));
			}
		}
		return isupdated;
	}

//Delete Site Actions

	public void clickondeleteicon() {
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickonDeleteBtn)).click();
	}

	public void CheckSiteCheckbox() {
		checkbox.click();
	}

	public boolean clickonDeletebutton() {
		boolean isclickondeletebtn = false;
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.elementToBeClickable(clickondeletebtn)).click();
		wait.until(ExpectedConditions.visibilityOf(DeleteMessage));

		if (DeleteMessage.isDisplayed()) {
			String message = DeleteMessage.getText();
			System.out.println("DeleteMessage: "+ message);

			if (message.contains("deleted successfully")) {
				isclickondeletebtn = true;
				wait.until(ExpectedConditions.invisibilityOf(DeleteMessage));
			}
		}
		return isclickondeletebtn;
	}
}