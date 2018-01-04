package commom;

import android.os.Environment;

import implementations.dm_kernel.MessageBoolImpl;
import implementations.dm_kernel.MessageClassImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGetHostImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageGlobalVarObjImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageListGlobalVarImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageLongImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.MessageTaskImpl;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public final class Constants {
    public final static class Serialization{
        public static final int MSG = 0;
        public static final int MSG_COMMONS = 1;
        public static final int MSG_CONTROL = 2;
        public static final int MSG_GETHOST = 3;
        public static final int MSG_GLOBALVARS = 4;
        public static final int MSG_REGISTER = 5;
        public static final int MSG_RESULT = 6;
        public static final int MSG_TASK = 7;
        public static final int MSG_LISTTASK = 8;
        public static final int MSG_GENERIC = 9;
        public static final int MSG_LONG = 10;
        public static final int MSG_BOOL = 11;
        public static final int MSG_CLASS = 12;
        public static final int MSG_LISTGLOBALVARS = 13;
        public static final int MSG_GLOBALVARSOBJ = 14;
        public static final int MSG_SENSOR = 15;
        public static final int MSG_METADATA = 16;

        public static final Schema[] schema =
                {
                        RuntimeSchema.getSchema(MessageImpl.class),
                        RuntimeSchema.getSchema(MessageCommonsImpl.class),
                        RuntimeSchema.getSchema(MessageControlImpl.class),
                        RuntimeSchema.getSchema(MessageGetHostImpl.class),
                        RuntimeSchema.getSchema(MessageGlobalVarImpl.class),
                        RuntimeSchema.getSchema(MessageRegisterImpl.class),
                        RuntimeSchema.getSchema(MessageResultImpl.class),
                        RuntimeSchema.getSchema(MessageTaskImpl.class),
                        RuntimeSchema.getSchema(MessageListTaskImpl.class),
                        RuntimeSchema.getSchema(MessageGenericImpl.class),
                        RuntimeSchema.getSchema(MessageLongImpl.class),
                        RuntimeSchema.getSchema(MessageBoolImpl.class),
                        RuntimeSchema.getSchema(MessageClassImpl.class),
                        RuntimeSchema.getSchema(MessageListGlobalVarImpl.class),
                        RuntimeSchema.getSchema(MessageGlobalVarObjImpl.class),
                        RuntimeSchema.getSchema(MessageSensorImpl.class),
                        RuntimeSchema.getSchema(MessageMetadataImpl.class)
                };
    }
    public final static class Environment{
        public static final String VmName = System.getProperty("java.vm.name");

        public static final int UDPPORT = 9696;

        public static final String JCLRoot(){
            if(VmName.equalsIgnoreCase("Dalvik")){
                String rootPath = android.os.Environment.getExternalStorageDirectory().toString();
                return rootPath+"/jcl_conf/";
            }else{
                return "../jcl_conf/";
            }
        }
        public static final String JCLConfig(){
            if(VmName.equalsIgnoreCase("Dalvik")){
                String rootPath = android.os.Environment.getExternalStorageDirectory().toString();
                return rootPath+"/jcl_conf/config.properties";
            }else{
                return "../jcl_conf/config.properties";
            }
        }
    }

    public final static class IoT{
        public static int TYPE_ACCELEROMETER = 0;
        public static int TYPE_AMBIENT_TEMPERATURE = 1;
        public static int TYPE_GRAVITY = 2;
        public static int TYPE_GYROSCOPE = 3;
        public static int TYPE_LIGHT = 4;
        public static int TYPE_LINEAR_ACCELERATION = 5;
        public static int TYPE_MAGNETIC_FIELD = 6;
        public static int TYPE_PRESSURE = 7;
        public static int TYPE_PROXIMITY = 8;
        public static int TYPE_RELATIVE_HUMIDITY = 9;
        public static int TYPE_ROTATION_VECTOR = 10;
        public static int TYPE_GPS = 11;
        public static int TYPE_AUDIO = 12;
        public static int TYPE_PHOTO = 13;

        public static String INPUT = "input";
        public static String OUTPUT = "output";
        public static int GENERIC = 0;
        public static int SERVO = 1;
    }
}
