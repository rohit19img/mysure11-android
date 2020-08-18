package com.img.mysure11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mysure11.R;

public class SpinnerAdapter extends BaseAdapter{

    Context context;
    String ar[];

    public SpinnerAdapter(Context context, String ar[]){
        this.context= context;
        this.ar= ar;
    }

    @Override
    public int getCount() {
        return ar.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v;
        TextView spinnerText;

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.spinner_text,null);

        spinnerText= (TextView)v.findViewById(R.id.spinnerText);
        spinnerText.setText(ar[i]);

        return v;
    }
}
