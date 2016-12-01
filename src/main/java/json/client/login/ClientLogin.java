package json.client.login;

import json.client.ClientJSON;

public class ClientLogin implements ClientJSON {

	private String Name;
	private String Password;
	private int Random;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public int getRandom() {
		return Random;
	}
	public void setRandom(int random) {
		Random = random;
	}
	
}
