/*******************************************************************************
 * Copyright (c) University of Luxembourg 2022
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Aymeric Le Drezen (aymeric.ledrezen.001@student.uni.lu)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import smrl.mr.crawljax.ProcessRunner;
import smrl.mr.language.Input;
import smrl.mr.language.MR;
import smrl.mr.language.MRDataProvider;
import smrl.mr.language.Output;

public class ROSOperations {
	/**
	 * Data Representation Function. 
	 * 
	 *  
	 * @param i	The index of the input sequence to be returned, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Scenario Scenario(int x){ 
		Scenario res = (Scenario) MR.CURRENT.getMRData("Scenario",x);
		return res;
	}
	
	/**
	 * Data Representation Function. 
	 * 
	 *  
	 * @param i	The index of the input sequence to be returned, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static ParallelScenario ParallelScenario(int x){ 
		ParallelScenario res = (ParallelScenario) MR.CURRENT.getMRData("ParallelScenario",x);
		return res;
	}
	
	
	public static Sleep Sleep(long millis){ 
		return new Sleep(millis);
	}
	
	/**
	 * Data Representation Function. 
	 * 
	 *  
	 * @param i	The index of the input sequence to be returned, with respect to the current data view.
	 * @return
	 */
	@MRDataProvider
	public static Command Request(int x){ 
		return (Command) MR.CURRENT.getMRData("Request",x);
	}

	
	public static boolean almostEqual(String s1, String s2, double percentage ) {
		List<String> list1 = convertPythonListToJavaList(s1);
		List<String> list2 = convertPythonListToJavaList(s2);
		
		double Distance = EditDistance(list1, list2);
	    double DistancePercentage = Distance / Math.max(list1.size(), list2.size());
	    if (DistancePercentage <= percentage)
	    {
	      return true;
	    }
	    else
	    {
	      return false;
	    }
	}		

	public static List<String> convertPythonListToJavaList(String pythonListString) {
		    List<String> javaList = new ArrayList<>();
		    String[] pythonListItems = pythonListString.substring(1, pythonListString.length() - 1).split(", ");
		    for (String item : pythonListItems) {
		        javaList.add(item);
		    }
		    return javaList;
	}
	
	public static int EditDistance(List<String> list1, List<String> list2) {
		int d[][] = new int[list1.size() + 1][list2.size() + 1];
		for (int i = 0; i <= list1.size(); i++) {
			for (int j = 0; j <= list2.size(); j++) {
				if (i == 0)
					d[i][j] = j;
				else if (j == 0)
					d[i][j] = i;
				else {
					d[i][j] = min(
							d[i - 1][j] + 1,
							d[i][j - 1] + 1,
							d[i - 1][j - 1] + ((list1.get(i - 1).equals(list2.get(j - 1))) ? 0 : 1));
					}
				}
			}
   
          return d[list1.size()][list2.size()];
      }
	 
	public static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}	

