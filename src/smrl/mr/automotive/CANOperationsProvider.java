package smrl.mr.automotive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import smrl.mr.crawljax.Account;
import smrl.mr.crawljax.WebInputCrawlJax;
import smrl.mr.language.Action;
import smrl.mr.language.OperationsProvider;
import smrl.mr.language.SystemConfig;
import smrl.mr.utils.RemoteFile;

public class CANOperationsProvider implements OperationsProvider {

	private File dataFolder;

	public CANOperationsProvider(File dataFolder) {
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
		List<String> content = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			
			String line = br.readLine();
			
			while (line != null) {
				if(!line.trim().isEmpty()){
					String path = line.trim();

					content.add(path);
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

		
		
		return content;
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
