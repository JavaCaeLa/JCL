package implementations.dm_kernel;

import interfaces.kernel.JCL_message_register;
import io.protostuff.Tag;

import java.io.File;
import java.io.FileInputStream;

import commom.Constants;

public class MessageRegisterImpl implements JCL_message_register {


    /**
     *
     */
    private static final long serialVersionUID = 2634372690930845808L;

    @Tag(1)
    private int type;
    @Tag(2)
    private byte[][] jars;
    @Tag(3)
    private String[] jarsNames;
    @Tag(4)
    private String className;
    @Tag(5)
    private byte typeD;

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
        try {
            this.jars = new byte[jars.length][];
            int i = 0;
            for (File f : jars) {
                byte[] oneF = new byte[(int) f.length()];
                FileInputStream fis = new FileInputStream(f);
                fis.read(oneF);
                this.jars[i] = oneF;
                i++;
                fis.close();
            }
        } catch (Exception e) {
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

        for (int i = 0; i < jarsNames.length; i++) {
            this.jarsNames[i] = jarsNames[i].getName();
        }
    }

    @Override
    public void setJarsNames(String[] files) {
        this.jarsNames = files;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getMsgType() {
        return Constants.Serialization.MSG_REGISTER;
    }

	@Override
	public byte getTypeDevice() {
		// TODO Auto-generated method stub
		return typeD;
	}

	@Override
	public void setTypeDevice(byte typeDevice) {
		typeD = typeDevice;		
	}
    
    
}
