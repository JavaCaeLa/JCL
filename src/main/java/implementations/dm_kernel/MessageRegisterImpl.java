package implementations.dm_kernel;

import interfaces.kernel.JCL_message_register;
import java.io.File;
import java.io.FileInputStream;

public class MessageRegisterImpl extends MessageImpl implements JCL_message_register{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2634372690930845808L;
	private byte[][] jars;
	private String[] jarsNames;
	
	private String className;

	@Override
	public String getClassName() {
		return this.className;
	}

	@Override
	public void setClassName(String className) {
		this.className = className;
	}
	
	@Override
	public byte[][] getJars() {
		return this.jars;
	}

	@Override
	public void setJars(File[] jars) {
		try{
			this.jars = new byte[jars.length][];
			int i=0;
			for(File f: jars){
				byte[] oneF = new byte[(int)f.length()];
				FileInputStream fis = new FileInputStream(f); 
				fis.read(oneF);
				this.jars[i] = oneF;
				i++;
				fis.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void setJars(byte[][] files) {
		this.jars = files;		
	}

	@Override
	public String[] getJarsNames() {
		return this.jarsNames;
	}

	@Override
	public void setJarsNames(File[] jarsNames) {
		
		this.jarsNames = new String[jarsNames.length];
		
		for(int i=0; i < jarsNames.length; i++){
			this.jarsNames[i] = jarsNames[i].getName();
		}
	}

	@Override
	public void setJarsNames(String[] files){
		this.jarsNames = files;
	}
	
	@Override
	public int getMsgType() {
		return MSG_REGISTER;
	}
}
