package implementations.util.IoT;

import interfaces.kernel.JCL_IoT_Sensing_Model;

class JCL_IoT_SensingModelRaspPi2 implements JCL_IoT_Sensing_Model{

	int[] model = { 	INVALID_PIN,		
	/* pin 1 */   INVALID_PIN, INVALID_PIN,	/* pin 2 */ 
	/* pin 3 */	  3, 4,		/* pin 4 */ 
	/* pin 5 */	  5, INVALID_PIN,	/* pin 6 */ 
	/* pin 7 */	  7, 8, 	/* pin 8 */
	/* pin 9 */	  INVALID_PIN, 10,	/* pin 10 */
	/* pin 11 */  11, 12,	/* pin 12 */
	/* pin 13 */  13, INVALID_PIN,	/* pin 14 */
	/* pin 15 */  15, 16,	/* pin 16 */
	/* pin 17 */  INVALID_PIN, 18,	/* pin 18 */
	/* pin 19 */  19, INVALID_PIN,	/* pin 20 */
	/* pin 21 */  21, 22,	/* pin 22 */
	/* pin 23 */  23, 24,	/* pin 24 */
	/* pin 25 */  INVALID_PIN, 26,	/* pin 26 */
	/* pin 27 */  INVALID_PIN, INVALID_PIN,	/* pin 28 */
	/* pin 29 */  29, INVALID_PIN,	/* pin 30 */
	/* pin 31 */  31, 32,	/* pin 32 */
	/* pin 33 */  33, INVALID_PIN,	/* pin 34 */
	/* pin 35 */  35, 36,	/* pin 36 */
	/* pin 37 */  37, 38,	/* pin 38 */
	/* pin 39 */  INVALID_PIN, 40,  /*pin 40*/
	/*pin for camera*/	41
				};
	@Override
	public int getGPIO(int port) {
		if ( port > model.length || port < 0)
			return INVALID_PIN;		
		return model[port];
	}
	
	@Override
	public boolean validPin(int port) {
		if ( port <= 0 || port > model.length || model[port] == INVALID_PIN )
			return false;
		return true;
	}	
	
	@Override
	public boolean isPortAnalog(int port) {
		return false;
	}

	@Override
	public boolean isPortDigital(int port) {
		return true;
	}
	
	@Override
	public boolean specialPin(int port) {
		if (port == 41)
			return true;
		return false;
	}
}
