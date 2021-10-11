package smrl.mr.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		vmWrapper.copyFromVM(path, tmpLocal.getAbsolutePath());
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
