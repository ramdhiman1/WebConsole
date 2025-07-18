package testCasesCode;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import base_Classes.Base_Page;

public class Application_Page_ProductionServer extends Base_Page {

	public Application_Page_ProductionServer() {
		super();
		initializeElements(this);

		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(20));

	}

	// Locators
	@FindBy(xpath="//img[@id='appSearchImg']")
    WebElement computerfilterbutton;
	
    @FindBy(xpath="//a[@onclick=\"filterGridByContent('');\"]")
    WebElement allcomputersfilter;
    
    @FindBy(xpath="//div[@id='dvPendingApplicationsGrid']//*[@class='dx-datagrid-content']//table//tr")
    List<WebElement>computerspendingtable;
    String computerstable= "//div[@id='dvPendingApplicationsGrid']//*[@class='dx-datagrid-content']//table//tr";
    
    
    
    public void setAllComputersFilter()
    {
    	wait.until(ExpectedConditions.visibilityOf(computerfilterbutton)).click();
    	wait.until(ExpectedConditions.visibilityOf(allcomputersfilter)).click();
    	
    }
    
    
    
    @FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//div[@class='dx-datagrid-headers dx-datagrid-nowrap']//tr//td[8]")
	WebElement AppsCompclick;	

	@FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-11')]")
	WebElement clickcomps;	

		
   @FindBy(xpath = "//button[@id='btnInstallApplication']")
	WebElement clickonInstallbtns;

    
    public void InstallAnySUApps(String randomPolicyName) {
    	try {
    		// Wait until the table is visible
    		List<WebElement> rows = wait.until(ExpectedConditions
    				.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='dvPendingApplicationsGrid']//*[@class='dx-datagrid-content']//table//tr")));

    		boolean policyFound = false;

    		// Loop through all rows
    		for (WebElement row : rows) {
    			WebElement policyCell = row.findElement(By.xpath(".//td[2]")); // Column 2 = Policy Name

    			// Check if this row contains the randomPolicyName
    			if (policyCell.getText().trim().equalsIgnoreCase(randomPolicyName)) {
    				System.out.println("✅ Found policy: " + randomPolicyName);

    				// Click the button/element inside Column 
    				wait.until(ExpectedConditions.visibilityOf(AppsCompclick)).click();
    				wait.until(ExpectedConditions.elementToBeClickable(clickcomps)).click();    				

    				System.out.println("👉 Clicked on Install Cloud Agent button in column 7.");
    				policyFound = true;
    				break; // Exit loop once found
    			}
    		}

    		if (!policyFound) {
    			System.out.println("❌ Policy with name '" + randomPolicyName + "' not found in the table.");
    		}
    	} catch (Exception e) {
    		System.out.println("❗ Exception occurred while searching or clicking install button: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    @FindBy(xpath = "//div[@id='dvConfirmCommonPopup']//div[@class='modal-header p-2 popupTitle']")
	WebElement confirmfrozenpopup;
    @FindBy(xpath = "//input[@id='btnConfirmCommonYes']")
	WebElement clickonInstallyesbtn1;
    
    @FindBy(xpath = "//div[@role='alert']")
	WebElement appaleartmessage;
    
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
    
    @FindBy(xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-11')]")
	WebElement appsColumnVersion;	
    public void monitorPidginAppStatus() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();

        int maxAttempts = 30;
        int waitSeconds = 3;

        System.out.println("📢 App installation initiated. Monitoring version/status...");

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                String script = """
                    let xpath = "//div[@id='dvPendingApplicationsGrid']//table//tr[1]//td[contains(@class,'prod-11')]";
                    let cell = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

                    if (!cell) return null;

                    // Try getting full visible text
                    let visibleText = cell.innerText.trim();

                    // Check for pseudo-element content (Downloading, Waiting, etc.)
                    let computedStyle = window.getComputedStyle(cell, '::before');
                    let beforeContent = computedStyle.getPropertyValue('content').replace(/["']/g, '');

                    // Merge pseudo + visible
                    let combinedStatus = beforeContent ? (beforeContent + (visibleText ? " " + visibleText : "")) : visibleText;

                    return combinedStatus;
                """;

                String statusText = (String) js.executeScript(script);

                if (statusText != null && !statusText.isEmpty()) {
                    System.out.println("⏳ Attempt " + attempt + " - Status: " + statusText);

                    // Match for version
                    if (statusText.matches(".*\\d+\\.\\d+(\\.\\d+)?(\\.\\d+)?")) {
                        String version = statusText.replaceAll(".*?(\\d+\\.\\d+(\\.\\d+)?(\\.\\d+)?).*", "$1");
                        System.out.println("✅ Installed Pidgin Version: " + version);
                        break;
                    }

                    // Intermediate states
                    if (statusText.toLowerCase().matches(".*(waiting|downloading|installing|updating|uninstalling|failed).*")) {
                        System.out.println("🔄 App is currently in intermediate state: " + statusText);
                    }

                } else {
                    System.out.println("⏳ Attempt " + attempt + " - Status cell empty or not found");
                }

            } catch (Exception e) {
                System.out.println("❌ Error on Attempt " + attempt + ": " + e.getMessage());
            }

            Thread.sleep(waitSeconds * 1000);
        }
    }










}
