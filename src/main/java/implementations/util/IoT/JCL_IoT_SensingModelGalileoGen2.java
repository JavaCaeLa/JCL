package implementations.util.IoT;

import interfaces.kernel.JCL_IoT_Sensing_Model;

class JCL_IoT_SensingModelGalileoGen2 implements JCL_IoT_Sensing_Model{
	
	int model[] = {
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,	// digital pins
			0, 1, 2, 3, 4, 5	// analog pins
	};
	
	@Override
	public int getGPIO(int port) {
		return model[port];
	}
	
	@Override
	public boolean validPin(int port) {
		if ( port < 0 || port > model.length )
			return false;
		return true;
	}
	
	@Override
	public boolean isPortAnalog(int port) {
		if (port > 13 && port < 20)
			return true;
		else
			return false;
	}
	
	@Override
	public boolean isPortDigital(int port) {
		if (port >= 0 && port <= 13)
			return true;
		else
			return false;
	}
	
	@Override
	public boolean specialPin(int port) {
		return false;
	}
}
