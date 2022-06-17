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

public class Sleep extends Command {
	
	private long sleep;
	
	public Sleep(long value ) {
		sleep = value;
	}
	
	public String execute() {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
		}
		output = "";
		return output;
	}

	public boolean executeAsynch() {
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
				output = "";
	
			}

		};

		t.start();
		return true;
	}

	@Override
	public String toString() {
		return "Sleep("+sleep+")";
	}

	
}
