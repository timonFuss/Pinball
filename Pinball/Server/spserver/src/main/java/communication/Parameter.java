package communication;


/**
 * Primitive Form eines Enums, dessen Stringrepräsentation lediglich nötig ist.
 * Jegliche Property-Namen sind hier festzulegen.
 * 
 * @author dalle001
 *
 */
public class Parameter {
	// Main
	public static final String USERNAME = "username";			// Nutzername
	public static final String SUCCESS = "success";				// Bestätigung/Ablehnung
	public static final String CLIENTLIST = "clientlist";		// Liste aller anwesenden Nutzer

	public static final String FIELDLIST = "fieldlist";				// Liste aller nutzbaren Spielfelder
	public static final String ROOM_ID = "roomid";					// "Raum", z.B. Spiel 17, Editor 3 (Protokoll hierfür kann abweichen!)
	public static final String CLIENT_RCV_FROM = "clientrcvfrom";	// Weg, über den zukünftig der Client empfangen soll
	public static final String CLIENT_SND_TO = "clientsendto";		// Weg, über den zukünftig der Client senden soll

	// Editor
	public static final String ID = "id";      
	
	// Editor + Game
	public static final String GAMEFIELDNAME = "gamefieldname";		// Spielfeldname
	public static final String NEWROOMNAME = "newroomname";
	
	public static final String POSX = "posx";
	public static final String POSY = "posy";
	public static final String ELEMENTTYPE = "elementtype";

	// Game
	public static final String ROOMNAME = "roomname";
	public static final String GAMEDURATION = "gameduration";
	
	public static final String ROT = "rot";	
	public static final String CHANGEABLE = "changeable";	// entscheidet, ob Spielelement von update-Methode beachtet
	
	public static final String BUTTON_UP = "button_up";
	public static final String BUTTON_DOWN = "button_down";
	public static final String BUTTON_LEFT = "button_left";
	public static final String BUTTON_RIGHT = "button_right";
	
	public static final String FLIPPER_LEFT = "flipper_left";
	public static final String FLIPPER_RIGHT = "flipper_right";
	
	public static final String POINTS = "points";
	
	public static final String UP = "up";

	
	// Trennzeichen bei Stringlisten - Inhalte dürfen dieses nicht enthalten!
	public static final String LISTELEMENTSEPARATOR = ";";
	public static final String IDTYPESEPERATOR = ":";
}
