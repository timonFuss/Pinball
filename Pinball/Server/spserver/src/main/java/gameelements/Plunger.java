package gameelements;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import collider.Collider;
import communication.GameProducer;
import datatypes.Vector2;
import enums.ElementType;

public class Plunger extends GameUpdateElement {

	private double roationSpeed = 0.5;
	private Ball ball;
	private GameProducer gameProducer;
	
	private Timer timer = new Timer();
	private float shootingTime = 3;
	private float count = -3;
	
	
	public float getCount() {
		return count;
	}

	public void startCount() {
		count = shootingTime;
	}


	float power = 100; // Kraft des Plungers
	double initialRotation = 45; // rotation für das Bild

	public Plunger(Vector2 position, Ball ball, GameProducer gameProducer) {
		super(ElementType.PLUNGER, position);
		this.ball = ball;
		this.gameProducer = gameProducer;
		repositionColliders();
		
		//timer für den Plunger
		// im editor wird für den ball null übergeben, deswegen diese abfrage
		if (ball != null)
			timer.schedule(task, 0, 500);
	}

	public void Update() {

		rotation = ((float)(roationSpeed)+rotation)%360;
	}
	
	// damit der Felix die Position in seiner Physik nicht draufrechnen braucht
	public void repositionColliders() {

		// kopiere Liste
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
	
	
	TimerTask task = new TimerTask() {
	     
	@Override
	public void run() {
		
	    	  if (count > -1){
	    		  count=count-0.5f;
	    	  }
	    	  if (count == 0){
	    		  //vForces = new Vector2(10,10);
	    		  
	    		  //versatz weil das ding rotiert
	    		  double versatz = 2;
	    		  
	    		  ball.vVelocity =  new Vector2((float)Math.sin((rotation+initialRotation+versatz)/180f*3.14f),(float)Math.cos((rotation+initialRotation+versatz)/180f*3.14f)).multiply(power);
	    		  gameProducer.sendShooutOut();
	    	  }
	    	  if (count == -0.5f){
	    		  //vForces = new Vector2(10,10);
	    		  //liberos freigeben
	    		  ball.canCollideWithLiberos = true;
	    		  ball.canCollideWithPlunger = true;
	    	  }
	    	  if(count == -3){
	    		  //initialen wert setzen
	    		  count = 3;
	    	  }
	    	  
	      	  }
	      };
}
