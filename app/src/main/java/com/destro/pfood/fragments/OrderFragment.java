package com.destro.pfood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.AppUser;
import com.destro.pfood.classes.IOnBackPressed;
import com.destro.pfood.classes.Order;
import com.destro.pfood.classes.OrderAdapter;
import com.destro.pfood.R;

public class OrderFragment extends Fragment implements IOnBackPressed {

    private View rootView;
    private ListView listView;
    private OrderAdapter adapter;
    private static final String TAG = "OrderFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_layout, container, false);

        getActivity().setTitle("Заказы");


        TextView textView = rootView.findViewById(R.id.text_if_empty);
        listView = rootView.findViewById(R.id.order_listview);
        adapter = new OrderAdapter(getActivity(), R.layout.order_view_layout, AppSettings.getInstance().orderList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        AppSettings.getInstance().orderAdapter = adapter;

        if (AppSettings.getInstance().orderList.isEmpty()) {
            Log.i("adad", "empty");
            listView.setVisibility(View.GONE);
            if (AppUser.getInstance().getUserLevelAccess() > 1) {
                textView.setText("Заказов пока нет");
            } else if (AppUser.getInstance().getUserLevelAccess() == 1){
                textView.setText("Ваш список заказов пуст");
            }
        } else{
            textView.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppSettings.getInstance().clickedOrder = (Order) adapterView.getItemAtPosition(i);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new OrderInfoFragment()).addToBackStack(null).commit();

            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {}
}
