package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserSetElementEvent extends EventObject {
	
	private float posX;
	private float posY;
	private String elementType;
	
	public EditorUserSetElementEvent(Object source, float posX, float posY, String elementType) {
		super(source);
		this.posX = posX;
		this.posY = posY;
		this.elementType = elementType;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public String getElementType() {
		return elementType;
	}
}
