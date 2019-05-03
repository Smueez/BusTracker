package com.example.user.bustracker;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class Bus_list extends ArrayAdapter<PassDataDriver> {

    private Activity context;
    private List<PassDataDriver> list;

    public Bus_list(Activity context,List<PassDataDriver>list){
        super(context,R.layout.list_layout,list);

        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View mylistview = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewName = mylistview.findViewById(R.id.textView6);
        TextView textViewDirtn = mylistview.findViewById(R.id.textView5);
        TextView textViewDriverID = mylistview.findViewById(R.id.textView7);
        TextView textViewemailID = mylistview.findViewById(R.id.textView8);

        PassDataDriver driver = list.get(position);

        textViewDirtn.setText(driver.getDirection());
        textViewName.setText(driver.getName());
        textViewDriverID.setText(driver.getDriverID());
        textViewemailID.setText(driver.getEmailId());
        return mylistview;
    }
}
