package spserver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import communication.ConnectionManager;
import lobby.MainModule;

public class MainModuleTest {

	private static final String TESTCONNECTIONID = "testCOnnectionID";
	MainModule mainModule;
	
	String destinationChannelID1 = "dummyzielqueue1";
	String destinationChannelID2 = "dummyzielqueue2";
	String destinationChannelID3 = "dummyzielqueue3";
	
	Destination destinationChannel1;
	Destination destinationChannel2;
	Destination destinationChannel3;	
	
	
	@Before
	public void setup() throws JMSException{
		mainModule = new MainModule();
		ConnectionManager.getInstance().createConnection(TESTCONNECTIONID);
		Session session = ConnectionManager.getInstance().getSession(TESTCONNECTIONID);
		destinationChannel1 = session.createQueue(destinationChannelID1);
		destinationChannel2 = session.createQueue(destinationChannelID2);
		destinationChannel3 = session.createQueue(destinationChannelID3);
	}
	
	@After
	public void clean(){
		ConnectionManager.getInstance().resetSingletonforUnitTest();
	}
	
	@Test
	public void login_acceptNotUsedUsername() {
		mainModule.login(destinationChannel1, destinationChannelID1, "Wollo");
		Assert.assertEquals(1, mainModule.getOnlineClientMap().size());
		Assert.assertTrue("onlineClients beinhaltet destinationChannelID1", mainModule.getOnlineClientMap().containsKey(destinationChannelID1));
	}
	
	@Test
	public void login_dontAcceptUsedUsername() {
		mainModule.login(destinationChannel1, destinationChannelID1, "Wollo");
		mainModule.login(destinationChannel2, destinationChannelID2, "Willi");
		mainModule.login(destinationChannel3, destinationChannelID3, "Wollo");
		Assert.assertEquals(2, mainModule.getOnlineClientMap().size());
		Assert.assertTrue("onlineClients beinhaltet destinationChannelID1", mainModule.getOnlineClientMap().containsKey(destinationChannelID1));
		Assert.assertTrue("onlineClients beinhaltet destinationChannelID2", mainModule.getOnlineClientMap().containsKey(destinationChannelID2));
		Assert.assertFalse("onlineClients beinhaltet nicht destinationChannelID3", mainModule.getOnlineClientMap().containsKey(destinationChannelID3));
	}
	
	@Test
	public void login_dontAcceptSameQueue() {
		mainModule.login(destinationChannel1, destinationChannelID1, "Wollo");
		mainModule.login(destinationChannel1, destinationChannelID1, "Willi");
		Assert.assertEquals(1, mainModule.getOnlineClientMap().size());
		Assert.assertTrue("onlineClients beinhaltet destinationChannelID1", mainModule.getOnlineClientMap().containsKey(destinationChannelID1));
	}
	
	@Test
	public void logout_onlineUsername() {
		mainModule.login(destinationChannel1, destinationChannelID1, "Wollo");
		mainModule.login(destinationChannel2, destinationChannelID2, "Willi");
		mainModule.logout(destinationChannelID2);
		Assert.assertEquals(1, mainModule.getOnlineClientMap().size());
		Assert.assertTrue("onlineClients beinhaltet destinationChannelID1", mainModule.getOnlineClientMap().containsKey(destinationChannelID1));
		Assert.assertFalse("onlineClients beinhaltet nicht destinationChannelID1", mainModule.getOnlineClientMap().containsKey(destinationChannelID2));
	}
	
	@Test(expected=AssertionError.class)
	public void logout_notOnlineUsername() {
		mainModule.logout(destinationChannelID1);
	}

}
