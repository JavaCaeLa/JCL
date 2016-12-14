package implementations.util.IoT;

import interfaces.kernel.JCL_IoT_Sensing_Model;

public class JCL_IoT_SensingModelRetriever {

	public static JCL_IoT_Sensing_Model getSensingModel(String platform){
		if ( platform.equals(JCL_IoT_Sensing_Model.GALILEO_GEN_2) )
			return new JCL_IoT_SensingModelGalileoGen2();
		if ( platform.equals(JCL_IoT_Sensing_Model.RASPBERRY_PI_BPLUS_REV_1) || 
									platform.equals(JCL_IoT_Sensing_Model.RASPBERRY_PI_2_B))
			return new JCL_IoT_SensingModelRaspPi2();
		if ( platform.equals(JCL_IoT_Sensing_Model.BEAGLEBONE_BLACK_REV_B) )
			return new JCL_IoT_SensingModelBeagleboneBlackRevB();
		return null; 
	}
}
