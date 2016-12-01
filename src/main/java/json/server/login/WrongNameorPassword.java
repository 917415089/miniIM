package json.server.login;

import json.server.ServerJSON;

public class WrongNameorPassword implements ServerJSON{

	private boolean verifyName;
	private boolean verfyPassword;
	
	public boolean isVerifyName() {
		return verifyName;
	}
	public void setVerifyName(boolean verifyName) {
		this.verifyName = verifyName;
	}
	public boolean isVerfyPassword() {
		return verfyPassword;
	}
	public void setVerfyPassword(boolean verfyPassword) {
		this.verfyPassword = verfyPassword;
	}
	
	
}
