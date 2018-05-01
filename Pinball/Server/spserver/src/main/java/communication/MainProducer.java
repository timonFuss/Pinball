package communication;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Konkreter Producer, welcher alle allgemeinen Nachrichten
 * an ein zu konfigurierendes Ziel versendet.
 * 
 * @author mbeus001
 *
 */
public class MainProducer extends Producer {

	/**
	 * @param channelName Ziel, an das in Zukunft gesendet werden soll
	 */
	public MainProducer(String connectionID, Destination channelDestination) {
		super(connectionID, channelDestination);
	}

	
	/**
	 * Antwort auf Login-Wunsch
	 * @param isOk Info ob Login erfolgreich oder nicht 
	 */
	public void sendLoginAnswer(boolean isOk) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.LOGINOK);
			response.setBooleanProperty(Parameter.SUCCESS, isOk);
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Benachrichtigung über Beitritt eines Nutzers
	 * @param username Name des hinzugekommenen Nutzers
	 */
	public void sendUserLogin(String username) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLIENTLOGIN);
			response.setStringProperty(Parameter.USERNAME, username);
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Benachrichtigung über Verlassen eines Nutzers
	 * @param username Name des verlassenden Nutzers
	 */
	public void sendUserLogout(String username) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLIENTLOGOUT);
			response.setStringProperty(Parameter.USERNAME, username);
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Liste aller momentan anwesenden Nutzer 
	 * @param encodedUsernameList angefertigte Nutzerliste
	 */
	public void sendOnlineList(String encodedUsernameList) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLIENTSONLINE);
			response.setStringProperty(Parameter.CLIENTLIST, encodedUsernameList);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Liste aller momentan nutzbaren Spielfelder
	 * @param encodedFieldList angefertigte Feldliste
	 */
	public void sendFieldList(String encodedFieldList) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.AVAILABLEFIELDS);
			response.setStringProperty(Parameter.FIELDLIST, encodedFieldList);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Versand einer Liste aller momentan offenen Spiele 
	 */
	public void sendOpenGamesList(Map<String, Integer> games) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.OPENGAMES);
			for (Map.Entry<String, Integer> game : games.entrySet()) {
				response.setIntProperty(game.getKey(), game.getValue());
			}
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Bestätigung zu einem gewünschten Spielbeitritt 
	 * @param clientreceivefrom Topicname, auf den der Client lauschen soll
	 * @param clientsendto Queuename, auf dem der Server lauscht
	 * @param roomName Name des Spielraums zur GUI-Anzeige
	 */
	public void sendGameJoinConfirmation(String clientreceivefrom, String clientsendto,
			   							 String roomName, String fieldName) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.YOURGAME);

			response.setBooleanProperty(Parameter.SUCCESS, true);
			response.setStringProperty(Parameter.CLIENT_RCV_FROM, clientreceivefrom);
			response.setStringProperty(Parameter.CLIENT_SND_TO, clientsendto);
			response.setStringProperty(Parameter.ROOMNAME, roomName);
			response.setStringProperty(Parameter.GAMEFIELDNAME, fieldName);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Verweigerung zu einem gewünschten Spielbeitritt 
	 */
	public void sendGameJoinDenial() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.YOURGAME);

			response.setBooleanProperty(Parameter.SUCCESS, false);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Versand einer Liste aller momentan offenen Editoren
	 */
	public void sendOpenEditorsList(Map<String, Integer> editors) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.OPENEDITORS);
			for (Map.Entry<String, Integer> editor : editors.entrySet()) {
				response.setIntProperty(editor.getKey(), editor.getValue());
			}
			producer.send(response);
			
			//Debugging: printProducerChannel();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Bestätigung zu einem gewünschten Editorbeitritt 
	 * @param clientreceivefrom Topicname, auf den der Client lauschen soll
	 * @param clientsendto Queuename, auf dem der Server lauscht
	 */
	public void sendEditorJoinConfirmation(String clientreceivefrom, String clientsendto,
										   String roomName, String fieldName) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.YOUREDITOR);

			response.setBooleanProperty(Parameter.SUCCESS, true);
			response.setStringProperty(Parameter.CLIENT_RCV_FROM, clientreceivefrom);
			response.setStringProperty(Parameter.CLIENT_SND_TO, clientsendto);
			response.setStringProperty(Parameter.ROOMNAME, roomName);
			response.setStringProperty(Parameter.GAMEFIELDNAME, fieldName);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Verweigerung zu einem gewünschten Editorbeitritt 
	 */
	public void sendEditorJoinDenial() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.YOUREDITOR);

			response.setBooleanProperty(Parameter.SUCCESS, false);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Versand einer Benachrichtigung eines Nichtmehrzugriffs auf einen Spielraum
	 * @param gameHandlerID Bezeichnung des betreffenden Spielraums
	 */
	public void sendClosingGame(String gameHandlerID) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLOSINGGAME);
			response.setStringProperty(Parameter.ROOM_ID, gameHandlerID);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Versand einer Benachrichtigung eines Nichtmehrzugriffs auf einen Editierraum
	 * @param editorHandlerID Bezeichnung des betreffenden Editierraums
	 */
	public void sendClosingEditor(String editorHandlerID) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLOSINGEDITOR);
			response.setStringProperty(Parameter.ROOM_ID, editorHandlerID);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
