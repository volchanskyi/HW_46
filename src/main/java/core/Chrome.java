package core;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Chrome {

    public static String decrypt(String encryptedText, SecretKey secretKey) throws NoSuchAlgorithmException,
	    NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	Cipher cipher;
	cipher = Cipher.getInstance("AES");
	cipher.init(Cipher.DECRYPT_MODE, secretKey);
	String decryptedText = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
	return decryptedText;
    }

    public static void main(String[] args) throws InterruptedException, InvalidKeyException, IllegalBlockSizeException,
    BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
	Logger.getLogger("").setLevel(Level.OFF);
	String url = "https://facebook.com/";
	String emailAddress = "testusera056@gmail.com";
	// String password = "passwordForUser056";
	String encPwd = "m2GDFq4v9uDWExV8fqINNsc4CRx50LdH7nToLJ6UWPA=";
	String copyright = "//*[@id=\'pageFooter\']/div[3]/div/span";
	String emailAddressId = "email";
	String pwdId = "pass";
	String loginBtnId = "loginbutton";
	String timelineBtnXpath = "//*[@id='u_0_a']/div[1]/div[1]/div/a/span";
	String friendsBtnXpath = "//div[2]/ul/li[3]/a/span";
	String accountSettingsBtn = "userNavigationLabel";
	String logoutbtnXpath = "Log Out";
	String mac_address;
	String cmd_mac = "ifconfig en0";
	String cmd_win = "cmd /C for /f \"usebackq tokens=1\" %a in (`getmac ^| findstr Device`) do echo %a";

	if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
	    mac_address = new Scanner(Runtime.getRuntime().exec(cmd_win).getInputStream()).useDelimiter("\\A").next()
		    .split(" ")[1].toLowerCase();
	} else {
	    mac_address = new Scanner(Runtime.getRuntime().exec(cmd_mac).getInputStream()).useDelimiter("\\A").next()
		    .split(" ")[4];
	}
	mac_address = mac_address.toLowerCase().replaceAll("-", ":");

	String decPwd = decrypt(encPwd, new SecretKeySpec(Arrays.copyOf(mac_address.getBytes("UTF-8"), 16), "AES"));
	String driverPath = "";
	if (System.getProperty("os.name").toUpperCase().contains("MAC"))
	    driverPath = "./resources/webdrivers/mac/chromedriver";
	else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
	    driverPath = "./resources/webdrivers/pc/chromedriver.exe";
	else
	    throw new IllegalArgumentException("Unknown OS");

	System.setProperty("webdriver.chrome.driver", driverPath);
	System.setProperty("webdriver.chrome.silentOutput", "true");
	ChromeOptions option = new ChromeOptions();
	option.addArguments("disable-infobars");
	option.addArguments("--disable-notifications");
	if (System.getProperty("os.name").toUpperCase().contains("MAC"))
	    option.addArguments("-start-fullscreen");
	else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
	    option.addArguments("--start-maximized");
	else
	    throw new IllegalArgumentException("Unknown OS");
	WebDriver driver = new ChromeDriver(option);
	System.out.println("Browser: Chrome");
	WebDriverWait wait = new WebDriverWait(driver, 15);
	driver.get(url);
	String result = null;
	String title = driver.getTitle();
	String copyR = driver.findElement(By.xpath(copyright)).getText();
	
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(emailAddressId))).clear();

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(emailAddressId))).sendKeys(emailAddress);

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(pwdId))).sendKeys(decPwd);

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(loginBtnId))).click();
//	Thread.sleep(200);
	
//	 System.out.println(title);
	if (!title.equals("Facebook - Log In or Sign Up")) {
	    result = "Login failed: " + driver.findElement(By.id("ErrorLineEx")).getText();
	} else
	    result = "Login success";

	System.out.println(result);
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(timelineBtnXpath))).click();

	String friends = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(friendsBtnXpath))).getText();

	if (friends.isEmpty()) {
	    friends = "0";
	}

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(accountSettingsBtn))).click();
	//
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(logoutbtnXpath))).click();

	driver.quit();

	System.out.println("Browser: Chrome");
	System.out.println("Title of the page: " + title);
	System.out.println("Copyright: " + copyR);
	System.out.println("Friends: You have " + friends + " friends");

	driver.quit();
    }
}
