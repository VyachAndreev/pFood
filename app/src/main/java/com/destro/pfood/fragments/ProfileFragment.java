package com.destro.pfood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.destro.pfood.activities.NavigationActivity;
import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.AppUser;
import com.destro.pfood.network_classes.NetworkUsers;
import com.destro.pfood.R;
import com.destro.pfood.response_models.PointModel;
import com.destro.pfood.response_models.ResponseModel;
import com.destro.pfood.model.UserItem;
import com.destro.pfood.utils.FirebaseUtils;
import com.destro.pfood.utils.KeyboardUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements NetworkUsers.AddUserCallback, NetworkUsers.GetUserInfoCallback {

    private static final String TAG = "ProfileFragment";

    public static final String KEY_IS_OPEN_FIREBASE = "key_is_open_firebase";

    private static final int LOGIN_REQUEST_CODE = 256;

    private EditText profileNameEt;
    private EditText profileAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().getBoolean(KEY_IS_OPEN_FIREBASE)) {
            goToLoginScreen();
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.profile_title);

        final Button loginButton = view.findViewById(R.id.profile_login_button);
        final Button logoutButton = view.findViewById(R.id.profile_logout_button);
        profileNameEt = view.findViewById(R.id.profile_name_et);
        profileAddress = view.findViewById(R.id.profile_addres_et);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        if (FirebaseUtils.isUserLoggedIn()) {
            loginButton.setText(R.string.save);
            logoutButton.setEnabled(true);
            logoutButton.setVisibility(View.VISIBLE);
            profileAddress.setEnabled(true);
            profileAddress.setVisibility(View.VISIBLE);
            profileNameEt.setEnabled(true);
            profileNameEt.setVisibility(View.VISIBLE);

            NetworkUsers.onGetUserInfoCallback(this);
            NetworkUsers.onAddUserCallback(this);

            if (!AppSettings.getInstance().users.containsKey(FirebaseAuth.getInstance().getUid())) {
                Toast.makeText(getContext(), "Чтобы завершить регистрацию, введите Ваши имя и адрес.", Toast.LENGTH_LONG).show();
            }

            //set data from database to edit text
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            profileAddress.setText(dataSnapshot.child("address").getValue(String.class));
                            profileNameEt.setText(dataSnapshot.child("name").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );


        } else {
            loginButton.setText(R.string.login);
            profileAddress.setEnabled(false);
            profileAddress.setVisibility(View.INVISIBLE);
            profileNameEt.setEnabled(false);
            profileNameEt.setVisibility(View.INVISIBLE);
            logoutButton.setEnabled(false);
            logoutButton.setVisibility(View.INVISIBLE);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                AppUser.getInstance().setUserLevelAccess(0);
                AppUser.getInstance().setUserName("");
                AppUser.getInstance().setUserAddress("");
                update();
                loginButton.setText(R.string.login);
                profileAddress.setText("");
                profileNameEt.setText("");
                profileAddress.setEnabled(false);
                profileAddress.setVisibility(View.INVISIBLE);
                profileNameEt.setEnabled(false);
                profileNameEt.setVisibility(View.INVISIBLE);
                logoutButton.setEnabled(false);
                logoutButton.setVisibility(View.INVISIBLE);
            }
        });

        //set on click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (!FirebaseUtils.isUserLoggedIn()) {
                    goToLoginScreen();
                } else {

                    KeyboardUtils.hideKeyboardFrom(getContext(), view);

                    if (!profileAddress.getText().toString().equals("") && !profileNameEt.getText().toString().equals("")) {

                        Map<String, Object> updates = new HashMap<>();

                        String name = profileNameEt.getText().toString();
                        String address = profileAddress.getText().toString();
                        updates.put("name", name);
                        updates.put("address", address);
                        AppUser.getInstance().setUserName(name);
                        AppUser.getInstance().setUserAddress(address);
                        boolean f = false;
                        if (!AppSettings.getInstance().users.containsKey(FirebaseAuth.getInstance().getUid())) {
                            f = true;
                        }
                        AppSettings.getInstance().users.put(FirebaseAuth.getInstance().getUid(), new UserItem(name, address));

                        database
                                .child("users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(updates)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(view, R.string.default_error, Snackbar.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Snackbar.make(view, R.string.edit_profile_success, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                        if (f) {
                            NetworkUsers.addUser(FirebaseAuth.getInstance().getUid());
                        }
                        update();
                    } else {
                        Toast.makeText(getContext(), "Имя пользователя и адрес не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.i("RESULT", "OK");
                try {
                    update();
                } catch (Exception e) {
                    Log.e("exception", "update failed");
                }
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new ProfileFragment()).addToBackStack(null).commit();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(getView(), getContext().getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    return;
                }

                Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private synchronized void update() {
        NavigationActivity navigationActivity = (NavigationActivity) getActivity();
        if (navigationActivity != null) {
            AppUser.getInstance().setUserLevelAccess(navigationActivity.getAccessLevel());
            navigationActivity.createOrderList();
        }
    }

    private void goToLoginScreen() {
        List<String> whitelistedCountries = new ArrayList<>();
        whitelistedCountries.add("ru");

        AuthUI.IdpConfig phoneLoginConfig = new AuthUI.IdpConfig.PhoneBuilder()
                .setWhitelistedCountries(whitelistedCountries)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(
                                phoneLoginConfig))
                        .build(),
                LOGIN_REQUEST_CODE);
    }

    @Override
    public void onResultCode(Integer resultCode) {

    }


    @Override
    public void onResult(PointModel result) {

    }

    @Override
    public void onResult(ResponseModel result) {
        Log.i("SUCCESS ADDING USER", result.message);
    }
}
