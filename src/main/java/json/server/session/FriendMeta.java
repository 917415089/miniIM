package json.server.session;

import json.server.ServerJSON;

public class FriendMeta implements ServerJSON{

	private String name;
	private String group;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	
}
