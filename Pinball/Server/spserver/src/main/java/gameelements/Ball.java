package gameelements;


//import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

import collider.Circle;
import collider.Collider;
import collider.Rectangle;
import communication.GameProducer;
import datatypes.Bounds;
import datatypes.Intersection;
import datatypes.Vector2;
import enums.ElementType;
import game.GameScoreCounter;


/**
 * @author mbeus001
 * beinhaltet noch stupid-Logik
 */
public class Ball extends GameUpdateElement{
	
	GameProducer gameProducer;
	
    //Enginefields 
	public float totalSpeed = 0.15f; // Geschwindigkeit des Balls, die Zeit Variable
    public float gravity = 0f; //Gravitation die auf den Ball wirkt
    public float fMass = 3.0f; //Gewicht des Balls
    public float fRadius = 0.1f; //Radius des Balls fuer die Berechnung des Luftwiederstandes
    public float airDensity = .15f; //Dichte der Luft z.b. beim blasen des windes oder wenn die kugel durch die luft fliegt
    public float dragCoeficcient = 0.3f; // wie schnell nimm die geschwindigkeit der kugel ab? Die Reibung des bodens
    public float windspeed = 0f; //Windgeschwindigkeit auf 1000 setzen fuer schwachen wind
    public float restitution = 0.9f; //Bouncyness des Balls, zwischen 0 und 1 -> bei 1 verliert der ball bei einer kollision keine Geschwindigkeit
    public float colliderRadius = 10; // die groesse des balls aus sicht der Collider
    public Vector2 vVelocity= new Vector2(); //BeschleunigungsVektor des Balls
    public boolean enableSpeedlimit = false;
    public boolean canCollideWithLiberos = false;
    public boolean canCollideWithPlunger= false;
    private boolean lstFrameWasLiberoCollision = false;
    
    //sound block fuer den hebel
    private float XFrames = 10;
    private float blockSoundXFrames = 0;

    Vector2 vPosition = new Vector2(); // Position
    float fSpeed; // aktuelle geschwindigkeit
    Vector2 vForces = new Vector2(); // Total force acting on the particle
    Vector2 vGravity = new Vector2(); // Gravity force vector

    int gameFieldWidth;
    int gameFieldHeight;
    float floor = 10;
    int flipPoints = 10;
    int liberoPoints = 3;
    int ownGoalPoints = -50;
    
    private float speedlimit = 12f; //laenge des vektors den der ball pro frame maximal laufen darf

    //kollisions vars
    Vector2 vPreviousPosition = new Vector2();
    Vector2 vImpactForces = new Vector2();
    private ArrayList<Collider> allColliders;
    public List<GameUpdateElement> agileElements;
    private List<GameElement> staticElements;
    private Plunger plunger;
    private GameScoreCounter gameScoreCounter;
    
	public Ball(Vector2 initPos ,int fensterbreite,  int fensterhoehe, List<GameUpdateElement> agileElements, List<GameElement> staticElements, GameProducer gameProducer) {
 
		super(ElementType.BALL, initPos);
		
		
		this.agileElements = agileElements;
		
		this.gameFieldWidth = fensterbreite;
		this.gameFieldHeight = fensterhoehe;
        
		this.staticElements = staticElements;
		
		this.gameProducer = gameProducer;
		
        vVelocity.x = 0.0f;
        vVelocity.y = 0.0f;
        fSpeed = 0.0f;
        vForces.x = 0.0f;
        vForces.y = 0.01f;
        vGravity.x = 0f;
        vGravity.y = fMass * gravity;
       
		//position in der physik engine setzen
		vPosition = initPos;
		vPreviousPosition = initPos;
		
		//collider fuer den test initialisieren
		allColliders = new ArrayList<Collider>();

	}
	
	public void injectGameScoreCounter(GameScoreCounter gameScoreCounter){
		this.gameScoreCounter = gameScoreCounter;
	}

	@Override
	public void Update() {
		
		CalcLoads();

        Vector2 a;
        Vector2 dv;
        Vector2 ds;

        // Integrate equation of motion:
        a = vForces.divide(fMass);

        dv = a.multiply(totalSpeed);
        
        vVelocity = vVelocity.add(dv);

        ds = vVelocity.multiply(totalSpeed);

        //speedlimit, die kugel darf nicht mehr laufen als der Radius des colliders sonst geht die kugel durch die collider

        //-5 ist die toleranzdamit die kugel nich so schnell geht das sie durch ritoerende hebel geht
        
        if (ds.length() > speedlimit) {
            ds = ds.normalised().multiply(speedlimit);
        }

        vPosition = vPosition.add(ds);
        
        fSpeed = vVelocity.length();

        position.y = vPosition.y*-1+gameFieldHeight -colliderRadius;
		position.x = vPosition.x - colliderRadius;

	}
	

	private void CalcLoads()
       {
           //Kraefte reset
           vForces.x = 0.0f;
           vForces.y = 0.0f;
           
           //Gravitation
           //vForces += vGravity;

           if (CheckForCollisions())
           {
               // Add Impact forces
               vForces = vForces.add(vImpactForces);
               
           }
           else
           {
           
        	   //Kommt es zu einer kollision werden alle Kraefte ignoriert und es werden nur die impact forces berechnet

               //Widerstand der Luft
               Vector2 vDrag = new Vector2(0, 0);
               float fDrag = 0;
               vDrag = vDrag.subtract(vVelocity);
               vDrag = vDrag.normalised();
               fDrag = 0.5f * airDensity * fSpeed * fSpeed *
               (3.14159f * fRadius * fRadius) * dragCoeficcient;
               vDrag = vDrag.multiply(fDrag);
               
               if (!Float.isNaN(vDrag.x))
               {
                   vForces =  vForces.add(vDrag);
               }

               //Kraft des Windes
               Vector2 vWind = new Vector2();
               vWind.x = 0.5f * airDensity * windspeed * (3.14159f * fRadius * fRadius) * dragCoeficcient;
               vForces = vForces.add(vWind);

               //Wirkung der Gravitation
               vForces = vForces.add(vGravity);

           }

      }
	
	
	
	  private boolean CheckForCollisions()
      {

          //Console.WriteLine(deltaTime);
          
          Vector2 n = new Vector2(); // normalenvektor der kollision
          float vrn; //speichert Vector2.Dot(vVelocity, n) um festzustellen ob sich der velocity vektor in richtung normale bewegt
          float J = 0; // speichert impuls
          Vector2 Fi = new Vector2();
          boolean hasCollision = false;
          float newx = 0;
          float newy = 0;
          float abschneiden = 0;

          vImpactForces.x = 0;
          vImpactForces.y = 0;
          
          /**         Auszug aus dem Physik Buch:
                      Over a given simulation time step, a particle may have
                      moved from some previous position (its position at the previous time step) to its current
                      position. If this current position puts the centroid coordinate of the particle within one
                      particle radius of the ground plane, then a collision might be occurring. We say might
                      because the other criteria we need to check in order to determine whether or not a
                      collision is happening is whether or not the particle is moving toward the ground plane.
                      If the particle is moving toward the ground plane and its within one radius of the ground
                      plane, then a collision is occurring. It may also be the case that the particle has passed
                      completely through the ground plane, in which case we assume a collision has occurred.
                      To prevent such penetration of the ground plane, we need to do two things. First, we
                      must reposition the particle so that it is just touching the ground plane. Second, 
                      we must apply some impact force resulting from  the collision in order to force the particle 
                      to either stop moving down into the ground plane or to move away from the ground plane. 
                      All these steps make up collision detection and response.
          **/

          //bodenkollision
          if (vPosition.y <= (floor))
         {
             n.x = 0f;
             n.y = 1;
             
             newy = floor;
             newx = vPosition.x;
              
       }
          
        //deckenkollision
          if (vPosition.y >= gameFieldHeight-colliderRadius)
          {
              
              n.x = 0f;
              n.y = -1f;
              
              newy = gameFieldHeight - colliderRadius;
              newx = vPosition.x;
               
        }
          
        //seitenkollisionen
          //abschneiden = 16;
          if (vPosition.x >= gameFieldWidth -colliderRadius)
          {
        	  n.x = -1f;
              n.y = 0;
              
              newy = vPosition.y;
              newx = gameFieldWidth - colliderRadius;
              
          
          }
          
        
          if (vPosition.x <= colliderRadius)
          {
        	  n.x = 1f;
              n.y = 0;
              
              newy = vPosition.y;
              newx = colliderRadius;
              
          }
          
          // pruefe ob Partikel in Richtung kollisonsobjekt bewegt, d.h. ob der velocity vector in seine richtung zeigt, wenn ja dann kann es sein das er mit dem boden kollidiert und es muss getestet werden
          vrn = vVelocity.dot(n);
          if (vrn < 0.0)
          {

              //J is a scalar equal to the negative of the relative velocity in the normal direction times the coefficient of restitution plus 1 times the particle mass.
              //Recall that the coefficient of restitution, _RESTITUTION, governs how elastic or inelastic the collision is, or in other words, how much energy is transferred back to the particle during the impact.
              J = -(vrn) * (restitution + 1) * fMass;

              //Fi is a vector that stores the impact force as derived from the impulse
              Fi = n.multiply(J).divide(totalSpeed);
          
              //hier steht fest welche Kraefte im naechsten frame auf den Ball wirken
              vImpactForces = vImpactForces.add(Fi);
        

              //jetzt muss der Ball auf die Stelle der Kollision zurueckgesetzt werden
              //die y position ist auf jedenfall die Position des Bodens + dem Radius
              vPosition.y = newy;
              vPosition.x = newx;

              hasCollision = true;
              gameProducer.sendWallHit();
          }
          
          for(GameElement staticElement : staticElements) {
        	  
        	  if(staticElement.getElementType() == ElementType.GOALAREA_PLAYER_1 ||
        			  staticElement.getElementType() == ElementType.GOALAREA_PLAYER_2 ||
        			  staticElement.getElementType() == ElementType.GOALAREA_PLAYER_3 ||
        			  staticElement.getElementType() == ElementType.GOALAREA_PLAYER_4){
        		  continue;
        	  }
                	  
          for (Collider collider : staticElement.colliderList) {
        	  
        	  
        	  //collider.getPos().x += staticElement.getPosition().x;
        	  //collider.getPos().y += staticElement.getPosition().y;
        	  
        	  
        	  //teste auf kreiskollision wenn der collider ein Kreis ist
        	  if(collider instanceof Circle){
        		  
        		 
        		  Circle circle = (Circle)collider;
        
        		  
        		//hole dir die pos des colliders
                  // ka warum -10f und +10 am ende!!!
                  Vector2 k = new Vector2(circle.getPos().x, circle.getPos().y *-1+ gameFieldHeight);

                  //d ist der vektor zwischen beiden kugeln
                  Vector2 d = vPosition.subtract(k);
                  
                  //wenn der Abstand beider Kugeln kleiner rbeider radien
                  float s = d.length() - (colliderRadius + circle.getRadius());
                  if (s <= 0.0)
                  {
                      //kollisionsnormale abfragen, der Vektor der von der bewegenden Kugel weg und zum target hin zeigt, normalisiert
                      n = d.normalised();
                    
                 
                      //Bemerkung: Achtung hier evtl velocities von anderen elementen als ParticleBallBehaviours eintragen  
                      
                      //checke ob der Kreis sich auch in richtung der kollisionsnormalen bewegt hat
                      vrn = vVelocity.dot(n);
                      

                	  //kreise kollidieren anders wenn das kollidierte objekt ein Libero ist
            		  if(staticElement instanceof Libero){
            			  //wenn der ball mit dem libero kollidiert sto�e den ball vom mittelpunkt weg
            			  
            			  //berechne J nach altem muster wie bei boden
                          //J = -(vrn) * (restitution + 1) * fMass;
                          //Fi = n.multiply(J / totalSpeed);
                          //vImpactForces = vImpactForces.add(Fi);

                          //s ist die groesse der Ueberlappung, n ist der Normalen Vektor der Kollision vposition ist die aktuelle position
                          //setze die kugel um diesen wert zurueck
                          //vPosition = vPosition.subtract(n.multiply(s));
                    
                          //sprung effekt draufrechnen
                          //Vector2 correctedBallPosition = new Vector2();
                          //correctedBallPosition.x = position.y*-1+ gameFieldHeight;
                          //correctedBallPosition.y = position.x;
                          /*vImpactForces = vImpactForces.add(d.multiply(staticElement.getBouncyness()));
                          
                          hasCollision = true;
            			  
                          return hasCollision;*/
                          
            		  }else{
            			  
            			  if (vrn < 0.0)
                          {

                    			  //berechne J nach altem muster wie bei boden
                                  J = -(vrn) * (restitution + 1) * fMass+staticElement.getBouncyness();
                                  Fi = n.multiply(J / totalSpeed);
                                  vImpactForces = vImpactForces.add(Fi);

                                  //s ist die groesse der Ueberlappung, n ist der Normalen Vektor der Kollision vposition ist die aktuelle position
                                  //setze die kugel um diesen wert zurueck
                                  vPosition = vPosition.subtract(n.multiply(s));
                                 
                                  hasCollision = true;
                                  gameProducer.sendElementHit(staticElement.getId());
                                  return hasCollision;
                    			  
                          }
            			  
            		  }

                  }
        		 
        	  }
        	  
        	  //teste auf Rechtangle kollision wenn der collider ein Rectangle ist
				if (collider instanceof Rectangle) {
					Rectangle rekt = (Rectangle) collider;
        		
        		  
        		  	//viereck pos und so
					float olx = rekt.getPos().x;
					float oly = rekt.getPos().y;
					float urx = olx + (float) rekt.getWidth();
					float ury = oly + (float) rekt.getHeight();
                      
					// checke fuer collisionen mit Axis Aligned Bounding box
					Vector2 v1 = new Vector2(vPosition.x, vPosition.y);
					Vector2 v2 = new Vector2(vPreviousPosition.x, vPreviousPosition.y);

					Vector2 quadMittelPunkt = new Vector2(olx + (urx - olx) / 2f, oly + (ury - oly) / 2f);

					v1.y = (v1.y * -1 + gameFieldHeight);
					v2.y = (v2.y * -1 + gameFieldHeight);

					// bekomme die aktuelle drehung
					float degree = rekt.getRotation() * -1f;
                      
	                //...und wenn dieses Verhalten Hebel ist
	                /*
	                if (rekt instanceof Flipper)
	                {
	                	// wenn rect flipper ist mache crazy sachen
		        	}
		            else {
		
		                //statische objekte drehen vom mittelpunkt
		                 
		            }*/
                      
					v1 = rotate_point(quadMittelPunkt, degree, v1);
					v2 = rotate_point(quadMittelPunkt, degree, v2);

					Intersection inter = checkIntersection(new Bounds(olx, oly, urx, ury), v2, v1, colliderRadius);

					if (inter != null) {

						if (!Float.isNaN(inter.cx) && !Float.isNaN(inter.cy)) {
                          //abfangen wenn die rechnung null ergibt
                    	    // Project Future Position
                    	

                              Vector2 c = new Vector2(inter.cx, inter.cy);
                              Vector2 i = new Vector2(inter.ix, inter.iy);

                              c = rotate_point(quadMittelPunkt, -degree , c);
                              i = rotate_point(quadMittelPunkt, -degree , i);

                              //tatsaechlich gelaufene laenge ausrechnen
                              Vector2 vec = new Vector2(c.x, c.y * -1 + gameFieldHeight);
                              
                              n = new Vector2(c.x - i.x, ((c.y * -1 + gameFieldHeight) - (-i.y * -1 + gameFieldHeight)));

                              vPosition = vec;

                              //kollisionsnormale abfragen, der Vektor der von der bewegenden Kugel weg und zum target hin zeigt, normalisiert
                              n = c.subtract(i);
                              n = n.multiply(-1);
                              n = n.normalised();
                              n.x *= -1;
                              //Bemerkung: Achtung hier evtl velocities von anderen elementen als ParticleBallBehaviours eintragen  

                              //checke ob der Kreis sich auch in richtung der kollisionsnormalen bewegt hat
                              vrn = vVelocity.dot(n);

                              // es hat geknallt

                             
							if (vrn < 0.0) {
                                  
								//berechne J nach altem muster wie bei boden
								J = -(vrn) * (restitution  + 1) * fMass+ staticElement.getBouncyness();
								Fi = n;
								Fi = Fi.multiply(J / totalSpeed);
								vImpactForces = vImpactForces.add(Fi);

								//s ist die groesse der Ueberlappung, n ist der Normalen Vektor der Kollision vposition ist die aktuelle position
								//setze die kugel um diesen wert zurueck

								hasCollision = true;

								if(isGoal(staticElement)){
									gameScoreCounter.addPoints( ((Goal) (staticElement)).getOwnerPlayerID(), ownGoalPoints);
								}
								gameProducer.sendElementHit(staticElement.getId());
								return hasCollision;
							}
						}
					}

				}

			}

		}
          
        //check auf kollision mit dem floppedyFlipps
          for (GameUpdateElement agileElement : agileElements) {
        	  if (agileElement instanceof Flipper){       		  
        		  
        		  //***** Anfang FlipperBerechnung
        		  
        		  Flipper floppedyFlipp = (Flipper)agileElement;
        		//viereck pos und so
                  float olx = floppedyFlipp.getPosition().x;
                  float oly = floppedyFlipp.getPosition().y;
                  //float urx = olx + (float)((Rectangle)floppedyFlipp.getColliderList().get(0)).getWidth();
                  float urx = olx + (float)floppedyFlipp.width;
                  float ury = oly + (float)floppedyFlipp.height;

                  //checke f�r collisionen mit Axis Aligned Bounding box
                  Vector2 v1 = new Vector2(vPosition.x,vPosition.y);
                  Vector2 v2 = new Vector2(vPreviousPosition.x,vPreviousPosition.y);

                  Vector2 quadMittelPunkt = new Vector2(olx + (urx - olx) / 2f, oly + (ury - oly) / 2f);

                  v1.y = (v1.y * -1 + gameFieldHeight);
                  v2.y = (v2.y * -1 + gameFieldHeight);

                  //bekomme die aktuelle drehung
                  float degree = floppedyFlipp.rotation*-1;
           
                  //rotiere um alternativen Mittelpunkt
                  //v1 = rotate_point(altMidpoint, degree, v1);
                  //v2 = rotate_point(altMidpoint, degree, v2);
                  
                  //Die berechnungen vom Hebel gehen anders weil er ein anderes Rotationszentrum hat
                  Vector2 altMidpoint = quadMittelPunkt;
                  
                  if( floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_1_LEFT
                  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_2_LEFT){
                	  altMidpoint = new Vector2(
                			  
            	              quadMittelPunkt.x-(floppedyFlipp.width/2)+floppedyFlipp.rotationOffset, 
            	              quadMittelPunkt.y);
                	  
                  }
              
                  if(floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_1_RIGHT
                  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_2_RIGHT){
                	  altMidpoint = new Vector2(
                			  
            	              quadMittelPunkt.x+(floppedyFlipp.width/2)-floppedyFlipp.rotationOffset, 
            	              quadMittelPunkt.y);
                	  
                  }
                  
                  if(floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_3_RIGHT
            	  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_4_RIGHT){
                	  
            	        	  altMidpoint = new Vector2(
            	        			  quadMittelPunkt.x,
            	    	              quadMittelPunkt.y+(floppedyFlipp.height/2)-floppedyFlipp.rotationOffset
            	    	      );
            	  }
                  
                  if(floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_3_LEFT
            	 ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_4_LEFT){
                	  
            	        	  altMidpoint = new Vector2(
            	        			  quadMittelPunkt.x,
            	        			  quadMittelPunkt.y-(floppedyFlipp.height/2)+floppedyFlipp.rotationOffset
            	    	      );
            	  }
                  
                  
                  v1 = rotate_point(altMidpoint, degree, v1);
                  v2 = rotate_point(altMidpoint, degree, v2);
        	          
                  if (intersects(v2, new Bounds(olx, oly, urx, ury)))
                  {
                	  	  //wenn Kugel oberhalb nehme nach oben stehende normale
                          n = generateNormal(v2, oly, ury, quadMittelPunkt, floppedyFlipp.width,floppedyFlipp.height);

                          boolean sidehit = false;

                          if (n.x == 1 || n.x == -1)
                          {
                              sidehit = true;
                          }
                          
                          //aufgrund des aufbaus von flipper 3 und 4 müssen hier sidehits gewechselt werden
                          if(floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_3_RIGHT
                    	  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_4_RIGHT
                    	  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_3_LEFT
             	    	  ||  floppedyFlipp.getElementType() == ElementType.FLIPPER_PLAYER_4_LEFT
                          ){
                        	  sidehit = !sidehit;
                          }
                          
                          //vPosition.Y = boden;
                          //rotiere die normale um den mittelpunkt nach gradzahl vom Viereck
                          n = rotate_point(new Vector2(0, 0), floppedyFlipp.rotation*-1 , n);

                          //n.X *= -1;
                          //Bemerkung: Achtung hier evtl velocities von anderen elementen als ParticleBallBehaviours eintragen  

                          //checke ob der Kreis sich auch in richtung der kollisionsnormalen bewegt hat
                          vrn = vVelocity.dot(n);

                          //je weiter die Kugel vom rotationszentrum entfernt ist desto gr�sser ist die kraft die auf die Kugel wirkt

                          //adde nur forces wenn es ein sidehit war
                          if (!sidehit)
                          {
                              //rechne diese force nur drauf wenn animation true ist
                        	  
                              if (floppedyFlipp.animationState && !floppedyFlipp.isHolding){
                        	  
                                  Vector2 rotationVector = v2.subtract(rotate_point(altMidpoint, floppedyFlipp.rotationSpeed, v2));
                                  
        						//mindest hitlaenge ist colliderradius
                                  float length = rotationVector.length()+floppedyFlipp.hebelForce;
                                  vImpactForces = vImpactForces.add( n.multiply((length*length*length* 20f)));
                           
                              }
                          }
                          else
                          {
                              //ernn side hit adde eine kleine force um den ball wegzudruecken
                              vImpactForces = vImpactForces.add(n.multiply(50f));
                          }

                          hasCollision = true;
                          
                          if(blockSoundXFrames<=0){
                        	  
                        	  gameScoreCounter.addPoints(floppedyFlipp.getOwnerPlayerID(), flipPoints);
                        	  gameProducer.sendElementHit(floppedyFlipp.getId());
                        	 
                        	  blockSoundXFrames = XFrames;
                          }
                          //kraefte der reflektion aufrechnen
                          if (vrn < 0.0)
                          {

                              //berechne J nach altem muster wie bei boden
                        	  J = -(vrn) * (restitution + 1) * fMass;
                              Fi = n;
                              Fi = Fi.multiply( J / totalSpeed);
                              vImpactForces = vImpactForces.add(Fi);
                              
                          }
                          
                         
                  }else{
                	  
                	  if(blockSoundXFrames>0){
                    	  
                    	  blockSoundXFrames -= 1;
                      }
                	  
                	  
                  }

                //***** Ende FlipperBerechnung
        		  
        		  //hasCollision = calculateFlipperCollision((Flipper)agileElement, n, vrn, J, Fi);
			} else if (agileElement instanceof Libero  && canCollideWithLiberos) {
				
				Circle circle = (Circle) agileElement.getColliderList().get(0);
				
        		  
          		//hole dir die pos des colliders
                // ka warum -10f und +10 am ende!!!
				Vector2 k = new Vector2(circle.getPos().x, circle.getPos().y * -1 + gameFieldHeight);

                //d ist der vektor zwischen beiden kugeln
                Vector2 d = vPosition.subtract(k);
                    
                    //wenn der Abstand beider Kugeln kleiner rbeider radien
				float s = d.length() - (colliderRadius + circle.getRadius());
				if (s <= 0.0) {
					// kollisionsnormale abfragen, der Vektor der von der bewegenden Kugel weg und zum target hin zeigt, normalisiert
					n = d.normalised();
                      
                   
                    //Bemerkung: Achtung hier evtl velocities von anderen elementen als ParticleBallBehaviours eintragen  
                        
                    //checke ob der Kreis sich auch in richtung der kollisionsnormalen bewegt hat
                    vrn = vVelocity.dot(n);
        		  
        		  
        		  
					//wenn der ball mit dem libero kollidiert sto�e den ball vom mittelpunkt weg
						  
					//berechne J nach altem muster wie bei boden
					//J = -(vrn) * (restitution + 1) * fMass;
					//Fi = n.multiply(J / totalSpeed);
					//vImpactForces = vImpactForces.add(Fi);
						
					//s ist die groesse der Ueberlappung, n ist der Normalen Vektor der Kollision vposition ist die aktuelle position
					//setze die kugel um diesen wert zurueck
					//vPosition = vPosition.subtract(n.multiply(s));
						
					//sprung effekt draufrechnen
					//Vector2 correctedBallPosition = new Vector2();
					//correctedBallPosition.x = position.y*-1+ gameFieldHeight;
					//correctedBallPosition.y = position.x;
					vImpactForces = vImpactForces.add(d.multiply(agileElement.getBouncyness()));
					gameScoreCounter.addPoints( ((Libero) (agileElement)).getOwnerPlayerID(), liberoPoints);

					hasCollision = true;
					if(!lstFrameWasLiberoCollision){
						gameProducer.sendElementHit(agileElement.getId());
					}
					lstFrameWasLiberoCollision = true;
					return hasCollision;
				}else{
					lstFrameWasLiberoCollision = false;
					
				}
			} else if (agileElement instanceof Plunger && canCollideWithPlunger) {
				Circle circle = (Circle) agileElement.getColliderList().get(0);

				// hole dir die pos des colliders
				// ka warum -10f und +10 am ende!!!
				Vector2 k = new Vector2(circle.getPos().x, circle.getPos().y * -1 + gameFieldHeight);

				// d ist der vektor zwischen beiden kugeln
				Vector2 d = vPosition.subtract(k);

				// wenn der Abstand beider Kugeln kleiner rbeider radien
				float s = d.length() - (colliderRadius + circle.getRadius());
				if (s <= 0.0) {
					// kollisionsnormale abfragen, der Vektor der von der bewegenden Kugel weg und zum target hin zeigt, normalisiert
					n = d.normalised();

					// Bemerkung: Achtung hier evtl velocities von anderen elementen als ParticleBallBehaviours eintragen

					// checke ob der Kreis sich auch in richtung der kollisionsnormalen bewegt hat
					vrn = vVelocity.dot(n);

					if (vrn < 0.0) {

						// berechne J nach altem muster wie bei boden
						J = -(vrn) * (restitution + agileElement.getBouncyness() + 1) * fMass;
						Fi = n.multiply(J / totalSpeed);
						vImpactForces = vImpactForces.add(Fi);

						// s ist die groesse der Ueberlappung, n ist der Normalen Vektor der Kollision vposition ist die aktuelle position 
						// setze die kugel um diesen wert zurueck
						vPosition = vPosition.subtract(n.multiply(s));

						hasCollision = true;
						
						gameProducer.sendElementHit(agileElement.getId());
						return hasCollision;

					}
				}
			}
		}
 
		vPreviousPosition = vPosition;
		return hasCollision;
		
      }
	  
      private boolean isGoal(GameElement staticElement) {
    	  if(     staticElement.getElementType()== ElementType.GOAL_PLAYER_1 ||
    			  staticElement.getElementType()== ElementType.GOAL_PLAYER_2 ||
    			  staticElement.getElementType()== ElementType.GOAL_PLAYER_3 ||
    			  staticElement.getElementType()== ElementType.GOAL_PLAYER_4)
    	  {
    		  vPreviousPosition= new Vector2(gameFieldWidth/2, gameFieldHeight/2);
    		  vPosition= new Vector2(gameFieldWidth/2, gameFieldHeight/2);
    		  //keine force wie z.b. wind die wirkt
    		  vForces = new Vector2(0,0);
    		  vImpactForces = new Vector2(0,0);
    		  
    		  //velocity ist was ge�ndert werden muss f�r den ansto� den plumbers
    		  vVelocity = new Vector2(0,0);
    		  
    		  //schuss realisieren
    		  
    		  //count dazu auf 3 setzen und die scedule methode aktivieren
    		  if(plunger == null){
    			  
    			  	//plunger laden falls null
    				for (GameElement gameElement : agileElements) {
    					if(gameElement instanceof Plunger){
    						plunger = (Plunger)gameElement;
    						
    						canCollideWithLiberos = false;
    						canCollideWithPlunger = false;
    						plunger.startCount();
    					}
    				}
    				
    				return true;
    			  
    		  }else{
    			  canCollideWithLiberos = false;
    			  canCollideWithPlunger = false;
    			  plunger.startCount();
    			  //ball kann nicht mit den liberos kollidieren wenn er im plunger ist
    			  
    			  return true;
    		  }
    		  
    	  }
    	  
    	  return false;
		
	}

	private Vector2 generateNormal(Vector2 v2, float oly,float ury, Vector2 quadMittelPunkt,float width, float height) {

          //wenn es nicht oben war und nicht unten aber kollidiert ist dann ist es links oder rechts
          if (v2.x < quadMittelPunkt.x- width/2)
          {
              //linke normale
              //Console.WriteLine("Links");
              return new Vector2(-1, 0);
          }

          if (v2.x > quadMittelPunkt.x + width / 2)
          {
              //rechte normale
              //Console.WriteLine("Rechts");
              return new Vector2(1, 0);
          }


          //oben und unten detektiv 
          if (v2.y < oly)
          {
              //drueber
              //Console.WriteLine("Drueber");
              return new Vector2(0, 1);
          }

          if (v2.y > ury)
          {
              //drunner
              //Console.WriteLine("Drunner");
              return new Vector2(0, -1);
          }
          return new Vector2(0,0);
      }
	  
	  private boolean intersects(Vector2 circlePosition, Bounds rect)
      {
          
          float rectWidth = rect.right - rect.left;
          float rectHeight = rect.bottom - rect.top;
          float rectPosX = rect.left + rectWidth / 2f;
          float rectPosY = rect.top + rectHeight / 2f;

          Vector2 circleDistance = new Vector2();

          circleDistance.x = Math.abs(circlePosition.x - rectPosX);
          circleDistance.y = Math.abs(circlePosition.y - rectPosY);
         
         if (circleDistance.x > (rectWidth / 2 + colliderRadius)) { return false; }
         if (circleDistance.y > (rectHeight / 2 + colliderRadius)) { return false; }

          
         if (circleDistance.x <= (rectWidth / 2)) { return true; }
         if (circleDistance.y <= (rectHeight / 2)) { return true; }
          
         float cornerDistance_sq = (circleDistance.x - rectWidth / 2) * (circleDistance.x - rectWidth / 2) + (circleDistance.y - rectHeight / 2) * (circleDistance.y - rectHeight / 2);
          
         return (cornerDistance_sq <= (colliderRadius* colliderRadius));
          
      }

      private Vector2 rotate_point(Vector2 cv, float angle, Vector2 p)
      {
          angle = angle / 180f * (float)Math.PI;
          float s = (float)Math.sin(angle);
          float c = (float)Math.cos(angle);

          p.x -= cv.x;
          p.y -= cv.y;
          
          float xnew = p.x * c - p.y * s;
          float ynew = p.x * s + p.y * c;
          
          p.x = xnew + cv.x;
          p.y = ynew + cv.y;
          return p;
      }
      
      private Intersection checkIntersection(Bounds bounds, Vector2 start, Vector2 end, float radius)
      {
          final float L = bounds.left;
          final float T = bounds.top;
          final float R = bounds.right;
          final float B = bounds.bottom;

          final float dx = end.x - start.x;
          final float dy = end.y - start.y;
          final float invdx = (dx == 0.0f ? 0.0f : 1.0f / dx);
          final float invdy = (dy == 0.0f ? 0.0f : 1.0f / dy);
          float cornerX = Float.MAX_VALUE;
          float cornerY = Float.MAX_VALUE;

          
          /** Left **/
          if ( start.x - radius < L && end.x + radius > L )
          {
              float ltime = ((L - radius) - start.x) * invdx;
              if (ltime >= 0.0f && ltime <= 1.0f)
              {
                  float ly = dy * ltime + start.y;
                  // war die kollision links?
                  if (ly >= T && ly <= B)
                  {
                      return new Intersection( dx * ltime + start.x, ly, ltime, -1, 0, L, ly );
                  }
              }
              cornerX = L;
          }

          /** Right **/
          if ( start.x + radius > R && end.x - radius < R )
          {
              float rtime = (start.x - (R + radius)) * -invdx;
              if (rtime >= 0.0f && rtime <= 1.0f)
              {
                  float ry = dy * rtime + start.y;
                  // war die kollision rechts?
                  if (ry >= T && ry <= B)
                  {
                      return new Intersection( dx * rtime + start.x, ry, rtime, 1, 0, R, ry );
                  }
              }
              cornerX = R;
          }

          /** Top **/
          if (start.y - radius < T && end.y + radius > T)
          {
              float ttime = ((T - radius) - start.y) * invdy;
              if (ttime >= 0.0f && ttime <= 1.0f)
              {
                  float tx = dx * ttime + start.x;
               // war die kollision Oben?
                  if (tx >= L && tx <= R)
                  {
                      return new Intersection( tx, dy * ttime + start.y, ttime, 0, -1, tx, T );
                  }
              }
              cornerY = T;
          }
   
          /** Bottom **/
          if (start.y + radius > B && end.y - radius < B)
          {
              float btime = (start.y - (B + radius)) * -invdy;
              if (btime >= 0.0f && btime <= 1.0f) {
                  float bx = dx * btime + start.x;
                  // war die kollision unten?
                  if (bx >= L && bx <= R)
                  {
                      return new Intersection( bx, dy * btime + start.y, btime, 0, 1, bx, B );
                  }
              }
              cornerY = B;
          }

          // Es gab karkeine intersektion!
          if (cornerX == Float.MAX_VALUE && cornerY == Float.MAX_VALUE)
          {
              return null;
          }

          if (cornerX != Float.MAX_VALUE && cornerY == Float.MAX_VALUE)
          {
              cornerY = (dy > 0.0f ? B : T);
          }
          if (cornerY != Float.MAX_VALUE && cornerX == Float.MAX_VALUE)
          {
              cornerX = (dx > 0.0f ? R : L);
          }

          /* S = start
           * E = ende
           * LTRB = Rectangle seite
           * I = {ix, iY} = punkt andem der kreis das rectangle trifft
           * C = Ecke der Intersektion
           * C=>I (r) = {nx, ny} = radius und intersektionsnormale
           * S=>C = eckendistanz
           * S=>I = intersectionDistanz
           * S=>E = l�nge
           * <S = innterer Winkel
           * <I = winkel1
           * <C = winkel2
           */

          double inverseRadius = 1.0 / radius;
          double lineLength = Math.sqrt( dx * dx + dy * dy );
          double cornerdx = cornerX - start.x;
          double cornerdy = cornerY - start.y;
          double cornerDistance = Math.sqrt( cornerdx * cornerdx + cornerdy * cornerdy );
          double innerAngle = Math.acos( (cornerdx * dx + cornerdy * dy) / (lineLength * cornerDistance) );
          
          // keine intersektion wenn der kreis zu nahe ist
          if (cornerDistance < radius)
          {
             return null;
          }
          
          // wenn der winkel 0 ist gabs  auch nix
          if (innerAngle == 0.0f)
          {
             float time = (float)((cornerDistance - radius) / lineLength);
             
             if (time > 1.0f || time < 0.0f)
             {
                 return null;
             }
             
             float ix = time * dx + start.x;
             float iy = time * dy + start.y;
             float nx = (float)(cornerdx / cornerDistance);
             float ny = (float)(cornerdy / cornerDistance);
             
             return new Intersection( ix, iy, time, nx, ny, cornerX, cornerY );
          }
          
          double innerAngleSin = Math.sin( innerAngle );
          double angle1Sin = innerAngleSin * cornerDistance * inverseRadius;

          if (Math.abs( angle1Sin ) > 1.0f)
          {
              return null;
          }

          double angle1 = Math.PI - Math.asin( angle1Sin );
          double angle2 = Math.PI - innerAngle - angle1;
          double intersectionDistance = radius * Math.sin( angle2 ) / innerAngleSin;

          // Solve for time
          float time = (float)(intersectionDistance / lineLength);

          if (time > 1.0f || time < 0.0f)
          {
              return null;
          }

          float ix = time * dx + start.x;
          float iy = time * dy + start.y;
          float nx = (float)((ix - cornerX) * inverseRadius);
          float ny = (float)((iy - cornerY) * inverseRadius);
          
          if(!Float.isNaN(ix)&&
        	 !Float.isNaN(ix)&&
        	 !Float.isNaN(iy)&&
        	 !Float.isNaN(time)&&
        	 !Float.isNaN(nx)&&
        	 !Float.isNaN(ny)&&
        	 !Float.isNaN(cornerX)&&
        	 !Float.isNaN(cornerY)
        	){
        	  
        	  return new Intersection( ix, iy, time, nx, ny, cornerX, cornerY );
          }else{
        	  return null;
        	  
          }
          
          
      }
      
     
      
}

