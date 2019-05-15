package ase2019.mr.singleTest;

import ase2019.mr.crawljax.WebOperationsProvider;
import ase2019.mr.language.MRBaseTest;
import ase2019.mr.owasp.OTG_AUTHZ_001b;
import ase2019.mr.owasp.OTG_AUTHZ_002;
import ase2019.mr.owasp.OTG_AUTHZ_002a;
import ase2019.mr.owasp.OTG_AUTHZ_002b;
import ase2019.mr.owasp.OTG_AUTHZ_002c;
import ase2019.mr.owasp.OTG_AUTHZ_002d;
import ase2019.mr.owasp.OTG_INPVAL_004;
import ase2019.mr.owasp.OTG_SESS_003;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleTest extends MRBaseTest {
	
	private static WebOperationsProvider provider;
	
	private static String system = "jenkins";
//	private static String system = "iws";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		//by default, the SUT is the Jenkins
		String configFile = "./testData/Jenkins/jenkinsSysConfig.json";
		
		if(SingleTest.system.equals("iws")){
			configFile = "./testData/IWS/iwsSysConfig.json";
		}
		
		
		provider = new WebOperationsProvider(configFile);
	}

	@Before
	public void setUp() throws Exception {
		setProvider(provider);
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
	public void test_OTG_INPVAL_004() {
		super.test(provider,OTG_INPVAL_004.class);
	}
	
	@Test
	public void test_OTG_SESS_003() {
		super.test(provider,OTG_SESS_003.class);
	}
	
}
