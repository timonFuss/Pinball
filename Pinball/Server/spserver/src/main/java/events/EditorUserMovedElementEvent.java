package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserMovedElementEvent extends EventObject {
	
	private int id;
	private float posX;
	private float posY;
	
	
	public EditorUserMovedElementEvent(Object source, int id, float posX, float posY) {
		super(source);
		this.id = id;
		this.posX = posX;
		this.posY = posY;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public int getId() {
		return id;
	}
}
