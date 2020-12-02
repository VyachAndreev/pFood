package com.destro.pfood.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.destro.pfood.R;

import java.util.ArrayList;

public class OrderCartAdapter extends ArrayAdapter<FoodCollectable> {

    private Context mContext;
    int mResource;
    Order order;

    public OrderCartAdapter(Context context, int resource, ArrayList<FoodCollectable> objects, Order order) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.order = order;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        int count = getItem(position).getFoodCount();
        String imageUrl = getItem(position).getImageUrl();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.cartFoodName);
        ImageView imageView = convertView.findViewById(R.id.cartFoodImage);
        TextView tvCount = convertView.findViewById(R.id.cartFoodCount);
        final TextView tvPrice = convertView.findViewById(R.id.cartFoodPrice);
        ImageButton bAdd = convertView.findViewById(R.id.button_add);
        ImageButton bRemove = convertView.findViewById(R.id.button_remove);

        tvPrice.setText(getItem(position).getPrice() * count + "\u20BD");

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItem(position).incCount();

                order.setPrice(order.getPrice() + getItem(position).getPrice());

                tvPrice.setText(getItem(position).getFullPrice() + "\u20BD");

                notifyDataSetChanged();
            }
        });

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(position).getFoodCount() != 0) {
                    getItem(position).decCount();
                    order.setPrice(order.getPrice() - getItem(position).getPrice());

                    tvPrice.setText(getItem(position).getFullPrice() + "\u20BD");

                    notifyDataSetChanged();
                }
            }
        });

        tvName.setText(name);
        tvCount.setText(Integer.toString(count));
        Glide.with(imageView).load(imageUrl).into(imageView);

        return convertView;
    }
}
