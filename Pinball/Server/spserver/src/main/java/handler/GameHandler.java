package handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.ServerLog;
import communication.ConnectionManager;
import communication.GameConsumer;
import communication.GameProducer;
import events.GameConsumerLobbyListener;
import events.PlayerFinishedLoadingEvent;
import events.PlayerIsReadyToPlayEvent;
import events.UserJoinedEvent;
import events.UserLeftEvent;
import field.GameField;
import game.Game;
import game.GameFieldEncoder;
import lobby.ClientState;
import lobby.MainModule;

/**
 * Dient zur Verwaltung eines Spielraums samt zugehoeriger Variablen.
 * @author dalle001
 *
 */
/**
 * @author Dennis
 *
 */
public class GameHandler extends Handler implements GameFinisher{

	// Elementare verwaltete Spielklassenobjekte und -variablen
	private Game game;
	private GameField gameField;
	private GameFieldEncoder gameFieldEncoder;
	
	// Kommunikation
	private GameConsumer gameConsumer;
	private GameProducer gameProducer;
	
	// Spielverwaltung
	private boolean openAccess;
	private int time;

	
	/**
	 * Erzeugung eines Objektes, welches eine Spielinstanz
	 * und damit verbundene Variablen und Kommunikationswege verwaltet.
	 * @param mmID Eindeutige Identifikationsvariable des Spiels
	 * @param fieldName Spielfeld, das als Vorlage für das Spiel dient
	 * @param mm MainModule zur Referenz
	 * @param roomName sichtbarer Spielraumname zur Wiedererkennung durch Nutzer
	 * @param time Spielzeit
	 */
	public GameHandler(int mmID, String fieldName, MainModule mm, String roomName, int time) {
		super(mm, mmID, 4, "game_", roomName, fieldName);
		
		/* Kommunikationswege und Komponenten die diese nutzen erstellen */
		ConnectionManager.getInstance().createConnection(CONNECTIONID);
		this.gameProducer = new GameProducer(CONNECTIONID, TOPICNAME);
		this.gameConsumer = new GameConsumer(CONNECTIONID, QUEUENAME);
		ConnectionManager.getInstance().startConnection(CONNECTIONID);
		ServerLog.logMessage("[" + CONNECTIONID +"] Spielraum unter dem Namen \"" + roomName +
							 "\" mit Spielfeld \"" + fieldName + "\" eröffnet");
		
		/* eigentliche Spieldaten */
		// Spiel selbst wird erst bei Spielstart erzeugt
		this.time = time;
		this.openAccess = true;
		
		/* Innere NotifiyChangeListener-Eventklasse */
		class GameHandlerGameConsumerLobbyListener implements GameConsumerLobbyListener {
			@Override
			public void playerJoinedFired(UserJoinedEvent e) {
				// Vermerken, dass Nutzer nun in der Lobby dieses Spiels ist
				User u = users.get(e.getUserID());
				u.setState(ClientState.GAMELOBBY);
				ServerLog.logMessage("[" + CONNECTIONID +"] " + u.getUsername() +
									 " ist Kommunikation beigetreten");

				// Melden an Mitspieler
				gameProducer.sendGameLobbyList(getUserList());
				gameProducer.sendUserLobbyMap(getUserLobbyMap());
				
				// Melden an Clients die die Spielraumliste sehen
				mainModule.resendOpenGamesForAll();
			}
			@Override
			public void playerLeftFired(UserLeftEvent e) {
				// Vermerken, dass Nutzer nun wieder im Menü ist
				User u = users.get(e.getUserID());
				u.setState(ClientState.GAMEROOMLOBBY);
				ServerLog.logMessage("[" + CONNECTIONID +"] " + u.getUsername() +
									 " hat Spiel-Kommunikationsweg verlassen");
				
				// Nutzer aus Spielerliste dieses Spiels entfernen
				removeUser(u);
			}
			@Override
			public void playerIsReadyToPlayFired(PlayerIsReadyToPlayEvent e) {
				// Bereitschaft des übergebenen Nutzers festhalten
				User u = users.get(e.getPlayerID());
				u.setWaitingToPlay(true);
				if (openAccess) {
					ServerLog.logMessage("[" + CONNECTIONID +"] " +
										 u.getUsername() + " ist spielbereit");
				} else {
					ServerLog.logMessage("[" + CONNECTIONID +"] " +
										 u.getUsername() + " ist revanchebereit");
				}
				
				// Änderung melden und wenn alle bereit Ladevorgang starten 
				anotherUserIsReady(e.getPlayerID());
			}
			@Override
			public void playerFinishedLoadingFired(PlayerFinishedLoadingEvent e) {
				// Bereitschaft des übergebenen Nutzers festhalten
				User u = users.get(e.getPlayerID());
				u.setWaitingToPlay(false);
				u.setPlaying(true);
				ServerLog.logMessage("[" + CONNECTIONID +"] " +
									 u.getUsername() + " hat Spielrunde geladen");
				
				// Änderung melden und wenn alle bereit Spiel starten
				anotherUserFinishedLoading(e.getPlayerID());
			}
		}
		gameConsumer.addGameConsumerLobbyListener
						(new GameHandlerGameConsumerLobbyListener());
	}

	/* (non-Javadoc)
	 * @see lobby.Handler#deleteSelf()
	 */
	protected void deleteSelf() {
		ServerLog.logMessage("[" + CONNECTIONID +"] Spielraum \"" +
				 roomName + "\" entfernt sich");
		if (openAccess) {
			// Falls Beitritte bisher noch erlaubt gewesen das Gegenteil mitteilen
			mainModule.sendClosingGameForAll(getID());
		}
		mainModule.removeGameHandler(mainModuleID, getID());
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
						newUser.getUsername() + " ist Spielraum beigetreten");
				newUser.setGameHandler(this);
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see lobby.Handler#removeUser(lobby.User)
	 */
	public boolean removeUser(User oldUser) {
		// nur wenn vorhanden
		if (users.containsKey(oldUser.getDestinationID())) {
			users.remove(oldUser.getDestinationID());
			ServerLog.logMessage("[" + CONNECTIONID + "] " +
					oldUser.getUsername() + " hat Spielraum verlassen");
			oldUser.setGameHandler(null);

			if (users.size() == 0 && game!=null && game.isRunning()){
				// Wenn Spielraum waehrend des Spiels leer geworden
				game.stop();
				deleteSelf();
				return true;
			} else if (users.size() == 0) {
				// Spielraum vor Spielbeginn leer -> wieder entfernen
				deleteSelf();
			} else {
				// Mitspielerverlust an Mitspieler melden
				gameProducer.sendGameLobbyList(getUserList());
				
				// Spielstart anstossen wenn nur noch spielbereite Spieler da
				if (areAllReady()) {
					triggerGameLoading();
					return true;
				}
			}
			
			
			if (openAccess) { // Sofern Clients noch beitreten koennen
				// Meldung an Clients die die Spielraumliste sehen
				mainModule.resendOpenGamesForAll();
				
			} else {
				// eine Spielrunde muss bereits gestartet worden sein 
				if (!game.isRunning() && users.size() > 0) { // Spielrunde zu Ende
					// -> Ein Spieler verlaesst nach Spielende den Spielraum
					// --> keine Revanche mehr moeglich
					gameProducer.sendNoRevanchePossible();
					deleteSelf();
				} // else-Fall bereits oben mit abgedeckt
			}
			
			return true;
		}
		return false;
	}

	/**
	 * Vermerkt einen Nutzer als psychisch spielbereit.
	 * @param userID ID (nicht Nutzername) des Nutzers, der sich nun bereit fuehlt
	 */
	private void anotherUserIsReady(String userID) {
		// Alle Teilnehmenden informieren
		String username = users.get(userID).getUsername();
		ServerLog.logMessage("[" + CONNECTIONID + "] " + username + " ist spielbereit");
		gameProducer.sendUserLobbyMap(getUserLobbyMap());
		
		if (areAllReady()) {
			triggerGameLoading();
		}
	}
	
	/**
	 * Prueft, ob alle anwesenden Spieler bereit sind.
	 * @return true wenn alle spielbereit
	 */
	private boolean areAllReady() {
		for (User u : users.values()) {
			if (!u.isWaitingToPlay()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Nutzer werden aufgefordert, das Spiel zu laden.
	 */
	private void triggerGameLoading() {
		// Spielernummern festlegen
		ArrayList<String> tempList = new ArrayList<>();
		int i = 1;
		for(String actUserID : users.keySet()){
			tempList.add(actUserID);
			users.get(actUserID).setPlayerNumber(i++);
		}
		// Erstellt mit vorhandenen Variablen die noetige Spielfeldlogik
		this.gameField = new GameField(fieldName, gameProducer);
		this.gameFieldEncoder = new GameFieldEncoder(gameField, gameProducer);

		// PlayerElements auf das Spielfeld laden und den Spielern zuordnen
		gameField.loadEssentialElements(tempList);
		
		// Spieler-Spielnummer-Zuordnung den Clients zeigen
		ServerLog.logMessage("[" + CONNECTIONID + "] Spieler-Spielernummer-Zuordnung verschickt.");
		gameProducer.sendPlayerNoMap(getPlayerNrMap());
		
		// Spielfeld an alle Clients schicken
		ServerLog.logMessage("[" + CONNECTIONID + "] GameLoadPacket verschickt.");
		gameFieldEncoder.sendGameLoadPacket();
	}
	
	/**
	 * Vermerkt einen Nutzer als technisch spielbereit (Spiel geladen).
	 * Haben alle Nutzer offiziell das Spiel geladen wird der Start angestossen. 
	 * @param userID ID (nicht Nutzername) des Nutzers, der fertig geladen hat
	 */
	private void anotherUserFinishedLoading(String userID) {
		boolean allReady = true;
		
		// Falls nun ALLE Teilnehmenden fertig geladen haben
		for (User u : users.values()) {
			if (!u.isPlaying()) {
				allReady = false;
			}
		}
		// starten wenn alle fertig sind und Spiel nicht bereits laeuft
		if (allReady && (game == null || !game.isRunning())) {
			startGame();
		}
	}
	

	/* ---- Getter komplexer Strukturen ---- */

	

	/**
	 * Startet ganz offiziell das Spiel für alle Teilnehmenden. Startet dabei
	 * den Spielprozess, setzt den Status aller Spieler auf Spielmodus,
	 * nummeriert die Spieler durch und teilt die Nummerierung mit.
	 */
	private void startGame() {
		// Vermerken, dass alle Spieler spielen
		for (User u : users.values()) {
			u.setState(ClientState.GAME);
		}
		
		// Spiel starten
		Map<String, String> userNames = new HashMap<String, String>();
		for (String userID : users.keySet()){
			userNames.put(userID, users.get(userID).getUsername());
		}
		game = new Game(CONNECTIONID, gameConsumer, gameProducer, gameField,
						gameFieldEncoder, userNames, time, this);
		game.start();
		
		// Spielstart mitteilen
		gameProducer.sendGameStarts(getRoomName(), game.getGameDurationInSeconds());

		// Raumzugang schliessen
		openAccess = false;
		
		// Schliessung an Clients die die Spielraumliste sehen melden
		ServerLog.logMessage("[" + CONNECTIONID + "] Spielschliessinfo an Clients in Game-Bereichen");
		mainModule.sendClosingGameForAll(getID());
	}

	/**
	 * Beendet ganz offiziell das Spiel für alle Teilnehmenden.
	 * Stoppt dabei den Spielprozess, setzt Spielvariablen aller Spieler
	 * zurueck und teilt jenen den finalen Punktestand mit.
	 * Es ist dabei offen, ob eine Revanche stattfindet oder bei Verlassen
	 * von Spielern sich dieses Spielverwaltungsobjekt entfernt.
	 */
	public void finishGame() {
		// Spiel beenden
		game.stop();
		ServerLog.logMessage("[" + CONNECTIONID + "] Spiel wurde beendet");
		
		if (users.size() < 1) {
			// Alle Spieler bereits nicht mehr anwesend
			deleteSelf();
		} else {
			// Spiel-Ende und finalen Punktestand mitteilen
			gameProducer.sendGameHighscores(game.getPlayerScoreMap());		

			// Spielervermerkung anpassen
			for (User u : users.values()) {
				u.setPlaying(false);
				u.resetPlayerNumber();
				// man geht vom Revanche-Wunsch aus
				// ansonsten kommt ja eine Verlassensnachricht
				u.setState(ClientState.GAMELOBBY);
			}
		}
	}

	
	/* ---- Getter ---- */
	
	/**
	 * Erstellt eine Map mit Zuordnung der Spieler zu ihrer Spielernummer.
	 * Key: Spielername
	 * Value: Spielernummer
	 * @return erstellte Map
	 */
	private Map<String, Boolean> getUserLobbyMap() {
		Map<String, Boolean> lobbyMap = new HashMap<String, Boolean>();
		for(User u : users.values()) {
			lobbyMap.put(u.getUsername(), u.isWaitingToPlay());
		}
		return lobbyMap;
	}
	
	/**
	 * Erstellt eine Map mit Zuordnung der Spieler zu ihrer Spielernummer.
	 * Key: Spielername
	 * Value: Spielernummer
	 * @return erstellte Map
	 */
	private Map<String, Integer> getPlayerNrMap() {
		Map<String, Integer> playerNoMap = new HashMap<String, Integer>();
		for(User u : users.values()) {
			playerNoMap.put(u.getUsername(), u.getPlayerNumber());
		}
		return playerNoMap;
	}
	
	/**
	 * Sagt, ob Beitritte moeglich sind
	 * @return true wenn Beitritt moeglich
	 */
	public boolean hasOpenAccess() {
		return openAccess;
	}
	
	
	/* ---- Unit-Test-Getter ---- */
	
	/**
	 * Unit-Test-Getter.
	 */
	public GameConsumer getGameConsumer() {
		return gameConsumer;
	}

	/**
	 * Unit-Test-Getter.
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * Unit-Test-Getter.
	 */
	public User getFirstPlayer() {
		return (User) users.values().toArray()[0];
	}
	/**
	 * Unit-Test-Getter.
	 */
	public User getUser(String playerID) {
		return users.get(playerID);
	}
}
