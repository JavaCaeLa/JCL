package implementations.dm_kernel.GUI.boardEnums;

public enum GalileoGen2Analog{
	PIN_A0 (14),
    PIN_A1 (15),
    PIN_A2 (16),
    PIN_A3 (17),
    PIN_A4 (18),
    PIN_A5 (19);
    
    private final int value;

    private GalileoGen2Analog(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public int getItemCount(){
        return GalileoGen2Analog.values().length;
    }
}
