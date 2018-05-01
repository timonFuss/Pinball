package communication;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Abstrakte Oberklasse fuer alle Nachrichten-Sendeklassen
 * in der Server-Client-Kommunikation.
 * Producer senden ueber Queues oder Topics, welche beide durch eine
 * Destination definiert sind (und somit indirekt das "Ziel" der Nachricht).
 * 
 * @author dalle001
 *
 */
public abstract class Producer {

	protected Session session;
	protected MessageProducer producer; // ActiveMQ-Producer-Objekt für Versand	

	/**
	 * (Super-)Konstruktor, welcher eine Identifikation des Ziels braucht,
	 * das die produzierten Nachrichten erhalten soll.
	 * Destination ist eine Art Zeiger/Adresse auf eine Queue bzw. ein Topic.
	 * Mit anderen Worten:
	 * Der Producer wird auf ein anzugebendes Sendeziel konfiguriert.
	 * @param channelName Name der Queue oder des Topics fuer diesen Producer
	 */

	public Producer(String connectionID, Destination channelDestination){		
			init(connectionID, channelDestination);
	}
	
	public Producer(String connectionID, String channelName){		
		session = ConnectionManager.getInstance().getSession(connectionID);
		try {
			init(connectionID, session.createTopic(channelName));
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}
	
	public void init(String connectionID, Destination channelDestination){
		session = ConnectionManager.getInstance().getSession(connectionID);

		try {
			// Producer wird erstellt mit Queue- oder Topic-Destination (Topic)
			producer = session.createProducer(channelDestination);
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}
	
	public String getDestinationChannelName() throws JMSException{
		return producer.getDestination().toString();
	}
}
