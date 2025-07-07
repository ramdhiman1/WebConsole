package test_Cases_Class;

import org.testng.annotations.Test;

import base_Classes.Base_Page;
import testCasesCode.Download_Agent;

public class TC04_Download_Agent extends Base_Page {
	
	@Test( priority = 1, description= "Make Sure Cloud Agent Gets download Properly")
	
	public void DownLoadCloudAgent() throws InterruptedException {
		Download_Agent ag = new Download_Agent();
		
		ag.ClickOnInstallCloudAgentbtn();
		
		Thread.sleep(100000);
		
	}
	
	
	
	

}
