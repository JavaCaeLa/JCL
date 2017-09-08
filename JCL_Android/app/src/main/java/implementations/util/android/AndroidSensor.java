package implementations.util.android;

import sensor.JCL_Sensor;

/**
 * Created by estevao on 03/03/17.
 */

public class AndroidSensor {
    private String name;
    private String size;
    private String participation;
    private String delay;
    private String audioTime;
    private int id;


    public AndroidSensor(int id, String name, String size, String participation, String delay, String audioTime) {
        this.name = name;
        this.size = size;
        this.participation = participation;
        this.delay = delay;
        this.audioTime = audioTime;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getParticipation() {
        return participation;
    }

    public void setParticipation(String participation) {
        this.participation = participation;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
