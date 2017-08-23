package implementations.dm_kernel.GUI.boardEnums;

public enum RaspPi2BAnalog{
	;
	@SuppressWarnings("unused")
	private final int value;

	private RaspPi2BAnalog(int value) {
		this.value = value;
	}

	public int getValue() {
		return -1;
	}

	public int getItemCount() {
		return 0;
	}

}
