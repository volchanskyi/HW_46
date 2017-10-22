package core;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HtmlUnit {
 
    
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
	String USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:55.0) Gecko/20100101 Firefox/55.0";
	String url = "https://www.facebook.com/";
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

	String decPwd = decrypt(encPwd,
		new SecretKeySpec(Arrays.copyOf(mac_address.getBytes("UTF-8"), 16), "AES"));
	

	WebDriver driver = new HtmlUnitDriver();
	((HtmlUnitDriver) driver).setJavascriptEnabled(true);
//	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	driver.manage().window().maximize();
	System.out.println("Browser: HtmlUnit");
	WebDriverWait wait = new WebDriverWait(driver, 15);
	driver.get(url);
	

	String title = driver.getTitle();
	String copyR = driver.findElement(By.xpath(copyright)).getText();

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(emailAddressId))).clear();

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(emailAddressId))).sendKeys(emailAddress);

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(pwdId))).sendKeys(decPwd);

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(loginBtnId))).click();
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(timelineBtnXpath))).click();

	String friends = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(friendsBtnXpath))).getText();


	if (friends.isEmpty()) {
	    friends = "0";
	}

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(accountSettingsBtn))).click();
	//
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(logoutbtnXpath))).click();

	System.out.println("Browser: HtmlUnit");
	System.out.println("Title of the page: " + title);
	System.out.println("Copyright: " + copyR);
	System.out.println("Friends: You have " + friends + " friends");
	  driver.quit();
    }
  
}
