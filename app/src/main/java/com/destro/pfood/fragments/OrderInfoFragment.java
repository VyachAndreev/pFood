package com.destro.pfood.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.AppUser;
import com.destro.pfood.classes.FoodCollectable;
import com.destro.pfood.R;
import com.destro.pfood.classes.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrderInfoFragment extends Fragment {

    private View rootView;
    private ArrayAdapter<String> adapter;

    private TextView status;
    private TextView name;
    private TextView phone;
    private TextView price;
    private ListView listView;
    private TextView address;
    private EditText comment;
    private TextView time;
    private TextView paymentType;
    private Button button, buttonEdit, buttonApply, buttonDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_description, container, false);

        status = rootView.findViewById(R.id.order_status);
        name = rootView.findViewById(R.id.order_name);
        phone = rootView.findViewById(R.id.order_phone);
        price = rootView.findViewById(R.id.order_price);
        listView = rootView.findViewById(R.id.food_listview);
        address = rootView.findViewById(R.id.order_address);
        comment = rootView.findViewById(R.id.order_commentary);
        button = rootView.findViewById(R.id.order_button);
        time = rootView.findViewById(R.id.order_time);
        paymentType = rootView.findViewById(R.id.order_payment);
        buttonEdit = rootView.findViewById(R.id.order_edit);
        buttonApply = rootView.findViewById(R.id.order_apply);
        buttonDelete = rootView.findViewById(R.id.order_delete);

        String st = AppSettings.getInstance().clickedOrder.getStatus();
        String s = AppSettings.getInstance().statusColor(st);
        if (s != null){
            status.setBackgroundColor(Color.parseColor(s));
            status.setText(st);
        } else{
            status.setText("Статус не задан");
        }
        name.setText(AppSettings.getInstance().clickedOrder.getName());
        phone.setText(AppSettings.getInstance().clickedOrder.getPhone());
        time.setText(AppSettings.getInstance().clickedOrder.getTime().get("orderTime"));
        address.setText(AppSettings.getInstance().clickedOrder.getAddress());
        comment.setText(AppSettings.getInstance().clickedOrder.getComment());
        paymentType.setText(AppSettings.getInstance().clickedOrder.getPaymentType());

        if (AppSettings.getInstance().clickedOrder.getPrice() > AppSettings.getInstance().freeDeliveryCost)
            price.setText(AppSettings.getInstance().clickedOrder.getPrice().toString() + "\u20BD");
        else
            price.setText((AppSettings.getInstance().clickedOrder.getPrice() + AppSettings.getInstance().deliveryCost) + "\u20BD");

        Log.i("CHECKCOMMENT", AppSettings.getInstance().clickedOrder.getComment());

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, AppSettings.getInstance().clickedOrder.getFoodCart());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.i("FoodList", AppSettings.getInstance().clickedOrder.getFoodCart().toString());

        if (AppUser.getInstance().getUserLevelAccess() != 2 || st.equals("Отменен") || st.equals("Выполнен") || st.equals("Доставлен")){
            buttonEdit.setVisibility(View.GONE);
            buttonApply.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }
        comment.setClickable(false);
        comment.setFocusable(false);
        if (comment.getText().toString().equals("")) {
            comment.setText("Отсутствует");
        }
        if (AppUser.getInstance().getUserLevelAccess() == 1){
            name.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            if (st.equals("0")) {
                status.setVisibility(View.GONE);
            }
        }
        if (AppUser.getInstance().getUserLevelAccess() == 1 || st.equals("Отменен") ||
                AppUser.getInstance().getUserLevelAccess() == 2 && (st.equals("Выполнен") || st.equals("Получен Курьером")) ||
                st.equals("Доставлен") ) {
            status.setEnabled(false);
            phone.setEnabled(false);
        } else {

            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT > 22) {

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 101);

                            return;
                        }
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phone.getText().toString().trim()));
                        startActivity(callIntent);
                    } else {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phone.getText().toString().trim()));
                        startActivity(callIntent);
                    }
                }
            });

            phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettings.getInstance().clickedOrder.setComment(comment.getText().toString());
                Order order = AppSettings.getInstance().clickedOrder;
                if (order.getFoodCart().size() > 0) {
                    String orderName = AppSettings.getInstance().orderKeyList.get(AppSettings.getInstance().orderList.indexOf(AppSettings.getInstance().clickedOrder));
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("orders").child(orderName).child("address").setValue(order.getAddress().replace("\n", ""));
                    database.child("orders").child(orderName).child("comment").setValue(order.getComment());
                    database.child("orders").child(orderName).child("foodCart").setValue(order.getFoodCart());
                    database.child("orders").child(orderName).child("name").setValue(order.getName());
                    database.child("orders").child(orderName).child("paymentType").setValue(order.getPaymentType());
                    database.child("orders").child(orderName).child("phone").setValue(order.getPhone());
                    database.child("orders").child(orderName).child("price").setValue(order.getPrice());
                    database.child("orders").child(orderName).child("status").setValue(order.getStatus());

                    Toast.makeText(getContext(), "Заказ был успешно изменен!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Заказ пуст!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        status.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                if (AppUser.getInstance().getUserLevelAccess() == 2) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.alert_status_change, null);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    mView.findViewById(R.id.cancelled).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(0);
                            setCook(true);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });

                    mView.findViewById(R.id.got).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(1);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });

                    mView.findViewById(R.id.processed).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(2);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });

                    mView.findViewById(R.id.cooked).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(3);
                            setCook(false);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });

                    mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } else if (AppUser.getInstance().getUserLevelAccess() == 3){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.alert_status_courier, null);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    mView.findViewById(R.id.got_by_courier).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(4);
                            setCourier(true);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });

                    mView.findViewById(R.id.delivered).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeStatus(5);
                            setCourier(false);
                            dialog.dismiss();
                            toOrderFragment();
                        }
                    });
                    mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.alert_yes_no, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mView.findViewById(R.id.alert_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            changeStatus(3);
                            setCook(false);
                        dialog.dismiss();
                        toOrderFragment();
                    }
                });

                mView.findViewById(R.id.alert_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.alert_yes_no, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mView.findViewById(R.id.alert_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeStatus(0);
                        setCook(true);

                        dialog.dismiss();

                        if (AppSettings.getInstance().orderList.size() == 1)
                            AppSettings.getInstance().menuItem.setTitle("Заказы");

                        toOrderFragment();

                    }
                });

                mView.findViewById(R.id.alert_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<FoodCollectable> orderCart = new ArrayList<>();

                for (String s : AppSettings.getInstance().clickedOrder.getFoodCart()) {
                    orderCart.add(new FoodCollectable(AppSettings.getInstance().findSingleFood(s), Integer.parseInt(s.substring(s.lastIndexOf(' ') + 1).replace("шт.", ""))));
                    Log.i("CHECKADDING", orderCart.get(0).getName() + " " + orderCart.get(0).getFoodCount());
                    Log.i("CHECKFOODCART", AppSettings.getInstance().foodCache.get(0).getName());
                }

                AppSettings.getInstance().orderCart = orderCart;

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new OrderEditFragment()).addToBackStack(null).commit();
            }
        });

        comment.setTextColor(Color.parseColor("#000000"));

        return rootView;
    }

    private void changeStatus(int i){
        String status;
        switch (i){
            case 0:
                status = "Отменен";
                break;
            case 1:
                status = "Получен";
                break;
            case 2:
                status = "В работе";
                break;
            case 3:
                status = "Выполнен";
                break;
            case 4:
                status = "Получен Курьером";
                break;
            case 5:
                status = "Доставлен";
                break;
            default:
                status = "";
                break;
        }
        FirebaseDatabase.getInstance().getReference("orders").child(
                AppSettings.getInstance().orderKeyList.get(AppSettings.getInstance().orderList.indexOf(AppSettings.getInstance().clickedOrder)))
                .child("status").setValue(status);
    }

    private void setCourier(boolean isPicked){
        String id = AppUser.getInstance().getUserID();
        String orderID = AppSettings.getInstance().orderKeyList.get(AppSettings.getInstance().orderList.indexOf(AppSettings.getInstance().clickedOrder));
        FirebaseDatabase.getInstance().getReference("orders").child(orderID)
                .child("courier").setValue(id);
        Locale loc = Locale.getDefault();
        String time = new SimpleDateFormat("HH:mm:ss", loc).format(new Date());
        if (isPicked){
            FirebaseDatabase.getInstance().getReference("pickedUp").child(id)
                    .child(orderID).setValue(time);
            FirebaseDatabase.getInstance().getReference("orders").child(orderID)
                    .child("time").child("pickedUpTime").setValue(time);
        } else{
            FirebaseDatabase.getInstance().getReference("delivered").child(id)
                    .child(orderID).setValue(time);
            FirebaseDatabase.getInstance().getReference("orders").child(orderID)
                    .child("time").child("deliveredTime").setValue(time);
        }
    }
    private void setCook(boolean isCancelled){
        String id = AppUser.getInstance().getUserID();
        String orderID = AppSettings.getInstance().orderKeyList.get(AppSettings.getInstance().orderList.indexOf(AppSettings.getInstance().clickedOrder));
        FirebaseDatabase.getInstance().getReference("orders").child(orderID)
                .child("cook").setValue(id);
        Locale loc = Locale.getDefault();
        String time = new SimpleDateFormat("HH:mm:ss", loc).format(new Date());
        if (isCancelled) {
            FirebaseDatabase.getInstance().getReference("cancelled").child(id)
                    .child(orderID).setValue(time);
        } else {
            FirebaseDatabase.getInstance().getReference("cooked").child(id)
                    .child(orderID).setValue(time);
        }
        FirebaseDatabase.getInstance().getReference("orders").child(orderID)
                .child("time").child("completeTime").setValue(time);
    }

    private void toOrderFragment() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.fragment_container, new OrderFragment(), "OrderToFragment").addToBackStack(null).commit();
    }
}
