package appl.simpleAppl;

import java.util.concurrent.Future;

import commom.Constants;
import implementations.dm_kernel.IoTuser.JCL_Expression;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_result;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;

public class appl9 {
	public static void main(String[] args) throws Exception {
		new appl9();
	}
	public appl9() throws Exception{
		JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();

		int numMaxRegistro = 1000;	int delay = 1000;
		
		Device arduino = iot.<Device>getIoTDevices().get(0);
		
		iot.setSensorMetadata(arduino, "TYPE_LIGHT", Constants.IoT.TYPE_LIGHT, numMaxRegistro, delay, Constants.IoT.INPUT, Constants.IoT.GENERIC);
		
		Sensor light = iot.<Sensor>getSensorByName(arduino, "TYPE_LIGHT").get(0);

		String ctxName = "myContext";
		
		
		//iot.registerMQTTContext(arduino, light, new JCL_Expression("S0<100"), ctxName+"1");
		
		iot.registerContext(arduino, light, new JCL_Expression("S0<100"), ctxName);

		JCL_IoTfacade.PacuHPC.register(UserServices.class, "userServices");
		
		Future<JCL_result> res = iot.addContextAction(ctxName, false, "userServices", "IoTActionTask", null);
		res.get();
		
		JCL_IoTfacade.PacuHPC.destroy();
	}
}
