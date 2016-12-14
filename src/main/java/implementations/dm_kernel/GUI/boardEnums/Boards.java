package implementations.dm_kernel.GUI.boardEnums;

public enum Boards {
	DEFAULT("Selecione o Modelo"),
	GalileoGen2("Intel Galileo Gen 2"),
	ArduinoMEGA("Arduino MEGA"),
	RaspPi2B("Raspberry Pi 2 Model B Rev 1"),
	Android("Android Device"),
	PC_Host("PC Host"),	
	BeagleboneBlack("Beaglebone Black Rev. B");
	
	private final String display;
    private Boards(String s) {
        display = s;
    }
    @Override
    public String toString() {
        return display;
    }
}
