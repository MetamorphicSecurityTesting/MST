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
import smrl.mr.language.MRData;

public class Scenario extends MRData {
	private List<Command> commands = new ArrayList<Command>();
	private ROSScenarioOutput output;

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
	
	public Scenario(Command... _commands) {
		for ( Command s : _commands ) {
			commands.add(s);
		}
	}
	
	public Scenario(String _commands[]) {
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

	public boolean execute() {
		ROSScenarioOutput output = new ROSScenarioOutput(); 
		for ( Command command : getCommands() ) {
			output.add( command.execute() );
		}

		synchronized (this) {
			this.output = output;	
		}

		return true;
	}

	Object scenario = new Object();
	
	public boolean executeAsynch() {
		ROSScenarioOutput _output = new ROSScenarioOutput(); 

		
		
		Thread t = new Thread() {

			@Override
			public void run() {

				for ( Command command : getCommands() ) {
					_output.add( command.execute() );
				}

//				System.out.println("PRE ASYNCH WRITTEN "+scenario);
//				System.out.flush();
				
				synchronized (scenario) {
					output = _output;
					scenario.notify();
				}
				
//				System.out.println("ASYNCH WRITTEN "+output +" "+scenario);
			}

		};

		t.start();


//		System.out.println("ASYNCH CONLCUDED");
		return true;
	}



	public boolean waitForTermination() {
		

		synchronized (scenario) {
			

			while ( output == null ) {
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
	
	public String getOutputForCommand( int x ) {
		return output.getOutputForCommand( x );
	}
	
	public String getOutput() {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < output.size() ; i++ ) {
			sb.append( output.getOutputForCommand( i ) );
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return commands.toString();
	}

	@Override
	public void addReassignment(MRData rhs) {
		super.addReassignment(rhs);
		
		scenario = new Object();
		
//		System.out.println("addReassignment "+scenario);
	}

	
}
