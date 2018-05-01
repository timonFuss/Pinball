package communication;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;


public class EditorProducer extends Producer {

	public EditorProducer(String connectionID, String channelName) {
		super(connectionID, channelName);
	}

	/**
	 * @return session, mit der dieser Producer erzeugt wurde
	 */
	public Session getSession(){
		return session;
	}
	
	/**
	 * Versand der Information, dass ein Element neu gesetzt wurde
	 * @param id id des neuen Elements
	 * @param posX x-Position des neuen Elements
	 * @param posY y-Position des neuen Elements
	 * @param elementType Element-Typ des neuen Elements
	 */
	public void elementSet(int id, float posX, float posY, String elementType) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.ELEMENTSET);
			response.setIntProperty(Parameter.ID, id);
			response.setFloatProperty(Parameter.POSX, posX);
			response.setFloatProperty(Parameter.POSY, posY);
			response.setStringProperty(Parameter.ELEMENTTYPE, elementType);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass ein Element verschoben wurde
	 * @param id id des verschobenen Elements
	 * @param posX neue x-Position des neuen Elements
	 * @param posY neue y-Position des neuen Elements
	 */
	public void elementMoved(int id, float posX, float posY) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.ELEMENTMOVED);
			response.setIntProperty(Parameter.ID, id);
			response.setFloatProperty(Parameter.POSX, posX);
			response.setFloatProperty(Parameter.POSY, posY);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass ein Element gelöscht wurde
	 * @param id id des gelöschten Elements
	 */
	public void elementDeleted(int id) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.ELEMENTDELETED);
			response.setIntProperty(Parameter.ID, id);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass der Name der Map geändert wurde
	 * @param id id des gelöschten Elements
	 */
	public void nameChanged(String newName) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.NAMECHANGED);
			response.setStringProperty(Parameter.GAMEFIELDNAME, newName);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass gespeichert wurde
	 */
	public void fieldSaved() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.FIELDSAVED);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand einer Liste aller Nutzer, die sich im Editor-Raum befinden
	 * @param encodedUsernameList angefertigte Mit-Editor-Nutzerliste
	 */
	public void sendEditorUserList(String encodedUsernameList) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.CLIENTSINGAME);
			response.setStringProperty(Parameter.CLIENTLIST, encodedUsernameList);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * versendet eine außerhalb des EditorConsumers erstellte TextMessage
	 * @param tm zu versendende TextMessage
	 */
	public void sendGamePacket(TextMessage tm) {
		try {
			producer.send(tm);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
