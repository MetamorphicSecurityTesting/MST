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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import smrl.mr.language.MRData;

public class ParallelScenario extends MRData {
	private List<Scenario> parallelScenarios = new ArrayList<Scenario>();


	
	public ParallelScenario(Scenario... scenarios) {
		for ( Scenario scenario : scenarios ) {
			parallelScenarios.add( scenario );
		}
	}

	

	public boolean execute() {
		
		for ( Scenario scenario : parallelScenarios ) {
			scenario.executeAsynch();
		}

		for ( Scenario scenario : parallelScenarios ) {
			scenario.waitForTermination();
		}

		return true;
	}
	
	public String getOutput() {
		StringBuffer sb = new StringBuffer();
		
		for ( Scenario scenario : parallelScenarios ) {
			sb.append ( scenario.getOutput() );
		}
		
		return sb.toString();
	}



	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ParallelScenario{\n");
		for ( Scenario scenario : parallelScenarios ) {
			sb.append(scenario.toString());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}


	
}
