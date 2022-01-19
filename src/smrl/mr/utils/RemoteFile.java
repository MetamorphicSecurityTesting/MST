package smrl.mr.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import smrl.mr.crawljax.ProcessRunner;
import smrl.mr.crawljax.VMWrapper;

public class RemoteFile {

	private String path;
	private VMWrapper vmWrapper;
	private File tmp;
	private File tmpLocal;

	public RemoteFile(VMWrapper vmWrapper, String path){
		this.vmWrapper = vmWrapper;
		this.path = path;
		
		this.tmp = new File("tmp");
		tmp.mkdirs();
		
		tmpLocal = new File( tmp, ""+System.currentTimeMillis()  );
		
		if ( vmWrapper == null ) {
			try {
				cp ( path, tmpLocal.getAbsolutePath() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			vmWrapper.copyFromVM(path, tmpLocal.getAbsolutePath());
		}
	}
	
	public static int cp( String orig, String dest) throws IOException {
		
		try(
			      InputStream in = new BufferedInputStream(
			        new FileInputStream(orig));
			      OutputStream out = new BufferedOutputStream(
			        new FileOutputStream(dest))) {
			 
			        byte[] buffer = new byte[1024];
			        int lengthRead;
			        while ((lengthRead = in.read(buffer)) > 0) {
			            out.write(buffer, 0, lengthRead);
			            out.flush();
			        }
			    }
		
		
		return 0;
	}
	
	public List<String> getLines(){
		URI uri = tmpLocal.toURI();
        try {
			List<String> lines = Files.readAllLines(Paths.get(uri),
			    Charset.defaultCharset());
			return lines;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return null;
	}
	
	/**
	 * Returns the lines in rhs not present in the current file.
	 * 
	 * @param rhs
	 * @return
	 */
	public List<String> newLines(RemoteFile rhs){
		List<String> lines = getLines();
		List<String> rhsLines = rhs.getLines();
		
		if ( rhsLines == null ) {
			return new ArrayList<>();
		}
		
		int deltaStart;
		if ( lines == null ) {
			deltaStart = 0;
		} else {
			String el = lines.get(lines.size()-1);
			deltaStart = rhsLines.indexOf(el);
		}
		
		
		return rhsLines.subList(deltaStart+1, rhsLines.size());
	}
}
