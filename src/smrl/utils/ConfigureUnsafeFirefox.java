package smrl.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

public class ConfigureUnsafeFirefox{
   public static void main(String[] args) {
	   
	   //before running this code please run "firefox -p" in order to create the profile "UnsafeEncryption"
	   ProfilesIni profile = new ProfilesIni();
	   FirefoxProfile myprofile = profile.getProfile("UnsafeEncryption");

	   
	   FirefoxOptions options = new FirefoxOptions();
       options.setProfile(myprofile);
       WebDriver driver = new FirefoxDriver(options);
       
      driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
      
      //the first time you run this, you need to configure "security.tls.max" in "about:config" page
      driver.get("https://www.tutorialspoint.com/index.htm");
      
 
   }
}
