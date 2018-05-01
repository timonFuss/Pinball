package events;

import java.util.EventListener;

public interface GameConsumerLobbyListener extends EventListener{
	
	void playerJoinedFired(UserJoinedEvent e);
	
	void playerLeftFired(UserLeftEvent e);
	
	void playerIsReadyToPlayFired(PlayerIsReadyToPlayEvent e);
	
	void playerFinishedLoadingFired(PlayerFinishedLoadingEvent e);
}

