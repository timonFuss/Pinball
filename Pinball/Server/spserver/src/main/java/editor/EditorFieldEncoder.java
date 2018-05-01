package editor;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import communication.Command;
import communication.EditorProducer;
import communication.Parameter;
import field.EditorField;
import gameelements.GameElement;

/**
 * @author mbeus001
 * hat Zugriff auf das Gamefield und kann nach dem definierten Protokoll alle veränderlichen Elemente und ihre Werte
 * in eine TextMessage kodieren
 */
public class EditorFieldEncoder {
	
	private EditorField editorField;
	private EditorProducer editorProducer;
	private Session session;
	
	/**
	 * @param gameField
	 * @param gameProducer
	 */
	public EditorFieldEncoder(EditorField editorField, EditorProducer editorProducer) {
		this.editorField = editorField;
		this.editorProducer = editorProducer;
		session = editorProducer.getSession();
	}
	
	public void sendEditorLoadPacket(){
		try {
			TextMessage tm = session.createTextMessage(Command.EDITORLOADPACKET);
			for(int id : editorField.getElements().keySet()){
				GameElement gameElement = editorField.getElements().get(id);
				tm.setFloatProperty(id + Parameter.IDTYPESEPERATOR + Parameter.POSX, editorField.getElements().get(id).getPosition().x);
				tm.setFloatProperty(id + Parameter.IDTYPESEPERATOR + Parameter.POSY, gameElement.getPosition().y);
				tm.setStringProperty(id + Parameter.IDTYPESEPERATOR + Parameter.ELEMENTTYPE, gameElement.getElementType().toString());
			}
			editorProducer.sendGamePacket(tm);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
