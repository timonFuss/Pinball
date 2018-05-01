

package datatypes;

/**
 * @author mbeus001
 * eigene implementierung einer Vecor-Klasse mit 2 Dimensionen
 */
public class Vector2 {
	public float x;
	public float y;
	
	/**
	 * 
	 * @param posX
	 * @param posY
	 */
	public Vector2(float posX, float posY){
		this.x = posX;
		this.y = posY;
	}
	
	/**
	 * 
	 */
	public Vector2(){
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * skaliert Vektor mit f
	 * @param f
	 */
	public void multiplyThis(float f) {
		this.y *= f;
		this.x *= f;
	}
	
	public Vector2 multiply(float f) {
		return new Vector2(x*f,y*f);
	}
	
	public Vector2 add(float f) {
		return new Vector2(x+f,y+f);
	}
	
	public Vector2 add(Vector2 f) {
		return new Vector2(x+f.x,y+f.y);
	}
	public Vector2 multiply(Vector2 f) {
		return new Vector2(x*f.x,y*f.y);
	}
	
	public Vector2 subtract(float f) {
		return new Vector2(x-f,y-f);
	}
	
	public Vector2 subtract(Vector2 f) {
		return new Vector2(x-f.x,y-f.y);
	}
	
	public Vector2 normalised() {
		float length = length();
		return new Vector2(x/length,y/length);
	}
	
	public float length() {
		float xsq = x*x;
		float ysq = y*y;
		return (float) Math.sqrt(xsq+ysq);
	}
	
	
	
	public Vector2 divide(float f) {
		
		return new Vector2(x/f,y/f);
	}

	public float dot(Vector2 n) {
		return x*n.x+y*n.y;
	}
	
	
}
