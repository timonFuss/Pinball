package communication;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import app.ServerLog;

/**
 * Baut grundlegendes Kommunikationskonzept auf
 * @author mbeus001
 *
 */
public class ConnectionManager {
	
	//beinhaltet tcp- und udp-Connections und Sessions, die ein Spiel braucht um zu kommunizieren
	private class ConnectionandSession{
		public Connection connection;
		public Session session;
		
		ConnectionandSession(){
			try {
				connection = factory.createConnection();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				e.printStackTrace();
			}			
		}
		
		void start(){
			try {
				connection.start();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	BrokerService embeddedBroker;
	private static ConnectionManager theInstance = null;		// Singleton-Instanz
	private ActiveMQConnectionFactory factory;
	
	private Map<String, ConnectionandSession> connectionMap;

	private final String BROKERURL = "tcp://localhost:1618";

	private ConnectionManager() {
		connectionMap = new HashMap<String, ConnectionandSession>();
		
		embeddedBroker = new BrokerService();
		try {
			embeddedBroker.setUseJmx(false); // unnützes tool?
			embeddedBroker.setPersistent(false);
			// Connector setzen
			embeddedBroker.addConnector("tcp://0.0.0.0:1618");
			//List<TransportConnector> l = tcpBroker.getTransportConnectors();
			embeddedBroker.start();			

			factory = new ActiveMQConnectionFactory(BROKERURL);
			
			System.out.println("_____________________________________________________________");
			ServerLog.logMessage("BrokerService initialisiert");
			ServerLog.logMessage("Die Server-IP lautet: "+ Inet4Address.getLocalHost().getHostAddress());			

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Singleton: Es darf global nur einen einzigen BrokerManager geben.
	 * @return BrokerManager des Servers
	 */
	public static ConnectionManager getInstance() {
		if (theInstance == null) {
			theInstance = new ConnectionManager();
		}
		return theInstance;
	}
	
	/**
	 * erzeugt ein ConnectionandSession-Objekt, dass eine connection sowie die dazugehörige session enthält
	 * und speichert es in der connectionMap, mit der id als key
	 * @param id
	 */
	public void createConnection(String id){
		if (!connectionMap.containsKey(id))
			connectionMap.put(id, new ConnectionandSession());
		else
			ServerLog.logError("Es gibt schon eine Connection mit dieser ID " + id);
	}
	
	/**
	 *  startet die connection die auf diese id gemapped ist
	 * @param id
	 */
	public void startConnection(String id){
		if (!connectionMap.containsKey(id))
			ServerLog.logError("Es gibt keine Connection mit dieser ID " + id);
		connectionMap.get(id).start();
	}
	/**
	 *  gibt die Session dieser id zurück
	 * @param id
	 * @return session
	 */
	public Session getSession(String id){
		if (!connectionMap.containsKey(id))
			ServerLog.logError("Es gibt keine Connection mit dieser ID " + id);
		return connectionMap.get(id).session;
	}
	
	/**
	 * entfernt connection und session, die auf diese id gemapped sind
	 * @param id
	 */
	public void removeConnection(String id){
		if (!connectionMap.containsKey(id))
			ServerLog.logError("Es gibt keine Connection mit dieser ID " + id);
		connectionMap.remove(id);
	}
	
	/**
	 * nur für Unittest!
	 * @return connectionMap
	 */
	public Map<String, ConnectionandSession> getConnectionMap() {
		return connectionMap;
	}
	
	/**
	 * nur fuer Unit-Tests da! löscht Singleton Instanz
	 */
	public void resetSingletonforUnitTest(){
		try {
			embeddedBroker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		theInstance = null;
	}

}
