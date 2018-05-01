package app;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Klasse, welche Serveraktivitäten loggen/ausgeben kann
 * 
 * @author mbeus001
 *
 */
public class ServerLog {
	
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return( "[" + sdf.format(cal.getTime()) + "] ");
	}
	
	public static void logMessage(String message){
		System.out.println(getTime() + message);
	}
	
	public static void logError(String message){
		throw new AssertionError(getTime() + message);
	}
	
	

}
