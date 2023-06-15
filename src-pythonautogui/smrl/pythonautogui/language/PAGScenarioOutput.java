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

import java.util.ArrayList;

public class PAGScenarioOutput {
	ArrayList<String> outputs = new ArrayList<String>();
	
	public void add(String commandOutput) {
		outputs.add( commandOutput );
	}
	
	public String getOutputForCommand(int pos ) {
		return outputs.get(pos);
	}

	public int size() {
		return outputs.size();
	}

}
