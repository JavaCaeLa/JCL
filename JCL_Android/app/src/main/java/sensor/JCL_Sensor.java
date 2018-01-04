package sensor;

import android.hardware.Sensor;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import java.io.Serializable;
import java.util.Vector;

import implementations.dm_kernel.MessageSensorImpl;


public class JCL_Sensor implements Serializable {
    private static final long serialVersionUID = -1157012456191573242L;

    public enum TypeSensor{
        TYPE_ACCELEROMETER (0, "TYPE_ACCELEROMETER", Sensor.TYPE_ACCELEROMETER),
        TYPE_AMBIENT_TEMPERATURE (1, "TYPE_AMBIENT_TEMPERATURE", Sensor.TYPE_AMBIENT_TEMPERATURE),
        TYPE_GRAVITY (2, "TYPE_GRAVITY", Sensor.TYPE_GRAVITY),
        TYPE_GYROSCOPE (3, "TYPE_GYROSCOPE", Sensor.TYPE_GYROSCOPE),
        TYPE_LIGHT (4, "TYPE_LIGHT", Sensor.TYPE_LIGHT),
        TYPE_LINEAR_ACCELERATION (5, "TYPE_LINEAR_ACCELERATION", Sensor.TYPE_LINEAR_ACCELERATION),
        TYPE_MAGNETIC_FIELD (6, "TYPE_MAGNETIC_FIELD", Sensor.TYPE_MAGNETIC_FIELD),
        TYPE_PRESSURE (7, "TYPE_PRESSURE", Sensor.TYPE_PRESSURE),
        TYPE_PROXIMITY (8, "TYPE_PROXIMITY", Sensor.TYPE_PROXIMITY),
        TYPE_RELATIVE_HUMIDITY (9, "TYPE_RELATIVE_HUMIDITY", Sensor.TYPE_RELATIVE_HUMIDITY),
        TYPE_ROTATION_VECTOR (10, "TYPE_ROTATION_VECTOR", Sensor.TYPE_ROTATION_VECTOR),
        TYPE_GPS (11, "TYPE_GPS", 11),
        TYPE_AUDIO (12, "TYPE_AUDIO", 12),
        TYPE_PHOTO (13, "TYPE_PHOTO", 13);

        public int id;
        public int androidId;
        public String name;


        TypeSensor (int id, String name, int androidId){
            this.id = id;
            this.name = name;
            this.androidId = androidId;
        }
    }
//    public final static int TYPE_ACCELEROMETER = 0;
//    public final static int TYPE_AMBIENT_TEMPERATURE = 1;
//    public final static int TYPE_GRAVITY = 2;
//    public final static int TYPE_GYROSCOPE = 3;
//    public final static int TYPE_LIGHT = 4;
//    public final static int TYPE_LINEAR_ACCELERATION = 5;
//    public final static int TYPE_MAGNETIC_FIELD = 6;
//    public final static int TYPE_PRESSURE = 7;
//    public final static int TYPE_PROXIMITY = 8;
//    public final static int TYPE_RELATIVE_HUMIDITY = 9;
//    public final static int TYPE_ROTATION_VECTOR = 10;
//    public final static int TYPE_GPS = 11;
//    public final static int TYPE_AUDIO = 12;
//    public final static int TYPE_PHOTO = 13;
//
//    public final static String NAME_TYPE_ACCELEROMETER = 0;
//    public final static String NAME_TYPE_AMBIENT_TEMPERATURE = 1;
//    public final static String NAME_TYPE_GRAVITY = 2;
//    public final static String NAME_TYPE_GYROSCOPE = 3;
//    public final static String NAME_TYPE_LIGHT = 4;
//    public final static String NAME_TYPE_LINEAR_ACCELERATION = 5;
//    public final static String NAME_TYPE_MAGNETIC_FIELD = 6;
//    public final static String NAME_TYPE_PRESSURE = 7;
//    public final static String NAME_TYPE_PROXIMITY = 8;
//    public final static String NAME_TYPE_RELATIVE_HUMIDITY = 9;
//    public final static String NAME_TYPE_ROTATION_VECTOR = 10;
//    public final static String NAME_TYPE_GPS = 11;
//    public final static String NAME_TYPE_AUDIO = 12;
//    public final static String NAME_TYPE_PHOTO = 13;



    private TypeSensor typeSensor;
    public Object value;
    private String dataType;


    public JCL_Sensor(TypeSensor typeSensor, Object value) {
        this.value = value;
        this.typeSensor = typeSensor;
    }
    public JCL_Sensor(TypeSensor typeSensor, Object value, String dataType) {
        this.value = value;
        this.typeSensor = typeSensor;
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getId() {
        return typeSensor.id;
    }

    public String getNameSensor() {
        return typeSensor.name;
    }

    public MessageSensorImpl convertToMessage_sensor(){
        MessageSensorImpl message = new MessageSensorImpl();
        message.setDevice(JCL_ANDROID_Facade.getInstance().getDevice());
        message.setSensor(typeSensor.id);
        message.setValue(getValue());
        message.setType(27);
        message.setDataType(getDataType());
        return message;
    }


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
