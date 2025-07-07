package test_Cases_Class;

import org.testng.Assert;
import org.testng.annotations.Test;

import base_Classes.Base_Page;
import testCasesCode.Login_Class;

public class TC01_Login_Class_Page extends Base_Page {
	
	@Test(priority = 1, groups= {"smoke", "Regression"}, description="Ensure that the user is logged in to the Cloud Console.")
	
	public void LoginOnCloudConsole() {
		
			
		Login_Class login = new Login_Class();
		
		login.EnterUserName();
		login.ClickOnNextBtn();
		login.EnterPassword();
		login.ClickOnSignBtn();
		
		String  ExpectedTitle = "Deep Freeze Cloud";
		String actualTitle = driver.get().getTitle();
		
		System.out.println("Page Title is: " + actualTitle);
		
		Assert.assertEquals(actualTitle, ExpectedTitle, "Login Page Title Does Not Match");  
				
		
	}

	
}
