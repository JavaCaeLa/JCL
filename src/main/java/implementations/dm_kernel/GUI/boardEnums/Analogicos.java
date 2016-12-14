package implementations.dm_kernel.GUI.boardEnums;

public enum Analogicos{
	PIN_A0 (0),
	PIN_A1 (1),
	PIN_A2 (2),
	PIN_A3 (3),
	PIN_A4 (4),
	PIN_A5 (5);
	
	private final int value;

	private Analogicos(int value) {
    	this.value = value;
    }
	
    public int getValue(){
    	return this.value;
    }
    
    public static int getItemCount(){
    	return Analogicos.values().length;
    }
}
