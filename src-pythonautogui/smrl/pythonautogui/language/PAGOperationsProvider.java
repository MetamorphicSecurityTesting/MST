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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.language.Action;
import smrl.mr.language.OperationsProvider;
import smrl.mr.language.SystemConfig;
import smrl.mr.utils.RemoteFile;

public class PAGOperationsProvider implements OperationsProvider {

	private File dataFolder;
	
	public PAGOperationsProvider(File dataFolder) {
		this.dataFolder = dataFolder;
	}

	@Override
	public Action DeleteCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLogin(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cannotReachThroughGUI(Object user, smrl.mr.language.Input lastURL) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cannotReachThroughGUI(Object user, String URL) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public smrl.mr.language.Output Output(smrl.mr.language.Input input, int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Output Output(smrl.mr.language.Input input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Input changeCredentials(smrl.mr.language.Input input, Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Session(smrl.mr.language.Input input, int x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List load(String dataName) {
		File f = new File( dataFolder, dataName );
		
		List<Object> content = new ArrayList<Object>();
		
		
		if ( "Scenario".equals(dataName)  ) {
			f = new File( dataFolder, "Scenarios" );
			File[] pythonScripts = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.startsWith("Test") && arg1.endsWith(".py");
				}
				
			});
			for ( File script : pythonScripts ) {
				content.add( new Scenario(script.getName(),loadContent(script)) );
			}
			
		} else if ( "IP".equals(dataName) ) {
			content.addAll(loadIPs());
		} else {
			if (! f.exists() ) {
				return content;
			}
			//TODO load file content?
		}
		
		

		
		
		return content;
	}

	private Set<String> loadIPs() {
		List<Scenario> scenarios = load("Scenario");
		Set<String> IPs = new HashSet<String>();
		
		for ( Scenario scenario : scenarios ) {
			String IP = scenario.getIP();
			if ( IP != null ) {
				IPs.add(IP);
			}
		}
		
		return IPs;
	}

	private Command[] loadContent(File f) {
		ArrayList<Command> commands = new ArrayList<Command>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			
			String line = br.readLine();
			
			while (line != null) {
				if(!line.trim().isEmpty()){
					commands.add(new Command(line));				
				}
				line = br.readLine();
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commands.toArray(new Command[commands.size()]);
	}

	@Override
	public boolean notVisibleWithoutLoggingIn(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] parametersWithDifferentValues(Action action1, Action action2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean urlOfActionChangesInDifferentExecutions(smrl.mr.language.Input input, int x) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action LogoutInAnotherTab() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] extractUserRoleParameters(Action action1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadEMailAction(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object deriveRandomData(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean userCanRetrieveContent(Object user, Object output) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean notAnonymous(Object user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEncrypted(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void nextTest() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean afterLogin(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserIdParameter(Action a, int parpos, Object user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLogout(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public smrl.mr.language.Input Input(Action[] as) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action newLoginAction(Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action newLoginAction(WebInputCrawlJax input, Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Input Input(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSignup(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public smrl.mr.language.Input Input(List<Action> actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdmin(Object user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFormInputForFilePath(Object fi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupervisorOf(Object user1, Object user2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isError(Object output) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<Action> actionsUpdatedUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SystemConfig getSysConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetProxy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKeepCache(boolean keep) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keepCache() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action newRequestUrlAction(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> reservedKeywords(Account user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isResetPassword(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RemoteFile remoteFile(Object mrData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Input changeCredentials(smrl.mr.language.Input input, Object user,
			boolean ignoreSameAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public smrl.mr.language.Output getCachedOutput(WebInputCrawlJax input) {
		// TODO Auto-generated method stub
		return null;
	}

}
