package gameelements;

import collider.Rectangle;
import datatypes.Vector2;
import enums.ElementType;

public class Goal extends MotionlessElement{

	private String ownerPlayerID;
	
	public Goal(ElementType elementType, Vector2 position, String ownerPlayerID) {
		super(elementType, position);
		this.ownerPlayerID = ownerPlayerID;
	}
	
	public String getOwnerPlayerID(){
		return ownerPlayerID;
	}

}
