package test_Cases_Class;

import org.testng.annotations.Test;

import base_Classes.Base_Page;
import testCasesCode.Policies_Page_ProductionServer;

import org.testng.annotations.Listeners;
import utilities.AllureListener;
@Listeners({AllureListener.class})
public class TC05_Policies_Page extends Base_Page {

   String randomPolicyName = randomeString().toUpperCase();
   String editedPolicyName = randomeString().toUpperCase();

   @Test(priority = 1, groups = {"regression"}, description="Create a policy with Enabled Some Products (i.e DF & AE) in policy ") 
   public void DFPolicyService() throws InterruptedException {
   	Policies_Page_ProductionServer dfPolicy = new Policies_Page_ProductionServer();      
       dfPolicy.clickAddPolicyButton();
       dfPolicy.selectDropdownPolicy();
       dfPolicy.enterPolicyName(editedPolicyName);
       dfPolicy.EnableDFService();
       Thread.sleep(5000);
       dfPolicy.ClickonDropdwonDF("Enable (Install and use below settings)");       
       dfPolicy.clickSaveButton();   
   
}
}