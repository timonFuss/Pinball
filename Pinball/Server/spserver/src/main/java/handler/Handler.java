package handler;

import java.util.HashMap;

import communication.Parameter;
import lobby.MainModule;

/**
 * Diese Oberklasse dient als Vorlage zur Verwaltung einer Spielmodulart.
 * @author dalle001
 *
 */
public abstract class Handler {

	// Verwaltung
	protected MainModule mainModule;		// Referenz an die Hauptverwaltungsklasse
	protected final int mainModuleID;		// Identifikation von this im MainModule
	
	// Kommunikation und Identifikation
	protected final String CONNECTIONID;	// ID von this fuer Zugriff von Clients
	protected final String QUEUENAME;		// ID der Kommunikations-Empfangskanals
	protected final String TOPICNAME;		// ID des Kommunikations-Sendekanals
	protected String roomName;				// fuer GUI sichtbarer Raumname
	protected String fieldName;				// Name des als Vorlage genutzten Feld
	
	// Mitspielerverwaltung
	protected HashMap<String, User> users;
	protected final int MAXUSERS;
	
	
	/**
	 * Erzeugung eines Objektes, welches eine Modulinstanz
	 * und damit verbundene Variablen und Kommunikationswege verwaltet.
	 * @param mm MainModule zur Referenz
	 * @param mmID Eindeutige Identifikationsvariable aus dem MainModule
	 * @param maxUsers Obergrenze der beteiligten Clients
	 * @param commuPrefix Modulbezeichnung fuer Kommunikationswegbetitelung
	 * @param roomName fuer GUI sichtbarer Name des Modulraums
	 * @param fieldName Name des als Vorlage genutzten Feld 
	 */
	public Handler(MainModule mm, int mmID, int maxUsers, String commuPrefix,
					String roomName, String fieldName){
		// Verwaltungsreferenz
		this.mainModule = mm;
		this.mainModuleID = mmID;
	
		// Kommunikationsreferenzen
		this.CONNECTIONID = commuPrefix + mmID;
		this.TOPICNAME = commuPrefix + "topic_" + mmID;
		this.QUEUENAME = commuPrefix + "queue_" + mmID;
		
		// Spielzusatzdaten
		this.fieldName = fieldName;
		if (roomName.equals(null)) {
			this.roomName = fieldName;
		} else {
			this.roomName = roomName;
		}
		this.MAXUSERS = maxUsers;
		this.users = new HashMap<String, User>();
	}
	
	
	/**
	 * Selbstentfernung aus der uebergeordneten Verwaltungsliste, wodurch jegliche
	 * Referenz auf this verloren geht und der GarbageCollector aufraeumt. 
	 */
	protected abstract void deleteSelf();
	
	
	/* ---- Ablauf ---- */
	
	/**
	 * Sagt, ob Nutzer einem Modulraum noch beitreten koennen
	 * @return true wenn noch mind. einer beitreten kann
	 */
	public boolean areSeatsAvailable() {
		return (MAXUSERS - getNumberOfUsers() > 0);
	}
	
	/**
	 * Fuegt einen Nutzer der Teilnehmendenliste dieses Modulraums hinzu.
	 * @param newUser hinzuzufuegender Nutzer
	 * @return false wenn kein Platz vorhanden oder Nutzer bereits dabei
	 */
	public abstract boolean addUser(User newUser);
	
	/**
	 * Entfernt einen Nutzer aus der Teilnehmendenliste dieses Modulraums.
	 * @param oldUser zu entfernender Nutzer
	 * @return false wenn der Nutzer nicht vermerkt ist
	 */
	public abstract boolean removeUser(User oldUser);
	
	
	/* ---- Getter ---- */

	/**
	 * Gibt die eindeutige Bezeichnung dieses Modulraums zurueck
	 * @return Stringname zur Zuordnung
	 */
	public String getID() {
		return CONNECTIONID;
	}
	
	/**
	 * Gibt die Kommunikationsadresse fuer den Empfangsstrom des Servers zurueck.
	 * @return Stringbezeichnung der Kommunikationsadresse
	 */
	public String getSendingAddress() {
		return TOPICNAME;
	}

	/**
	 * Gibt die Kommunikationsadresse fuer den Sendestrom des Servers zurueck.
	 * @return Stringbezeichnung der Kommunikationsadresse
	 */
	public String getReceivingAddress() {
		return QUEUENAME;
	}
	
	/**
	 * Gibt die Textbezeichnung dieses Modulraums zurueck
	 * @return Stringname des Modulraums zur Zuordnung
	 */
	public String getRoomName() {
		return roomName;
	}
	
	/**
	 * Gibt die Textbezeichnung des genutzten Feldes zurueck
	 * @return Stringname des Feldes
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sagt, wie viele Nutzer an diesem Modulraum bisher teilnehmen
	 * @return Anzahl aktiver Mitspieler
	 */
	public int getNumberOfUsers() {
		return users.size();
	}
	
	/**
	 * Sagt, wie viele Nutzer einem Modulraum noch beitreten koennen
	 * @return Zahl moeglicher zusätzlicher Spieler
	 */
	public int getOpenSeats() {
		return (MAXUSERS - getNumberOfUsers());
	}

	/**
	 * Erstellt eine Liste aller Nutzer dieses Modulraums ohne weitere
	 * Zusatzinformationen, separiert mit einem Trennzeichen-Parameter.
	 */
	protected String getUserList(){
		String encodedUsernameList = "";
		
		// Alle Usernamen einer encodierten Stringliste hinzufuegen
		for (User u : users.values()) {
	    	encodedUsernameList += u.getUsername() + Parameter.LISTELEMENTSEPARATOR;
		}
		
		return encodedUsernameList;
	}
}
