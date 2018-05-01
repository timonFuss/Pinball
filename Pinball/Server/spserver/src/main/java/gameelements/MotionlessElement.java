package gameelements;

import java.util.ArrayList;

import collider.Collider;
import collider.ColliderReader;
import datatypes.Vector2;
import enums.ElementType;

public class MotionlessElement extends GameElement {
	
	private boolean hasloaded = false;
	public MotionlessElement(ElementType elementType, Vector2 position) {
		super(elementType, position);
		repositionColliders();
	}
	
	// damit der Felix die Position in seiner Physik nicht draufrechnen braucht
	public void repositionColliders(){
	
		//kopiere Liste
		ArrayList<Collider> c = new ArrayList<Collider>();
		for (Collider collider : colliderList) {
			try {
				c.add(collider.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		colliderList = c;
		
		for (Collider collider : colliderList) {
			collider.setPos(collider.getPos().add(position));
		}
				
	}
	
}
