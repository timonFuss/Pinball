package spserver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import communication.Command;
import communication.ConnectionManager;
import communication.MainConsumer;
import communication.Parameter;
import lobby.MainModule;

public class MainConsumerTest {

	final String CHANNEL = "testChannel";
	final String CONNECTION_ID = "testConnection";
	MainConsumer mainConsumer;
	Session session;
	Destination destination;
	
	@Before
	public void setup() throws JMSException{
		ConnectionManager.getInstance().createConnection(CONNECTION_ID);
		mainConsumer = new MainConsumer(CONNECTION_ID, CHANNEL, new MainModule());
		ConnectionManager.getInstance().startConnection(CONNECTION_ID);
		session = ConnectionManager.getInstance().getSession(CONNECTION_ID);
		destination = session.createQueue("Queue");
	}
	
	@After
	public void clean(){
		ConnectionManager.getInstance().resetSingletonforUnitTest();
	}
	
	@Test
	public void testLoginCommand() throws JMSException {
		TextMessage textMessage = null;		
		textMessage = session.createTextMessage(Command.LOGIN);
		textMessage.setJMSReplyTo(destination);
		textMessage.setStringProperty(Parameter.USERNAME, "Willi");
		mainConsumer.onMessage(textMessage);
	}
	
	@Test
	public void testLogoutCommand() throws JMSException {
		//erst einloggen, damit das ausloggen keine Exception wirft
		TextMessage loginMessage = null;
		loginMessage = session.createTextMessage(Command.LOGIN);
		loginMessage.setJMSReplyTo(destination);
		loginMessage.setStringProperty(Parameter.USERNAME, "Willi");
	
		TextMessage textMessage = null;		
		textMessage = session.createTextMessage(Command.LOGOUT);
		textMessage.setJMSReplyTo(destination);
		mainConsumer.onMessage(loginMessage);
		mainConsumer.onMessage(textMessage);
	}
	
	@Test
	public void testWhoIsOnCommand() throws JMSException {
		TextMessage textMessage = null;
		textMessage = session.createTextMessage(Command.WHOISON);
		textMessage.setJMSReplyTo(destination);
		mainConsumer.onMessage(textMessage);
	}
	
}
