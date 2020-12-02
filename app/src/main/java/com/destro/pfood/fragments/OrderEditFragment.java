package com.destro.pfood.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.Food;
import com.destro.pfood.classes.FoodCollectable;
import com.destro.pfood.classes.Order;
import com.destro.pfood.classes.OrderCartAdapter;
import com.destro.pfood.R;

import java.util.ArrayList;

public class OrderEditFragment extends Fragment {

    private View rootView;
    private ListView listView;
    private OrderCartAdapter adapter;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_edit, container, false);

        listView = rootView.findViewById(R.id.order_edit_listview);
        adapter = new OrderCartAdapter(getActivity(), R.layout.foodcart_view_layout, AppSettings.getInstance().orderCart, AppSettings.getInstance().clickedOrder);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.height = 380 * AppSettings.getInstance().orderCart.size() + 10;
        listView.setLayoutParams(lp);

        button = rootView.findViewById(R.id.order_addposition);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                View mView = getLayoutInflater().inflate(R.layout.alert_addposition, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                ListView alertListView = mView.findViewById(R.id.alert_listview);
                ArrayList<String> orderCache = new ArrayList<>();

                for (Food f : AppSettings.getInstance().foodCache) {
                    orderCache.add(f.getName());
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, orderCache);
                alertListView.setAdapter(arrayAdapter);
                adapter.notifyDataSetChanged();

                alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Food clickedFood = AppSettings.getInstance().findSingleFood((String) adapterView.getItemAtPosition(i));
                        Order order = AppSettings.getInstance().clickedOrder;

                        if (!AppSettings.getInstance().isOrderContains(clickedFood)) {
                            AppSettings.getInstance().orderCart.add(new FoodCollectable(clickedFood, 1));
                            order.setPrice(order.getPrice() + clickedFood.getPrice());
                            adapter.notifyDataSetChanged();
                        }

                        dialog.dismiss();

                        ViewGroup.LayoutParams lp = listView.getLayoutParams();
                        lp.height = 380 * AppSettings.getInstance().orderCart.size() + 10;
                        listView.setLayoutParams(lp);
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        AppSettings.getInstance().clickedOrder.getFoodCart().clear();

        for (FoodCollectable f : AppSettings.getInstance().orderCart) {
            if (f.getFoodCount() != 0) {
                AppSettings.getInstance().clickedOrder.getFoodCart().add(f.getName() + " " + f.getFoodCount() + "шт.");
            }
        }

        AppSettings.getInstance().orderCart.clear();
        super.onDestroyView();
    }
}
