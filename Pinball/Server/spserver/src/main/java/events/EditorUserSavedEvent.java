package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserSavedEvent extends EventObject {

	public EditorUserSavedEvent(Object source) {
		super(source);
	}

}
