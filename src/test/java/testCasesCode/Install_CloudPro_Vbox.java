package testCasesCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import org.openqa.selenium.WebDriver;

import base_Classes.Base_Page;

public class Install_CloudPro_Vbox extends Base_Page {

	public Install_CloudPro_Vbox(WebDriver driver) {
		super();
	}

	String remoteHost = "192.168.30.115";
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
		int maxRetries = 5;
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
		String[] copyCommand = { "cmd.exe", "/C", psExecPath, "\\\\" + remoteHost, "-u", vmUser, "-p", vmPassword, "-s",
				"cmd", "/c", "\"net use x: \\\\192.168.30.177\\d\\dfcloud\\downloads /user:Administrator aloha1 && "
						+ "copy x:\\installer.exe e:\\ && copy x:\\runinstaller.bat e:\\ && net use x: /delete\"" };

		System.out.println("📂 Executing file copy via PsExec...");
		Process process = new ProcessBuilder(copyCommand).start();
		printProcessOutput(process);
		process.waitFor();
	}

	// Step 4: Run installer batch file remotely via PsExec
	public void installApplication() throws IOException, InterruptedException {
		String[] runInstallerCmd = { "cmd.exe", "/C", psExecPath, "\\\\" + remoteHost, "-u", vmUser, "-p", vmPassword,
				"-s", "-d", "e:\\runinstaller.bat" };

		System.out.println("🚀 Executing PsExec to run e:\\runinstaller.bat...");
		Process process = new ProcessBuilder(runInstallerCmd).start();
		printProcessOutput(process);
		process.waitFor();
	}

	// Optional: Rename installer file before copying if needed
	public void renameInstaller(String policyname) {
		String renamefile = "ren " + policyname + " installer.exe";
		String[] command = { "cmd.exe", "/C", renamefile };
		System.out.println("🔧 Renaming installer file...");
		try {
			Process process = new ProcessBuilder(command).start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
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
}
