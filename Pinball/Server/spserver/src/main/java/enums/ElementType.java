package enums;


//die Zahl hinter dem Enum ist der Wert der Bouncyness der auf den jeweiligen Typ draufgerechnet wird

public enum ElementType {
	STEIN_0(20),
	STEIN_1(30.3f),
	STEIN_2(40.6f),
	WAND_STARK_0(40.3f),
	WAND_STARK_1(40.3f),
	WAND_STARK_2(40.3f),
	WAND_STARK_3(40.3f),
	WAND_SCHWACH_0(20),
	WAND_SCHWACH_1(20),
	WAND_SCHWACH_2(20),
	WAND_SCHWACH_3(20),
	FLIPPER_PLAYER_1_LEFT(0),FLIPPER_PLAYER_1_RIGHT(0),
	FLIPPER_PLAYER_2_LEFT(0),FLIPPER_PLAYER_2_RIGHT(0),
	FLIPPER_PLAYER_3_LEFT(0),FLIPPER_PLAYER_3_RIGHT(0),
	FLIPPER_PLAYER_4_LEFT(0),FLIPPER_PLAYER_4_RIGHT(0),
	
	LIBERO_PLAYER_1(0),
	LIBERO_PLAYER_2(0),
	LIBERO_PLAYER_3(0),
	LIBERO_PLAYER_4(0),
	
	GOALAREA_PLAYER_1(0),
	GOALAREA_PLAYER_2(0),
	GOALAREA_PLAYER_3(0),
	GOALAREA_PLAYER_4(0),
	
	GOALPOST_PLAYER_1(0),GOALPOST_PLAYER_2(0),
	GOALPOST_PLAYER_3(0),GOALPOST_PLAYER_4(0),
	
	
	GOAL_PLAYER_1(0),
	GOAL_PLAYER_2(0),
	GOAL_PLAYER_3(0),
	GOAL_PLAYER_4(0),
	
	PLUNGER(0),
	
	CURVE_0(0),
	CURVE_1(0),
	CURVE_2(0),
	CURVE_3(0),
	
	BALL(0);
	
	public static ElementType convert(String str) {
        for (ElementType ele : ElementType.values()) {
            if ( ele.toString().equals(str.toUpperCase()) ) {
                return ele;
            }
        }
        return null;
    }
	
	 public static String toLower(ElementType ele){
		 String lower= ele.toString();
		return  lower.toLowerCase();
		 
	 }
	 
	private float value; 
		
	private ElementType(float value) { 
		this.value = value; 
		
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	
	
}
