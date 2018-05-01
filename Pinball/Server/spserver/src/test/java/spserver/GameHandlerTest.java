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

public class GameHandlerTest {

	final String MAIN_CONNECTION_ID = "mainConnection";
	GameConsumer gameConsumer;
	Session session;
	Destination destination1, destination2;
	String destID1 = "Queue1", destID2 = "Queue2";
	
	MainModule mainModule;
	GameHandler dummyGame;
	
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
	}
	
	@After
	public void clean(){
		ConnectionManager.getInstance().resetSingletonforUnitTest();
	}
	
	@Test
	public void testGamePlayerNumber() throws JMSException {
		Assert.assertEquals(2, dummyGame.getNumberOfUsers());
		
		TextMessage textMessage = session.createTextMessage(Command.COMMU_LEFT);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		Assert.assertEquals(1, dummyGame.getNumberOfUsers());
		
		textMessage = session.createTextMessage(Command.JOINGAME);
		textMessage.setJMSReplyTo(destination1);
		textMessage.setStringProperty(Parameter.ROOM_ID, "game_0");
		mainModule.getMainConsumer().onMessage(textMessage);
		Assert.assertEquals(2, dummyGame.getNumberOfUsers());
	}
	
	
	@Test
	public void testPlayerStateHandling() throws JMSException {
		
		TextMessage textMessage = null;
		textMessage = session.createTextMessage(Command.COMMU_JOINED);
		textMessage.setJMSReplyTo(destination1);

		Assert.assertNotEquals(ClientState.GAMELOBBY, dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())).getState());

		gameConsumer.onMessage(textMessage);
		Assert.assertEquals(ClientState.GAMELOBBY, dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())).getState());
		
		textMessage = session.createTextMessage(Command.COMMU_LEFT);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		Assert.assertNull(dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())));
	}
	
	@Test
	public void testPlayerIsReadyCommand() throws JMSException {
		TextMessage textMessage = null;
		textMessage = session.createTextMessage(Command.COMMU_JOINED);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);

		textMessage = session.createTextMessage(Command.READYTOPLAY);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		
		Assert.assertTrue(dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())).isWaitingToPlay());
	}
	
	@Test
	public void testGameStartsWithPlayersPlaying() throws JMSException {
		TextMessage textMessage = null;
		textMessage = session.createTextMessage(Command.COMMU_JOINED);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		
		textMessage = session.createTextMessage(Command.READYTOPLAY);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);

		Assert.assertTrue(dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())).isWaitingToPlay());
		
		textMessage = session.createTextMessage(Command.LOADINGCOMPLETE);
		textMessage.setJMSReplyTo(destination1);
		gameConsumer.onMessage(textMessage);
		
		Assert.assertTrue(dummyGame.getUser(gameConsumer.getDestinationID(textMessage.getJMSReplyTo())).isPlaying());
	}

	
}
