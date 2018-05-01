package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ButtonRightEvent extends EventObject {
	
	boolean keyAction;
	private String player;
	
	public ButtonRightEvent(Object source,  boolean keyAction, String player) {
		super(source);
		this.keyAction = keyAction; 
		this.player = player;
	}

	
	public boolean getKeyAction() {
		return keyAction;
	}
	
	public String getPlayer() {
		return player;
	}
	
}
