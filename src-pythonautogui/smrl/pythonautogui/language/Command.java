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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smrl.mr.language.MRData;

public class Command extends MRData {
	
	private String stringCommand;
	protected String output;
	
	protected Command() {
	}
	
	//python3.8 ros  
	public Command( String string ) {
		stringCommand = string;
	}

	public List<String> getStringTokens() {
		ArrayList<String> list = new ArrayList<String>();
		for ( String item : stringCommand.split("\t") ) {
			list.add( item );	
		}
		return list;
	}
	

	

	@Override
	public String toString() {
		return stringCommand;
	}

	String IPADDRESS_PATTERN = 
	        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

	public String getIP() {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(stringCommand);
		if (matcher.find()) {
		    return matcher.group();
		} else{
		    return null;
		}
	}

	public boolean replaceAll(String origin, String replacement) {
		String orig = stringCommand;
		stringCommand = stringCommand.replace(origin, replacement);
		return ! orig.equals(stringCommand);
	}

	public MRData clone() {
		return new Command(stringCommand);
	}
}
