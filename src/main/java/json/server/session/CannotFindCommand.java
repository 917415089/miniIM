package json.server.session;

import json.server.ServerJSON;

public class CannotFindCommand implements ServerJSON{

	private String WrongCommand;

	public String getWrongCommand() {
		return WrongCommand;
	}

	public void setWrongCommand(String wrongCommand) {
		WrongCommand = wrongCommand;
	}

}
