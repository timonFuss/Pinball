package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserUndoEvent extends EventObject {

	public EditorUserUndoEvent(Object source) {
		super(source);
	}
}
