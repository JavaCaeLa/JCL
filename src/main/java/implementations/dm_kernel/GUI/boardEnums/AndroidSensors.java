package implementations.dm_kernel.GUI.boardEnums;

public enum AndroidSensors {
	TYPE_ACCELEROMETER(0),  
	TYPE_AMBIENT_TEMPERATURE(1),  
	TYPE_GRAVITY(2),  
	TYPE_GYROSCOPE(3),  
	TYPE_LIGHT(4), 
	TYPE_LINEAR_ACCELERATION(5),  
	TYPE_MAGNETIC_FIELD(6),  
	TYPE_PRESSURE(7),  
	TYPE_PROXIMITY(8),  
	TYPE_RELATIVE_HUMIDITY(9),  
	TYPE_ROTATION_VECTOR(10),  
	TYPE_GPS(11),  
	TYPE_AUDIO(12), 
	TYPE_PHOTO(13);

	private final int value;

	private AndroidSensors(int value) {
		this.value = value;
	}

	public int getValue(){
		return this.value;
	}

	public int getItemCount(){
		return AndroidSensors.values().length;
	}
}
