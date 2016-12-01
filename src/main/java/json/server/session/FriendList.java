package json.server.session;

import java.util.List;

import json.server.ServerJSON;

public class FriendList implements ServerJSON{

	private List<FriendMeta> friends;

	public List<FriendMeta> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendMeta> friends) {
		this.friends = friends;
	}
	
	
}
