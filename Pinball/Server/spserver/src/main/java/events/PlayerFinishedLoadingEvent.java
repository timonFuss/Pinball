package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PlayerFinishedLoadingEvent extends EventObject {

	private String playerID;
	
	public PlayerFinishedLoadingEvent(Object source, String playerID) {
		super(source);
		this.playerID = playerID;
	}

	public String getPlayerID() {
		return playerID;
	}
}
