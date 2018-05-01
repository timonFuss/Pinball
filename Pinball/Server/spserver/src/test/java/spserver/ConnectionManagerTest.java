package spserver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import communication.ConnectionManager;

//Kommentar der voll sinnvoll ist
public class ConnectionManagerTest {

	final String CONNECTION_ID1 = "connection1";
	final String CONNECTION_ID2 = "connection2";
	final String CONNECTION_ID3 = "connection3";
	
	@After
	public void clean(){
		ConnectionManager.getInstance().resetSingletonforUnitTest();
	}
	
	@Test
	public void createConnectionCreatesSession() {
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
		
		Assert.assertNotNull(ConnectionManager.getInstance().getSession(CONNECTION_ID1));
		Assert.assertEquals(1, ConnectionManager.getInstance().getConnectionMap().size());
	}
	
	@Test
	public void createSeveralConnectionsWorks() {
		Assert.assertEquals(0, ConnectionManager.getInstance().getConnectionMap().size());
		
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
		ConnectionManager.getInstance().createConnection(CONNECTION_ID2);
		ConnectionManager.getInstance().createConnection(CONNECTION_ID3);
		
		Assert.assertNotNull(ConnectionManager.getInstance().getSession(CONNECTION_ID1));
		Assert.assertNotNull(ConnectionManager.getInstance().getSession(CONNECTION_ID2));
		Assert.assertNotNull(ConnectionManager.getInstance().getSession(CONNECTION_ID3));
		
		Assert.assertEquals(3, ConnectionManager.getInstance().getConnectionMap().size());
	}
	
	@Test
	public void removeConnectionWorks() {
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
		ConnectionManager.getInstance().removeConnection(CONNECTION_ID1);
		
		Assert.assertEquals(0, ConnectionManager.getInstance().getConnectionMap().size());
	}
	
	@Test
	public void removeSeveralConnectionsWorks() {
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
		ConnectionManager.getInstance().createConnection(CONNECTION_ID2);
		ConnectionManager.getInstance().createConnection(CONNECTION_ID3);
		
		ConnectionManager.getInstance().removeConnection(CONNECTION_ID1);
		ConnectionManager.getInstance().removeConnection(CONNECTION_ID2);
		
		Assert.assertEquals(1, ConnectionManager.getInstance().getConnectionMap().size());
		Assert.assertNotNull(ConnectionManager.getInstance().getSession(CONNECTION_ID3));
	}
	
	@Test(expected=AssertionError.class)
	public void getNotExistingConnectionDoesntWork(){
		ConnectionManager.getInstance().getSession(CONNECTION_ID1);
	}
	
	@Test(expected=AssertionError.class)
	public void startNotExistingConnectionDoesntWork(){
		ConnectionManager.getInstance().startConnection(CONNECTION_ID1);
	}
	
	@Test(expected=AssertionError.class)
	public void removeNotExistingConnectionDoesntWork(){
		ConnectionManager.getInstance().removeConnection(CONNECTION_ID1);
	}
	
	@Test(expected=AssertionError.class)
	public void recreateConnectionDoesntWork(){
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
		ConnectionManager.getInstance().createConnection(CONNECTION_ID1);
	}

}
