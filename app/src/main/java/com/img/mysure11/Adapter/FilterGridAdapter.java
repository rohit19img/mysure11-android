package com.img.mysure11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mysure11.GetSet.filterGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class FilterGridAdapter extends BaseAdapter{

    Context context;
    ArrayList<filterGetSet> list;

    public FilterGridAdapter(Context context, ArrayList<filterGetSet> list){
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v;

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.filter_item,null);

        TextView filterText =(TextView)v.findViewById(R.id.filterText);
        filterText.setText(list.get(i).getValue());

        if(list.get(i).isSelected())
            v.setBackground(context.getResources().getDrawable(R.drawable.filter_selected));
        else
            v.setBackground(context.getResources().getDrawable(R.drawable.filter_deselected));

        final int finalI = i;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(finalI).isSelected())
                    list.get(finalI).setSelected(false);
                else
                    list.get(finalI).setSelected(true);

                notifyDataSetChanged();
            }
        });

        return v;
    }
}
