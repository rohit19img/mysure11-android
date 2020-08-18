package com.img.mysure11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mysure11.GetSet.priceCardGetSet;
import com.img.mysure11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PriceCardAdapter extends BaseAdapter{

    Context context;
    ArrayList<priceCardGetSet> list;

    public PriceCardAdapter(Context context, ArrayList<priceCardGetSet> list){
        this.context=context;
        this.list= list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        TextView rank,price;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.price_card,null);

        rank= (TextView)v.findViewById(R.id.rank);
        price=(TextView)v.findViewById(R.id.price);

        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###");

        rank.setText("Rank: "+list.get(i).getStart_position());
        price.setText("₹ "+nf2.format(Integer.parseInt(list.get(i).getPrice())));

        return v;
    }
}
