/*******************************************************************************
 * Copyright (c) University of Luxembourg 2022
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.pythonautogui.language;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;



import smrl.mr.crawljax.ProcessRunner;

public class ScriptExecutor {
	String python = System.getProperty("python","python3");
	
	public static int execute(Scenario scenario) throws IOException {

		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		List<String> exec = new ArrayList<>();
		exec.add("");
		
		File script = new File("MSTscript.py");
		Path filePath = script.toPath();
		Files.deleteIfExists(filePath);
		Files.createFile(filePath);
		for (Command cmd : scenario.getCommands()) {
		    Files.write(filePath, (cmd + System.lineSeparator()).getBytes(),
		    StandardOpenOption.APPEND);
		}
		
		
		exec.add(script.getAbsolutePath());

		try {
			int exitCode = ProcessRunner.run( exec, "", outputBuffer, errorBuffer, (int) 0 );

			System.out.println("DONE");
			
			System.out.println(outputBuffer.toString());
			
			System.out.println(errorBuffer.toString());
			
			return exitCode;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}
}
