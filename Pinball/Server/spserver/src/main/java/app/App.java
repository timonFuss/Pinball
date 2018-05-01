package app;

import collider.ColliderReader;
import lobby.MainModule;

/**
 * Startklasse, welche Server startet
 * 
 * @author mbeus001
 *
 */
public class App {
	
	@SuppressWarnings("unused")
	public static void main(String[] argv) {

		ColliderReader tmp = ColliderReader.getInstance( );
		MainModule mainModule = new MainModule();
	}
	
}
