package spserver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import communication.Command;
import communication.ConnectionManager;
import communication.GameConsumer;
import communication.Parameter;
import handler.GameHandler;
import lobby.ClientState;
import lobby.MainModule;

public class GameTest {

	final String MAIN_CONNECTION_ID = "mainConnection";
	GameConsumer gameConsumer;
	Session session;
	Destination destination1, destination2;
	String destID1 = "Queue1", destID2 = "Queue2";
	
	MainModule mainModule;
	GameHandler dummyGame;
	
	final int SHORTGAMETIME = 1000;
	
	@Before
	public void setup() throws JMSException{
		// Kommunikation
		mainModule = new MainModule();
		session = ConnectionManager.getInstance().getSession(MAIN_CONNECTION_ID);
		destination1 = session.createQueue(destID1);
		destination2 = session.createQueue(destID2);
		mainModule.login(destination1, destID1, "Kontrolletti");
		mainModule.login(destination2, destID2, "Bibi Blocksberg");

		// Hauptzeug
		mainModule.userCreatesNewGame(destID1, "DefaultSpielfeld", "Testspiel", 30);
		dummyGame = mainModule.getGameHandler(0);
		gameConsumer = dummyGame.getGameConsumer();
		
		// Spieler dem Dummy-Spiel hinzufuegen
		TextMessage textMessage = null;
		textMessage = session.createTextMessage(Command.JOINGAME);
		textMessage.setJMSReplyTo(destination1);
		textMessage.setStringProperty(Parameter.ROOM_ID, "game_0");
		mainModule.getMainConsumer().onMessage(textMessage);
		
		textMessage.setJMSReplyTo(destination2);
		mainModule.getMainConsumer().onMessage(textMessage);
		
		// Beitrittsbestaetigungen
		clientsSendGameMessage(Command.COMMU_JOINED);
		
		clientsSendGameMessage(Command.READYTOPLAY);

		// Spiel starten
		clientsSendGameMessage(Command.LOADINGCOMPLETE);

		// Spieldauer manipulieren
		dummyGame.getGame().setGameTime(SHORTGAMETIME);
		
	}
	
	@After
	public void clean(){
		ConnectionManager.getInstance().resetSingletonforUnitTest();
	}
	
	/**
	 * Auslagerung des Sendens von gewuenschten Nachrichten
	 * der Dummy-Clients an den Server.
	 * @param command Command-Stringbefehl
	 * @throws JMSException Sollte nicht durch die Tests passieren.
	 */
	private void clientsSendGameMessage(String command) throws JMSException {
		TextMessage textMessage = session.createTextMessage(command);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		
		textMessage.setJMSReplyTo(destination2);
		gameConsumer.onMessage(textMessage);
	}
	
	
//	@Test
//	public void testPlayerStateHandling() throws JMSException {
//		
//		TextMessage textMessage = null;
//		
//		textMessage = session.createTextMessage(Command.COMMU_LEFT);
//		textMessage.setJMSReplyTo(destination1);
//		gameConsumer.onMessage(textMessage);
//		Assert.assertNull(dummyGame.getPlayer(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())));
//	}

	
	@Test
	public void testGameRunsAfterPlayerLeavesGame() throws JMSException {
		int numberOfStartingPlayers = dummyGame.getNumberOfUsers();
		
		try {
			Thread.sleep(SHORTGAMETIME / 2);
		} catch (InterruptedException e) {
			System.out.println("Threadschlaf im Test testGameRunsAfterPlayerLeavesGame() übersprungen.");
		}
		
		TextMessage textMessage = session.createTextMessage(Command.COMMU_LEFT);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		
		
		Assert.assertTrue(dummyGame.getNumberOfUsers() < numberOfStartingPlayers);
	}
	
	@Test
	public void testGameRunsAfterPlayerLogout() throws JMSException {
		int numberOfStartingPlayers = dummyGame.getNumberOfUsers();
		
		try {
			Thread.sleep(SHORTGAMETIME / 2);
		} catch (InterruptedException e) {
			System.out.println("Threadschlaf im Test testGameRunsAfterPlayerLogout() übersprungen.");
		}
		
		TextMessage textMessage = session.createTextMessage(Command.LOGOUT);
		textMessage.setJMSReplyTo(destination1);
		mainModule.getMainConsumer().onMessage(textMessage);
		
		Assert.assertTrue(dummyGame.getNumberOfUsers() < numberOfStartingPlayers);
	}
	
}
