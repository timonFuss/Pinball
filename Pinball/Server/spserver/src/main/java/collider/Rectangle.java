package collider;

import datatypes.Vector2;

/**
 * Instanziiert ein Rectangle, der vom Collider erbt */
public class Rectangle extends Collider{
	private float height;
	private float width;
	private float rotation;
	
	public Rectangle(Vector2 pos,float height,float width,float rotation) {
		super(pos);
		setHeight(height);
		setWidth(width);
		setRotation(rotation);
	}
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	/**Entspicht das Textdatei-Format**/
	@Override
	public String toString() {
		return "Rectangle "+super.toString()+" "+ height +" "+ width +" "+ rotation;
	}
	

	
}
