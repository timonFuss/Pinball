package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class UserJoinedEvent extends EventObject {

	private String userID;
	
	public UserJoinedEvent(Object source, String userID) {
		super(source);
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}
}
