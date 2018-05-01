package collider;

import datatypes.Vector2;

public class Collider implements Cloneable {
	
	private Vector2 pos;
	
	public Collider(Vector2 pos){
		setPos(pos);
	}
	
	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	@Override
	public String toString(){
		return pos.x+" "+pos.y;
	}
	
	 @Override
	 public Collider clone() throws CloneNotSupportedException {
	    return (Collider)super.clone();
	}
	

	
}
