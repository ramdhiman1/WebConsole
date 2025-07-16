package testCasesCode;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Multiple_Login_ProductionServer extends Base_Page {

	public Multiple_Login_ProductionServer() {
		super();
		initializeElements(this); // PageFactory initialization
		wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
	}

	// ✅ Username field
	@FindBy(xpath = "//input[@id='txtUserName']")
	WebElement usernameInput;

	public void EnterUserName(String username) {
		wait.until(ExpectedConditions.visibilityOf(usernameInput));
		usernameInput.clear();
		usernameInput.sendKeys(username);
	}

	// ✅ Next button (after username)
	@FindBy(xpath = "//input[@id='btnlogin']")
	WebElement NextBtn;

	public void ClickOnNextBtn() {
		wait.until(ExpectedConditions.elementToBeClickable(NextBtn)).click();
	}

	// ✅ Password field
	@FindBy(xpath = "//input[@id='txtPassword']")
	WebElement passwordInput;

	public void EnterPassword(String password) {
		wait.until(ExpectedConditions.visibilityOf(passwordInput));
		passwordInput.clear();
		passwordInput.sendKeys(password);
	}

	// ✅ Sign in button (same as next)
	@FindBy(xpath = "//input[@id='btnlogin']")
	WebElement SigninBtn;

	public void ClickOnSignBtn() {
		wait.until(ExpectedConditions.elementToBeClickable(SigninBtn)).click();
		
		
		
	}
	
	
	
}
