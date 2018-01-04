package com.jcl.jcl_androidapplication;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private List<AndroidSensorTab> items;
    private int layout;
    private Map<Integer, Boolean> checked;

    public ListViewSensorAdapter(Context context,
                                 List<AndroidSensorTab> items) {
        this.items = items;
        this.layout = R.layout.item_sensors;
        mInflater = LayoutInflater.from(context);
        checked = new HashMap<>();

    }
    public void changeItems(List<AndroidSensorTab> items) {
        this.items = items;
    }
    public void addItem(AndroidSensorTab item) {
        items.add(item);
    }
    public int getCount() {
        return items.size();
    }

    public AndroidSensorTab getItem(int position) {

        return items.get(position);
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
        AndroidSensorTab item = items.get(position);
        view = mInflater.inflate(layout, null);
//        CheckBox c = ((CheckBox) view.findViewById(R.id.txtEnable));
//        c.setChecked(item.isParticipation());
//        c.setText(item.getName());
//        RelativeLayout l = ((RelativeLayout) view.findViewById(R.id.rlvItemSensor));
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && item.getParticipation().equals("true")) {
//            l.setBackgroundDrawable( view.getContext().getResources().getDrawable(R.drawable.background_border2) );
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && item.getParticipation().equals("true")){
//            l.setBackground( ContextCompat.getDrawable(view.getContext(), R.drawable.background_border2));
//        }

        ((TextView) view.findViewById(R.id.txtType)).setText("Name: "+item.getType());
        ((TextView) view.findViewById(R.id.txtDevice)).setText("Device: "+item.getDevice());
        ((TextView) view.findViewById(R.id.txtValue)).setText("Value: "+item.getValue());
//        ((TextView) view.findViewById(R.id.txtSize)).setText("Size: "+item.getSize()+" (un)");
//        if (item.getAudioTime()!=null) {
//            TextView t = (TextView) view.findViewById(R.id.txtAudioSize);
//            t.setVisibility(View.VISIBLE);
//            t.setText("Audio time: "+item.getAudioTime()+" (s)");
//        }
        return view;
    }

    public List<AndroidSensorTab> getItems() {
        return items;
    }

    public void setItems(List<AndroidSensorTab> items) {
        this.items = items;
    }

    public Map<Integer, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(Map<Integer, Boolean> checked) {
        this.checked = checked;
    }
}
