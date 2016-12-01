package json.client.session;

import java.util.List;

import json.client.ClientJSON;

public class RemoveFriend implements ClientJSON {

	private List<String> friends;

	public List<String> getFriends() {
		return friends;
	}

	public void setFriends(List<String> friends) {
		this.friends = friends;
	}
	
	
}
