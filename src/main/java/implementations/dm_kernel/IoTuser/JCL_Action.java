package implementations.dm_kernel.IoTuser;

import java.util.Map.Entry;

public class JCL_Action {
	private String className, methodName;
	private Object[] param;
	private boolean useSensorValue;
	private boolean isActing;
	private Entry<String, String> deviceNickname, actuatorNickname;
	private Long ticket;
	private String hostTicketIP, hostTicketPort, hostTicketMac, hostTicketPortSuperPeer;
	
	public JCL_Action(boolean useSensorValue, Long ticket, String hostTicketIP, String hostTicketPort, String hostTicketMac, String hostTicketPortSuperPeer, String className, String methodName, Object[] param) {
		this.className = className;
		this.methodName = methodName;
		this.param = param;
		this.useSensorValue = useSensorValue;
		this.isActing = false;
		this.ticket = ticket;
		this.hostTicketIP = hostTicketIP;
		this.hostTicketPort = hostTicketPort;
		this.hostTicketMac = hostTicketMac;
		this.hostTicketPortSuperPeer = hostTicketPortSuperPeer;
	}
	
	public JCL_Action(Entry<String, String> deviceNickname, Entry<String, String> actuatorNickname, Object[] commands){
		this.deviceNickname = deviceNickname;
		this.actuatorNickname = actuatorNickname;
		this.param = commands;
		this.isActing = true;
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

	public boolean isActing() {
		return isActing;
	}

	public Entry<String, String> getDeviceNickname() {
		return deviceNickname;
	}
	
	public Entry<String, String> getActuatorNickname() {
		return actuatorNickname;
	}

	public Long getTicket() {
		return ticket;
	}

	public String getHostTicketIP() {
		return hostTicketIP;
	}

	public String getHostTicketPort() {
		return hostTicketPort;
	}

	public String getHostTicketMac() {
		return hostTicketMac;
	}

	public String getHostTicketPortSuperPeer() {
		return hostTicketPortSuperPeer;
	}
	
	
}