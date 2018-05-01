package field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import app.ServerLog;
import communication.GameProducer;
import datatypes.Vector2;
import enums.ElementType;
import game.GameScoreCounter;
import gameelements.Ball;
import gameelements.Flipper;
import gameelements.GameElement;
import gameelements.GameUpdateElement;
import gameelements.Goal;
import gameelements.GoalArea;
import gameelements.Libero;
import gameelements.MotionlessElement;
import gameelements.Plunger;

/**
 * @author mbeus001
 * beinhaltet alle Elemente eines Spielfeldes, verwaltet statische Elemente in Liste staticElements 
 * und veränderliche Elemente in Liste agileElements
 *
 */
public class GameField extends Field {
	
	private class PlayerGameElements{
		Libero libero;
		Flipper leftFlipper;
		Flipper rightFlipper;
		@SuppressWarnings("unused")
		GoalArea goalArea;
	}
	
	private GameProducer gameProducer;
	
	private List<GameElement> staticElements;
	private List<GameUpdateElement> agileElements;
	public List<Libero> liberos;
	private Ball ball;

	public List<String> playerIDs;
	private Map<String, PlayerGameElements> playerGameElementsMap;
	
	
	public GameField(String fieldName, GameProducer gameProducer){
		this.gameProducer = gameProducer;
		
		this.fieldName = fieldName;
		
		// Elementverwaltung
		agileElements = new LinkedList<GameUpdateElement>();
		staticElements = new ArrayList<GameElement>();
		readField();
		liberos = new ArrayList<Libero>();
		
		playerGameElementsMap = new HashMap<String, PlayerGameElements>();
	}
	
	
	public void injectGameScoreCounter(GameScoreCounter gameScoreCounter){
		ball.injectGameScoreCounter(gameScoreCounter);
	}
	
	public void loadEssentialElements(List<String> playerIDs){
		// lese PlayerObjects-Datei ein und fülle die Elemente in das playerObjects-Dictionary
		// erzeuge aber keine Objekte für Spieler die es nicht gibt!
		// adde flipper zu flippers-Liste!
		this.playerIDs = playerIDs;
		// Map mit leeren PlayerObjects bestücken, die später befüllt werden
		for(String playerID : playerIDs){
			playerGameElementsMap.put(playerID, new PlayerGameElements());
		}
		
		readEssentialElements();
	}
	
	@Override
	protected void addFieldElementToCollection(GameElement ele) {
		staticElements.add(ele);
	}

	@Override
	protected void addPlayerElementToCollection(String[] cells) {
		float posX = Float.parseFloat(cells[1].replace(",", "."));
		float posY = Float.parseFloat(cells[2].replace(",", "."));
		
		//Sonderfall für Plunger und Ball
		if(cells[0].contains("BALL")){
			ball = new Ball(new Vector2(posX,posY), WIDTH, HEIGHT, agileElements, staticElements, gameProducer);
			agileElements.add(ball);
			return;
		}
		else if(cells[0].contains("PLUNGER")){
			Plunger plunger = new Plunger(new Vector2(posX, posY), ball, gameProducer);
			agileElements.add(plunger);	
			return;
		}
		
		int playerNumber = 0;
		if(cells[0].contains("1")) playerNumber = 1;
		else if(cells[0].contains("2")) playerNumber = 2;
		else if(cells[0].contains("3")) playerNumber = 3;
		else if(cells[0].contains("4")) playerNumber = 4;
		
		// wenn ein Element für einen Spieler ausgelesen wird, der nicht mitmacht weil weniger Leute spielen
		if (playerNumber > playerIDs.size())
			return;
		// hole das aktPlayerGameElements-Objekt für die aktuell ausgelesene Nummer, Achtung, Player zählen ab 1, ArrayList zählt ab 0, also playerNumber-1!
		PlayerGameElements aktPlayerGameElements = playerGameElementsMap.get(playerIDs.get(playerNumber-1));
		
		if(cells[0].contains("FLIPPER")){

			Flipper flipper = null;
			if(cells[0].contains("LEFT")){
				flipper = new Flipper(ElementType.convert(cells[0]), new Vector2(posX, posY), playerIDs.get(playerNumber-1),true, gameProducer);
				
				aktPlayerGameElements.leftFlipper = flipper;
			} else if(cells[0].contains("RIGHT")) {
				flipper = new Flipper(ElementType.convert(cells[0]), new Vector2(posX, posY), playerIDs.get(playerNumber-1),false, gameProducer);
				
				aktPlayerGameElements.rightFlipper = flipper;
			}
			agileElements.add(flipper);
		}
		else if(cells[0].contains("LIBERO")){
			Libero libero = new Libero(ElementType.convert(cells[0]), new Vector2(posX, posY), playerIDs.get(playerNumber-1),WIDTH,HEIGHT);
			libero.setBouncyness(10);
			
			agileElements.add(libero);

			liberos.add(libero);
			aktPlayerGameElements.libero = libero;
		}
        //goalpost				
		else if(cells[0].contains("POST")){
			
			MotionlessElement motionlessElement =new MotionlessElement(ElementType.convert(cells[0]), new Vector2(posX, posY));
			staticElements.add(motionlessElement);
			
		}
		//goal
		else if(cells[0].contains("GOAL_")){
			Goal goal= new Goal(ElementType.convert(cells[0]), new Vector2(posX, posY), playerIDs.get(playerNumber-1));
			staticElements.add(goal);
		}
		//Goalarea
		else if(cells[0].contains("AREA_")){
			GoalArea goalarea= new GoalArea(ElementType.convert(cells[0]), new Vector2(posX, posY));
			staticElements.add(goalarea);

			for (Libero liber : liberos) {
				liber.setGoalArea(goalarea);
			}
		}
		
	}


	public void update(){
		for (GameUpdateElement agileElement : agileElements){
			agileElement.Update();
		}
	}
	
	/**
	 * @param List<GameElement>
	 * @return void
	 */
	public void ausgabe( List<GameElement> spielfeld)
	{
		for(GameElement ele :spielfeld ){
			System.out.println(ele.toString());
		}
			
	}

	/**
	 * 
	 * @return staticElements
	 */
	public List<GameElement> getCollidingElements() {
		return staticElements;
	}

	/**
	 * 
	 * @return width
	 */
	public int getWidth() {
		return WIDTH;
	}
	
	/**
	 * @return height
	 */
	public int getHeight() {
		return HEIGHT;
	}

	/**
	 * @return agileElements
	 */
	public List<GameUpdateElement> getAgileElements() {
		return agileElements;
	}
	
	/**
	 * @return ball
	 */
	public Ball getBall(){
		return ball;
	}
	
	public Flipper getRightFlipper(String ID){
		if (!playerGameElementsMap.containsKey(ID)){
			ServerLog.logError("Dieser Spieler ist nicht hinterlegt!");
			return null;
		}
		return playerGameElementsMap.get(ID).rightFlipper;
	}
	
	
	public Flipper getLeftFlipper(String ID){
		if (!playerGameElementsMap.containsKey(ID)){
			ServerLog.logError("Dieser Spieler ist nicht hinterlegt!");
			return null;
		}
		return playerGameElementsMap.get(ID).leftFlipper;
	}
	
	
	public Libero getLibero(String ID){
		if (!playerGameElementsMap.containsKey(ID)){
			ServerLog.logError("Dieser Spieler ist nicht hinterlegt!");
			return null;
		}
		return playerGameElementsMap.get(ID).libero;
	}


}
