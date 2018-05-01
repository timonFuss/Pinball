package events;

import java.util.EventListener;

public interface EditorConsumerEditorListener extends EventListener{
	void userSetElement(EditorUserSetElementEvent e);

	void userMovedElement(EditorUserMovedElementEvent e);

	void userDeletedElement(EditorUserDeletedElementEvent e);

	void userUndo(EditorUserUndoEvent e);

	void userRedo(EditorUserRedoEvent e);

	void userChangedName(EditorUserChangedNameEvent e);

	void userSaved(EditorUserSavedEvent e);
}
