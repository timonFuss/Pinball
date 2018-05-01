package events;

import java.util.EventListener;

public interface GameConsumerGameListener extends EventListener{
	
	void addForceFired(AddForceEvent e);
	void buttonUpFired(ButtonUpEvent e);
	void buttonDownFired(ButtonDownEvent e);
	void buttonLeftFired(ButtonLeftEvent e);
	void buttonRightFired(ButtonRightEvent e);
	void flipperLeftFired(FlipperLeftEvent e);
	void flipperRightFired(FlipperRightEvent e);
}
