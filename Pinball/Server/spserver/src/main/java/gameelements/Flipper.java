package gameelements;
import collider.Rectangle;
import communication.GameProducer;
import datatypes.Vector2;
import enums.ElementType;

public class Flipper extends GameUpdateElement {
	
	GameProducer gameProducer;

	//flipper Daten aus dem collider nehmen
	public float rotationSpeed = 9;
	public boolean moveup = true;
	public float openAngle = 60;
	
	// asu dem collider holen später
	public float height;
	public float width;
	
	public boolean animationState = false;
	public boolean isHolding = false;
	float rotationOffset = 12; // muss das selbe offset sein wie im Flipper element auf dem client sonst geht das ganze nicht richtig!
	public float hebelForce = rotationSpeed/3f; // kraft mit der der Hebel auf den ball wirkt, irgendwie war rotationSpeed/2 gut
	private boolean reversed;
	
	private String ownerPlayerID;

	// neuer Konstruktor für den Flipper, der seinen Collider aus dem ColliderGenerator holt
	public Flipper(ElementType whichFlipper, Vector2 pos, String ownerPlayerID, boolean reversed, GameProducer gameProducer){
		super(whichFlipper, pos);
		
		this.gameProducer = gameProducer;
		
		if(reversed){
			this.reversed = reversed;
		}
		
		if(whichFlipper == ElementType.FLIPPER_PLAYER_2_LEFT
		|| whichFlipper == ElementType.FLIPPER_PLAYER_2_RIGHT
		|| whichFlipper == ElementType.FLIPPER_PLAYER_4_LEFT
		|| whichFlipper == ElementType.FLIPPER_PLAYER_4_RIGHT
		){ this.reversed = !this.reversed; }
		
		if(getColliderList()!= null){
			height = ((Rectangle)getColliderList().get(0)).getHeight();
			width = ((Rectangle)getColliderList().get(0)).getWidth();
			rotation = ((Rectangle)getColliderList().get(0)).getRotation();
		}
		
		this.ownerPlayerID = ownerPlayerID;
	}

	@Override
	public void Update() {
		
		//key down setzt animationstate true
		if(animationState){
			
			//hindrehen
			if(reversed){
			
				if(rotation > -(openAngle/2)){
					rotation -= rotationSpeed;
					isHolding = false;
				}else{
					isHolding = true;
				}
				
				
			}else{
				
				if(rotation < (openAngle/2)){
					rotation += rotationSpeed;
					isHolding = false;
				}else{
					isHolding = true;
				}
				
			}
			
		}else{
			
			//zurueckdrehen
			if(reversed){
				
				if(rotation < (openAngle/2)){
					rotation += rotationSpeed;
					isHolding = false;
				}
				
				
			}else{
			
				if(rotation > -(openAngle/2)){
					rotation -= rotationSpeed;
					isHolding = false;
				}
				
			}
			
		
		}
		
	}
	
	private void switchRotation(){
			moveup = !moveup;
			gameProducer.sendElementHit(getId());
	}
	
	public void setAnimationState(boolean b){
		animationState = b;
		gameProducer.sendFlipperMove(b);
}
	
	public String getOwnerPlayerID(){
		return ownerPlayerID;
	}
}
