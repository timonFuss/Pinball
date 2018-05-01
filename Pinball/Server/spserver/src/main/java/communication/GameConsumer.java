package communication;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.swing.event.EventListenerList;

import events.AddForceEvent;
import events.ButtonDownEvent;
import events.ButtonLeftEvent;
import events.ButtonRightEvent;
import events.ButtonUpEvent;
import events.FlipperLeftEvent;
import events.FlipperRightEvent;
import events.GameConsumerGameListener;
import events.GameConsumerLobbyListener;
import events.PlayerFinishedLoadingEvent;
import events.PlayerIsReadyToPlayEvent;
import events.UserJoinedEvent;
import events.UserLeftEvent;

/**
 * Konkreter Consumer, welcher alle spielbezogenen Nachrichten empfängt und
 * abhängig vom erhaltenen Befehl die Inhalte weiterdelegiert.
 * 
 * @author dalle001
 *
 */
public class GameConsumer extends Consumer {
	
	private EventListenerList gameConsumerLobbyListeners;
	private EventListenerList gameConsumerGameListeners;
	
	/**
	 * Konstruktor, der die Queue für spielbezogene Nachrichten von Clients
	 * anlegt und diese anzapft.
	 * @param connectionID Hieran wird die Verarbeitung erhaltener Befehle delegiert
	 * @param queueName Name des Nachrichtenkanals, dessen Nachrichten man erhält
	 */
	public GameConsumer(String connectionID, String queueName) {
		super(connectionID, queueName);
		gameConsumerLobbyListeners = new EventListenerList();
		gameConsumerGameListeners = new EventListenerList();
	}
	
	
	/* (non-Javadoc)
	 * @see communication.Consumer#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		if (!(message instanceof TextMessage)) {
			throw new AssertionError("Im GameConsumer wird eine TextMessage erwartet!");
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
			switch (command){
				/* Lobby */
				case Command.COMMU_JOINED: {
					notifyPlayerJoinedLobby(new UserJoinedEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				case Command.COMMU_LEFT: {
					notifyPlayerLeftLobby(new UserLeftEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				case Command.READYTOPLAY: {
					notifyPlayerIsReadyToPlay(new PlayerIsReadyToPlayEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				case Command.LOADINGCOMPLETE: {
					notifyPlayerFinishedLoading(new PlayerFinishedLoadingEvent(this, getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				/* Game */
				case Command.DEBUG_FORCEVECTOR: {
					notifyAddForceFired(new AddForceEvent(this, textMessage.getFloatProperty(Parameter.POSX), textMessage.getFloatProperty(Parameter.POSY)));
				} break;
								
				case Command.BUTTON_UP: {
					notifyButtonUp(new ButtonUpEvent(this, textMessage.getBooleanProperty(Parameter.BUTTON_UP), getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				case Command.BUTTON_DOWN: {
					notifyButtonDown(new ButtonDownEvent(this, textMessage.getBooleanProperty(Parameter.BUTTON_DOWN), getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				case Command.BUTTON_LEFT: {
					notifyButtonLeft(new ButtonLeftEvent(this, textMessage.getBooleanProperty(Parameter.BUTTON_LEFT), getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				case Command.BUTTON_RIGHT: {
					notifyButtonRight(new ButtonRightEvent(this, textMessage.getBooleanProperty(Parameter.BUTTON_RIGHT), getDestinationID(textMessage.getJMSReplyTo())));
				} break;
				
				case Command.FLIPPER_LEFT: {
					notifyFlipperLeft(new FlipperLeftEvent(this, textMessage.getBooleanProperty(Parameter.FLIPPER_LEFT) , getDestinationID(textMessage.getJMSReplyTo()  )));
				} break;
				
				case Command.FLIPPER_RIGHT: {
					notifyFlipperRight(new FlipperRightEvent(this, textMessage.getBooleanProperty(Parameter.FLIPPER_RIGHT) , getDestinationID(textMessage.getJMSReplyTo()  )));
					//notifyFlipperRight(new FlipperRightEvent(this, textMessage.getStringProperty(Parameter.FLIPPER_RIGHT) , textMessage.getStringProperty(Parameter.FLIPPER_RIGHT_DOWN), getDestinationID(textMessage.getJMSReplyTo() )));
				} break;
				
				default: throw new AssertionError("Unerwarteter Befehl: " + command);
			}
		} catch (JMSException e) {
			throw new AssertionError("Fehler bei Dekodierung der erhaltenen Message");
		}
	}
	
	
	/* --- Listenergeraffel LobbyListener --- */
	public void addGameConsumerLobbyListener(GameConsumerLobbyListener gameConsumerLobbyListener){
		gameConsumerLobbyListeners.add(GameConsumerLobbyListener.class, gameConsumerLobbyListener);
	}
	public void removeGameConsumerLobbyListener(GameConsumerLobbyListener gameConsumerLobbyListener){
		gameConsumerLobbyListeners.remove(GameConsumerLobbyListener.class, gameConsumerLobbyListener);
	}
	private void notifyPlayerJoinedLobby(UserJoinedEvent e) {
		for (GameConsumerLobbyListener gcll : gameConsumerLobbyListeners.getListeners(GameConsumerLobbyListener.class)){
			gcll.playerJoinedFired(e);
		}
	}
	private void notifyPlayerLeftLobby(UserLeftEvent e) {
		for (GameConsumerLobbyListener gcll : gameConsumerLobbyListeners.getListeners(GameConsumerLobbyListener.class)){
			gcll.playerLeftFired(e);
		}
	}
	private void notifyPlayerIsReadyToPlay(PlayerIsReadyToPlayEvent e) {
		for (GameConsumerLobbyListener gcll : gameConsumerLobbyListeners.getListeners(GameConsumerLobbyListener.class)){
			gcll.playerIsReadyToPlayFired(e);
		}
	}
	private void notifyPlayerFinishedLoading(PlayerFinishedLoadingEvent e) {
		for (GameConsumerLobbyListener gcll : gameConsumerLobbyListeners.getListeners(GameConsumerLobbyListener.class)){
			gcll.playerFinishedLoadingFired(e);
		}
	}
	
	/* --- Listenergeraffel GameListener --- */
	public void addGameConsumerGameListener(GameConsumerGameListener gameConsumerGameListener){
		gameConsumerGameListeners.add(GameConsumerGameListener.class, gameConsumerGameListener);
	}
	public void removeGameConsumerGameListener(GameConsumerGameListener gameConsumerGameListener){
		gameConsumerGameListeners.remove(GameConsumerGameListener.class, gameConsumerGameListener);
	}
	private void notifyAddForceFired(AddForceEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.addForceFired(e);
		}
	}
		
	private void notifyButtonUp(ButtonUpEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.buttonUpFired(e);
		}
	}
	
	private void notifyButtonDown(ButtonDownEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.buttonDownFired(e);
		}
	}
	
	private void notifyButtonLeft(ButtonLeftEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.buttonLeftFired(e);
		}
	}
	
	private void notifyButtonRight(ButtonRightEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.buttonRightFired(e);
		}
	}
	
	private void notifyFlipperLeft(FlipperLeftEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.flipperLeftFired(e);
		}
	}
	
	private void notifyFlipperRight(FlipperRightEvent e) {
		for (GameConsumerGameListener gcgl : gameConsumerGameListeners.getListeners(GameConsumerGameListener.class)){
			gcgl.flipperRightFired(e);
		}
	}
}