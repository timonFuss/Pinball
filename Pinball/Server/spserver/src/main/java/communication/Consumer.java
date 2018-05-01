package communication;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Abstrakte Oberklasse fuer alle Nachrichten-Empfangsklassen
 * in der Server-Client-Kommunikation.
 * Alle Consumer auf Serverseite sind als Queues vorgesehen,
 * weshalb bereits in der Oberklasse eine passende Queue erstellt werden kann.
 *  
 * @author dalle001
 * 
 */
public abstract class Consumer implements MessageListener {
	
	MessageConsumer consumer;  // ActiveMQ-Consumer-Objekt für Message-Empfang
	
	/**
	 * (Super-)Konstruktor, welcher den Queue-Namen zur Identifikation braucht.
	 * Dieser Name sollte allen Clienten bekannt sein,
	 * damit diese hierrueber den Server erreichen können. 
	 * @param queueName	Name der Server-Queue
	 */
	public Consumer(String connectionID, String queueName){
		Session session = ConnectionManager.getInstance().getSession(connectionID);
		try {
			// Erstellung bereits hier, da alle Unterklassen nur Queues nutzen
			consumer = session.createConsumer(session.createQueue(queueName));
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Filtert aus einer Destination die Bezeichnung der
	 * zugehoerigen Queue bzw. des zugehoerigen Topics.
	 * @param destination "Zeiger"/Adresse auf Queue bzw. Topic
	 * @return  String-Name von Queue bzw. Topic
	 */
	public String getDestinationID(Destination destination){
		String destinationId = null;
		try {
			destinationId = ((Queue)destination).getQueueName();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return destinationId;
	}
	
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public abstract void onMessage(Message message);
	
}
