package editor;

import communication.EditorConsumer;
import field.EditorField;
/**
 * 
 * @author mbeus001
 *
 */
public class Editor {
	
	EditorInputHandler editorInputHandler;
	
	public Editor(EditorConsumer editorConsumer, EditorField editorField) {
		
		this.editorInputHandler = new EditorInputHandler(editorField, editorConsumer);
		
	}
}
