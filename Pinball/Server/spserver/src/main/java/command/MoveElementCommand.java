package command;

import field.EditorField;

public class MoveElementCommand implements Command {
	
	private EditorField editorField;
	private int id;
	private float moveX, moveY;
	
	public MoveElementCommand(EditorField editorField, int id, float moveX, float moveY) {
		this.editorField = editorField;
		this.id = id;
		this.moveX = moveX;
		this.moveY = moveY;
	}
	
	@Override
	public boolean execute() {
		// Element in EditorField platzieren
		return editorField.moveElement(id, moveX, moveY);
	}

	@Override
	public boolean undo() {
		// Element aus EditorField entfernen
		return editorField.moveElement(id, -moveX, -moveY);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ", " + ", " + moveX + "/" + moveY + " " + ")";
	}
}
