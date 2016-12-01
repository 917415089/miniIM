package json.client.session;

import json.client.ClientJSON;

public class AddFriendResult implements ClientJSON {

	private String requestorname;
	private String requestorgroup;
	private boolean receiverestate;
	private String receivername;
	private String receivergroup;
	public String getRequestorname() {
		return requestorname;
	}
	public void setRequestorname(String requestorname) {
		this.requestorname = requestorname;
	}
	public String getRequestorgroup() {
		return requestorgroup;
	}
	public void setRequestorgroup(String requestorgroup) {
		this.requestorgroup = requestorgroup;
	}
	public boolean isReceiverestate() {
		return receiverestate;
	}
	public void setReceiverestate(boolean receiverestate) {
		this.receiverestate = receiverestate;
	}
	public String getReceivername() {
		return receivername;
	}
	public void setReceivername(String receivername) {
		this.receivername = receivername;
	}
	public String getReceivergroup() {
		return receivergroup;
	}
	public void setReceivergroup(String receivergroup) {
		this.receivergroup = receivergroup;
	}
	
	
}
