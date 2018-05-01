package communication;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;


public class GameProducer extends Producer {
	
	/**
	 *  connectionID
	 * @param connectionID: zum Zugriff auf die zugehörige Connection im ConnectionManager
	 * @param channelName: Name des Topics auf das gesendet wird
	 */
	public GameProducer(String connectionID, String channelName) {
		super(connectionID, channelName); //erzeuge tcp-Session und Producer
	}
	
	/**
	 * @return session, mit der dieser Producer erzeugt wurde
	 */
	public Session getSession(){
		return session;
	}

	
	/**
	 * Versand einer Liste aller Nutzer, die sich in einer Gamelobby befinden
	 * @param encodedUsernameList angefertigte Mitspieler-Nutzerliste
	 */
	public void sendGameLobbyList(String encodedUsernameList) {
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
	 * Versand der Nutzer des Spielraums samt Bereitschaftsempfindung
	 * @param lobbymap Map: Spielername->Bereitschaftsstatus (true/false) 
	 */
	public void sendUserLobbyMap(Map<String, Boolean> lobbymap) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.PLAYERREADYTOPLAY);
			
			for (Map.Entry<String, Boolean> user : lobbymap.entrySet()) {
				response.setBooleanProperty(user.getKey(), user.getValue());
			}
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendNewPlayerScore(String playerName, int newScore){
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.POINTS);
			response.setStringProperty(Parameter.USERNAME, playerName);
			response.setIntProperty(Parameter.POINTS, newScore);
			producer.send(response);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass der Ball mit einem Element kollidiert ist
	 * @param id id des getroffenen Elements
	 */
	public void sendElementHit(int id) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.ELEMENTHIT);
			response.setIntProperty(Parameter.ID, id);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass der Ball mit einer Wand kollidiert ist
	 */
	public void sendWallHit() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.WALLHIT);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass sich ein Flipper bewegt hat
	 */
	public void sendFlipperMove(boolean up) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.FLIPPERMOVE);
			response.setBooleanProperty(Parameter.UP, up);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Versand der Information, dass der Ball aus dem Plungeer geschossen wurde
	 */
	public void sendShooutOut() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.SHOOTOUT);
			producer.send(response);			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * versendet eine außerhalb des GameConsumers erstellte TextMessage
	 * @param tm zu versendende TextMessage
	 */
	public void sendGamePacket(TextMessage tm) {
		try {
			producer.send(tm);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	
	public void sendGameHighscores(Map<String, Integer> map) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.GAMEOVER);
			for (Map.Entry<String, Integer> playerScore : map.entrySet()) {
				response.setIntProperty(playerScore.getKey(), playerScore.getValue());
			}
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	

	public void sendPlayerNoMap(Map<String, Integer> playermap) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.PLAYERNUMBERLIST);
			for (Map.Entry<String, Integer> player : playermap.entrySet()) {
				response.setIntProperty(player.getKey(), player.getValue());
			}
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendNoRevanchePossible() {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.NOREVANCHE);
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendGameStarts(String gameName, int gameDuration) {
		try {
			TextMessage response = session.createTextMessage();
			response.setText(Command.GAMESTARTS);
			response.setStringProperty(Parameter.ROOMNAME, gameName);
			response.setIntProperty(Parameter.GAMEDURATION, gameDuration);
			producer.send(response);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
