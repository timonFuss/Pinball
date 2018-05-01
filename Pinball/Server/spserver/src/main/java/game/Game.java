package game;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.ServerLog;
import communication.GameConsumer;
import communication.GameProducer;
import field.GameField;
import handler.GameFinisher;
/**
 * 
 * @author mbeus001
 *
 */
public class Game implements Runnable {
	
	private Thread t;
	private GameProducer gameProducer;
	private GameConsumer gameConsumer;
	@SuppressWarnings("unused")
	private InputHandler inputHandler;
	private GameField gameField;
	private GameFieldEncoder gameFieldEncoder;
	private GameFinisher gameFinisher;
	private GameScoreCounter gameScoreCounter;
	
	private final int MAXFRAMES = 120; // so oft wird pro Sekunde die Physik berechnet
	private long frameStartTime = 0;
	private long usedTime;
	private final long TIMEFORFRAME = 1000/MAXFRAMES; // soviele Millisekunden ist zeit für jedes Frame
	private int frame = 0;
	
	private boolean running;
	private final String CONNECTIONID;
	
	private final int SENDEVERYXFRAME = 1; // alle wieviel Frames soll ein updatePaket geschickt werden?
	
	private int gametimeSeconds;
	private long gameTime;

	/**
	 * @param gameID
	 */
	public Game(String connectionid, GameConsumer gc, GameProducer gp, GameField gameField, GameFieldEncoder gameFieldEncoder, Map<String, String> players, int time, GameFinisher gameFinisher) {
		// Spielidentifikation
		this.CONNECTIONID = connectionid;
		
		// eigentliches Spiel
		this.gameField = gameField;		
		this.gameConsumer = gc;
		this.gameProducer = gp;
		this.gameFieldEncoder = gameFieldEncoder;
		this.gametimeSeconds = time;
		this.gameTime = time * 1000;
		
		this.gameFinisher = gameFinisher;
		gameScoreCounter = new GameScoreCounter(players, gameProducer);		
		inputHandler = new InputHandler(gameField, gameConsumer);
		gameField.injectGameScoreCounter(gameScoreCounter);

	}
	
	/**
	 * initialisiert den Thread
	 */
	public void start () {
		ServerLog.logMessage("[" + CONNECTIONID + "] Gamethread gestartet");
		this.t = new Thread (this, CONNECTIONID);
		this.running = true;
		this.t.start();
	}

	/**
	 * hier läuft der MainLoop des Spiels, der PhysikCalculator wird geupdated, die verbleibende Zeit bis das nächste Frame gerendert wird 
	 * wird gesleept, und alle x Frames wird ein GameupdatePacket vom GameFieldEncoder verschickt
	 */
	@Override
	public void run() {
		try {
			startTimer();
			while (this.running){
				frameStartTime = System.nanoTime();
				gameField.update();
				usedTime = (System.nanoTime() - frameStartTime) / 1000000;
				if(TIMEFORFRAME-usedTime>0){
					Thread.sleep(TIMEFORFRAME-usedTime);
				}

				if (frame%SENDEVERYXFRAME == 0){
					gameFieldEncoder.sendGamePacket();
				}
				
				frame++;
				if (frame>MAXFRAMES) frame = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.gameFinisher.finishGame();
			ServerLog.logError("[" + CONNECTIONID + "] unterbrochen.");
		}
	}
	
	/**
	 * laeuft so lange, wie die Spielzeit festgelegt wurde und ruft dann finishGame im gameFinisher auf
	 */
	private void startTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				gameFinisher.finishGame();
			}
		}, gameTime);
	}

	public void stop() {
		this.running = false;
		ServerLog.logMessage("[" + CONNECTIONID + "] Gamethtread beendet");
	}
	
	
	/* ---- Einfache Getter ---- */
	
	/**
	 * Unit-Test-Getter.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Gibt die angedachte gesamte Spieldauer dieser Spielrunde wieder.
	 * @return Maximalspielzeit in Sekunden
	 */
	public int getGameDurationInSeconds() {
		return gametimeSeconds;
	}
	
	/* ---- Unit-Test-Getter ---- */
	
	/**
	 * Unit-Test-Setter.
	 */
	public void setGameTime(long t) {
		gameTime = t;
	}
	
	public Map<String, Integer> getPlayerScoreMap(){
		return gameScoreCounter.getPlayerScoreMap();
	}
}
