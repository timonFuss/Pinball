package editor;

import command.DeleteElementCommand;
import command.MoveElementCommand;
import command.SetElementCommand;
import communication.EditorConsumer;
import enums.ElementType;
import events.EditorConsumerEditorListener;
import events.EditorUserChangedNameEvent;
import events.EditorUserDeletedElementEvent;
import events.EditorUserMovedElementEvent;
import events.EditorUserRedoEvent;
import events.EditorUserSavedEvent;
import events.EditorUserSetElementEvent;
import events.EditorUserUndoEvent;
import field.EditorField;

public class EditorInputHandler {
	
	private CommandManager commandManager;

	public EditorInputHandler(EditorField editorField, EditorConsumer editorConsumer) {
		
		class EditorInputHandlerEditorConsumerEditorListener implements EditorConsumerEditorListener{

			@Override
			public void userSetElement(EditorUserSetElementEvent e) {
				commandManager.execute(new SetElementCommand(editorField, e.getPosX(), e.getPosY(), ElementType.convert(e.getElementType())));
			}

			@Override
			public void userMovedElement(EditorUserMovedElementEvent e) {
				commandManager.execute(new MoveElementCommand(editorField, e.getId(), e.getPosX(), e.getPosY()));				
			}

			@Override
			public void userDeletedElement(EditorUserDeletedElementEvent e) {
				commandManager.execute(new DeleteElementCommand(editorField, e.getId()));
			}
			
			@Override
			public void userChangedName(EditorUserChangedNameEvent e) {
				editorField.changeName(e.getNewName());
			}
			
			@Override
			public void userUndo(EditorUserUndoEvent e) {
				commandManager.undo();				
			}

			@Override
			public void userRedo(EditorUserRedoEvent e) {
				commandManager.redo();				
			}

			@Override
			public void userSaved(EditorUserSavedEvent e) {
				editorField.saveFile();
			}
		}
		editorConsumer.addEditorConsumerEditorListener(new EditorInputHandlerEditorConsumerEditorListener());
		commandManager = new CommandManager();
	}
}
