package base_Classes;

import java.io.File;
import com.aventstack.extentreports.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.ArrayList;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import utilities.ExtentReportManager;
import utilities.ScreenRecorderUtil;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;
import java.net.URL;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Base_Page {
	protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	public static ThreadLocal<String> driverName = new ThreadLocal<>();  // ✅ Add this
//	public static WebDriver driver; // WebDriver instance
	public Logger logger; // Logger instance
	public WebDriverWait wait; // WebDriverWait instance
	public static Properties p; // Properties object to store config data
	public static String downloadFilepath; // Download file path from properties
	private static String resultsDirectory; // For storing allure results
	private static Properties properties; // Properties object to store config data
	LocalDateTime now;
	DateTimeFormatter formatter;
	String formattedDateTime;

	@BeforeTest
	
	@Epic("EP001")
	@Step("Setup WebDriver for browser: {br}")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Setup WebDriver for selected browser.")
	@Feature("Browser Setup")
	@Story("Story:Browser Setup")
	@Parameters({ "os", "browser" })

	public void setup(@Optional("Windows") String os, @Optional("chrome") String br) throws Exception {
		driverName.set(br);  // ✅ This line is required to avoid null in your report
		// Loading config.properties file
		FileReader file = new FileReader("./src/test/resources/config.properties");
		p = new Properties();
		p.load(file);

		logger = LogManager.getLogger(this.getClass()); // Logger initialization

		// Create a unique results directory for allure
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		resultsDirectory = "allure-results" + File.separator + "Run_" + timestamp;
		new File(resultsDirectory).mkdirs();
		System.setProperty("allure.results.directory", resultsDirectory); // Set allure results directory

		// Load properties from configuration file
		downloadFilepath = p.getProperty("downloaddirectory");

		if (p.getProperty("execution_env").equalsIgnoreCase("remote")) {

			DesiredCapabilities capabilities = new DesiredCapabilities();

			// os

			if (os.equalsIgnoreCase("Windows 10")) {

				capabilities.setPlatform(Platform.WIN10);
			}

			else if (os.equalsIgnoreCase("Win 11")) {

				capabilities.setPlatform(Platform.WIN11);
			}

			else if (os.equalsIgnoreCase("Win 8.1")) {

				capabilities.setPlatform(Platform.WIN8_1);
			}

			/*
			 * else if(os.equalsIgnoreCase("linux")) {
			 * capabilities.setPlatform(Platform.LINUX); }
			 * 
			 * else if(os.equalsIgnoreCase("mac")) { capabilities.setPlatform(Platform.MAC);
			 * }
			 */
			else {
				System.out.println("no maching os");
				return;
			}

			// browser

			switch (br.toLowerCase()) {
			case "chrome":
				capabilities.setBrowserName("chrome");
				// WebDriverManager.chromedriver().setup(); // ChromeDriver setup
				// driver = new ChromeDriver(); // Pass the options to the ChromeDriver

				break;

			case "edge":
				capabilities.setBrowserName("MicrosoftEdge");
				// WebDriverManager.edgedriver().setup(); // EdgeDriver setup
				// driver = new EdgeDriver();

				break;

			case "firefox":
				capabilities.setBrowserName("firefox");

				break;

			default:
				System.out.println("No Matching Browser");
				return;
			}
			driver.set(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities));

		}

		if (p.getProperty("execution_env").equalsIgnoreCase("local"))
			// Setup WebDriver based on browser parameter

			switch (br.toLowerCase()) {
			case "edge":
				WebDriverManager.edgedriver().setup(); // EdgeDriver setup
				driver.set(new EdgeDriver());
				break;
	
				
			case "chrome":
			    ChromeOptions chromeOptions = new ChromeOptions();

			    // Disable password manager popup & set download preferences
			    java.util.Map<String, Object> prefs = new java.util.HashMap<String, Object>();
		        prefs.put("download.default_directory", downloadFilepath);
		        prefs.put("download.prompt_for_download", false);
		        prefs.put("safebrowsing.enabled", true);
		        prefs.put("profile.password_manager_enabled", false);
		        prefs.put("credentials_enable_service", false);
		        prefs.put("profile.password_manager_leak_detection", false);
		        chromeOptions.setExperimentalOption("prefs", prefs);
		      //  chromeOptions = new EdgeOptions();
		        chromeOptions.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});	   
		        chromeOptions.setExperimentalOption("prefs", prefs);
				
				WebDriverManager.chromedriver().setup(); // ChromeDriver setup
				driver.set(new ChromeDriver(chromeOptions)); // Pass the options to the ChromeDriver
				break;
			case "firefox":
				// Set Firefox options
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.dir", "D:\\DFCloud\\Downloads"); // Specify your
																										// path
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
				profile.setPreference("security.fileuri.strict_origin_policy", false);
				profile.setPreference("browser.download.useDownloadDir", true);

				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setProfile(profile); // Set the profile in Firefox options
				WebDriverManager.firefoxdriver().setup(); // FirefoxDriver setup
				driver.set(new FirefoxDriver(firefoxOptions)); // Pass the options to the FirefoxDriver
				break;
			default:
				System.out.println("This is an invalid browser name...");
				return;
			}

		if (getDriver() != null) {
			driver.get().manage().deleteAllCookies();
			driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
			wait = new WebDriverWait(driver.get(), Duration.ofSeconds(20)); // Initialize WebDriverWait
			
			driver.get().get(p.getProperty("logincloudURL"));
		//	driver.get().get("https://www4.faronicsbeta.com/en/Account/Login");
			driver.get().manage().window().maximize();

			ScreenRecorderUtil.startRecord(br); // Start screen recording

		}

	}

	@AfterTest
	@Step("Teardown WebDriver")
	public void tearDown() {

		try {
			if (driver != null) {

				//captureScreen("TestName", resultsDirectory);
				driver.get().quit();
				logger.info("WebDriver closed successfully.");
				driver.remove(); // Clean up ThreadLocal instance
				ScreenRecorderUtil.stopRecord();
			}
		} catch (Exception e) {
			logger.error("Error during WebDriver teardown: " + e.getMessage());
		}
	}

	public static WebDriver getDriver() {
		return driver.get();
	}

	// Method to load properties from a file
	public static Properties loadProperties(String filePath) {
		Properties prop = new Properties();
		try (FileInputStream input = new FileInputStream(filePath)) {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to load properties from file: " + filePath);
		}
		return prop;
	}

	// Randomly generated string (alphabetic)
	@Step("Generate random string")
	public String randomeString() {
		return RandomStringUtils.randomAlphabetic(10); // Generate random alphabetic string
	}

	// Randomly generated number
	public String randomNumber() {
		return RandomStringUtils.randomNumeric(10); // Generate random numeric string
	}

	// Randomly generated alphanumeric string with special character
	public String randomeAlphaNumberic() {
		String generatedstring = RandomStringUtils.randomAlphabetic(5); // Generate random alphabetic string
		String generatednumber = RandomStringUtils.randomNumeric(5); // Generate random numeric string
		return generatedstring + "@" + generatednumber; // Combine and return
	}

	// @FindBy(xpath = "(//span[@id='dvTaskStatus'])[1]")

	@FindBy(id = "Taskstatus")

	WebElement navigatetoTaskStatus;

	public void openTaskStausPage() {
		navigatetoTaskStatus.click();
	}

	@FindBy(xpath = "//div[@class='dx-datagrid-content']//table//tbody//tr[1]//td[5]")
	WebElement clickonfirstrow;
	@FindBy(xpath = "//div[@class='dx-datagrid-content']//table//tbody//tr[1]//td[4]")
	WebElement clicktaskname;

	public void checkfirestrow1() throws InterruptedException {
		for (int r = 1; r <= 30; r++) {
			driver.get().navigate().refresh();
			Thread.sleep(5000);

			WebElement element = clickonfirstrow;
			WebElement element2 = clicktaskname;
			String statusText = element.getText(); // Get the text from the element
			String taskName = element2.getText(); // Get the task name text

			// Print the task name and status
			System.out.println("Task Name: " + taskName + ", Status for row " + r + ": " + statusText);
			// Print the status text
			// System.out.println("Status for row " + r + ": " + statusText);

			if (statusText.contains("Execute")) {
				System.out.println("Task executed successfully");
				break;
			} else if (statusText.contains("Failed")) {
				System.out.println("Task execution failed");
				break;
			}

		}
	}

	@Step("Capture screen for test: {testName}")
	@Attachment(value = "Screenshot", type = "image/png")
	// Updated captureScreen method to save screenshots in the report folder
/*	public String captureScreen(String testName, String reportDir) throws IOException {
		// Create directory if it doesn't exist
		new File(reportDir).mkdirs();

		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = testName + "_" + timestamp + ".png";
		String filePath = reportDir + File.separator + fileName; // Use File.separator for OS compatibility

		TakesScreenshot ts = (TakesScreenshot) driver.get();
		File source = ts.getScreenshotAs(OutputType.FILE);
		File target = new File(filePath);

		// Copy the screenshot to the target file location
		Files.copy(source.toPath(), target.toPath());

		return target.getAbsolutePath(); // Return the full path for the report
	}   */
	public String captureScreen(String testName, String reportDir) throws IOException {
	    new File(reportDir).mkdirs();

	    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    String fileName = testName + "_" + timestamp + ".png";
	    String filePath = reportDir + File.separator + fileName;

	    if (driver.get() == null) {
	        System.out.println("❌ WebDriver is null, screenshot skipped.");
	        return "";
	    }

	    TakesScreenshot ts = (TakesScreenshot) driver.get();
	    File source = ts.getScreenshotAs(OutputType.FILE);
	    File target = new File(filePath);
	    Files.copy(source.toPath(), target.toPath());

	    System.out.println("✅ Screenshot saved at: " + filePath);
	    return target.getAbsolutePath();
	}

	
	//////////////////////////////////////////////////

	public List<String[]> readExcelData(String filePath, String sheetName) {
	    List<String[]> dataList = new ArrayList<>();

	    try (FileInputStream fis = new FileInputStream(filePath);
	         Workbook workbook = new XSSFWorkbook(fis)) {

	        Sheet sheet = workbook.getSheet(sheetName);
	        int rowCount = sheet.getPhysicalNumberOfRows();

	        for (int i = 1; i < rowCount; i++) {
	            Row row = sheet.getRow(i);
	            String url = row.getCell(1).getStringCellValue();        // Column B
	            String username = row.getCell(2).getStringCellValue();   // Column C
	            String password = row.getCell(3).getStringCellValue();   // Column D

	            dataList.add(new String[]{url, username, password});
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return dataList;
	}

	
	// 👇 Add this in your Base_Page class
	public void logToReport(Status status, String message) {
	    System.out.println("[LOG] " + message);

	    // ✅ Extent Report
	    try {
	        ExtentReportManager.getTest().log(status, message);
	    } catch (Exception e) {
	        System.out.println("Extent logging failed: " + e.getMessage());
	    }

	    // ✅ Allure Report
	    try {
	        Allure.step(message);
	    } catch (Exception e) {
	        System.out.println("Allure logging failed: " + e.getMessage());
	    }
	}



	// Initialize page elements
	public void initializeElements(Object page) {
		PageFactory.initElements(driver.get(), page); // Initialize page elements using PageFactory
	}

}