package lobby;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;

import app.ServerLog;
import communication.ConnectionManager;
import communication.MainConsumer;
import communication.Parameter;
import field.Field;
import handler.EditorHandler;
import handler.GameHandler;

/**
 * Programmmodul, welches allgemeine Angelegenheiten auf Serverseite behandelt
 * (ausgenommen Spiel- und Editor-Ereignisse).
 * 
 * @author mbeus001
 *
 */
public class MainModule {

	private final String MAIN_TOSERVER = "mainToServer";  // Name der Queue zum Server
	private final String MAIN_CONNECTION_ID = "mainConnection";  // id der Connection, die fürs allgmeine genutzt wird
	
	// Verwaltung aller anwesenden Clients
	private Map<String, ClientHandler> onlineClients = new HashMap<String, ClientHandler>();
	private MainConsumer mainConsumer;
	
	// Verwaltung aller laufenden Spiele
	private Map<Integer, GameHandler> gameHandlers = new HashMap<Integer, GameHandler>();
	private int gameID = 0;

	// Verwaltung aller laufenden Editiersitzungen
	private Map<Integer, EditorHandler> editorHandlers = new HashMap<Integer, EditorHandler>();
	private int editorID = 0;
	
	public MainModule(){
		ConnectionManager.getInstance().createConnection(MAIN_CONNECTION_ID);
		mainConsumer = new MainConsumer(MAIN_CONNECTION_ID, MAIN_TOSERVER, this);
		ConnectionManager.getInstance().startConnection(MAIN_CONNECTION_ID);
	}
	

	/**
	 * Verarbeitet einen Login-Wunsch, schickt eine Rueckmeldung und
	 * benachrichtigt andere Teilnehmer bei und über erfolgreichen Login.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param destinationID Identifikation des Nutzers (nicht username)
	 * @param username Gewuenschter Nutzername des Login-Anfragestellers
	 */
	public void login(Destination destinationChannel, String destinationID, String username){
		boolean nameUsed = false;

		// über Map iterieren und suchen ob es den Namen schon gibt
		for (ClientHandler value : onlineClients.values()) {
		    if(value.getUsername().equals(username)){
		    	nameUsed = true;
		    	break;
		    }
		}
		
		if (nameUsed) {
			/* User mit username bereits angemeldet */
			
			// Vermerken der Antwortadresse durch Konstruktor ohne username
			// ( -> dieser Client ist aus Konvention hier so kein valider user)
			ClientHandler newCH = new ClientHandler(MAIN_CONNECTION_ID, destinationChannel);
			// Garbage-Collector sollte eben erstelltes Objekt bald einsammeln
			
			ServerLog.logMessage(username + " ist schon vergeben, Login verweigert");
			
			// Antwort mit Ablehnung an Anfrager schicken
			newCH.getResponseProducer().sendLoginAnswer(false);			
			return;
		}
		
		
		// Vermerken des neuen Nutzers
		ClientHandler newCH = new ClientHandler(MAIN_CONNECTION_ID, destinationChannel, username, destinationID);
		onlineClients.put(destinationID, newCH);

		ServerLog.logMessage( username + " ist jetzt online");
		
		/* Benachrichtigung an alle Nutzer ueber den neuen Nutzer */
		// Iteriere über alle keys in der Map der Nutzer
		for (String key : onlineClients.keySet()) {
			
			// Wenn key gleich der übergebenen Destination (Antwortzieladresse) ist:
		    if(key.equals(destinationID)){
		    	// Benutzer mit Login-Wunsch erhält seine Bestätigung
		    	onlineClients.get(key).getResponseProducer().sendLoginAnswer(true);		    	
		    
		    } else {
		    	// Jeder andere Benutzer wird über den Neuling informiert
		    	onlineClients.get(key).getResponseProducer().sendUserLogin(username);
		    }
		}
	}
	
	/**
	 * Verarbeitet einen Anwendungsende-Wunsch und
	 * benachrichtigt andere Teilnehmer über das Verlassen des Nutzers.
	 * @param destinationChannel Adresse der Queue des abzumeldenden Nutzers
	 */
	public void logout(String destinationID){
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			ServerLog.logError("Auszuloggender Nutzer ist nicht angemeldet!");
			return;
		}
		ClientHandler leavingUser = onlineClients.get(destinationID);
		
		// Hole den passenden username vom auszuloggenden Nutzer
		String logoutUsername = leavingUser.getUsername();

		// Nutzer auf Serverseite entfernen
		onlineClients.remove(destinationID);
		
		// wenn er in einer Spiel-Lobby oder Editor-Lobby ist, ihn von dort entfernen
		GameHandler logoutUserGameHandler = leavingUser.getGameHandler();
		EditorHandler logoutUserEditorHandler = leavingUser.getEditorHandler();
		
		if (logoutUserGameHandler != null)
			logoutUserGameHandler.removeUser(leavingUser);
		
		if (logoutUserEditorHandler != null)
			logoutUserEditorHandler.removeUser(leavingUser);
		
		ServerLog.logMessage(logoutUsername + " ist jetzt offline");
		
		// Alle übrigen Nutzer über den Verlust benachrichtigen
		for (String key : onlineClients.keySet()) {				
		    onlineClients.get(key).getResponseProducer().sendUserLogout(logoutUsername);
		}
	}

	/**
	 * Verarbeitet eine Anforderung einer Liste aller anwesenden Nutzer
	 * und verschickt diese an den Anfrager.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param destinationID Identifikation des Nutzers (nicht username) 
	 */
	public void sendUserlist(Destination destinationChannel, String destinationID) {
		/* ClientHandler holen */
		ClientHandler clientHandler;
		
		if (!onlineClients.containsKey(destinationID)){
			// Wenn anonymer Nutzer dann über temporären Producer
			clientHandler = new ClientHandler(MAIN_CONNECTION_ID, destinationChannel);
			// Garbage-Collector sollte eben erstelltes Objekt bald einsammeln
			
			// Log-Ausgabe
			ServerLog.logMessage("User-Liste angefordert von einem anonymen Nutzer: " + destinationID);
		} else {
			clientHandler = onlineClients.get(destinationID);

			// Log-Ausgabe
			String askingUsername = onlineClients.get(destinationID).getUsername();
			ServerLog.logMessage("User-Liste angefordert von " + askingUsername);
		}

		/* Stringliste erstellen */
		String encodedUsernameList = "";
		
		// Alle Usernamen (außer den des Anfragers) einer encodierten Stringliste hinzufügen
		for (String key : onlineClients.keySet()) {
		    if(!key.equals(destinationID)){
		    	encodedUsernameList += onlineClients.get(key).getUsername()
		    			+ Parameter.LISTELEMENTSEPARATOR;
		    }
		}

		/* Antwort */
		clientHandler.getResponseProducer().sendOnlineList(encodedUsernameList);
	}

	/**
	 * Verarbeitet eine Anforderung einer Liste aller nutzbaren Spielfelder
	 * und verschickt diese an den Anfrager.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param destinationID Identifikation des Nutzers (nicht username) 
	 */
	public void sendFieldList(Destination destinationChannel, String destinationID) {
		/* Ablehn-Bedingungen */
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht
			ServerLog.logError("Unautorisierter Nutzer wollte Editorraum erstellen!");
			return;
		}
		
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// Log-Ausgabe
		ServerLog.logMessage("Liste verfügbarer Spielfelder angefordert von " +
								clientHandler.getUsername());
		
		
		/* Stringliste erstellen */
		String encodedFieldList = "";
		
		for (String fieldName : Field.getAvailableFields()) {
			encodedFieldList += fieldName + Parameter.LISTELEMENTSEPARATOR;
		}
		
		/* Antwort */
		clientHandler.getResponseProducer().sendFieldList(encodedFieldList);
	}
	
	
	/* ---- Editor ---- */
	
	/**
	 * Erstellt einen neuen Editor samt zugehöriger Komponenten auf dem Server
	 * @param fieldName Spielfeld, welches als Vorlage dienen soll
	 * @param name Name des zu erstellenden Spielraums
	 * @return erstelltes Spielverwaltungs-Objekt; null wenn Erzeugung fehlgeschlagen
	 */
	private EditorHandler createNewEditor(String fieldName, String name) {
		/* Ablehn-Bedingungen */
		if (!Field.getAvailableFields().contains(fieldName)) {
			// Field mit gegebener ID existiert nicht
			return null;
		}
		
		/* Editor erstellen */
		// Neuen Editor (via EditorHandler) erzeugen
		EditorHandler newEditorHandler = new EditorHandler(editorID, fieldName, this, name);
		
		// EditorHandler hinterlegen
		editorHandlers.put(editorID, newEditorHandler);
		editorID++;
		
		return newEditorHandler;
	}
	
	/**
	 * Verarbeitet einen von außen kommenden Wunsch einer Editorerstellung
	 * @param destinationID Adresse, an die der Server antworten soll
	 * @param fieldName Name des Spielfelds, welches als Vorlage dienen soll
	 * @param name Name des zu erstellenden Spielraums
	 */
	public void userCreatesNewEditor(String destinationID, String fieldName, String name) {
		/* Ablehn-Bedingungen */
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht
			ServerLog.logError("Unautorisierter Nutzer wollte Editorraum erstellen!");
			return;
		}
		
		/* Spiel erstellen */
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		EditorHandler newEditor = createNewEditor(fieldName, name);
		if (newEditor == null) {
			// Editorerstellung hat nicht geklappt
			clientHandler.getResponseProducer().sendEditorJoinDenial();
			return;
		}
		ServerLog.logMessage(clientHandler.getUsername() +
							 " hat den Editierraum " + name + " eröffnet.");

		/* Spielerstellung erfolgreich */
		directUserToEditor(destinationID, newEditor.getID());		
	}
	
	/**
	 * Versucht, einen Nutzer zu einem Editor zuzuordnen und meldet zurück,
	 * ob dies geklappt hat oder nicht.
	 * 
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param desiredRoom Editor, zu dem der anfragende Nutzer hinzugefügt werden möchte
	 */
	public void directUserToEditor(String destinationID, String desiredRoom) {
		/* Ablehn-Bedingungen */
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			ServerLog.logError("Einem Editor beitretender Nutzer ist nicht angemeldet!");
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// existiert der gewünschte desiredRoom überhaupt?
		EditorHandler editorHandler = null;
		for (EditorHandler eh : editorHandlers.values()) {
			if (eh.getID().equals(desiredRoom)) {
				editorHandler = eh;
				break;
			}
		}
		if (editorHandler == null) {
			clientHandler.getResponseProducer().sendEditorJoinDenial();
			return;
		}
		
		// ist der desiredRoom zugänglich (noch Platz, noch offen)?
		if (!editorHandler.areSeatsAvailable()) {
			clientHandler.getResponseProducer().sendEditorJoinDenial();
		}

		
		/* Auf Serverseite den Spieler zum Editor hinzufügen */
		if (!editorHandler.addUser(clientHandler)) {
			// wenn dennoch fehlgeschlagen (z.B. Nutzer bereits im Editor) dies mitteilen
			clientHandler.getResponseProducer().sendEditorJoinDenial();
		}
		
		ServerLog.logMessage(clientHandler.getUsername() + " ist Editor " +
							 editorHandler.getID() + " (" +
							 editorHandler.getRoomName() + ") beigetreten");
		/* Antwort über Producer aus ClientHandler */
		clientHandler.getResponseProducer()
			.sendEditorJoinConfirmation(editorHandler.getSendingAddress(),
										editorHandler.getReceivingAddress(),
										editorHandler.getRoomName(),
										editorHandler.getTemplateFieldName());
	}
	
	/**
	 * Verarbeitet eine Anforderung einer Liste aller offenen Editorsitzungen
	 * und verschickt diese an den Anfrager.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 */
	public void sendOpenEditors(String destinationID) {
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// Log-Ausgabe
		ServerLog.logMessage("Liste offener Editierräume angefordert von " +
								clientHandler.getUsername());

		// alle existierenden Editoren durchgehen
		Map<String, Integer> editors = new HashMap<String, Integer>();
		for (EditorHandler editorHandler : editorHandlers.values()) {
			// Zwischenmap befüllen
			editors.put(editorHandler.getID() + Parameter.LISTELEMENTSEPARATOR +
						editorHandler.getRoomName(), editorHandler.getNumberOfUsers());
		}

		// Antwort (Map für Nachricht als Parameter)
		clientHandler.getResponseProducer().sendOpenEditorsList(editors);
		
		// Zustand des Nutzers im ClientHandler vermerken: Nun in Spielübersicht
		clientHandler.setState(ClientState.EDITORROOMLOBBY);
	}
	
	/**
	 * Sendet eine Liste aller offenen Editiersitzungen an alle,
	 * die sich die Editorraumliste ansehen.
	 */
	public void resendOpenEditorsForAll() {
		for (ClientHandler c : onlineClients.values()) {
			if (c.getState() == ClientState.EDITORROOMLOBBY) {
				sendOpenEditors(c.getDestinationID());
			}
		}
	}
	
	/**
	 * Informiert ueber den nicht mehr moeglichen Zugriff auf einen Editierraum.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param editorHandlerID ID des betreffenden EditorHandlers
	 */
	private void sendClosingEditor(String destinationID, String editorHandlerID) {
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// Mitteilung
		clientHandler.getResponseProducer().sendClosingEditor(editorHandlerID);
	}
	
	/**
	 * Informiert alle ueber den nicht mehr moeglichen Zugriff auf einen Spielraum.
	 * @param gameHandlerID ID des betreffenden GameHandlers
	 */
	public void sendClosingEditorForAll(String editorID) {
		// Log-Ausgabe
		ServerLog.logMessage("[" + editorID +"] "
					+ "Editorschliessinfo an Clients in Editor-Bereichen");

		for (ClientHandler c : onlineClients.values()) {
			if (c.getState() == ClientState.EDITORROOMLOBBY ||
				c.getState() == ClientState.EDITOR) {
				
				sendClosingEditor(c.getDestinationID(), editorID);
			}
		}
	}
	
	/**
	 * Entfernt ein Editorverwaltungsobjekt aus der Hauptverwaltung des MainModules.
	 * @param id Identifikation des Editorverwaltungsobjekts
	 * @return
	 */
	public void removeEditorHandler(int id, String editorID) {
		sendClosingEditorForAll(editorID);
		editorHandlers.remove(id);
	}
	
	
	/* ---- Spiel ---- */
	
	/**
	 * Erstellt ein neues Spiel samt zugehöriger Komponenten auf dem Server
	 * @param gameFieldName ID des Spielfelds, welches als Vorlage dienen soll
	 * @param name Name des zu erstellenden Spielraums
	 * @param time Spielzeit
	 * @return erstelltes Spielverwaltungs-Objekt; null wenn Erzeugung fehlgeschlagen
	 */
	private GameHandler createNewGame(String gameFieldName, String name, int time) {
		/* Ablehn-Bedingungen */
		if (!Field.getAvailableFields().contains(gameFieldName)) {
			// Field mit gegebener ID existiert nicht
			ServerLog.logError("Es sollte ein Spielraum mit unbekanntem Spielfeld eröffnet werden.");
			return null;
		}
		
		/* Spiel erstellen */
		// Neues Game (via GameHandler) erzeugen
		GameHandler newGameHandler = new GameHandler(gameID, gameFieldName, this, name, time);
		
		// GameHandler hinterlegen
		gameHandlers.put(gameID, newGameHandler);
		gameID++;
		
		return newGameHandler;
	}
	
	/**
	 * Verarbeitet einen von außen kommenden Wunsch einer Spielerstellung
	 * @param destination Adresse, an die der Server antworten soll
	 * @param gameFieldName Name des Spielfelds, welches als Vorlage dienen soll
	 * @param name Name des zu erstellenden Spielraums
	 * @param time Spielzeit
	 */
	public void userCreatesNewGame(String destinationID, String gameFieldName, String name, int time) {
		/* Ablehn-Bedingungen */
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht
			ServerLog.logMessage("Unautorisierter Nutzer wollte Spielraum erstellen!");
			return;
		}
		
		/* Spiel erstellen */
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		GameHandler newGame = createNewGame(gameFieldName, name, time);
		if (newGame == null) {
			// Spielerstellung hat nicht geklappt
			clientHandler.getResponseProducer().sendGameJoinDenial();
			return;
		}
		ServerLog.logMessage(clientHandler.getUsername() + " hat den Spielraum " + name + " eröffnet.");

		/* Spielerstellung erfolgreich */
		directUserToGame(destinationID, newGame.getID());		
	}
	
	/**
	 * Versucht, einen Nutzer zu einem Spiel zuzuordnen und meldet zurück,
	 * ob dies geklappt hat oder nicht.
	 * 
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param desiredRoom Spiel, zu dem der anfragende Nutzer hinzugefügt werden möchte
	 */
	public void directUserToGame(String destinationID, String desiredRoom) {
		/* Ablehn-Bedingungen */
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			ServerLog.logError("Einem Spiel beitretender Nutzer ist nicht angemeldet!");
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// existiert der gewünschte desiredRoom überhaupt?
		GameHandler gameHandler = null;
		for (GameHandler gh : gameHandlers.values()) {
			if (gh.getID().equals(desiredRoom)) {
				gameHandler = gh;
				break;
			}
		}
		if (gameHandler == null) {
			clientHandler.getResponseProducer().sendGameJoinDenial();
			return;
		}
		
		// ist der desiredRoom zugänglich (noch Platz, noch offen)?
		if (!gameHandler.areSeatsAvailable()) {
			clientHandler.getResponseProducer().sendGameJoinDenial();
		}

		
		/* Auf Serverseite den Spieler zum Spiel hinzufügen */
		if (!gameHandler.addUser(clientHandler)) {
			// wenn dennoch fehlgeschlagen (z.B. Nutzer bereits im Spiel) dies mitteilen
			clientHandler.getResponseProducer().sendGameJoinDenial();
		}
		
		ServerLog.logMessage(clientHandler.getUsername() + " ist " +
							 gameHandler.getID() + " (" +
							 gameHandler.getRoomName() + ") beigetreten");
		/* Antwort über Producer aus ClientHandler */
		clientHandler.getResponseProducer()
			.sendGameJoinConfirmation(gameHandler.getSendingAddress(),
									  gameHandler.getReceivingAddress(),
									  gameHandler.getRoomName(),
									  gameHandler.getFieldName());
	}
	
	/**
	 * Verarbeitet eine Anforderung einer Liste aller offenen Spiele
	 * und verschickt diese an den Anfrager.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 */
	public void sendOpenGames(String destinationID) {
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// Log-Ausgabe
		ServerLog.logMessage("Liste offener Spiele gesendet an " + clientHandler.getUsername());

		// alle existierenden Spiele durchgehen
		Map<String, Integer> games = new HashMap<String, Integer>();
		for (GameHandler gameHandler : gameHandlers.values()) {
			// Zwischenmap befüllen
			if (gameHandler.hasOpenAccess()) {
				games.put(gameHandler.getID() + Parameter.LISTELEMENTSEPARATOR +
						  gameHandler.getRoomName(), gameHandler.getNumberOfUsers());
			}
		}

		// Antwort (Map für Nachricht als Parameter)
		clientHandler.getResponseProducer().sendOpenGamesList(games);
		
		// Zustand des Nutzers im ClientHandler vermerken: Nun in Spielübersicht
		clientHandler.setState(ClientState.GAMEROOMLOBBY);
	}
	
	/**
	 * Sendet eine Liste aller offenen Spiele an alle,
	 * die sich die Spielraumliste ansehen.
	 */
	public void resendOpenGamesForAll() {
		// Log-Ausgabe
		ServerLog.logMessage("Alle Clients in der Gameroomlobby erhalten erneut Spielraumliste");
		for (ClientHandler c : onlineClients.values()) {
			if (c.getState() == ClientState.GAMEROOMLOBBY) {
				sendOpenGames(c.getDestinationID());
			}
		}
	}
	
	/**
	 * Informiert ueber den nicht mehr moeglichen Zugriff auf einen Spielraum.
	 * @param destinationChannel Adresse, an die der Server antworten soll
	 * @param gameHandlerID ID des betreffenden GameHandlers
	 */
	private void sendClosingGame(String destinationID, String gameHandlerID) {
		// Nur wenn der Nutzer überhaupt eingeloggt ist
		if (!onlineClients.containsKey(destinationID)){
			// Wenn der Anfrager nicht eingeloggt ist antwortet der Server nicht	
			return;
		}
		// Passende Client-Infos holen
		ClientHandler clientHandler = onlineClients.get(destinationID);

		// Mitteilung
		clientHandler.getResponseProducer().sendClosingGame(gameHandlerID);
	}
	
	/**
	 * Informiert alle ueber den nicht mehr moeglichen Zugriff auf einen Spielraum.
	 * @param gameHandlerID ID des betreffenden GameHandlers
	 */
	public void sendClosingGameForAll(String gameID) {
		// Log-Ausgabe
		ServerLog.logMessage("[" + gameID + "] "
				+ "Spielschliessinfo an Clients in Game-Bereichen");
		
		for (ClientHandler c : onlineClients.values()) {
			if (c.getState() == ClientState.GAMEROOMLOBBY ||
				c.getState() == ClientState.GAMELOBBY ||
				c.getState() == ClientState.GAME) {
				
				sendClosingGame(c.getDestinationID(), gameID);
			}
		}
	}
	
	/**
	 * Entfernt ein Spielverwaltungsobjekt aus der Hauptverwaltung des MainModules.
	 * @param id Identifikation des Spielverwaltungsobjekts
	 * @return
	 */
	public void removeGameHandler(int id, String gameID) {
		gameHandlers.remove(id);
	}


	/* ---- Unit-Test-Getter ---- */
	
	public MainConsumer getMainConsumer() {
		return mainConsumer;
	}
	
	public Map<String, ClientHandler> getOnlineClientMap() {
		return onlineClients;
	}
	
	public GameHandler getGameHandler(int id) {
		return gameHandlers.get(id);
	}

}
