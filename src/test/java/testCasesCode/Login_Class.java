package testCasesCode;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Login_Class  extends Base_Page{
	
	public Login_Class() {
		
		super();
		
		initializeElements(this);
		
	 wait = new WebDriverWait(driver.get(), Duration.ofSeconds(10));
		
	
			
	}
    //Enter User Name
	@FindBy(xpath= "//input[@id='txtUserName']")
	WebElement UserName;
	
	public void EnterUserName() {
		
		wait.until(ExpectedConditions.visibilityOf(UserName));		
		UserName.clear();
	//	UserName.sendKeys("rammehard@alohatechnology.com");
		
		UserName.sendKeys(p.getProperty("username"));
		
	}
	
	//Click on Next btn
	
	@FindBy(xpath="//input[@id='btnlogin']")
	WebElement NextBtn;
	
	public void ClickOnNextBtn() {
		
		wait.until(ExpectedConditions.visibilityOf(NextBtn)).click();
		
	}
	
	// Enter Password
	
	@FindBy(xpath="//input[@id='txtPassword']")
	WebElement Password;
	
	public void EnterPassword() {
		
		wait.until(ExpectedConditions.visibilityOf(Password));
		Password.clear();
		Password.sendKeys("Aloha@123");	
		
	}
	
	// Click on Sign Btn
	
	@FindBy(xpath="//input[@id='btnlogin']")
    WebElement SigninBtn;
	
	public void ClickOnSignBtn() {
		
		wait.until(ExpectedConditions.visibilityOf(SigninBtn)).click();
	}
}
