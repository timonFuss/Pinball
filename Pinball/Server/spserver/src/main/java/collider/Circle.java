package collider;

import datatypes.Vector2;

public class Circle extends Collider{

	private float radius;
	
	public Circle(Vector2 pos, float radius){
		super(pos);
		setRadius(radius);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	/*
	 * Entspricht der Textformat*/
	@Override
	public String toString() {
		return "Kreis "+ super.toString()+" "+radius;
	}

}
