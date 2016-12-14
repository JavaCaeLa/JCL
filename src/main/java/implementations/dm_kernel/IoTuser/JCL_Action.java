package implementations.dm_kernel.IoTuser;

public class JCL_Action {
	private String className, methodName;
	private Object[] param;
	private boolean useSensorValue;
	
	public JCL_Action(String className, String methodName, Object[] param, boolean useSensorValue) {
		this.className = className;
		this.methodName = methodName;
		this.param = param;
		this.useSensorValue = useSensorValue;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParam() {
		return param;
	}
	public void setParam(Object[] param) {
		this.param = param;
	}

	public boolean isUseSensorValue() {
		return useSensorValue;
	}

	public void setUseSensorValue(boolean useSensorValue) {
		this.useSensorValue = useSensorValue;
	}
}



