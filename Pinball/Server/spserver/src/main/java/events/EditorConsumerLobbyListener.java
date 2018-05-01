package events;

import java.util.EventListener;

public interface EditorConsumerLobbyListener extends EventListener {

	void editorUserJoinedFired(UserJoinedEvent e);
	
	void editorUserLeftFired(UserLeftEvent e);
}
