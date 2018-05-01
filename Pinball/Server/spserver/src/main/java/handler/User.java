package handler;

import lobby.ClientState;

/**
 * @author mbeus001
 *
 */
public interface User {

	/* ---- Identifikation ---- */
	public String getUsername();
	public String getDestinationID();

	
	/* ---- Allgemein ---- */
	public ClientState getState();
	public void setState(ClientState cs);
	
	
	/* ---- Editor ---- */
	public EditorHandler getEditorHandler();
	public void setEditorHandler(EditorHandler gameHandler);
	
	
	/* ---- Game ---- */
	public GameHandler getGameHandler();
	public void setGameHandler(GameHandler gameHandler);
	
	public void setWaitingToPlay(boolean now);
	public boolean isWaitingToPlay();
	public void setPlaying(boolean now);
	public boolean isPlaying();
	
	public void setPlayerNumber(int number);
	public int getPlayerNumber();
	public void resetPlayerNumber();
	
}
