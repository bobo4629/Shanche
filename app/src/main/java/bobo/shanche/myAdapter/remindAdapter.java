package bobo.shanche.myAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bobo.shanche.R;

/**
 * Created by bobo1 on 2016/8/7.
 */

public class remindAdapter extends BaseAdapter{
    private String[] strs;
    private Context pContext;

    public remindAdapter(Context pContext,String[] strs) {
        this.strs = strs;
        this.pContext=pContext;
    }


    @Override
    public int getCount() {
        return strs.length;
    }

    @Override
    public Object getItem(int position) {
        return strs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(pContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent,false);
        }
        TextView textView = (TextView)convertView.findViewById(android.R.id.text1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        textView.setTextColor(Color.WHITE);
        textView.setText(strs[position]);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(pContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
        }
        TextView textView = (TextView)convertView.findViewById(android.R.id.text1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setTextColor(Color.BLACK);
        textView.setText(strs[position]);
        return convertView;
    }
}
