package com.hpc.jcl_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.dx.rop.cst.CstArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import implementations.util.android.AndroidSensor;

/**
 * Created by estevao on 03/03/17.
 */

public class ListViewConfAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String[]> items;
    private Map<Integer, String> mapItems;
    private int layout;

    public ListViewConfAdapter(Context context, List<String[]> items, Map<Integer, String> mapItems) {
        this.items = items;
        this.layout = R.layout.item_list;
        mInflater = LayoutInflater.from(context);
        this.mapItems = mapItems;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    public String[] getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Map<Integer, String> getMapItems() {
        return mapItems;
    }

    public void setMapItems(Map<Integer, String> mapItems) {
        this.mapItems = mapItems;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String item = items.get(i)[0];
        view = mInflater.inflate(layout, null);
        ((TextView) view.findViewById(R.id.txtConf)).setText(item);
        return view;
    }

    public List<String[]> getItems() {
        return items;
    }

    public void setItems(List<String[]> items) {
        this.items = items;
    }
}
