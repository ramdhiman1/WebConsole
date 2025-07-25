package testCasesCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base_Classes.Base_Page;

public class Install_CloudPro_Vbox_Production extends Base_Page {

	public Install_CloudPro_Vbox_Production() {
		super();
		initializeElements(this);
		wait = new WebDriverWait(driver.get(), Duration.ofSeconds(15));
	}

	String remoteHost = "192.168.30.187";
	String remoteMachineName = "DESKTOP-VO6FI92";
	String vmUser = "Administrator";
	String vmPassword = "aloha";
	String psExecPath = "d:/DFCloud/Downloads/PSTools/PsExec64.exe";

	// Step 1: Start the VirtualBox VM
	public void StartVM() {
		try {
			String[] command = { "C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe", "startvm", "1234-W-10-64-EN",
					"--type", "gui" };
			Process process = new ProcessBuilder(command).start();
			process.waitFor();
			System.out.println("✅ VM started successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Step 2: Ping the remote VM to check availability
	public void pingVM() {
		int retryInterval = 10000;
		int maxRetries = 10;
		boolean isConnected = false;

		for (int i = 1; i <= maxRetries; i++) {
			try {
				InetAddress inetAddress = InetAddress.getByName(remoteHost);
				System.out.println("🔄 Pinging " + remoteHost + " (Attempt " + i + ")");
				if (inetAddress.isReachable(5000)) {
					System.out.println("✅ Ping successful. The remote host is up.");
					isConnected = true;
					break;
				} else {
					System.out.println("❌ Ping failed.");
				}
			} catch (IOException e) {
				System.out.println("❌ Ping exception: " + e.getMessage());
			}

			if (i < maxRetries) {
				try {
					Thread.sleep(retryInterval);
				} catch (InterruptedException ie) {
					System.out.println("Sleep interrupted: " + ie.getMessage());
				}
			}
		}

		if (!isConnected) {
			System.out.println("❌ Remote host is unreachable after all attempts.");
		}
	}

	// Step 3: Copy installer files to remote VM using PsExec
	public void copyInstallerUsingPsExec() throws IOException, InterruptedException {
		/*
		 * String[] copyCommand = { "cmd.exe", "/C", psExecPath,
		 * "\\\\" + remoteHost, "-u", vmUser, "-p", vmPassword, "-s", "cmd", "/c",
		 * "\"net use x: \\\\192.168.30.177\\d\\dfcloud\\downloads /user:Administrator aloha1 && "
		 * +
		 * "copy x:\\installer.exe e:\\ && copy x:\\runinstaller.bat e:\\ && net use x: /delete\""
		 * };
		 */

		String[] copyCommand = { "cmd.exe", "/C", psExecPath, "\\\\" + remoteHost, "-u", vmUser, "-p", vmPassword, "-s",
				"cmd", "/c",
				"\"net use x: \\\\192.168.30.177\\d\\dfcloud\\downloads /user:Administrator aloha1 && "
						+ "copy x:\\installer.exe e:\\ && " + "copy x:\\runinstaller.bat e:\\ && "
						+ "del x:\\installer.exe && " + // ✅ This line deletes the installer
						"net use x: /delete\"" };

		System.out.println("📂 Executing file copy via PsExec...");
		Process process = new ProcessBuilder(copyCommand).start();
		printProcessOutput(process);
		process.waitFor();
	}

	// Step 4: Run installer batch file remotely via PsExec
	/*
	 * public void installApplication() throws IOException, InterruptedException {
	 * String[] runInstallerCmd = { "cmd.exe", "/C", psExecPath,
	 * "\\\\" + remoteHost, "-u", vmUser, "-p", vmPassword, "-s", "-d",
	 * "e:\\runinstaller.bat" };
	 * 
	 * System.out.println("🚀 Executing PsExec to run e:\\runinstaller.bat...");
	 * Process process = new ProcessBuilder(runInstallerCmd).start();
	 * printProcessOutput(process); process.waitFor();
	 */

	// Step 4: Run installer batch file remotely via PsExec
	public void installCloudAgentandProducts() throws IOException, InterruptedException {
		String command = psExecPath + " \\\\" + remoteHost + " -u " + vmUser + " -p " + vmPassword
				+ " -s -i 1 cmd /c e:\\runinstaller.bat";

		System.out.println("🚀 Executing PsExec to run e:\\runinstaller.bat...");
		ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", command);
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		printProcessOutput(process);
		process.waitFor();
	}

	// Optional: Rename installer file before copying if needed
	/*
	 * public void renameInstaller(String policyname) { String renamefile = "ren " +
	 * policyname + " installer.exe"; String[] command = { "cmd.exe", "/C",
	 * renamefile }; System.out.println("🔧 Renaming installer file..."); try {
	 * Process process = new ProcessBuilder(command).start(); process.waitFor(); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */

	public void renameInstaller() {
		String folderPath = "D:\\DFCloud\\Downloads";
		String prefix = "FWAWebInstaller";

		File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("❌ Folder does not exist: " + folderPath);
			return;
		}

		// Find the file that starts with "FWAWebInstaller"
		File[] matchingFiles = folder.listFiles((dir, name) -> name.startsWith(prefix) && name.endsWith(".exe"));

		if (matchingFiles != null && matchingFiles.length > 0) {
			File originalFile = matchingFiles[0]; // Take the first match
			File renamedFile = new File(folderPath + File.separator + "installer.exe");

			if (renamedFile.exists()) {
				System.out.println("⚠️ installer.exe already exists. Deleting it...");
				renamedFile.delete();
			}

			boolean success = originalFile.renameTo(renamedFile);

			if (success) {
				System.out.println("✅ Renamed '" + originalFile.getName() + "' to 'installer.exe'");
			} else {
				System.out.println("❌ Failed to rename file: " + originalFile.getName());
			}
		} else {
			System.out.println("❌ No file found starting with '" + prefix + "' in: " + folderPath);
		}
	}

	// NEW: Check if a file exists on remote VM using PsExec
	public boolean isFileExists(String filePath) {
		try {
			String command = String.format(
					"%s \\\\%s -u %s -p %s -s cmd /c \"if exist \"%s\" (echo FOUND) else (echo NOTFOUND)\"", psExecPath,
					remoteHost, vmUser, vmPassword, filePath);

			System.out.println("🟡 Checking file using PsExec: " + filePath);

			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
			pb.redirectErrorStream(true);
			Process process = pb.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("📤 OUTPUT: " + line);
				if (line.trim().equalsIgnoreCase("FOUND")) {
					System.out.println("✅ Installed Cloud Products Path Found: " + filePath);
					return true;
				}
			}

			process.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// ✅ Overloaded method – add this below in the same class
	public boolean isFileExists(String filePath, String productName) {
		boolean exists = isFileExists(filePath); // call existing method
		if (exists) {
			System.out.println("✅ " + productName + " Installed Path: " + filePath);
		} else {
			System.out.println("❌ " + productName + " NOT found at: " + filePath);
		}
		return exists;
	}

	// Helper: Print output of any command
	private void printProcessOutput(Process process) throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String s;

		while ((s = stdInput.readLine()) != null) {
			System.out.println("OUTPUT: " + s);
		}
		while ((s = stdError.readLine()) != null) {
			System.out.println("Actions: " + s);
		}
	}

	// ✅ Now We are on Computers Page, so search installed machine.
	@FindBy(xpath = "//div[@aria-label='Search in data grid']//input[@role='textbox']")
	WebElement clickonSearchbox;

	public void SearchMachine() {
		wait.until(ExpectedConditions.visibilityOf(clickonSearchbox)).clear();
		clickonSearchbox.click();
		clickonSearchbox.sendKeys(remoteMachineName); // ✅ Using the global variable
	}

	// Now check Machine is Online / offline
	

/*	public void selectOnlineWorkstation() throws InterruptedException {
		int retryCount = 0;
		int maxRetries = 10;
		boolean onlineMachineFound = false;

		while (retryCount < maxRetries && !onlineMachineFound) {
			// Get all rows in the table
			List<WebElement> rowData = driver.get()
					.findElements(By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr"));

			for (WebElement row : rowData) {
				// Get the status from the 5th column (Online/Offline)
				WebElement compStatus = row.findElement(By.xpath(".//td[5]"));

				// Check if the machine is online
				if (compStatus.getText().equalsIgnoreCase("online")) {
					// Get the title from the 2nd column
					WebElement titleElement = row.findElement(By.xpath(".//td[2]//img"));
					String titleAttribute = titleElement.getAttribute("title");

					// Check if the title is "Cloud Agent"
					if ("Cloud Agent".equalsIgnoreCase(titleAttribute)) {

						// Find the checkbox in the first column and select it
						WebElement checkbox = row.findElement(By.xpath(".//td[1]"));
						checkbox.click(); // Click the Online checkbox to select the machine

						onlineMachineFound = true; // Mark that the machine was found
						break; // Break out of the loop once machine is found
					}
				}
			}

			if (!onlineMachineFound) {
				// Retry if no online machine with "Cloud Agent" found
				System.out.println("No online machine with 'Cloud Agent' title found, retrying attempt "
						+ (retryCount + 1) + "...");
				Thread.sleep(20000); // Wait for 5 seconds before retrying
				driver.get().navigate().refresh(); // Refresh the page
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr"))); // Wait for the table
																									// to reload
				retryCount++;
			}
		}

		if (onlineMachineFound) {
			// Continue with the rest of your test here
			System.out.println("Online machine selected successfully. Continuing with further tests...");

			// TODO: Add the next steps of your test after selecting the online machine
			// Example: click a button, perform another action, etc.

		} else {
			System.out.println("Max retries reached. No online machine with 'Cloud Agent' title found.");
			// Handle what to do if no machine was found after max retries
			// For example, you could fail the test or exit the process.
		}
	}
*/
	
	
	public void selectOnlineWorkstation() throws InterruptedException {
		int retryCount = 0;
		int maxRetries = 5;
		boolean onlineMachineFound = false;

		while (retryCount < maxRetries && !onlineMachineFound) {
			List<WebElement> rowData = driver.get()
					.findElements(By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr[1]"));

			for (WebElement row : rowData) {

				boolean isOnline = false;

				// ✅ Check from column 3 to 7 for "online"
				for (int col = 3; col <= 7; col++) {
					try {
						WebElement statusCell = row.findElement(By.xpath(".//td[" + col + "]"));
						String statusText = statusCell.getText().trim();

						if (statusText.equalsIgnoreCase("online")) {
							isOnline = true;
							break;
						}
					} catch (Exception e) {
						System.out.println("❌ Failed to read status from column " + col);
					}
				}

				if (isOnline) {
					// ✅ Now check if it's Cloud Agent
					try {
						WebElement titleElement = row.findElement(By.xpath(".//td[2]//img"));
						String titleAttribute = titleElement.getAttribute("title");

						if ("Cloud Agent".equalsIgnoreCase(titleAttribute)) {
							// ✅ Select the machine checkbox
							WebElement checkbox = row.findElement(By.xpath(".//td[1]"));
							checkbox.click();

							onlineMachineFound = true;
							System.out.println("✅ Online machine with 'Cloud Agent' found and selected.");
							break;
						}
					} catch (Exception e) {
						System.out.println("❌ Failed to verify 'Cloud Agent' for selected row.");
					}
				}
			}

			if (!onlineMachineFound) {
				System.out.println("🔄 No online machine with 'Cloud Agent' found, retrying attempt " + (retryCount + 1) + "...");
				Thread.sleep(20000);
				driver.get().navigate().refresh();
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr")));
				retryCount++;
			}
		}

		if (onlineMachineFound) {
			System.out.println("✅ Machine selected. Proceeding to next step...");
		} else {
			System.out.println("❌ Max retries reached. No suitable online machine found.");
		}
	}

	
	
	
	public void printAllProductStatuses() throws InterruptedException {
	    int retryCount = 0;
	    int maxRetries = 5;
	    boolean onlineMachineFound = false;

	    while (retryCount < maxRetries && !onlineMachineFound) {

	        // ✅ Get all machine rows from the table
	        List<WebElement> rowData = driver.get().findElements(
	                By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr[1]"));

	        for (WebElement row : rowData) {

	            // ✅ Check if any column between 3 and 7 has "online" status
	            boolean isOnline = false;
	            for (int i = 3; i <= 7; i++) {
	                WebElement statusCell = row.findElement(By.xpath(".//td[" + i + "]"));
	                if (statusCell.getText().trim().equalsIgnoreCase("online")) {
	                    isOnline = true;
	                    break;
	                }
	            }

	            if (isOnline) {
	                // ✅ Check if the product in Column 2 is "Cloud Agent"
	                WebElement titleElement = row.findElement(By.xpath(".//td[2]//img"));
	                String titleAttribute = titleElement.getAttribute("title");

	                if ("Cloud Agent".equalsIgnoreCase(titleAttribute)) {
	                    // ✅ Select the checkbox (Column 1)
	                    WebElement checkbox = row.findElement(By.xpath(".//td[1]"));
	                    checkbox.click();
	                    onlineMachineFound = true;

	                    // ✅ Get column headers
	                    List<WebElement> headers = driver.get().findElements(
	                            By.xpath("//tr[@class='dx-row dx-column-lines dx-header-row']//td"));

	                    System.out.println("\n📊 Product Status (With Column Names):");

	                    // ✅ Loop from column 9 to 22
	                    for (int col = 9; col <= 22; col++) {
	                        try {
	                            // Fetch header name or fallback to tooltip or "Column X"
	                            WebElement headerCell = headers.get(col - 1); // index is 0-based
	                            String headerName = headerCell.getText().trim();

	                            if (headerName.isEmpty()) {
	                                headerName = headerCell.getAttribute("title"); // fallback to tooltip
	                            }
	                            if (headerName == null || headerName.isEmpty()) {
	                                headerName = "Column " + col;
	                            }

	                            // Get product status value from the cell
	                            WebElement productStatusElement = row.findElement(By.xpath(".//td[" + col + "]//div"));
	                            String productTitle = productStatusElement.getAttribute("title");

	                            System.out.println("🔹 " + headerName + ": " + productTitle);

	                        } catch (Exception e) {
	                            System.out.println("❌ Column " + col + ": Header/Status not found.");
	                        }
	                    }
	                    break; // break the row loop once an online machine is found
	                }
	            }
	        }

	        // ✅ Retry if no online Cloud Agent machine found
	        if (!onlineMachineFound) {
	            System.out.println("❗ No online machine with 'Cloud Agent' found. Retrying attempt " + (retryCount + 1) + "...");
	            Thread.sleep(20000);
	            driver.get().navigate().refresh();
	            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
	                    By.xpath("//div[@class='dx-scrollable-container']//table//tbody//tr")));
	            retryCount++;
	        }
	    }

	    // ✅ Final status
	    if (onlineMachineFound) {
	        System.out.println("✅ Online machine selected and product statuses printed.");
	    } else {
	        System.out.println("❌ Max retries reached. No suitable online machine found.");
	    }
	}



	// Now Reboot Thawed Machine

	@FindBy(xpath = "//a[normalize-space()='Deep Freeze']")
	WebElement dfdropdown;
	@FindBy(xpath = "//a[@id='aDeepFreeze_Frozen']")
	WebElement dffrozenaction;
	@FindBy(xpath = "//a[@id='aDeepFreeze_Thawed']")
	WebElement dfthawedaction;
	@FindBy(xpath = "//input[@id='btnRebootThawedOk']")
	WebElement btnthawlockaction;
	@FindBy(xpath = "//h4[@id='dvheader_text']")
	WebElement popupmessage;

	public void dfthawedAction() {
		dfdropdown.click();
		dfthawedaction.click();
		wait.until(ExpectedConditions.visibilityOf(popupmessage));
		btnthawlockaction.click();
		wait.until(ExpectedConditions.invisibilityOf(popupmessage));
		System.out.println("DF Reboot thawed action executed successfully");

	}
	
	//Now Uninstall the Cloud Agent 
	
	@FindBy(xpath="//div[@class='Maintainance-Comp listImage']")
	  WebElement mmdropdown;
	
	@FindBy(xpath="//a[@id='UninstallCA_Computers']")
	  WebElement clickonUninstallCA;
	@FindBy(xpath="//input[@id='btnUninstallCAYes']")
	  WebElement clickonYesBtn;
	@FindBy(xpath="//span[@id='SpanServiceMsgbox']")
	  WebElement actionmessage;
	
	
	
	
	
	public void UninstallCloudAgent()
	  {
		  wait.until(ExpectedConditions.visibilityOf(mmdropdown)).click();
		  wait.until(ExpectedConditions.visibilityOf(clickonUninstallCA)).click();
		  wait.until(ExpectedConditions.visibilityOf(clickonYesBtn)).click();
		  wait.until(ExpectedConditions.visibilityOf(actionmessage));
		  if (actionmessage.getText().contains("Uninstalling Cloud Agent"))
		  {
			  wait.until(ExpectedConditions.invisibilityOf(actionmessage));
		  }
		  System.out.println("Uninstalling Cloud Agent action executed successfully");
		  
	  }
	

}
