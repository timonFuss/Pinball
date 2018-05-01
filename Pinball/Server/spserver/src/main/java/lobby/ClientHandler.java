package lobby;

import javax.jms.Destination;

import communication.MainProducer;
import handler.EditorHandler;
import handler.GameHandler;
import handler.User;

/**
 * Objekt, welches serverrelevante Informationen zu jedem Nutzer bündelt.
 * 
 * @author mbeus001
 *
 */
public class ClientHandler implements User {

	private Destination senderDestiantion;  // Zeiger/Adresse auf Queue, an der der Client hängt
	private String username; 				// Nutzereingabe zur Identifikation
	private MainProducer responseProducer; 	// Producer des Servers, der an Client-Queue sendet
	private ClientState state = ClientState.INVALID; // Nutzerstatus
	private String destinationID;
	private GameHandler myGameHandler; // GameHandler der Lobby, in der sich der Client befindet, damit er sich bei Logout selbst entfernen kann
	private EditorHandler myEditorHandler; // EditorHandler, in der sich der Client befindet, damit er sich bei Logout selbst entfernen kann
	private boolean isWaitingToPlay;
	private boolean isPlaying;
	private int playerNumber;
	private final int DEFAULTPLAYERNUMBER = -1; 

	/**
	 * Konstruktor stellt Zuordnung der per Queue immer erhaltenen
	 * Antwortadresse, des vom Server zu erstellenden Antwort-Producers und des
	 * Nutzernamens her.
	 * 
	 * @param senderDestiantion
	 *            "Zeiger"/Adresse auf Queue bzw. Topic
	 * @param username
	 *            String-Repräsentation des Nutzers
	 */
	public ClientHandler(String connectionID, Destination senderDestiantion, String username, String destinationID) {
		this.senderDestiantion = senderDestiantion;
		this.username = username;
		this.responseProducer = new MainProducer(connectionID, senderDestiantion);
		this.state = ClientState.MENU;
		this.destinationID = destinationID;
		this.isWaitingToPlay = false;
		this.isPlaying = false;
		this.playerNumber = DEFAULTPLAYERNUMBER;
	}

	/**
	 * Konstruktor für Nutzer, die nicht am System teilnehmen dürfen. Bei
	 * fehlerhaftem Login ist eine Ablehnantwort nötig, wozu die Antwortadresse
	 * an den Clienten zwischengemerkt werden muss.
	 * 
	 * @param senderDestiantion
	 */
	public ClientHandler(String connectionID, Destination senderDestiantion) {
		this.responseProducer = new MainProducer(connectionID, senderDestiantion);
	}
	

	public Destination getDestination() {
		return senderDestiantion;
	}

	public String getUsername() {
		return username;
	}

	public MainProducer getResponseProducer() {
		return responseProducer;
	}

	public ClientState getState() {
		return state;
	}

	public void setState(ClientState cs) {
		this.state = cs;
	}

	public String getDestinationID() {
		return destinationID;
	}

	public boolean isWaitingToPlay() {
		return isWaitingToPlay;
	}

	public void setWaitingToPlay(boolean now) {
		isWaitingToPlay = now;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean now) {
		isPlaying = now;
	}
	

	public String toString() {
		return "Username: " + username + " (" + state.toString() + ")";
	}

	public GameHandler getGameHandler() {
		return myGameHandler;
	}

	public void setGameHandler(GameHandler gameHandler) {
		myGameHandler = gameHandler;
		
	}


	@Override
	public void setPlayerNumber(int number) {
		// Nur Spielernummern zwischen 1 und 10 erlaubt
		if (number > 0 && number < 10) {
			playerNumber = number;
		} else {
			playerNumber = DEFAULTPLAYERNUMBER;
		}
	}

	@Override
	public int getPlayerNumber() {
		return playerNumber;
	}

	@Override
	public void resetPlayerNumber() {
		playerNumber = DEFAULTPLAYERNUMBER;
	}

	public EditorHandler getEditorHandler() {
		return myEditorHandler;
	}

	public void setEditorHandler(EditorHandler editorHandler) {
		myEditorHandler = editorHandler;
		

	}
}
