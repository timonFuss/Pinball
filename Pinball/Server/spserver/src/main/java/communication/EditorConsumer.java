package communication;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.swing.event.EventListenerList;

import events.EditorConsumerEditorListener;
import events.EditorConsumerLobbyListener;
import events.EditorUserChangedNameEvent;
import events.EditorUserDeletedElementEvent;
import events.EditorUserMovedElementEvent;
import events.EditorUserRedoEvent;
import events.EditorUserSavedEvent;
import events.EditorUserSetElementEvent;
import events.EditorUserUndoEvent;
import events.UserJoinedEvent;
import events.UserLeftEvent;
public class EditorConsumer extends Consumer {
	
	private EventListenerList editorConsumerLobbyListeners;
	private EventListenerList editorConsumerEditorListeners;

	/**
	 * Konstruktor, der die Queue für editorbezogene Nachrichten von Clients
	 * anlegt und diese anzapft.
	 * @param connectionID Hieran wird die Verarbeitung erhaltener Befehle delegiert
	 * @param queueName Name des Nachrichtenkanals, dessen Nachrichten man erhält
	 */
	public EditorConsumer(String connectionID, String queueName) {
		super(connectionID, queueName);
		editorConsumerEditorListeners = new EventListenerList();
		editorConsumerLobbyListeners = new EventListenerList();
	}

	@Override
	public void onMessage(Message message) {
		if (!(message instanceof TextMessage)) {
			throw new AssertionError("Im EditorConsumer wird eine TextMessage erwartet!");
		}
		
		/* Interpretation */
		TextMessage textMessage = (TextMessage) message;
		String command;
		
		try {
			command = textMessage.getText();
		} catch (JMSException e) {
			e.printStackTrace();
			throw new AssertionError("Fehler bei Verarbeitung der Message: "
									 + textMessage.toString());
		}
		
		/* Delegation */
		try {
			switch (command){
				/* Lobby */
				case Command.COMMU_JOINED: {
					notifyPlayerJoinedEditor(new UserJoinedEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				case Command.COMMU_LEFT: {
					notifyPlayerLeftEditor(new UserLeftEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				/* Editor */
				case Command.SETELEMENT: {
					notifyUserSetElement(new EditorUserSetElementEvent(this, textMessage.getFloatProperty(Parameter.POSX), textMessage.getFloatProperty(Parameter.POSY), textMessage.getStringProperty(Parameter.ELEMENTTYPE)));
				} break;
				case Command.MOVEELEMENT: {					
					notifyUserMovedElement(new EditorUserMovedElementEvent(this, textMessage.getIntProperty(Parameter.ID), textMessage.getFloatProperty(Parameter.POSX), textMessage.getFloatProperty(Parameter.POSY)));
				} break;
				case Command.DELETEELEMENT: {					
					notifyUserDeletedElement(new EditorUserDeletedElementEvent(this, textMessage.getIntProperty(Parameter.ID)));
				} break;
				case Command.UNDO: {					
					notifyUserUndo(new EditorUserUndoEvent(this));
				} break;
				case Command.REDO: {					
					notifyUserRedo(new EditorUserRedoEvent(this));
				} break;
				case Command.CHANGENAME: {					
					notifyUserChangedName(new EditorUserChangedNameEvent(this, textMessage.getStringProperty(Parameter.GAMEFIELDNAME)));
				} break;
				case Command.SAVEEFIELD: {					
					notifyUserSavedName(new EditorUserSavedEvent(this));
				} break;
				
				default: throw new AssertionError("Unerwarteter Befehl: " + command);
			}
		} catch (JMSException e) {
			throw new AssertionError("Fehler bei Dekodierung der erhaltenen Message");
		}
	}


	

	/* --- Listenergeraffel LobbyListener --- */
	public void addEditorConsumerLobbyListener(EditorConsumerLobbyListener editorConsumerLobbyListener){
		editorConsumerLobbyListeners.add(EditorConsumerLobbyListener.class, editorConsumerLobbyListener);
	}
	public void removeEditorConsumerLobbyListener(EditorConsumerLobbyListener editorConsumerLobbyListener){
		editorConsumerLobbyListeners.remove(EditorConsumerLobbyListener.class, editorConsumerLobbyListener);
	}
	private void notifyPlayerJoinedEditor(UserJoinedEvent e) {
		for (EditorConsumerLobbyListener ecll : editorConsumerLobbyListeners.getListeners(EditorConsumerLobbyListener.class)){
			ecll.editorUserJoinedFired(e);
		}
	}
	private void notifyPlayerLeftEditor(UserLeftEvent e) {
		for (EditorConsumerLobbyListener ecll : editorConsumerLobbyListeners.getListeners(EditorConsumerLobbyListener.class)){
			ecll.editorUserLeftFired(e);
		}
	}
	
	/* --- Listenergeraffel EditorListener --- */
	
	public void addEditorConsumerEditorListener(EditorConsumerEditorListener editorConsumerEditorListener){
		editorConsumerEditorListeners.add(EditorConsumerEditorListener.class, editorConsumerEditorListener);
	}
	public void removeEditorConsumerEditorListener(EditorConsumerEditorListener editorConsumerEditorListener){
		editorConsumerEditorListeners.remove(EditorConsumerEditorListener.class, editorConsumerEditorListener);
	}
	
	private void notifyUserSetElement(EditorUserSetElementEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userSetElement(e);
		}
	}
	
	private void notifyUserMovedElement(EditorUserMovedElementEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userMovedElement(e);
		}
	}
	
	private void notifyUserDeletedElement(EditorUserDeletedElementEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userDeletedElement(e);
		}
	}
	
	private void notifyUserUndo(EditorUserUndoEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userUndo(e);
		}
	}
	
	private void notifyUserRedo(EditorUserRedoEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userRedo(e);
		}
	}
	
	private void notifyUserChangedName(EditorUserChangedNameEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userChangedName(e);
		}
	}
	
	private void notifyUserSavedName(EditorUserSavedEvent e) {
		for (EditorConsumerEditorListener ecll : editorConsumerEditorListeners.getListeners(EditorConsumerEditorListener.class)){
			ecll.userSaved(e);
		}
	}
	
}
