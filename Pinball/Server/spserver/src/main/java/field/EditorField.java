package field;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.ServerLog;
import communication.EditorProducer;
import datatypes.Vector2;
import enums.ElementType;
import gameelements.Ball;
import gameelements.Flipper;
import gameelements.GameElement;
import gameelements.Goal;
import gameelements.GoalArea;
import gameelements.Libero;
import gameelements.MotionlessElement;
import gameelements.Plunger;

/**
 * @author mbeus001 beinhaltet alle Elemente eines Spielfeldes, verwaltet
 *         statische Elemente in Liste staticElements und veränderliche Elemente
 *         in Liste agileElements
 *
 */
public class EditorField extends Field {

	private EditorProducer editorProducer;

	private Map<Integer, GameElement> gameElements = new HashMap<Integer, GameElement>();
	private int id = 0;

	private final String DUMMYID = "dummyid";

	/**
	 * @param width
	 *            die Gesamtbreite des Spielfeldes
	 * @param height
	 *            die Gesamthöhe des Spielfeldes
	 */
	public EditorField(EditorProducer editorProducer, String fieldName) {
		this.editorProducer = editorProducer;
		this.fieldName = fieldName;

		// lade vom Editierenden gesetzte Elemente
		readField();
		// lade Elemente, die immer da sind, wie Flipper
		readEssentialElements();
	}

	/**
	 * 
	 * @param fieldname
	 * @param spielbausteine
	 */
	public void saveFile() {

		String pfad = new File("").getAbsolutePath();
		File saveFile = new File(pfad + FIELDPATH + fieldName);
		ServerLog.logMessage("Neues Spielfeld gespeichert: " + fieldName);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(saveFile));

			for (int key : gameElements.keySet()) {
				// Wenn das GameElement ein Flipper, Libero, Plunger ist-> nicht
				// doppelt speichern
				if (!isEssentialElementType(gameElements.get(key).getElementType())) {
					out.write(gameElements.get(key).toString());
					out.newLine();
				}
			}
			out.close();

			// Benachrichtige Clients, dass gesaved wurde
			editorProducer.fieldSaved();
			
			// serverintern vermerken, dass Spielfelddateien anders sind
			// (Liste verfuegbarer Spielfelder ist dirty)
			Field.fieldsOnServerManipulated();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param Map<Integer,
	 *            GameElement>
	 * @return void
	 */
	public void ausgabe(Map<Integer, GameElement> spielfeld) {
		for (int key : spielfeld.keySet()) {
			System.out.println(spielfeld.get(key).toString());
		}
	}

	/**
	 * @param Map<Integer,
	 *            GameElement>
	 * @return Liste der Elemente
	 */
	public Map<Integer, GameElement> getElements() {
		return gameElements;
	}

	/**
	 * gibt zurueck ob es ein essential ELement ist, das nicht gespeichert, oder manipuliertw erden darf
	 * @param elementType der zu prüfende Type
	 * @return boolean ob es essential ist
	 */	
	public boolean isEssentialElementType(ElementType elementType) {
		return (elementType == ElementType.FLIPPER_PLAYER_1_LEFT ||
				elementType == ElementType.FLIPPER_PLAYER_1_RIGHT ||
				elementType == ElementType.FLIPPER_PLAYER_2_LEFT ||
				elementType == ElementType.FLIPPER_PLAYER_2_RIGHT ||
				elementType == ElementType.FLIPPER_PLAYER_3_LEFT ||
				elementType == ElementType.FLIPPER_PLAYER_3_RIGHT ||
				elementType == ElementType.FLIPPER_PLAYER_4_LEFT ||
				elementType == ElementType.FLIPPER_PLAYER_4_RIGHT ||
				elementType == ElementType.LIBERO_PLAYER_1 ||
				elementType == ElementType.LIBERO_PLAYER_2 ||
				elementType == ElementType.LIBERO_PLAYER_3 ||
				elementType == ElementType.LIBERO_PLAYER_4 ||
				elementType == ElementType.PLUNGER ||
				elementType == ElementType.BALL ||
				elementType == ElementType.GOALPOST_PLAYER_1 ||
				elementType == ElementType.GOALPOST_PLAYER_2 ||
				elementType == ElementType.GOALPOST_PLAYER_3 ||
				elementType == ElementType.GOALPOST_PLAYER_4 ||
				elementType == ElementType.GOAL_PLAYER_1 ||
				elementType == ElementType.GOAL_PLAYER_2 ||
				elementType == ElementType.GOAL_PLAYER_3 ||
				elementType == ElementType.GOAL_PLAYER_4 ||
				elementType == ElementType.GOALAREA_PLAYER_1 ||
				elementType == ElementType.GOALAREA_PLAYER_2 ||
				elementType == ElementType.GOALAREA_PLAYER_3 ||
				elementType == ElementType.GOALAREA_PLAYER_4);
	}
	
	/**
	 * gibt zurück ob es eine Curve ist, die nicht verschoben werden darf und nur spezielle Positionen haben darf
	 * @param elementType der zu prüfende Type
	 * @return boolean ob es eine Curve ist
	 */	
	public boolean isCurve(ElementType elementType) {
		return (elementType == ElementType.CURVE_0 ||
				elementType == ElementType.CURVE_1 ||
				elementType == ElementType.CURVE_2 ||
				elementType == ElementType.CURVE_3);
	}
	
	/**
	 * gibt zurück ob es eine Curve ist, die nicht verschoben werden darf und nur spezielle Positionen haben darf
	 * @param elementType der zu prüfende Type
	 * @return boolean ob es eine Curve ist
	 */	
	public void placeCurve(GameElement gameElement) {
		ElementType elementType = gameElement.getElementType();
		if (elementType == ElementType.CURVE_0){
			System.out.println("CURVE_0");
			gameElement.setPosition(new Vector2(0, 0));
		}else if (elementType == ElementType.CURVE_1 ){
			gameElement.setPosition(new Vector2(528, 0));
		} else if (elementType == ElementType.CURVE_2){
			gameElement.setPosition(new Vector2(0, 528));
		} else if (elementType == ElementType.CURVE_3){
			gameElement.setPosition(new Vector2(528, 528));
		}
	}

	/**
	 * 
	 * @param id
	 *            id des zu setzenden Objektes
	 * @param posX
	 *            x-Position des neuen Objekts
	 * @param posY
	 *            y-Position des neuen Objekts
	 * @param elementType
	 *            ElementType des neuen Objektes
	 */
	public int setElement(float posX, float posY, ElementType elementType) {
		if (isEssentialElementType(elementType))
			return -1;
		GameElement newGameElement = new GameElement(elementType, new Vector2(posX, posY));
		gameElements.put(id, newGameElement);

		// sollte es eine Curve sein, korrigiere die Position
		if (isCurve(newGameElement.getElementType())) {
			placeCurve(newGameElement);
		}
		editorProducer.elementSet(id, newGameElement.getPosition().x, newGameElement.getPosition().y, elementType.toString());
		return id++;
	}

	/**
	 * 
	 * @param id
	 *            wenn ein gelöschtes element wiederhergestellt wird, benutze
	 *            die selbe id die es vorher hatte
	 * @param posX
	 *            x-Position des wiederhergestellten Objekts
	 * @param posY
	 *            y-Position des wiederhergestellten Objekts
	 * @param elementType
	 *            ElementType des wiederhergestellten Objektes
	 * @return
	 */
	public int restoreElement(int id, float posX, float posY, ElementType elementType) {
		gameElements.put(id, new GameElement(elementType, new Vector2(posX, posY)));
		editorProducer.elementSet(id, posX, posY, elementType.toString());
		return id;

	}

	/**
	 * @param id
	 *            id des zu bewegenden Objektes
	 * @param moveX
	 *            x-Wert des 2D-Vektors, um den das Element verschoben wird
	 * @param moveY
	 *            y-Wert des 2D-Vektors, um den das Element verschoben wird
	 */
	public boolean moveElement(int id, float moveX, float moveY) {
		if (isEssentialElementType(gameElements.get(id).getElementType()) || isCurve(gameElements.get(id).getElementType()))
			return false;

		Vector2 oldPosition = gameElements.get(id).getPosition();
		gameElements.get(id).setPosition(new Vector2(oldPosition.x + moveX, oldPosition.y + moveY));
		editorProducer.elementMoved(id, gameElements.get(id).getPosition().x, gameElements.get(id).getPosition().y);
		return true;
	}

	/**
	 * @param id
	 *            des zu löschenden Elements
	 */
	public boolean deleteElement(int id) {
		// wenns ein essentielles Element ist oder die ID nicht gibt, darf es nicht gelöscht werden
		if (isEssentialElementType(gameElements.get(id).getElementType()) || !gameElements.containsKey(id))
			return false;

		gameElements.remove(id);
		editorProducer.elementDeleted(id);
		return true;

	}

	/**
	 * 
	 * @param newName
	 *            neuer Name fuer das EditorField
	 */
	public void changeName(String newName) {
		ServerLog.logMessage("neuer Spielfeldname im Editor gesetzt: " + newName);
		fieldName = newName;
		editorProducer.nameChanged(newName);
	}

	/**
	 * @return fieldName, Name des EditorFields
	 */
	public String getName() {
		return fieldName;
	}

	public GameElement getGameElement(int id) {
		return gameElements.get(id);
	}

	
	@Override
	protected void addFieldElementToCollection(GameElement ele) {
		gameElements.put(id++, ele);
	}

	@Override
	protected void addPlayerElementToCollection(String[] cells) {
		GameElement actGameElement = null;
		
		float posX = Float.parseFloat(cells[1].replace(",", "."));
		float posY = Float.parseFloat(cells[2].replace(",", "."));
		
		// Sonderfall für Plunger und Ball
		if (cells[0].contains("BALL"))
			actGameElement = new Ball(new Vector2(posX, posY), WIDTH, HEIGHT, null, null, null);
		else if (cells[0].contains("PLUNGER"))
			actGameElement = new Plunger(new Vector2(posX, posY), null, null);
		else if (cells[0].contains("FLIPPER"))
			actGameElement = new Flipper(ElementType.convert(cells[0]), new Vector2(posX, posY), DUMMYID,
					cells[0].contains("LEFT"), null);
		else if (cells[0].contains("LIBERO"))
			actGameElement = new Libero(ElementType.convert(cells[0]), new Vector2(posX, posY), DUMMYID, WIDTH,
					HEIGHT);
		else if (cells[0].contains("POST"))
			actGameElement = new MotionlessElement(ElementType.convert(cells[0]), new Vector2(posX, posY));
		else if (cells[0].contains("GOAL_"))
			actGameElement = new Goal(ElementType.convert(cells[0]), new Vector2(posX, posY), DUMMYID);
		else if (cells[0].contains("AREA_"))
			actGameElement = new GoalArea(ElementType.convert(cells[0]), new Vector2(posX, posY));

		gameElements.put(id++, actGameElement);
		
	}
}
