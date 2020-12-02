package com.destro.pfood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.destro.pfood.classes.FoodCategoryAdapter;
import com.destro.pfood.R;


public class MenuFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private FoodCategoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_activity, container, false);

        getActivity().setTitle("Меню");

        recyclerView = rootView.findViewById(R.id.recyclerview_id);
        mAdapter = new FoodCategoryAdapter(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }
}
