package command;

import enums.ElementType;
import field.EditorField;

public class DeleteElementCommand implements Command{
	
	private EditorField editorField;
	private int id;
	private float posX, posY;
	private ElementType elementType;

	public DeleteElementCommand(EditorField editorField, int id) {
		this.editorField = editorField;
		this.id = id;
	}
	
	@Override
	public boolean execute() {
		posX = editorField.getGameElement(id).getPosition().x;
		posY = editorField.getGameElement(id).getPosition().y;
		elementType = editorField.getGameElement(id).getElementType();
		return editorField.deleteElement(id);
	}

	@Override
	public boolean undo() {
		// Element aus EditorField entfernen
		id = editorField.restoreElement(id, posX, posY, elementType);
		
		if (id<0) return false;		
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ", " + ", " + posX + "/" + posY + " " + elementType  + ")";
	}
}
