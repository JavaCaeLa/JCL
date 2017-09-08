package com.hpc.jcl_android;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import implementations.util.android.AndroidSensor;

/**
 * Created by estevao on 09/12/16.
 */

public class ListViewSensorAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<AndroidSensor> items;
    private int layout;
    private Map<Integer, Boolean> checked;
    Map<Integer, Integer> sensors;

    public ListViewSensorAdapter(Context context,
                                        List<AndroidSensor> items, Map<Integer, Integer> sensors) {
        this.items = items;
        this.layout = R.layout.item_sensors;
        mInflater = LayoutInflater.from(context);
        checked = new HashMap<>();
        this.sensors = sensors;

    }
    public void changeItems(List<AndroidSensor> items) {
        this.items = items;
    }
    public void addItem(AndroidSensor item) {
        items.add(item);
    }
    public int getCount() {
        return items.size();
    }

    public AndroidSensor getItem(int position) {
        return items.get(position);
    }

    public AndroidSensor getItemById(int id) {
        return items.get(sensors.get(id));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public boolean clickItem(int position){
        if (checked.containsKey(position) && checked.get(position)) {
            checked.put(position, false);
            return false;
        }
        else {
            checked.put(position, true);
            return true;
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        AndroidSensor item = items.get(position);
        view = mInflater.inflate(layout, null);
//        CheckBox c = ((CheckBox) view.findViewById(R.id.txtEnable));
//        c.setChecked(item.isParticipation());
//        c.setText(item.getName());
        RelativeLayout l = ((RelativeLayout) view.findViewById(R.id.rlvItemSensor));
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && item.getParticipation().equals("true")) {
            l.setBackgroundDrawable( view.getContext().getResources().getDrawable(R.drawable.background_border2) );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && item.getParticipation().equals("true")){
            l.setBackground( ContextCompat.getDrawable(view.getContext(), R.drawable.background_border2));
        }

        ((TextView) view.findViewById(R.id.txtSensor)).setText(item.getName());
        ((TextView) view.findViewById(R.id.txtEnable)).setText("enabled: "+item.getParticipation());
        ((TextView) view.findViewById(R.id.txtDelay)).setText("Delay: "+item.getDelay() +" (ms)");
        ((TextView) view.findViewById(R.id.txtSize)).setText("Size: "+item.getSize()+" (un)");
        if (item.getAudioTime()!=null) {
            TextView t = (TextView) view.findViewById(R.id.txtAudioSize);
            t.setVisibility(View.VISIBLE);
            t.setText("Audio time: "+item.getAudioTime()+" (s)");
        }
        return view;
    }

    public List<AndroidSensor> getItems() {
        return items;
    }

    public void setItems(List<AndroidSensor> items) {
        this.items = items;
    }

    public Map<Integer, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(Map<Integer, Boolean> checked) {
        this.checked = checked;
    }
}
