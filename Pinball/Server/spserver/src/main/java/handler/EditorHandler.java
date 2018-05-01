package handler;

import app.ServerLog;
import communication.ConnectionManager;
import communication.EditorConsumer;
import communication.EditorProducer;
import editor.Editor;
import editor.EditorFieldEncoder;
import events.EditorConsumerLobbyListener;
import events.UserJoinedEvent;
import events.UserLeftEvent;
import field.EditorField;
import lobby.ClientState;
import lobby.MainModule;

/**
 * Dient zur Verwaltung eines Editierraums samt zugehoeriger Variablen.
 * @author dalle001
 *
 */
public class EditorHandler extends Handler {

	// Elementare verwaltete Editorklassenobjekte und -variablen
	private EditorField editorField;
	private EditorFieldEncoder editorFieldEncoder;
	private String templateFieldName;
	
	// Kommunikation
	private EditorConsumer editorConsumer;
	private EditorProducer editorProducer;
	
	
	/**
	 * Erzeugung eines Objektes, welches eine Editorrauminstanz und damit verbundene
	 * Variablen und Kommunikationswege verwaltet.
	 * @param mmID Eindeutige Identifikationsvariable des Editorraums
	 * @param fieldName Spielfeld, das als Vorlage für den Editor dient
	 * @param maxPlayers Obergrenze an Spielern, die im Editor basteln koennen
	 * @param roomName Fuer Nutzer zur Wiedererkennung sichtbarer Spielraumname
	 */
	public EditorHandler(int mmID, String fieldName, MainModule mm, String roomName) {
		super(mm, mmID, 10, "editor_", roomName, fieldName);
		this.templateFieldName = fieldName;

		/* Kommunikationswege und Komponenten die diese nutzen erstellen */
		ConnectionManager.getInstance().createConnection(CONNECTIONID);
		this.editorProducer = new EditorProducer(CONNECTIONID, TOPICNAME);
		this.editorConsumer = new EditorConsumer(CONNECTIONID, QUEUENAME);
		ConnectionManager.getInstance().startConnection(CONNECTIONID);
		ServerLog.logMessage("[" + CONNECTIONID +"] Editierraum unter dem Namen \"" + roomName +
							 "\" mit Spielfeldvorlage \"" + fieldName + "\" eröffnet");

		/* eigentlicher Editor */
		this.editorField = new EditorField(editorProducer, fieldName);
		this.editorFieldEncoder = new EditorFieldEncoder(editorField, editorProducer);
		new Editor(editorConsumer, editorField);
		
		/* Innere NotifiyChangeListener-Eventklasse */
		class EditorHandlerEditorConsumerLobbyListener implements EditorConsumerLobbyListener {
			@Override
			public void editorUserJoinedFired(UserJoinedEvent ev) {
				// Vermerken, dass Nutzer nun in der Lobby dieses Spiels ist
				User joiningUser = users.get(ev.getUserID());
				joiningUser.setState(ClientState.EDITOR);
				ServerLog.logMessage("[" + CONNECTIONID +"] " + joiningUser.getUsername() +
									 " ist Editor-Kommunikation beigetreten");

				// Melden an Mitspieler
				editorProducer.sendEditorUserList(getUserList());
				
				// Melden an Clients die die Spielraumliste sehen
				mainModule.resendOpenEditorsForAll();
				
				//an alle User das EditorLoadPacket verschicken
				//Clients die es nicht brauchen muessen es verwerfen
				editorFieldEncoder.sendEditorLoadPacket();
			}
			@Override
			public void editorUserLeftFired(UserLeftEvent ev) {
				// Vermerken, dass Nutzer nun wieder im Menü ist
				User leavingUser = users.get(ev.getUserID());
				leavingUser.setState(ClientState.EDITORROOMLOBBY);
				ServerLog.logMessage("[" + CONNECTIONID +"] " + leavingUser.getUsername() +
									 " hat Editor-Kommunikationsweg verlassen");

				// Nutzer aus Spielerliste dieses Spiels entfernen
				removeUser(leavingUser);

				// Melden an Mitspieler
				editorProducer.sendEditorUserList(getUserList());
				
				// Melden an Clients die die Spielraumliste sehen
				mainModule.resendOpenEditorsForAll();
			}
		}
		editorConsumer.addEditorConsumerLobbyListener
						(new EditorHandlerEditorConsumerLobbyListener());
	}

	/* (non-Javadoc)
	 * @see lobby.Handler#deleteSelf()
	 */
	protected void deleteSelf() {
		ServerLog.logMessage("[" + CONNECTIONID +"] Editierraum \"" +
							 roomName + "\" entfernt sich");
		mainModule.removeEditorHandler(mainModuleID, getID());
	}

	
	/* ---- Ablauf ---- */
	
	/* (non-Javadoc)
	 * @see lobby.Handler#addUser(lobby.User)
	 */
	public boolean addUser(User newUser) {
		// nur wenn noch Platz
		if (getOpenSeats() > 0) {
			// nur wenn noch nicht bereits vorhanden
			if (!users.containsKey(newUser.getDestinationID())) {
				users.put(newUser.getDestinationID(), newUser);
				ServerLog.logMessage("[" + CONNECTIONID + "] " +
						newUser.getUsername() + " ist Editierraum beigetreten");
				newUser.setEditorHandler(this);
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see lobby.Handler#removeUser(lobby.User)
	 */
	public boolean removeUser(User oldUser) {
		// nur wenn User vorhanden
		if (users.containsKey(oldUser.getDestinationID())) {
			users.remove(oldUser.getDestinationID());
			ServerLog.logMessage("[" + CONNECTIONID + "] " +
					oldUser.getUsername() + " hat Editierraum verlassen");
			oldUser.setEditorHandler(null);
			
			// Wenn niemand mehr da: Schluss mit Editierraum
			if (users.size() <= 0){
				deleteSelf();
			}
			return true;
		}
		return false;
	}

	
	/* ---- Getter ---- */
	
	public String getTemplateFieldName() {
		return templateFieldName;
	}
	
	
	/* ---- Unit-Test-Getter ---- */
	
	/**
	 * Unit-Test-Getter.
	 */
	public EditorConsumer getEditorConsumer() {
		return editorConsumer;
	}
}
