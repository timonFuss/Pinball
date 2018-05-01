package command;

import enums.ElementType;
import field.EditorField;

public class SetElementCommand implements Command {

	private EditorField editorField;
	private float posX, posY;
	private ElementType elementType;
	private int id = -1;
	
	public SetElementCommand(EditorField editorField, float posX, float posY, ElementType elementType) {
		this.editorField = editorField;
		this.posX = posX;
		this.posY = posY;
		this.elementType = elementType;
	}
	
	@Override
	public boolean execute() {
		// Element in EditorField platzieren
		// bei redo soll ein zuvor schon erstelltes Element mit der selben ID angelegt werden, nicht mit einer neuen
		if (id==-1){ // ist nur -1, solange noch keine setElement-Methode aufgerufen wurde, es also auch nichts zu restoren gibt
			id = editorField.setElement(posX, posY, elementType);
		} else {
			editorField.restoreElement(id, posX, posY, elementType);
		}
		
		if (id<0) return false;
		return true;
	}

	@Override
	public boolean undo() {
		// Element aus EditorField entfernen
		return editorField.deleteElement(id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ", " + ", " + posX + "/" + posY + " " + elementType  + ")";
	}
	
}
