/*******************************************************************************
 *    Copyright 2019 Fabrizio Pastore, Leonardo Mariani, and other authors indicated in the source code below.
 *   
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package smrl.mr.crawljax;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;



public class ProcessRunner {
	private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class.getCanonicalName());

	/**
	 * Runs the main method of the given class in a standalone process.
	 * Stops the process if it takes more than maxExecutionTime seconds to finish.
	 * 
	 * @param clazz
	 * @param args
	 * @param maxExecutionTime
	 * @return 
	 * @throws IOException
	 */
	public static int run( List<String> command, String stdInCommands, final Appendable outputBuffer, final Appendable errorBuffer, long maxExecutionTime ) throws IOException{
		return run(command, stdInCommands, outputBuffer, errorBuffer, maxExecutionTime, null);
	}
	
	public static int run( List<String> command, String stdInCommands, Appendable _outputBuffer, Appendable _errorBuffer, long maxExecutionTime, File dir) throws IOException{
		return run(command, stdInCommands, _outputBuffer, _errorBuffer, maxExecutionTime, dir, null);
	}
	
	public static int run( List<String> command, String stdInCommands, Appendable _outputBuffer, Appendable _errorBuffer, long maxExecutionTimeMillis, File dir, Map<String, String> env ) throws IOException{
		
		if ( _outputBuffer == null ){
			_outputBuffer = new OutputStreamWriter(System.out);
		}
		
		if ( _errorBuffer == null ){
			_errorBuffer = new OutputStreamWriter(System.err);
		}
		
		final Appendable outputBuffer = _outputBuffer;
		final Appendable errorBuffer = _errorBuffer;
		
		LOGGER.info("Executing "+command.toString());
		
		String[] cmdArray = command.toArray(new String[command.size()]);
		
		ProcessBuilder pb = new ProcessBuilder(command);
		if ( dir != null ){
			pb.directory(dir);
		}
		if ( env != null ){
			pb.environment().putAll(env);
		}

		
		final Process p = pb.start();
		
		final BufferedInputStream in = new BufferedInputStream(p.getInputStream());
		final BufferedInputStream err = new BufferedInputStream(p.getErrorStream());

		
		
		StopperThread stopperThread=null;
		//Start time limit thread if necessary
		if ( maxExecutionTimeMillis > 0 ){
			stopperThread = new StopperThread(p,maxExecutionTimeMillis);
			stopperThread.start();
		}
		
		
		//Start daikon
		Thread t = new Thread() {

			public void run() {
				try {
					while (true) {
						int c = in.read();
						//System.out.println("c "+c);
						if (c < 0)
							break;
						else
							outputBuffer.append((char)c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t.start();
		Thread t1 = new Thread() {

			public void run() {
				try {
					while (true) {
						int c = err.read();
						if (c < 0)
							break;
						else
							errorBuffer.append((char)c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t1.start();
		try {
			
			OutputStream stdin = p.getOutputStream();
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
			writer.append(stdInCommands);
			writer.flush();
			System.out.println("!!!STARTED");
			int exitCode = p.waitFor();
			
			while ( t1.isAlive() || t.isAlive() ){
				Thread.sleep(100);
			}
			
			in.close();
			err.close();

			//if we are here p has been terminated, thus we can stop the stopperThead
			if ( stopperThread != null && stopperThread.isAlive() ){
				stopperThread.terminate();	
			}
			
			System.out.println("!!!! "+outputBuffer.toString());
			
			LOGGER.info("Exit code "+exitCode);
			return exitCode;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
		
	}
}
