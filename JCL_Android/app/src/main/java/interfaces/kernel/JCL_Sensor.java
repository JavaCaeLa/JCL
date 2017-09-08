package interfaces.kernel;

import java.io.Serializable;

public interface JCL_Sensor extends Serializable {
	public abstract Object getObject();
	
	public abstract void setObject(Object object);
	
	public abstract String getType();
	
	public abstract void showData();
	
	public abstract long getTime();
	
	public abstract void setTime(long time);
	
	public abstract void setDataType(String dataType);
}
