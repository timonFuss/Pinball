package editor;

import java.util.ArrayList;
import java.util.List;

import command.Command;

public class CommandManager {
	//private EditorField editorField;
	private List<Command> commandList;
	private int index = 0;
	
	public CommandManager(/*EditorField editorField*/){
		//this.editorField = editorField;
		commandList = new ArrayList<Command>();
	}
	
	public void execute(Command command){
		boolean success = command.execute();
		if (success){
			// es soll an der aktuellen index-Position eingefuegt werden, nicht am Ende
			// also verkleinere die Liste wenn die Listengroesse groesser ist als der Index
			if (index < commandList.size()) {
				commandList = commandList.subList(0, index);
			}
			commandList.add(command);
			index++;
			
		}
			
	}
	
	public void undo(){
		if (index > 0)
			commandList.get(--index).undo();
	}
	
	public void redo(){
		if (index >= commandList.size()){
			
			return;
		}
		
		if (commandList.get(index).execute()){
			index++;
		}
	}
}
