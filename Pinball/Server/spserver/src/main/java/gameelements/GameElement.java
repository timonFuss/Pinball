package gameelements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import collider.Collider;
import collider.ColliderReader;
import datatypes.Vector2;
import enums.ElementType;

/**
 * @author mbeus001
 * beinhaltet alle Variablen, die jedes Spielelement haben wird wie die Position
 */
public class GameElement {
	protected int id;
	protected Vector2 position;
	protected ElementType elementType;
	protected List<Collider> colliderList;
	static int idCounter = 0;
	private float bouncyness = 0;
	
	
	/**
	 * @param position
	 * @param elementType
	 */
	public GameElement(ElementType elementType, Vector2 position){
		this.id = idCounter;
		this.elementType = elementType;
		idCounter++;
		colliderList = new LinkedList<Collider>();
		colliderList = ColliderReader.getInstance().getCollider(elementType);
		this.position = position;
		bouncyness = elementType.getValue();
	}
	
	
	@Override
	public java.lang.String toString() {	
		return this.elementType+" "+this.position.x+" "+this.position.y;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public ElementType getElementType(){
		return elementType;
	}
	
	public List<Collider> getColliderList(){
		return colliderList;
	}
	
	public float getBouncyness() {
		return bouncyness;
	}

	public void setBouncyness(float bouncyness) {
		this.bouncyness = bouncyness;
	}

}
