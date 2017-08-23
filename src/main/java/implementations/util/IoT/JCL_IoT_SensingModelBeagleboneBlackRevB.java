package implementations.util.IoT;

import interfaces.kernel.JCL_IoT_Sensing_Model;

class JCL_IoT_SensingModelBeagleboneBlackRevB implements JCL_IoT_Sensing_Model {
	
	int model[] = {
						INVALID_PIN,
	/*PIN 1*/	INVALID_PIN, INVALID_PIN, /* PIN 2*/
	/*PIN 3*/	INVALID_PIN, INVALID_PIN, /* PIN 4*/
	/*PIN 5*/	INVALID_PIN, INVALID_PIN, /* PIN 6*/
	/*PIN 7*/	7, 8, /* PIN 8*/
	/*PIN 9*/	9, 10, /* PIN 10*/
	/*PIN 11*/	11, 12, /* PIN 12*/
	/*PIN 13*/	13, 14, /* PIN 14*/
	/*PIN 15*/	15, 16, /* PIN 16*/
	/*PIN 17*/	17, 18, /* PIN 18*/
	/*PIN 19*/	19, INVALID_PIN, /* PIN 20*/
	/*PIN 21*/	INVALID_PIN, INVALID_PIN, /* PIN 22*/
	/*PIN 23*/	INVALID_PIN, INVALID_PIN, /* PIN 24*/
	/*PIN 25*/	INVALID_PIN, 26, /* PIN 26*/
	/*PIN 27*/	27, 28, /* PIN 28*/
	/*PIN 29*/	29, 30, /* PIN 30*/
	/*PIN 31*/	31, 32, /* PIN 32*/
	/*PIN 33*/	33, 34, /* PIN 34*/
	/*PIN 35*/	35, 36, /* PIN 36*/
	/*PIN 37*/	37, 38, /* PIN 38*/
	/*PIN 39*/	39, 40, /* PIN 40*/
	/*PIN 41*/	41, 42, /* PIN 42*/
	/*PIN 43*/	43, 44, /* PIN 44*/
	/*PIN 45*/	45, 46, /* PIN 46*/
	/*PIN 47*/	INVALID_PIN, INVALID_PIN, /* PIN 48*/
	/*PIN 49*/	INVALID_PIN, INVALID_PIN, /* PIN 50*/
	/*PIN 51*/	INVALID_PIN, INVALID_PIN, /* PIN 52*/
	/*PIN 53*/	INVALID_PIN, INVALID_PIN, /* PIN 54*/
	/*PIN 55*/	INVALID_PIN, INVALID_PIN, /* PIN 56*/
	/*PIN 57*/	57, 58, /* PIN 58*/
	/*PIN 59*/	59, 60, /* PIN 60*/
	/*PIN 61*/	61, 62, /* PIN 62*/
	/*PIN 63*/	63, 64, /* PIN 64*/
	/*PIN 65*/	65, 66, /* PIN 66*/
	/*PIN 67*/	67, 68, /* PIN 68*/
	/*PIN 69*/	69, 70, /* PIN 70*/
	/*PIN 71*/	71, 72, /* PIN 72*/
	/*PIN 73*/	73, 74, /* PIN 74*/
	/*PIN 75*/	75, 76, /* PIN 76*/
	/*PIN 77*/	77, INVALID_PIN, /* PIN 78*/
	/*PIN 79*/	79, INVALID_PIN, /* PIN 80*/
	/*PIN 81*/	81, 82, /* PIN 82*/
	/*PIN 83*/	83, 32, /* PIN 84*/
	/*PIN 85*/	85, 86, /* PIN 86*/
	/*PIN 87*/	87, 88, /* PIN 88*/
	/*PIN 89*/	INVALID_PIN, INVALID_PIN, /* PIN 90*/
	/*PIN 91*/	INVALID_PIN, INVALID_PIN /* PIN 92*/
	};
	
	
	@Override
	public int getGPIO(int port) {
		if ( port > model.length || port < 0)
			return INVALID_PIN;		
		return model[port];
	}

	@Override
	public boolean isPortAnalog(int port) {
		if (port == 79 || (port >= 81 && port <= 86) )
			return true;
		return false;
	}

	@Override
	public boolean isPortDigital(int port) {
		if (port != 79 && (port < 81 || port > 86) )
			return true;
		return false;
	}

	@Override
	public boolean validPin(int port) {
		if ( port < 0 || port > model.length || model[port] == INVALID_PIN )
			return false;
		return true;
	}
	@Override
	public boolean specialPin(int port) {
		return false;
	}
}
