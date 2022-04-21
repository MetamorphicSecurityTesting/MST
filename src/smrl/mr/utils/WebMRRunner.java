package smrl.mr.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import smrl.mr.crawljax.WebOperationsProvider;
import smrl.mr.language.MRRunner;

public class WebMRRunner {

	

	public static void main(String[] args) {
		
		
		//by default, the SUT is the Jenkins
		//"./testData/JenkinsICSE/jenkinsSysConfigDEMO.FABRIZIO.json";
		String configFile = args[0];
		String MR = args[1];
		
		
//		String classpath = System.getProperty("java.class.path");
//		System.out.println(classpath);
//        
//        System.exit( 1 );
        
		
		smrl.mr.crawljax.WebProcessor.DEFAULT_HEADLESS = false;
		WebOperationsProvider provider = new WebOperationsProvider(configFile);
		
		Class mr;
		try {
			mr = Class.forName( MR );
			List<String> failures = MRRunner.runAndGetFailures( provider, mr );
			
			
			Path file = Paths.get(MR+"-WebMRRunner.FAILURES.txt");
			try {
				Files.write(file, failures, StandardCharsets.UTF_8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if ( failures.size() > 0 ) {
				System.out.println(failures);
				
				System.exit( 1 );
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			System.exit( 2 );
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			System.exit( 2 );
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			System.exit( 2 );
		} 
		
		
	}

}
