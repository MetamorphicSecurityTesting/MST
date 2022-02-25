package smrl.mr.language;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SessionValue{
	private SessionId SessionID;
	//static WebDriver driver = new ChromeDriver();
	static WebDriver driver = new FirefoxDriver();
	public SessionId getSessionID() {
		this.SessionID = ((RemoteWebDriver) driver).getSessionId();
		return SessionID;
	}
	
	public static void main(String[] args) {
      //set chromedriver.exe file path
      //System.setProperty("webdriver.chrome.driver",         "C:\\Users\\ghs6kor\\Desktop\\Java\\chromedriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\nbaya076\\geckodriver.exe");
      //implicit wait
      driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
      //URL launch
      driver.get("https://www.tutorialspoint.com/index.htm");
      //get webdriver session id
      SessionId s = ((RemoteWebDriver) driver).getSessionId();
      System.out.println("Session Id is: " + s);
      //browser close
      driver.quit();
   }
	
	
}