package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class AddForceEvent extends EventObject {

	private float posX;
	private float posY;
	
	public AddForceEvent(Object source, float posX, float posY) {
		super(source);
		this.posX = posX;
		this.posY = posY;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

}
