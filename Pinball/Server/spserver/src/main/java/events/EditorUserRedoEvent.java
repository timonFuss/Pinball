package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorUserRedoEvent extends EventObject {

	public EditorUserRedoEvent(Object source) {
		super(source);
	}

}
