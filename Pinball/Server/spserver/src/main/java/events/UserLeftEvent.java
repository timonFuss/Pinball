package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class UserLeftEvent extends EventObject {

	private String userID;
	
	public UserLeftEvent(Object source, String userID) {
		super(source);
		this.userID = userID;
	}
	
	public String getUserID() {
		return userID;
	}

}
