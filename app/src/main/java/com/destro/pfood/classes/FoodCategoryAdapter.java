package com.destro.pfood.classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.destro.pfood.fragments.FoodFragment;
import com.destro.pfood.R;
import com.destro.pfood.model.FoodItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<FoodCategory> mData;

    private static final String TAG = "FoodCategoryAdapter";

    public FoodCategoryAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("categories");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    mData.add(new FoodCategory(ds.getKey(), mData.size(), new ArrayList<Food>(), ds.child("0").child("imageUrl").getValue(String.class)));
                }

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error" + databaseError.toString());
            }
        });

        Log.i("ADAPTER_TEST", "ADAPTER INIT");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i("ADAPTER_TEST", "VIEWHOLDER CREATE");
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_category, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Log.i("ADAPTER_TEST", "ITEM INSERTED");
        holder.tv_name.setText(mData.get(position).getName());
        if (mData.get(position).getImageUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(mData.get(position).getImageUrl()).into(holder.iv_image);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettings.getInstance().clickedCategory = mData.get(position);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_container, new FoodFragment()).addToBackStack(null).commit();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        ImageView iv_image;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.category_name);
            iv_image = itemView.findViewById(R.id.category_image);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}
