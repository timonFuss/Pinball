package game;

import communication.GameConsumer;
import events.AddForceEvent;
import events.ButtonDownEvent;
import events.ButtonLeftEvent;
import events.ButtonRightEvent;
import events.ButtonUpEvent;
import events.FlipperLeftEvent;
import events.FlipperRightEvent;
import events.GameConsumerGameListener;
import field.GameField;
import gameelements.Ball;

public class InputHandler {
	
	private Ball ball;

	public InputHandler(GameField field, GameConsumer gameConsumer) {
		ball = field.getBall();
		
		class PhysicCalculatorGameConsumerGameListener implements GameConsumerGameListener{

			@Override
			public void addForceFired(AddForceEvent e) {
				addForce(e.getPosX(), e.getPosY());				
			}			
			
			public void buttonUpFired(ButtonUpEvent e){
				if(e.getKeyAction()==false){
					field.getLibero(e.getPlayer()).setWalk("up");
				}
				if(e.getKeyAction()==true){
					field.getLibero(e.getPlayer()).unSetWalk("up");
				}
			}
			
			public void buttonDownFired(ButtonDownEvent e){
				if(e.getKeyAction()==false){
					field.getLibero(e.getPlayer()).setWalk("down");
				}
				if(e.getKeyAction()==true){
					field.getLibero(e.getPlayer()).unSetWalk("down");
				}
			}
			
			public void buttonLeftFired(ButtonLeftEvent e){
				if(e.getKeyAction()==false){
					field.getLibero(e.getPlayer()).setWalk("left");
				}
				if(e.getKeyAction()==true){
					field.getLibero(e.getPlayer()).unSetWalk("left");
				}
			}
			
			public void buttonRightFired(ButtonRightEvent e){
				if(e.getKeyAction()==false){
					field.getLibero(e.getPlayer()).setWalk("right");
				}
				if(e.getKeyAction()==true){
					field.getLibero(e.getPlayer()).unSetWalk("right");
				}
			}
			
						
			//bewege den flipper von spieler 1, holt sich bisher nur die flipper von spieler 1
			public void  flipperLeftFired(FlipperLeftEvent e) {				
				if( e.getKeyAction()==false){
					field.getLeftFlipper(e.getPlayer()).setAnimationState(true);
				}
				
				if( e.getKeyAction()==true){
					field.getLeftFlipper(e.getPlayer()).setAnimationState(false);
				}
				//field.getLeftFlipper("FLIPPER_PLAYER_1_LEFT");
			}
			
			public void  flipperRightFired(FlipperRightEvent e) {
				//flipperRight(e.getFlipperRight(), e.getAction(), e.getPlayer());
				//field.getLeftFlipper("FLIPPER_PLAYER_2_LEFT").animationState = true;
				if( e.getKeyAction()==false){
					field.getRightFlipper(e.getPlayer()).setAnimationState(true);
				}
				
				if( e.getKeyAction()==true){
					field.getRightFlipper(e.getPlayer()).setAnimationState(false);
				}
			}
		}
		gameConsumer.addGameConsumerGameListener(new PhysicCalculatorGameConsumerGameListener());
		
	}
	
	
	public void addForce(float x, float y){
		
		// alle hebel anfuehren...		 

		ball.vVelocity.x = x/1f;
		ball.vVelocity.y = y/1f; 
	}
}
