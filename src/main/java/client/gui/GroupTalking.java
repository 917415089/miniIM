package client.gui;

import java.util.List;

import json.client.ClientJSON;

public class GroupTalking implements ClientJSON {

	private List<String> namelist;

	public List<String> getNamelist() {
		return namelist;
	}

	public void setNamelist(List<String> namelist) {
		this.namelist = namelist;
	}
	
	
}
