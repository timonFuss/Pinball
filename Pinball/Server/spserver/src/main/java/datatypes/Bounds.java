package datatypes;

//klasse zur effektiven berechnung von statischen Quadraten
public class Bounds {
	 public float left;
     public float top;
     public float right;
     public float bottom;

     public Bounds()
     {
         this.left = 0;
         this.top = 0;
         this.right = 0;
         this.bottom = 0;
     }
     public Bounds(float left, float top, float right, float bottom)
     {
         this.left = left;
         this.top = top;
         this.right = right;
         this.bottom = bottom;
     }
     public float getWidth()
     {
         return right - left;
     }
     public float getHeight()
     {
         return bottom - top;
     }
}
