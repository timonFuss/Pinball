package lobby;

/**
 * Nutzerstatus, definiert durch allgemeinen Anwendungszustand des Clients.
 * 
 * @author dalle001
 *
 */
public enum ClientState {
	
	MENU, EDITORROOMLOBBY, EDITOR, GAMELOBBY, GAMEROOMLOBBY, GAME, INVALID;
	
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
