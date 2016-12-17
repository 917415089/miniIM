package json.client.session;

import java.util.List;

public class SendGroupMessage {

	private List<String> friendlist;
	private String message;
	private String name;
	public List<String> getFriendlist() {
		return friendlist;
	}
	public void setFriendlist(List<String> friendlist) {
		this.friendlist = friendlist;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
