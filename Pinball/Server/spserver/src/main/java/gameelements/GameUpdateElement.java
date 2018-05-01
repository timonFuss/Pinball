package gameelements;

import datatypes.Vector2;
import enums.ElementType;

/**
 * @author mbeus001
 *
 */
public abstract class GameUpdateElement extends GameElement{

	protected float rotation;
	/**
	 * @param id
	 */
	public GameUpdateElement(ElementType elementType, Vector2 position) {
		super(elementType,position);
	}
	
	/**
	 * jedes veränderliche Element hat eine Update-Methode, in der elementspezifische Logik implementiert wird
	 */
	public abstract void Update();
	
	/**
	 * @return rotation
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * @param rotation
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
}
