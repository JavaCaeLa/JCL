package com.jcl.jcl_androidapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import interfaces.kernel.JCL_Sensor;

/**
 * Created by estevao on 03/03/17.
 */

public class ListViewItemsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Object> items;
    private int layout;

    public ListViewItemsAdapter(Context context, List<Object> items) {
        this.items = items;
        this.layout = R.layout.item_list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }


    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String item = i+" = "+items.get(i).toString().split("::")[0];
        view = mInflater.inflate(layout, null);
        ((TextView) view.findViewById(R.id.txtConf)).setText(item);
        return view;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }
}
