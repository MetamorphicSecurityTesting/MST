package smrl.mr.language.actions;

import com.google.gson.JsonArray;

import smrl.mr.crawljax.Account;
import smrl.mr.language.Action;

public class ResetSUTAction extends Action {

	public ResetSUTAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setChannel(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCipherSuite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setEncryption(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUrl(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setMethod(String method) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOldMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setId(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containAccount(Account acc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containCredential(Account acc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Account getCredential(String userParam, String passwordParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action changeCredential(Account acc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChannelChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonArray toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containFormInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonArray getFormInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containFormInputForFilePath() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMethodChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
