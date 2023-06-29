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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import smrl.mr.crawljax.ProcessRunner;
import smrl.mr.language.MRData;

public class Scenario extends MRData {
	String name;
	ArrayList<Command> commands = new ArrayList<Command>();

	//	@Override
	//	public MRData clone() throws CloneNotSupportedException {
	//		Scenario _cloned = (Scenario) super.clone();
	//		_cloned.scenario = scenario;
	//		
	////		System.out.println("CLONE "+scenario);
	//		return _cloned;
	//	}

	public Scenario(Scenario... _scenarios) {
		for ( Scenario s : _scenarios ) {
			commands.addAll(s.getCommands());
		}
	}

	public Scenario(String name, Command... _commands) {
		this.name = name;
		for ( Command s : _commands ) {
			commands.add(s);
		}
	}

	public Scenario(String name, String _commands[]) {
		this.name = name;
		for ( String command : _commands ) {
			commands.add(new Command ( command ));
		}
	}

	public List<Command> getCommands() {
		return commands;
	}

	public boolean add( Command cmd ) {
		return commands.add( cmd );
	}

	Integer returnValue;//to become an image?
	public boolean execute() {
		
		final Scenario scenario = this;

		Thread t = new Thread() {

			@Override
			public void run() {

				Integer _output = null;
				try {
					_output = ScriptExecutor.execute( scenario );
				} catch (IOException e) {
					_output = -1;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (scenario) {
					returnValue = _output;
					scenario.notify();
				}
			}

		};

		t.start();

		waitForTermination();

		return true;
	

}




Object scenario = new Object();

public boolean waitForTermination() {


	synchronized (scenario) {


		while ( returnValue == null ) {
			try {
				scenario.wait(1000);

				//					System.out.println("WAKE "+output+" "+scenario);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	return true;
}

/**
 * Returns the exit state of the process
 * @return
 */
public Integer getExistState() {
	return returnValue;
}

/**
 * Replaces all the nstances of 'orig' with 'replacement.
 * Returns true if anything has been replaced
 * 
 * @param origin
 * @param replacement
 * @return
 */
public boolean replaceAll(String origin, String replacement) {
	this.setAlreadyUsedInRHS();
	boolean ret = false;
	for ( Command command : commands ) {
		ret |= command.replaceAll(origin,replacement);
	}
	return ret;
}

//public String getOutput() {
//	return output;
//}

@Override
public String toString() {
	return "Scenario "+name+": "+commands.toString();
}

public boolean hasIP() {
	return getIP() != null;
}

public String getIP() {
	for ( Command command : commands ) {
		String IP = command.getIP();
		if ( IP != null ) {
			return IP;
		}
	}
	return null;
}

public ScreenShot getScreenShot() {
	return null;
}

@Override
public MRData clone() throws CloneNotSupportedException {
	Scenario result = (Scenario) super.clone();
	result.commands = new ArrayList<Command>();
	for ( Command command : commands ) {
		result.add((Command) command.clone());
	}
	
	return result;
}



}
