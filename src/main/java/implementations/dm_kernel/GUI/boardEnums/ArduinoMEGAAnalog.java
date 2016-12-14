package implementations.dm_kernel.GUI.boardEnums;

public enum ArduinoMEGAAnalog{
	PIN_A0 (54),
    PIN_A1 (55),
    PIN_A2 (56),
    PIN_A3 (57),
    PIN_A4 (58),
    PIN_A5 (59),
	PIN_A6 (60),
	PIN_A7 (61),
	PIN_A8 (62),
	PIN_A9 (63),
	PIN_A10 (64),
	PIN_A11 (65),
	PIN_A12 (66),
	PIN_A13 (67),
	PIN_A14 (68),
	PIN_A15 (69);
    
    private final int value;

    private ArduinoMEGAAnalog(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public int getItemCount(){
        return ArduinoMEGAAnalog.values().length;
    }
}
