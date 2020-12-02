package com.destro.pfood.navigation;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class Router {
    public static void openFragmentSimply(FragmentActivity activity, @IdRes int containerId, Fragment fragment) {
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(containerId, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
