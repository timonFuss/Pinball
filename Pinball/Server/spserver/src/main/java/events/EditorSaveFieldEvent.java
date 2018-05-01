package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class EditorSaveFieldEvent extends EventObject {

	public EditorSaveFieldEvent(Object source) {
		super(source);
	}
}
