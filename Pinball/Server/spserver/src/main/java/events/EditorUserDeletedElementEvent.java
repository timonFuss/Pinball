package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserDeletedElementEvent extends EventObject {

	private int id;
	
	public EditorUserDeletedElementEvent(Object source, int id) {
		super(source);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
