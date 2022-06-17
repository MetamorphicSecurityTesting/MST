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
package smrl.ros.language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import smrl.mr.crawljax.ProcessRunner;

public class CommandExecutor {
	public static String executeCommand(Command command) {

		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		List<String> exec = new ArrayList<>();
		exec.addAll(command.getStringTokens());

		try {
			int exitCode = ProcessRunner.run( exec, "", outputBuffer, errorBuffer, (int) 0 );

			System.out.println("DONE");
			
			System.out.println(outputBuffer.toString());
			
			System.out.println(errorBuffer.toString());
			
			return outputBuffer.toString()+"\n"+errorBuffer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
