package gameelements;

import java.util.ArrayList;
import java.util.List;

import collider.Circle;
import collider.Collider;
import collider.Rectangle;
import datatypes.Vector2;
import enums.ElementType;

public class Libero extends GameUpdateElement {

	private String ownerPlayerID;
	public Vector2 walk = new Vector2(0,0);
	private float speed = 4;
	private Vector2 move;
	private float width;
	private float height;
	
	private static final float LIBEROBREITE = 30; // sorry aber hier muss das mnauell reingeschrieben werden weil man den wert ausprobieren mus und dieser dann fix bleibt
	private static final float HORIZONTALPADDING = 10; 
	private static final float LIBEROHOEHE = 25; 
	
	private Vector2 colliderPadding = new Vector2(17,12);
	private List<GameElement> goalAreas;
	
	public Libero(ElementType whichLibero, Vector2 position, String ownerPlayerID,float width, float height) {
		super(whichLibero, position);
		this.width = width;
		this.height = height;
		this.ownerPlayerID = ownerPlayerID;
		move = new Vector2(0,0);
		goalAreas = new ArrayList<GameElement>();
		
	}
	
	public void setGoalArea(GameElement goalArea){
		goalAreas.add(goalArea);
	}

	@Override
	public void Update() {
		
		Vector2 newpos = position.add(walk);
		
		//kollision mit strafraum
		for(GameElement goalArea : goalAreas){
			
			Vector2 pos = ((Rectangle)(goalArea.getColliderList().get(0))).getPos();
			float areaWidth = ((Rectangle)(goalArea.getColliderList().get(0))).getWidth();
			float areaHeight = ((Rectangle)(goalArea.getColliderList().get(0))).getHeight();
			
			float left = newpos.x+colliderPadding.x+((Circle)this.getColliderList().get(0)).getRadius();
			float up = newpos.y+colliderPadding.y+((Circle)this.getColliderList().get(0)).getRadius();
			
			
			if((left > pos.x && left < pos.x+areaWidth) && (up > pos.y && up < pos.y+areaHeight)){
				
					newpos.x -= walk.x;
					newpos.y -= walk.y;
				
			}
			
		
		}
		
		//kollision mit wand
		if(newpos.x < HORIZONTALPADDING){
			newpos.x = HORIZONTALPADDING;
		}
		
		if(newpos.x > width-((Circle)this.getColliderList().get(0)).getRadius()*2-LIBEROBREITE-HORIZONTALPADDING){
			newpos.x = width-((Circle)this.getColliderList().get(0)).getRadius()*2-LIBEROBREITE-HORIZONTALPADDING;
		}
		
		if(newpos.y < 0){
			newpos.y = 0;
		}
		if(newpos.y > height-((Circle)this.getColliderList().get(0)).getRadius()*2-LIBEROHOEHE){
			newpos.y = height-((Circle)this.getColliderList().get(0)).getRadius()*2-LIBEROHOEHE;
		}
		
		
		move = position.subtract(newpos);
		position = newpos;
		
		//collider einfach auf pos setzen, man muss nur das erste element der liste nehemn weil der kollider nur ein Kreis ist
		colliderList.get(0).setPos(new Vector2(((Circle)colliderList.get(0)).getRadius()+position.x+colliderPadding.x,
											   ((Circle)colliderList.get(0)).getRadius()+position.y+colliderPadding.y));
		
	}

	public void setWalk(String direction){
		if(direction == "up"){
			walk.y -= speed;					
		}
		if(direction == "down"){
			walk.y += speed;
		}
		if(direction == "left"){
			walk.x -= speed;
		}
		if(direction == "right"){
			walk.x += speed;
		}
	}
	
	public void unSetWalk(String direction){
		if(direction == "up"){
			walk.y += speed;					
		}
		if(direction == "down"){
			walk.y -= speed;
		}
		if(direction == "left"){
			walk.x += speed;
		}
		if(direction == "right"){
			walk.x -= speed;
		}
	}
	
	public String getOwnerPlayerID(){
		return ownerPlayerID;
	}

	public Vector2 getMoveVector() {
		return move;
	}
}
