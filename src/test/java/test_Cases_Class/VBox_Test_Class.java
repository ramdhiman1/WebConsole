package test_Cases_Class;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

public class VBox_Test_Class {

    WebDriver driver; // Assume driver initialized from base class
    WebDriverWait wait;

    private static final String VBOX_MANAGE_PATH = "C:\\Program Files\\Oracle\\VirtualBox\\VirtualBoxVM.exe";
    private static String Window1; // Excel ya properties file se padhenge

    @Test(description = "Download agent and install on VM")
    public void installAgentOnVM() throws IOException, InterruptedException {

        // 🧠 Step 1: Read from .properties file (like Excel)
        readWindowFromProperties();

        // 🧪 Step 2: Download agent using Selenium
        driver.findElement(By.xpath("//a[@id='optiondropdownMenu']")).click();
        Thread.sleep(3000);
        driver.findElement(By.xpath("//input[@id='btnDownloadNow']")).click();
        Thread.sleep(Duration.ofMinutes(2).toMillis());

        // 🗂️ Step 3: Find latest downloaded .exe file
        File latestFile = getLatestExeFromDownloads();
        if (latestFile == null) {
            System.out.println("❌ No .exe file found.");
            return;
        }
        System.out.println("✅ Latest file: " + latestFile.getName());

        // 🖥️ Step 4: VM Details
        String vmName = "1234-W-11-24H2";
        String username = "Administrator";
        String password = "aloha";
        String guestPath = "C:\\Users\\Administrator\\Desktop\\";
        String guestFullPath = guestPath + latestFile.getName();

        // 🚀 Step 5: Start VM
        startVM(vmName);
        Thread.sleep(Duration.ofMinutes(2).toMillis());

        // 📤 Step 6: Copy file to VM
        if (copyToVM(vmName, username, password, latestFile, guestPath)) {
            System.out.println("✅ File copied to VM.");

            // ⚙️ Step 7: Execute .exe file inside VM
            if (executeFileInVM(vmName, username, password, guestFullPath)) {
                System.out.println("✅ File executed.");

                // ✅ Step 8: Validate computer is added & online
                driver.navigate().refresh();
                wait = new WebDriverWait(driver, Duration.ofMinutes(5));
                By condition1 = By.xpath("(//div[@id='dvPendingApplicationsGrid']//table)[4]//tr//td//span[text()='" + Window1 + "']");
                wait.until(ExpectedConditions.presenceOfElementLocated(condition1));

                if (driver.findElements(condition1).size() > 0) {
                    System.out.println("✔️ Computer added to Pending list.");

                    Thread.sleep(Duration.ofMinutes(2).toMillis());
                    driver.navigate().refresh();
                    Thread.sleep(5000);

                    wait = new WebDriverWait(driver, Duration.ofMinutes(10));
                    By condition2 = By.xpath("(//div[@id='dvPendingApplicationsGrid']//table)[4]//tr//td//span[text()='" + Window1 + "']/preceding-sibling::div[@title='Offline']");
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(condition2));

                    if (driver.findElements(condition2).isEmpty()) {
                        System.out.println("💻 Computer is ONLINE.");
                    } else {
                        System.out.println("❌ Computer is still OFFLINE.");
                    }

                } else {
                    System.out.println("❌ Computer not added.");
                }
            } else {
                System.out.println("❌ File execution failed.");
            }
        } else {
            System.out.println("❌ File copy to VM failed.");
        }
    }

    // 📘 Utility: Read from properties file
    private void readWindowFromProperties() throws IOException {
        FileInputStream file = new FileInputStream("src/test/resources/testdata.properties");
        Properties prop = new Properties();
        prop.load(file);
        Window1 = prop.getProperty("Window1");
        System.out.println("📘 Window1: " + Window1);
    }

    // 📘 Utility: Get latest .exe file from Downloads
    private File getLatestExeFromDownloads() {
        String downloadPath = System.getProperty("user.home") + "\\Downloads";
        File[] files = new File(downloadPath).listFiles();
        if (files == null) return null;

        return Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".exe"))
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    // 📘 Utility: Start VirtualBox VM
    private void startVM(String vmName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(VBOX_MANAGE_PATH, "startvm", vmName, "--type", "gui");
        Process p = pb.start();
        p.waitFor();
        System.out.println("🖥️ VM started: " + vmName);
    }

    // 📘 Utility: Copy file to guest
    private boolean copyToVM(String vmName, String user, String pass, File hostFile, String guestDir)
            throws IOException, InterruptedException {
        String[] command = {
                VBOX_MANAGE_PATH, "guestcontrol", vmName, "copyto",
                "--username", user,
                "--password", pass,
                "--target-directory", guestDir,
                hostFile.getAbsolutePath()
        };
        Process p = new ProcessBuilder(command).start();
        return p.waitFor() == 0;
    }

    // 📘 Utility: Execute file inside VM
    private boolean executeFileInVM(String vmName, String user, String pass, String guestFilePath)
            throws IOException, InterruptedException {
        String[] command = {
                VBOX_MANAGE_PATH, "guestcontrol", vmName,
                "--username", user,
                "--password", pass,
                "run", "--exe", "cmd.exe", "--", "/c", guestFilePath
        };
        Process p = new ProcessBuilder(command).start();
        return p.waitFor() == 0;
    }
}
