package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import collider.Circle;
import collider.Collider;
import collider.Rectangle;
import datatypes.Vector2;
import enums.ElementType;




public class ColliderReader {
	

	private static ColliderReader collider= new ColliderReader();
	private HashMap<ElementType,ArrayList<Collider>> colMap;
	private ColliderReader(){
		colMap=readFile();
	}
	
	public static ColliderReader getInstance(){

		return collider;
	}
	
	/**
	 * return HashMap<ElementType, ArrayList<gameelements.Collider>> 
	 */
	public HashMap<ElementType, ArrayList<Collider>> readFile(){
		
		ArrayList<Collider> collider_objekt=new ArrayList<Collider>();
		HashMap<ElementType, ArrayList<Collider>> colliderMap= new HashMap<ElementType,ArrayList<Collider>>();
		BufferedReader br = null;
		FileReader fr = null;
		Vector2 v= null;
		ElementType key = null;
		String sCurrentLine;
		String cells[];
		Circle kreis;
		Rectangle rect;
		float width,height, rotation,posX, posY;
		boolean startswithKreis=false, startswithRect=false;
		String path="/resources/collider_Coordinates.txt";
		String filePath = new File("").getAbsolutePath();
	 
			try {

				fr = new FileReader(filePath + path);
				
				br = new BufferedReader(new FileReader(filePath + path));
				
				 while ((sCurrentLine = br.readLine()) != null) {
						//read Line by Line
			    		//if line leer ist, d.h die Collider_List alle Collider vom entsprechenden Objekt hat.
			    		//dann musst du mit einem neuen Collider_List anfangen.
						//speichere den Namen des Spielbausteins in Map als key 
				
			    		if(sCurrentLine.isEmpty()){
							
							colliderMap.put(key, collider_objekt);
							collider_objekt= new ArrayList<Collider>();
							
						}
						else{
	
							if(!sCurrentLine.contains(" ")){
								key= ElementType.convert(sCurrentLine);
							}
							else{
								cells = sCurrentLine.split(" ");
								
								startswithKreis=sCurrentLine.startsWith("Kreis") ;
								startswithRect= sCurrentLine.startsWith("Rectangle");
								
								//if die Zeile mit Kreis anfängt
								//erstelle v= Vector2(cells[1],cels[2])
								//erstelle Circle(v, cells[3])
								if(startswithKreis ){
									
									posX= Float.parseFloat(cells[2].replace(",", "."));
									posY= Float.parseFloat(cells[3].replace(",", "."));
									v= new Vector2(posX, posY);
									
									kreis=new Circle(v, Float.parseFloat(cells[1].replace(",", ".")));
									collider_objekt.add(kreis);
							       
							    }// if die Zeile mit Rectangle anfängt  
									//erstelle vr=Vector2(cells[1], cells[2])
									//erstelle Rectangle(vr, Float.parseFloat(cells[3]), cells[4], cells[5])
								else if(startswithRect ){
									posX= Float.parseFloat(cells[3].replace(",", "."));
									posY= Float.parseFloat(cells[4].replace(",", "."));
									v= new Vector2(posX, posY);
									
									height= Float.parseFloat(cells[2].replace(",", "."));
									width= Float.parseFloat(cells[1].replace(",", "."));
									rotation= Float.parseFloat(cells[5].replace(",", "."));
									
									rect= new Rectangle(v, height, width, rotation);
									collider_objekt.add(rect);
								}
							}

						}
					}

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (br != null)
						br.close();

					if (fr != null)
						fr.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
		return colliderMap;

		}
	/*
	 *Um die ganze Map auszugeben */
	public  void ausgabe( HashMap<ElementType,ArrayList<Collider>> colMap)
	{
		Set<Entry<ElementType, ArrayList<Collider>>> set= colMap.entrySet();
		Iterator<Entry<ElementType, ArrayList<Collider>>> i= set.iterator();

		while(i.hasNext()){
			
			Map.Entry m= (Map.Entry)i.next();
			
			ArrayList<Collider> collider_array= colMap.get(m.getKey());
			for(Collider c: collider_array){
				if(c instanceof Circle){
					Circle ci=(Circle)c;
					System.out.println(ci.toString());
				}else{
					Rectangle re=(Rectangle)c;
					System.out.println(re.toString());
				}
			}
		}
		
	}
	/**Um nur den Collider auszugeben*/
	public void collider_ausgabe( ArrayList<Collider> colliders){
		for(Collider cl:colliders){
			if(cl instanceof Circle){
				Circle ci= (Circle)cl;
				System.out.println(ci.toString());
			}else{
				Rectangle re= (Rectangle)cl;
				System.out.println(re.toString());

			}
		}
	}
	public  ArrayList<Collider> getCollider( ElementType name){
		
		return colMap.get(name);
	}

	
}
