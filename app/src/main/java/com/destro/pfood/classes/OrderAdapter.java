package com.destro.pfood.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.destro.pfood.R;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderAdapter extends ArrayAdapter<Order> {

    private Context mContext;
    int mResource;

    public OrderAdapter(Context context, int resource, ArrayList<Order> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        HashMap<String, String> time = getItem(position).getTime();
        String phone = getItem(position).getPhone();
        String address = getItem(position).getAddress();
        String status = getItem(position).getStatus();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.order_name);
        TextView tvTime = convertView.findViewById(R.id.order_time);
        TextView tvPhone = convertView.findViewById(R.id.order_phone);
        TextView tvAddress = convertView.findViewById(R.id.order_address);
        TextView tvStatus = convertView.findViewById(R.id.order_status);


        if (AppUser.getInstance().getUserLevelAccess() > 1) {
            tvName.setText(name);
        } else{
            tvName.setText("");
            tvPhone.setTextColor(Color.parseColor("#000000"));
        }

        tvPhone.setText(phone);
        tvTime.setText(time.get("orderTime"));
        tvAddress.setText(address);
        tvStatus.setText(status);


        String s = AppSettings.getInstance().statusColor(status);
        if (s != null){
            tvStatus.setTextColor(Color.parseColor(s));
        } else{
            tvStatus.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
