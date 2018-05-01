package game;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import communication.Command;
import communication.GameProducer;
import communication.Parameter;
import field.GameField;
import gameelements.GameElement;
import gameelements.GameUpdateElement;

/**
 * @author mbeus001
 * hat Zugriff auf das Gamefield und kann nach dem definierten Protokoll alle veränderlichen Elemente und ihre Werte
 * in eine TextMessage kodieren
 */
public class GameFieldEncoder {
	
	private GameField gameField;
	private GameProducer gameProducer;
	private Session session;
	
	/**
	 * @param gameField
	 * @param gameProducer
	 */
	public GameFieldEncoder(GameField gameField, GameProducer gameProducer) {
		this.gameField = gameField;
		this.gameProducer = gameProducer;
		session = gameProducer.getSession();
		
		
	}
	
	/**
	 * erzeugt eine TextMessage, die alle Werte der veränderlichen Elemente eines Spielfeldes enthält
	 * und sagt dem producer, er soll die Message verschicken
	 */
	public void sendGamePacket(){
		try {
			TextMessage tm = session.createTextMessage(Command.GAMEUPDATEPACKET);
			for(GameUpdateElement gameElement : gameField.getAgileElements()){
				int elementID = gameElement.getId();
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSX, gameElement.getPosition().x);
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSY, gameElement.getPosition().y);
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.ROT, gameElement.getRotation());
			}
			gameProducer.sendGamePacket(tm);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendGameLoadPacket(){
		try {
			TextMessage tm = session.createTextMessage(Command.GAMEFIELDLOADPACKET);
			for(GameElement gameElement : gameField.getCollidingElements()){
				int elementID = gameElement.getId();
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSX, gameElement.getPosition().x);
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSY, gameElement.getPosition().y);
				tm.setStringProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.ELEMENTTYPE, gameElement.getElementType().toString());
				tm.setBooleanProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.CHANGEABLE, false);
			}
			for(GameElement gameElement : gameField.getAgileElements()){
				int elementID = gameElement.getId();
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSX, gameElement.getPosition().x);
				tm.setFloatProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.POSY, gameElement.getPosition().y);
				tm.setStringProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.ELEMENTTYPE, gameElement.getElementType().toString());
				tm.setBooleanProperty(elementID + Parameter.IDTYPESEPERATOR + Parameter.CHANGEABLE, true);
			}
			gameProducer.sendGamePacket(tm);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
