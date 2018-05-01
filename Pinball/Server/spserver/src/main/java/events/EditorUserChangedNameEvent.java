package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserChangedNameEvent extends EventObject {

	private String newName;
	
	public EditorUserChangedNameEvent(Object source, String newName) {
		super(source);
		this.newName = newName;
	}

	public String getNewName() {
		return newName;
	}
}
