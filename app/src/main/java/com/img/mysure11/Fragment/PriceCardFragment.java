package com.img.mysure11.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.img.mysure11.Adapter.PriceCardAdapter;
import com.img.mysure11.Extras.ExpandableHeightListView;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.priceCardGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PriceCardFragment extends Fragment {

    ExpandableHeightListView priceCard;
    ArrayList<priceCardGetSet> list;

    Context context;

    public PriceCardFragment() {
        // Required empty public constructor
    }

    public PriceCardFragment(ArrayList<priceCardGetSet> list) {
        this.list = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_price_card, container, false);
        context = getActivity();

        priceCard= v.findViewById(R.id.priceCard);
        priceCard.setExpanded(true);
        priceCard.setAdapter(new PriceCardAdapter(context,list));

        return v;
    }

}
