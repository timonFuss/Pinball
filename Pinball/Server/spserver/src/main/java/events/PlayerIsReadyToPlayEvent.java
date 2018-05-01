package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PlayerIsReadyToPlayEvent extends EventObject {

	private String playerID;
	
	public PlayerIsReadyToPlayEvent(Object source, String playerID) {
		super(source);
		this.playerID = playerID;
	}

	public String getPlayerID() {
		return playerID;
	}
}
