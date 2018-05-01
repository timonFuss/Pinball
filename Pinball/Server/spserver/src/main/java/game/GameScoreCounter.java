package game;

import java.util.HashMap;
import java.util.Map;

import app.ServerLog;
import communication.GameProducer;

/**
 * Verteilt die Punkte an den richtigen Spieler
 * @author smerb001
 *
 */
public class GameScoreCounter {
	
	private class PlayerNameScore{
		String name;
		int score;
		
		public PlayerNameScore(String name, int score){
			this.name = name;
			this.score = score;
		}
	}
	
	GameProducer gameProducer;
	private final int STARTPOINTS = 100;
	
	private Map<String, PlayerNameScore> playerScoreMap;
	
	public GameScoreCounter(Map<String, String> players, GameProducer gameProducer){
		playerScoreMap = new HashMap<String, PlayerNameScore>();
		for (String playerID : players.keySet()){
			playerScoreMap.put(playerID, new PlayerNameScore(players.get(playerID), STARTPOINTS));
		}
		this.gameProducer = gameProducer;
	}
	
	public void addPoints(String playerID, int points){
		if (!playerScoreMap.containsKey(playerID)){
			ServerLog.logError("Dieser Player ist nicht vorhanden!");
			return;
		}		
		playerScoreMap.get(playerID).score += points;
		gameProducer.sendNewPlayerScore(playerScoreMap.get(playerID).name, playerScoreMap.get(playerID).score);
	}
	
	public Map<String, Integer> getPlayerScoreMap(){
		Map<String, Integer> scoremap = new HashMap<String, Integer>();
		for(String playerName: playerScoreMap.keySet()){
				scoremap.put(playerScoreMap.get(playerName).name , playerScoreMap.get(playerName).score);
		}
		
		return scoremap;
	}

	
}



