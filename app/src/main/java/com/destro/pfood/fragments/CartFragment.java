package com.destro.pfood.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.AppUser;
import com.destro.pfood.classes.FoodCartAdapter;
import com.destro.pfood.classes.FoodCollectable;
import com.destro.pfood.R;
import com.destro.pfood.navigation.Router;
import com.destro.pfood.utils.FirebaseUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class CartFragment extends Fragment {

    private View rootView;
    private Spinner spinner;
    private FoodCartAdapter foodCartAdapter;
    private ListView foodListView;
    private TextView tvPrice;
    private EditText commentaryET;
    private TextView deliveryAddress;
    String[] data = {"Наличными", "Картой"};

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_cart, container, false);

        AppSettings.getInstance().fullPrice = rootView.findViewById(R.id.cfPrice);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, data);

        spinner = rootView.findViewById(R.id.payment_spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Выберите способ оплаты");

        tvPrice = rootView.findViewById(R.id.deliveryPrice);
        AppSettings.getInstance().deliveryPrice = tvPrice;

        if (AppSettings.getInstance().freeDeliveryCost == null) {}
        else if (AppSettings.getInstance().fullNumPrice >= AppSettings.getInstance().freeDeliveryCost)
            tvPrice.setText("Бесплатно");
        else
            tvPrice.setText(AppSettings.getInstance().deliveryCost + " \u20BD");

        commentaryET = rootView.findViewById(R.id.commentary);

        deliveryAddress = rootView.findViewById(R.id.delivery_address);

        deliveryAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deliveryAddress.setRawInputType(InputType.TYPE_CLASS_TEXT);
        deliveryAddress.setTextColor(Color.parseColor("#000000"));

        final ArrayList<FoodCollectable> foodCollectable = new ArrayList<>();
        for (FoodCollectable f : AppSettings.getInstance().foodCart) {
            if (f.getFoodCount() != 0) {
                foodCollectable.add(f);
            }
        }

        foodListView = rootView.findViewById(R.id.listview_food);
        foodCartAdapter = new FoodCartAdapter(getActivity(), R.layout.foodcart_view_layout, foodCollectable);
        foodListView.setAdapter(foodCartAdapter);
        AppSettings.getInstance().foodListView = foodListView;

        foodListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if (AppUser.getInstance().getUserLevelAccess() > 0 && AppUser.getInstance().getUserAddress() != null) {
            String a = AppUser.getInstance().getUserAddress();
            deliveryAddress.setText(a);
            deliveryAddress.setHint(a);
        }

        if (!AppSettings.getInstance().foodCart.isEmpty()) {
            ViewGroup.LayoutParams lp = foodListView.getLayoutParams();
            lp.height = 380 * AppSettings.getInstance().foodCart.size();
            foodListView.setLayoutParams(lp);
        }
        else
        {
            ViewGroup.LayoutParams lp = foodListView.getLayoutParams();
            lp.height = 0;
            foodListView.setLayoutParams(lp);
        }

        spinner.setPrompt("Выберите способ оплаты");
        spinner.setSelection(1);

        getActivity().setTitle("Корзина");

        Button orderButton = rootView.findViewById(R.id.order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
                Integer currentTime = Integer.parseInt(Calendar.getInstance(tz).getTime().toString().substring(11, 16).replace(":", ""));

                if (currentTime >= Integer.parseInt(AppSettings.getInstance().latestTime) || currentTime < Integer.parseInt(AppSettings.getInstance().earliestTime)) {
                    Toast.makeText(getContext(), "На данный момент мы не работаем. Приносим свои извинения.", Toast.LENGTH_SHORT).show();
                }
                else if (AppUser.getInstance().getUserID() != null && AppUser.getInstance().getUserAddress() != null && AppUser.getInstance().getUserName() != null) {
                    if (!foodCollectable.isEmpty()) {
                        Log.i("VIBE CHECK", "1");
                        if (isNullableItems(foodCollectable) && foodCollectable.size() == 1) {
                            Log.i("VIBE CHECK", "2");
                            Toast toast = Toast.makeText(getContext(), "Ваша корзина не должна быть пуста", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            if (FirebaseUtils.isUserLoggedIn()) {
                                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                                ArrayList<String> food_cart = new ArrayList<>();

                                for (FoodCollectable f : foodCollectable) {
                                    food_cart.add(f.getName() + " " + f.getFoodCount() + "шт.");
                                }

                                String orderAddress;
                                String commentary = commentaryET.getText().toString();


                                if (deliveryAddress.getText().toString().isEmpty()) {
                                    orderAddress = AppUser.getInstance().getUserAddress();
                                } else
                                    orderAddress = deliveryAddress.getText().toString();

                                HashMap<String, String> time = new HashMap<>();
                                Locale loc = Locale.getDefault();
                                time.put("orderTime", new SimpleDateFormat("HH:mm:ss", loc).format(new Date()));
                                time.put("completeTime", null);
                                time.put("pickedUpTime", null);
                                time.put("deliveredTime", null);

                                Log.i("IDTest", AppSettings.getInstance().max.toString());
                                String date = new SimpleDateFormat("dd\\MM\\yyyy", loc).format(new Date());
                                if (!AppSettings.getInstance().date.equals(date)){
                                    AppSettings.getInstance().max = 0;
                                    AppSettings.getInstance().date = date;
                                }
                                String curTime = new SimpleDateFormat("dd\\MM\\yyyy_" + AppSettings.getInstance().max.toString(), loc).format(new Date());
                                database.child("orders").child(curTime).child("address").setValue(orderAddress.replace("\n", ""));
                                database.child("orders").child(curTime).child("comment").setValue(commentary);
                                database.child("orders").child(curTime).child("foodCart").setValue(food_cart);
                                database.child("orders").child(curTime).child("name").setValue(AppUser.getInstance().getUserName());
                                database.child("orders").child(curTime).child("paymentType").setValue(spinner.getSelectedItem().toString());
                                database.child("orders").child(curTime).child("phone").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                database.child("orders").child(curTime).child("price").setValue(AppSettings.getInstance().fullNumPrice);
                                database.child("orders").child(curTime).child("status").setValue("0");
                                database.child("orders").child(curTime).child("time").setValue(time);


                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                View mView = getLayoutInflater().inflate(R.layout.alert_thanks, null);

                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                dialog.show();
//
                                mView.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();

                                        getFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                                .replace(R.id.fragment_container, new OrderFragment()).addToBackStack(null).commit();

                                        clearCart();
                                    }
                                });

                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        getFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                                .replace(R.id.fragment_container, new OrderFragment()).addToBackStack(null).commit();

                                        clearCart();
                                    }
                                });

                            } else {
                                Snackbar.make(view, R.string.order_snack_please_login_text, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.order_snacl_login_action, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Fragment fragment = new ProfileFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(ProfileFragment.KEY_IS_OPEN_FIREBASE, true);
                                                fragment.setArguments(bundle);
                                                Router.openFragmentSimply(getActivity(), R.id.fragment_container, fragment);
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                    else {
                        Log.i("VIBE CHECK", "2");
                        Toast toast = Toast.makeText(getContext(), "Ваша корзина не должна быть пуста", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    Log.i("VIBE CHECK", "2");
                    Toast toast = Toast.makeText(getContext(), "Пожалуйста, авторизуйтесь.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AppSettings.getInstance().deliveryPrice = null;

        Iterator<FoodCollectable> iterator = AppSettings.getInstance().foodCart.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getFoodCount() == 0){
                iterator.remove();
            }
        }
    }

    public void clearCart() {
        AppSettings.getInstance().foodCart.clear();
        AppSettings.getInstance().fullNumPrice = 0;

        AppSettings.getInstance().ivCircle.setVisibility(View.INVISIBLE);
        AppSettings.getInstance().tvNum.setText("");
    }

    public Boolean isNullableItems(ArrayList<FoodCollectable> fc) {
        Boolean flag = false;

        for (FoodCollectable f : fc) {
            if (f.getFoodCount() == 0) {
                flag = true;
                break;
            }
        }

        return flag;
    }
}
