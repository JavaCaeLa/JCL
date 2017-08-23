package implementations.dm_kernel.GUI.boardEnums;

public enum BeagleboneBlackRevBAnalog {
	P9_33 (79),
	P9_35 (81),
    P9_36 (82),
    P9_37 (83),
    P9_38 (84),
    P9_39 (85),
    P9_40 (86);
	
    private final int value;

    private BeagleboneBlackRevBAnalog(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public int getItemCount(){
        return BeagleboneBlackRevBAnalog.values().length;
    }
    
}
