package testCasesCode;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Download_Agent extends Base_Page{

	public Download_Agent() {
		
		super();
		
		
		initializeElements(this);

		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(20));
		
	}
	
	@FindBy(xpath="//span[normalize-space()='Install Cloud Agent']")
	WebElement ClickonCLoudAgentbtn;
	
	
	@FindBy(xpath="//input[@id='Install']")
	WebElement ClickonDownloadbtn;
	
	public void ClickOnInstallCloudAgentbtn() {
		
		wait.until(ExpectedConditions.visibilityOf(ClickonCLoudAgentbtn)).click();
		
		wait.until(ExpectedConditions.visibilityOf(ClickonDownloadbtn)).click();
	}
	
	
	
	
}
