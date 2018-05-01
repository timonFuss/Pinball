package communication;


/**
 * Primitive Form eines Enums, dessen Stringrepräsentation lediglich nötig ist.
 * Jegliche Befehle sind hier festzulegen.
 * 
 * @author dalle001
 *
 */
public class Command {
	
	/* Client to Server */
	// Main
	public static final String LOGIN = "login";					// Loginwunsch
	public static final String LOGOUT = "logout";				// Logoutwunsch
	public static final String WHOISON = "whoison";				// Interesse nach Liste aller Anwesenden

	// Editor + Game
	public static final String WHICHFIELDS = "whichfields";				// Frage nach vorhandenen Spielfeldern
	public static final String WHICHOPENEDITORS = "whichopeneditors";	// Frage nach offenen beitretbaren Spielfeldern
	public static final String WHICHOPENGAMES = "whichopengames";		// Frage nach offenen beitretbaren Spielen
	
	public static final String STARTEDITOR = "starteditor";			// Eröffnen einer Editier-Sitzung
	public static final String STARTGAME = "startgame";			// Eröffnen einer Spiel-Sitzung
	public static final String JOINEDITOR = "joineditor";			// Beitritt zu einer Editier-Sitzung
	public static final String JOINGAME = "joingame";			// Beitritt zu einer Spiel-Sitzung
	
	public static final String COMMU_JOINED = "joinedcommunication"; 	// Client hat auf seiner Seite den Beitritt durchgefuehrt
	public static final String COMMU_LEFT = "leftcommunication"; 		// Client hat auf seiner Seite den Beitritt durchgefuehrt
	public static final String READYTOPLAY = "readytoplay";				// Manuelle Spielstartbestätigung
	public static final String LOADINGCOMPLETE = "loadingcomplete";		// (Spielfeld) laden fertig
	
	// Editor
	public static final String SETELEMENT = "setelement";      // Nutzer versucht, Element zu setzen
	public static final String MOVEELEMENT = "moveelement";    // Nutzer versucht, Element zu bewegen
	public static final String DELETEELEMENT = "deleteelement";// Nutzer versucht, Element zu entfernen
	public static final String UNDO = "undo";                  // Nutzer will letzten Schritt ungeschehen machen
	public static final String REDO = "redo";                  // Nutzer will letzten Schritt doch wieder geschehen machen
	public static final String SAVEEFIELD = "savefield";        // Spielfeld-Stringbezeichnung wurde angepasst
	
	public static final String CHANGENAME = "changename";      // Nutzer will Stringbezeichnung des Spielfelds anpassen
	
	// Game
	
	public static final String DEBUG_FORCEVECTOR = "debug_forcevector";
		
	public static final String BUTTON_UP = "button_up";
	public static final String BUTTON_DOWN = "button_down";
	public static final String BUTTON_LEFT = "button_left";
	public static final String BUTTON_RIGHT = "button_right";
	
	public static final String FLIPPER_LEFT = "flipper_left";
	public static final String FLIPPER_RIGHT = "flipper_right";
	
	public static final String POINTS = "points";
	
	
	/* Server to Client */
	// Main
	public static final String CLIENTLOGIN = "clientlogin";		// Notification zu fremden Logins
	public static final String CLIENTLOGOUT = "clientlogout";	// Notification zu fremden Logouts
	public static final String LOGINOK = "loginok";				// Antwort auf Loginwunsch
	public static final String CLIENTSONLINE = "clientsonline";	// Versand der Liste aller Anwesenden

	// Editor + Game (1:1)
	public static final String AVAILABLEFIELDS = "availablefields";	// vorhandene Spielfelder
	public static final String OPENEDITORS = "openeditors";			// Offene beitretbare aktuelle Editiersessions
	public static final String OPENGAMES = "opengames";				// Offene beitretbare aktuelle Spiele
	public static final String CLOSINGEDITOR = "closingeditor";		// Vorhandener Editierraum schliesst
	public static final String CLOSINGGAME = "closinggame";			// Vorhandener Spielraum schliesst 
	
	public static final String YOUREDITOR = "youreditor";		// Antwort zu Editier-Sitzungsanfrage
	public static final String YOURGAME = "yourgame";			// Antwort zu Spiel-Sitzungsanfrage
	
	// Editor (Topic)
	public static final String EDITORLOADPACKET = "editorloadpacket";	// Editorpaket
	
	public static final String ELEMENTSET = "elementset";    	   // Element wurde gesetzt
	public static final String ELEMENTMOVED = "elementmoved"; 	   // Element wurde verschoben
	public static final String ELEMENTDELETED = "elementdeleted";  // Element wurde entfernt
	public static final String NAMECHANGED = "namechanged";        // Spielfeld-Stringbezeichnung wurde angepasst
	public static final String FIELDSAVED = "fieldsaved";        // Spielfeld-Stringbezeichnung wurde angepasst
	
	// Game (Topic)
	public static final String PLAYERREADYTOPLAY = "playerreadytoplay";		// Manuelle Spielstartbestätigung
	public static final String CLIENTSINGAME = "clientsingame";				// Liste aller Spieler in bestimmtem Raum
	public static final String PLAYERNUMBERLIST = "playernumberlist";		// Liste aller Spieler und zugehoeriger Spielernummer			
	public static final String GAMEFIELDLOADPACKET = "gamefieldloadpacket";	// Spielfeldpaket
	public static final String GAMEUPDATEPACKET = "gameupdatepacket";		// Updatepakete im Spiel
	
	public static final String ELEMENTHIT = "elementhit";
	public static final String WALLHIT = "wallhit";
	public static final String FLIPPERMOVE = "flippermove";
	public static final String SHOOTOUT = "shootout";

	public static final String GAMESTARTS = "gamestarts";	// Signal zu Spielstart
	public static final String GAMEOVER = "gameover";		// Signal zu Spielende
	public static final String NOREVANCHE = "norevanche";	// Revanche nicht mehr moeglich, Spielraum schliesst
	
}
