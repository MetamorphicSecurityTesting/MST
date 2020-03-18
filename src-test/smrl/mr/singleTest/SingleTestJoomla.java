package smrl.mr.singleTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRBaseTest;
import smrl.mr.owasp.OTG_AUTHN_001;
import smrl.mr.owasp.OTG_AUTHN_004;
import smrl.mr.owasp.OTG_AUTHN_010;
import smrl.mr.owasp.OTG_AUTHZ_001a;
import smrl.mr.owasp.OTG_AUTHZ_001b;
import smrl.mr.owasp.OTG_AUTHZ_001b2;
import smrl.mr.owasp.OTG_AUTHZ_002;
import smrl.mr.owasp.OTG_AUTHZ_002a;
import smrl.mr.owasp.OTG_AUTHZ_002b;
import smrl.mr.owasp.OTG_AUTHZ_002c;
import smrl.mr.owasp.OTG_AUTHZ_002d;
import smrl.mr.owasp.OTG_AUTHZ_002e;
import smrl.mr.owasp.OTG_AUTHZ_003;
import smrl.mr.owasp.OTG_AUTHZ_004;
import smrl.mr.owasp.OTG_BUSLOGIC_005;
import smrl.mr.owasp.OTG_CONFIG_007;
import smrl.mr.owasp.OTG_CRYPST_004;
import smrl.mr.owasp.OTG_INPVAL_003;
import smrl.mr.owasp.OTG_INPVAL_004;
import smrl.mr.owasp.OTG_SESS_003;
import smrl.mr.owasp.OTG_SESS_006;
import smrl.mr.owasp.OTG_SESS_007;
import smrl.mr.owasp.OTG_SESS_008;

public class SingleTestJoomla extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "joomla";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.out.println("*** Starting time: " + getCurrentTime() +" ***");
		
		//by default, the SUT is the Joomla!
		String configFile = "./testData/Joomla/joomlaSysConfig.json";
		
		provider = new WebOperationsProvider(configFile);
	}

	@Before
	public void setUp() throws Exception {
		setProvider(provider);
	}
	
	@AfterClass
    public static void printEndingTime() {
		System.out.println("*** Ending time: " + getCurrentTime() + " ***");
    }   
	
	@Test
	public void test_OTG_AUTHN_004() {
		super.test(provider,OTG_AUTHN_004.class);
	}
	
	@Test
	public void test_OTG_AUTHN_010() {
		super.test(provider,OTG_AUTHN_010.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_001b() {
		super.test(provider,OTG_AUTHZ_001b.class);
	}

	@Test
	public void test_OTG_AUTHZ_002() {
		super.test(provider,OTG_AUTHZ_002.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_002a() {
		super.test(provider,OTG_AUTHZ_002a.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_002b() {
		super.test(provider,OTG_AUTHZ_002b.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_002c() {
		super.test(provider,OTG_AUTHZ_002c.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_002d() {
		super.test(provider,OTG_AUTHZ_002d.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_002e() {
		super.test(provider,OTG_AUTHZ_002e.class);
	}
	
	@Test
	public void test_OTG_INPVAL_004() {
		super.test(provider,OTG_INPVAL_004.class);
	}
	
	@Test
	public void test_OTG_SESS_003() {
		super.test(provider,OTG_SESS_003.class);
	}
	
		
	@Test
	public void test_OTG_AUTHZ_001a() {
		super.test(provider,OTG_AUTHZ_001a.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_001b2() {
		super.test(provider,OTG_AUTHZ_001b2.class);
	}
	
	@Test
	public void test_OTG_AUTHN_001() {
		super.test(provider,OTG_AUTHN_001.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_003() {
		super.test(provider,OTG_AUTHZ_003.class);
	}
	
	@Test
	public void test_OTG_AUTHZ_004() {
		super.test(provider,OTG_AUTHZ_004.class);
	}
	
	@Test
	public void test_OTG_BUSLOGIC_005() {
		super.test(provider,OTG_BUSLOGIC_005.class);
	}
	
	@Test
	public void test_OTG_CONFIG_007() {
		super.test(provider,OTG_CONFIG_007.class);
	}
	
	@Test
	public void test_OTG_CRYPST_004() {
		super.test(provider,OTG_CRYPST_004.class);
	}
	
	@Test
	//Run with proxy
	public void test_OTG_INPVAL_003() {
		super.test(provider,OTG_INPVAL_003.class);
	}
	
	@Test
	public void test_OTG_SESS_006() {
		super.test(provider,OTG_SESS_006.class);
	}
	
	@Test
	public void test_OTG_SESS_007() {
		super.test(provider,OTG_SESS_007.class);
	}
	
	@Test
	public void test_OTG_SESS_008() {
		super.test(provider,OTG_SESS_008.class);
	}
	
	public static String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now).toString();  
	}

}