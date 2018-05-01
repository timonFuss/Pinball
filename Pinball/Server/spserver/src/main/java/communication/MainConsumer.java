package communication;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import lobby.MainModule;

/**
 * Konkreter Consumer, welcher alle allgemeinen Nachrichten empfängt und
 * abhängig vom erhaltenen Befehl die Inhalte weiterdelegiert.
 * 
 * @author mschw001
 *
 */
public class MainConsumer extends Consumer {

	private MainModule mainModule;  // Programmmodul, welches Nachrichten verarbeitet

	/**
	 * Konstruktor, der die Queue für allgemeine Nachrichten von Clients
	 * anlegt und diese anzapft. 
	 * @param queueName Name des Nachrichtenkanals, dessen Nachrichten man erhält
	 * @param mainModule Hieran wird die Verarbeitung erhaltener Befehle delegiert
	 */
	public MainConsumer(String connectionID, String queueName, MainModule mainModule) {
		super(connectionID, queueName);
		this.mainModule = mainModule;
	}

	
	/* (non-Javadoc)
	 * @see communication.Consumer#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		// Nicht unterstützte Nachrichtenformate
		if (!(message instanceof TextMessage)) {
			throw new AssertionError("Im MainConsumer wird eine TextMessage erwartet!");
		}
		
		/* Interpretation */
		TextMessage textMessage = (TextMessage) message;
		String command;
		
		try {
			command = textMessage.getText();
		} catch (JMSException e) {
			e.printStackTrace();
			throw new AssertionError("Fehler bei Verarbeitung der Message: "
									 + textMessage.toString());
		}
		
		/* Delegation */
		try {
			switch (command) {
			case Command.LOGIN: {
				mainModule.login(textMessage.getJMSReplyTo(), getDestinationID(textMessage.getJMSReplyTo()),
						textMessage.getStringProperty(Parameter.USERNAME));
			} break;

			case Command.LOGOUT: {
				mainModule.logout(getDestinationID(textMessage.getJMSReplyTo()));
			} break;

			case Command.WHOISON: {
				mainModule.sendUserlist(textMessage.getJMSReplyTo(), getDestinationID(textMessage.getJMSReplyTo()));
			} break;

			case Command.WHICHFIELDS: {
				mainModule.sendFieldList(textMessage.getJMSReplyTo(), getDestinationID(textMessage.getJMSReplyTo()));
			} break;

			
			case Command.WHICHOPENGAMES: {
				mainModule.sendOpenGames(getDestinationID(textMessage.getJMSReplyTo()));
			} break;

			case Command.STARTGAME: {
				mainModule.userCreatesNewGame(getDestinationID(textMessage.getJMSReplyTo()),
						textMessage.getStringProperty(Parameter.GAMEFIELDNAME),
						textMessage.getStringProperty(Parameter.NEWROOMNAME),
						textMessage.getIntProperty(Parameter.GAMEDURATION));
			} break;

			case Command.JOINGAME: {
				mainModule.directUserToGame(getDestinationID(textMessage.getJMSReplyTo()),
						textMessage.getStringProperty(Parameter.ROOM_ID));
			} break;

			
			case Command.WHICHOPENEDITORS: {
				mainModule.sendOpenEditors(getDestinationID(textMessage.getJMSReplyTo()));
			} break;

			case Command.STARTEDITOR: {
				mainModule.userCreatesNewEditor(getDestinationID(textMessage.getJMSReplyTo()),
						textMessage.getStringProperty(Parameter.GAMEFIELDNAME),
						textMessage.getStringProperty(Parameter.NEWROOMNAME));
			} break;

			case Command.JOINEDITOR: {
				mainModule.directUserToEditor(getDestinationID(textMessage.getJMSReplyTo()),
						textMessage.getStringProperty(Parameter.ROOM_ID));
			} break;

			default:
				throw new AssertionError("Unerwarteter Befehl: " + command);
			}
		} catch (JMSException e) {
			throw new AssertionError("Fehler bei Dekodierung der erhaltenen Message");
		}
	}

}
