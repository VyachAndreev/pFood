package com.destro.pfood.activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.AppUser;
import com.destro.pfood.classes.Food;
import com.destro.pfood.classes.Order;
import com.destro.pfood.fragments.CartFragment;
import com.destro.pfood.fragments.MenuFragment;
import com.destro.pfood.fragments.OrderFragment;
import com.destro.pfood.fragments.ProfileFragment;
import com.destro.pfood.R;
import com.destro.pfood.model.UserItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.destro.pfood.activities.App.CHANNEL_ID;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NotificationManagerCompat notificationManager;
    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Пожалуйста, подождите...");

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppSettings.getInstance().tvNum = toolbar.findViewById(R.id.circle_number);
        AppSettings.getInstance().ivCircle = toolbar.findViewById(R.id.rcircle);
        AppSettings.getInstance().tvNum.setVisibility(View.INVISIBLE);
        AppSettings.getInstance().ivCircle.setVisibility(View.INVISIBLE);

        notificationManager = NotificationManagerCompat.from(this);


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //toolbar
        toolbar.findViewById(R.id.cart_container_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartFragment cartFragment = (CartFragment) getSupportFragmentManager().findFragmentByTag("CartFragment");
                if (cartFragment == null || !cartFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.fragment_container, new CartFragment(), "CartFragment").addToBackStack(null).commit();
                    navigationView.setCheckedItem(R.id.nav_basket);
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set delivery settings
        FirebaseDatabase.getInstance().getReference("delivery_settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppSettings.getInstance().deliveryCost = dataSnapshot.child("delivery_cost").getValue(Integer.class);
                AppSettings.getInstance().freeDeliveryCost = dataSnapshot.child("free_delivery_cost").getValue(Integer.class);
                AppSettings.getInstance().earliestTime = dataSnapshot.child("earliest_time").getValue(String.class);
                AppSettings.getInstance().latestTime = dataSnapshot.child("latest_time").getValue(String.class);

                TextView contacts = navigationView.getHeaderView(0).findViewById(R.id.contacts);
                contacts.setText(dataSnapshot.child("phones").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set users
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            AppSettings.getInstance().users.put(dataSnapshot1.getKey(), new UserItem(dataSnapshot1.child("name").getValue(String.class), dataSnapshot1.child("address").getValue(String.class)));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        //set menu
        FirebaseDatabase.getInstance().getReference("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot exPostSnapshot : postSnapshot.getChildren()) {
                        String name = exPostSnapshot.child("name").getValue(String.class);
                        Integer price = exPostSnapshot.child("price").getValue(Integer.class);
                        String description = exPostSnapshot.child("description").getValue(String.class);
                        String imageUrl = exPostSnapshot.child("imageUrl").getValue(String.class);
                        String products = exPostSnapshot.child("products").getValue(String.class);
                        Boolean sale = exPostSnapshot.child("sale").getValue(Boolean.class);

                        if (sale == null) sale = false;

                        AppSettings.getInstance().foodCache.add(new Food(name, price, description, products, imageUrl, sale));
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        AppSettings.getInstance().max = 0;

        //set chiefs and couriers
        setAdmins();


        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(NavigationActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.network_error, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mView.findViewById(R.id.error_ok_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        System.exit(0);
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                        System.exit(0);
                    }
                });
            }
        };

        AppUser.getInstance().setUserLevelAccess(getAccessLevel());
        createOrderList();

        if (!isNetworkAvailable()) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(NavigationActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.network_error, null);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            mView.findViewById(R.id.error_ok_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    System.exit(0);
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                    System.exit(0);
                }
            });
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MenuFragment(), "MenuFragment").commit();

            navigationView.setCheckedItem(R.id.nav_menu);
        }

        if (AppSettings.getInstance().fullNumPrice != 0) {
            AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
            AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

            AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");
        }
    }

    private synchronized void setAdmins() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("chef_ids").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AppSettings.getInstance().chef_ids.add(postSnapshot.getValue(String.class));
                    }
                    AppUser.getInstance().setUserLevelAccess(getAccessLevel());
                    createOrderList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            FirebaseDatabase.getInstance().getReference("courier_ids").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AppSettings.getInstance().courier_ids.add(postSnapshot.getValue(String.class));
                    }
                    AppUser.getInstance().setUserLevelAccess(getAccessLevel());
                    createOrderList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public synchronized int getAccessLevel() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AppUser.getInstance().setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
            if (AppSettings.getInstance().chef_ids.contains(AppUser.getInstance().getUserID())) {
                return 2;
            } else if (AppSettings.getInstance().courier_ids.contains(AppUser.getInstance().getUserID())) {
                return 3;
            } else {
                return 1;
            }
        } else {
            AppUser.getInstance().setUserID(null);
            return 0;
        }
    }

    public synchronized void createOrderList() {
        if (AppUser.getInstance().getUserLevelAccess() > 0) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.admin_drawer_menu);
            AppSettings.getInstance().menuItem = navigationView.getMenu().findItem(R.id.nav_orders);
            menuItem = navigationView.getMenu().findItem(R.id.nav_orders);

            AppUser.getInstance().setUserPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            FirebaseDatabase.getInstance().getReference("users").child(AppUser.getInstance().getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    AppUser.getInstance().setUserName(name);
                    AppUser.getInstance().setUserAddress(address);
                    try {
                        setUsersData(name != null);
                        Log.i("username", AppUser.getInstance().getUserName());
                    } catch (Exception e) {
                        Log.e("username", "name is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


            FirebaseDatabase.getInstance().getReference("orders").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AppSettings.getInstance().orderList.clear();
                    AppSettings.getInstance().orderKeyList.clear();


                    Locale loc = Locale.getDefault();
                    AppSettings.getInstance().date = new SimpleDateFormat("dd\\MM\\yyyy", loc).format(new Date());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String phone = postSnapshot.child("phone").getValue(String.class);
                        String status = postSnapshot.child("status").getValue(String.class);
                        String courier = null;
                        String currentID = postSnapshot.getKey();
                        int current = Integer.parseInt(currentID.substring(currentID.indexOf("_") + 1));
                        Log.i("date", currentID.substring(0, currentID.indexOf("_")));
                        if (AppSettings.getInstance().date.equals(currentID.substring(0, currentID.indexOf("_"))) && current >= AppSettings.getInstance().max){
                            AppSettings.getInstance().max = current + 1;
                            Log.i("MaxTest", AppSettings.getInstance().max.toString());
                        }
                        try {
                            courier = postSnapshot.child("courier").getValue(String.class);
                        } catch (Exception e) {
                            Log.e("courier not found",
                                    e.getMessage());
                        }
                        boolean b = courier != null && courier.equals(AppUser.getInstance().getUserID());
                        if(status != null) {
                            if (AppUser.getInstance().getUserLevelAccess() == 2 && !(status.equals("Получен Курьером")) && !(status.equals("Доставлен")) ||
                                    AppUser.getInstance().getUserLevelAccess() == 3 && (status.equals("Выполнен") || b && !(status.equals("Доставлен"))) ||
                                    AppUser.getInstance().getUserPhone().equals(phone)) {
                            try {
                                String address = postSnapshot.child("address").getValue(String.class);
                                String comment = postSnapshot.child("comment").getValue(String.class);
                                ArrayList<String> foodCart = new ArrayList<>();
                                for (DataSnapshot exPostSnapshot : postSnapshot.child("foodCart").getChildren()) {
                                    foodCart.add(exPostSnapshot.getValue(String.class));
                                }
                                String name = postSnapshot.child("name").getValue(String.class);
                                String payment_type = postSnapshot.child("paymentType").getValue(String.class);
                                Integer price = postSnapshot.child("price").getValue(Integer.class);
                                HashMap<String, String> time = new HashMap<>();
                                for (DataSnapshot exPostSnapshot : postSnapshot.child("time").getChildren()) {
                                    time.put(exPostSnapshot.getKey(), exPostSnapshot.getValue(String.class));
                                }

                                Order order = new Order(name, address, phone, time, foodCart, comment, price, payment_type, status);
                                AppSettings.getInstance().orderKeyList.add(postSnapshot.getKey());
                                AppSettings.getInstance().orderList.add(order);
                                Log.i("asdas", Integer.valueOf(AppUser.getInstance().getUserLevelAccess()).toString());
                                if (AppUser.getInstance().getUserLevelAccess() == 2 && status.equals("0") ||
                                        AppUser.getInstance().getUserLevelAccess() == 3 && status.equals("Выполнен")) {
                                    sendNotification("Уведомление о заказе", "Поступил новый заказ");
                                } else if (status.equals("Выполнен")) {
                                    sendNotification("Уведомление о заказе", "Ваш заказ готов и скоро будет доставлен");
                                }
                                if (AppUser.getInstance().getUserLevelAccess() == 1) {
                                    menuItem.setTitle("Мои Заказы");
                                } else {
                                    if (AppSettings.getInstance().orderList.isEmpty()) {
                                        menuItem.setTitle("Заказы");
                                    } else {
                                        menuItem.setTitle("Заказы (" + AppSettings.getInstance().orderList.size() + ")");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Exception", "Order fields naming exception");
                            }
                            }
                        }
                        if (AppSettings.getInstance().orderAdapter != null) {
                            AppSettings.getInstance().orderAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            TextView name = navigationView.getHeaderView(0).findViewById(R.id.header_name);
            TextView phone = navigationView.getHeaderView(0).findViewById(R.id.header_phone);
            name.setText("");
            phone.setText("");
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu);
        }
    }

    private void sendNotification(String title, String message) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cart_white)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUsersData(boolean nonNull) {
        if (nonNull) {
            TextView name = navigationView.getHeaderView(0).findViewById(R.id.header_name);
            TextView phone = navigationView.getHeaderView(0).findViewById(R.id.header_phone);
            name.setText(AppUser.getInstance().getUserName());
            phone.setText(AppUser.getInstance().getUserPhone());
        } else {
            TextView name = navigationView.getHeaderView(0).findViewById(R.id.header_name);
            TextView phone = navigationView.getHeaderView(0).findViewById(R.id.header_phone);
            name.setText("");
            phone.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu:
                MenuFragment menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag("MenuFragment");
                if (menuFragment == null || !menuFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.fragment_container, new MenuFragment(), "MenuFragment").addToBackStack(null).commit();
                    navigationView.setCheckedItem(R.id.nav_menu);
                }
                break;

            case R.id.nav_profile:
                ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                if (profileFragment == null || !profileFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment").addToBackStack(null).commit();
                    navigationView.setCheckedItem(R.id.nav_profile);
                }
                break;

            case R.id.nav_basket:
                CartFragment cartFragment = (CartFragment) getSupportFragmentManager().findFragmentByTag("CartFragment");
                if (cartFragment == null || !cartFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.fragment_container, new CartFragment(), "CartFragment").addToBackStack(null).commit();
                    navigationView.setCheckedItem(R.id.nav_basket);
                }
                break;


            case R.id.nav_orders:
                OrderFragment orderFragment = (OrderFragment) getSupportFragmentManager().findFragmentByTag("OrderFragment");
                if (orderFragment == null || !orderFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.fragment_container, new OrderFragment(), "OrderFragment").addToBackStack(null).commit();
                    navigationView.setCheckedItem(R.id.nav_orders);
                }
                break;

            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        OrderFragment orderFragment = (OrderFragment) getSupportFragmentManager().findFragmentByTag("OrderToFragment");
        if (orderFragment == null || !orderFragment.isVisible()) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else
                super.onBackPressed();
        }
    }

}
