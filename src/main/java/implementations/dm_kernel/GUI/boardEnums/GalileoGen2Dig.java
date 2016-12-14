package implementations.dm_kernel.GUI.boardEnums;

public enum GalileoGen2Dig{
	PIN_0 (0),
    PIN_1 (1),
    PIN_2 (2),
    PIN_3 (3),
    PIN_4 (4),
    PIN_5 (5),
    PIN_6 (6),
    PIN_7 (7),
    PIN_8 (8),
    PIN_9 (9),
    PIN_10 (10),
    PIN_11 (11),
    PIN_12 (12),
    PIN_13 (13);
    
    private final int value;

    private GalileoGen2Dig(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public int getItemCount(){
        return GalileoGen2Dig.values().length - 1;
    }
}
