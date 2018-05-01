package field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import app.ServerLog;
import datatypes.Vector2;
import enums.ElementType;
import gameelements.GameElement;
import gameelements.MotionlessElement;

public abstract class Field {
	
	// statische Uebersicht aller auf dem Server liegenden Spielfelder
	private static ArrayList<String> availableFields = new ArrayList<String>();
	private static boolean availableFieldsUnconsistent = true;
	
	// bewusst ohne private, damit PhysicCalculator drauf zugreifen kann !!
	final int WIDTH  = 800;
	final int HEIGHT = 800;
	
	protected String fieldName;
	
	public static final String FIELDPATH = "/resources/fields/";
	public static final String ESSENTIALELEMENTSFILE = "/resources/EssentialElements/";
	
	
	/**
	 * Aufzurufen bei einer Aenderung von Spielfelddateien ausserhalb dieser Klasse.
	 */
	public static void fieldsOnServerManipulated() {
		availableFieldsUnconsistent = true;
	}
	
	/**
	 * Schaut, welche Spielfelder auf dem Server liegen.
	 */
	private static void browseAvailableFields() {
		availableFields.clear();
		File fieldDirectory = new File(new File("").getAbsolutePath() + Field.FIELDPATH);

		File[] fieldFiles = fieldDirectory.listFiles();
		for (int i = 0; i < fieldFiles.length; i++) {
			if (fieldFiles[i].isFile()) { // Unterordner ignorieren
				availableFields.add(fieldFiles[i].getName());
			}
		}
	}
	
	/**
	 * Gibt die stets aktuell verfuegbaren Spielfelder auf dem Server zurueck
	 * @return Stringliste der Spielfeldnamen
	 */
	public static ArrayList<String> getAvailableFields() {
		if (availableFieldsUnconsistent) {
			ServerLog.logMessage("Spielfeld-Verfügbarkeitsliste wird aktualisiert");
			browseAvailableFields();
			availableFieldsUnconsistent = false;
		}
		return availableFields;
	}

	
	protected void readField() {
		BufferedReader br = null;
		FileReader fr = null;
		String sCurrentLine;
		String cells[];
		
		float posX, posY;
		
		String relPath = FIELDPATH + fieldName;
		String sysLocation = new File("").getAbsolutePath();

		try {
			
			fr = new FileReader(sysLocation + relPath);
			br = new BufferedReader(new FileReader(sysLocation + relPath));

			while ((sCurrentLine = br.readLine()) != null) {
				cells = sCurrentLine.split(" ");
				posX = Float.parseFloat(cells[1].replace(",", "."));
				posY = Float.parseFloat(cells[2].replace(",", "."));
				
				GameElement ele = new MotionlessElement(ElementType.convert(cells[0]), new Vector2(posX, posY));
				addFieldElementToCollection(ele);
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
	}
	
	protected void readEssentialElements() {
		BufferedReader br = null;
		FileReader fr = null;
		String sCurrentLine;
		String cells[];
		
		String path = ESSENTIALELEMENTSFILE;

		String sysLocation = new File("").getAbsolutePath();
		try {
			fr = new FileReader(sysLocation + path);
			br = new BufferedReader(new FileReader(sysLocation + path));

			while ((sCurrentLine = br.readLine()) != null) {
				cells = sCurrentLine.split(" ");
				addPlayerElementToCollection(cells);
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
	}

	protected abstract void addFieldElementToCollection(GameElement ele);
	
	protected abstract void addPlayerElementToCollection(String[] cells);
}
