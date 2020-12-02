package com.destro.pfood.utils;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUtils {

    public static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getUserId(){
        return FirebaseAuth.getInstance().getUid();
    }


}
