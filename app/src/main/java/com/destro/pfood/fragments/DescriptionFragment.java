package com.destro.pfood.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.destro.pfood.classes.AppSettings;
import com.destro.pfood.classes.Food;
import com.destro.pfood.classes.FoodCollectable;
import com.destro.pfood.R;

public class DescriptionFragment extends Fragment {

    private View rootView;
    private ImageView foodImage, foodAction;
    private TextView foodName, foodPrice, foodDescription, foodProducts, foodActionPercent;
    private Button addButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_description, container, false);

        getActivity().setTitle("Подробнее");

        foodImage = rootView.findViewById(R.id.description_image);
        foodName = rootView.findViewById(R.id.foodName);
        foodPrice = rootView.findViewById(R.id.foodPrice);
        foodDescription = rootView.findViewById(R.id.foodDescription);
        addButton = rootView.findViewById(R.id.addToCart);
        foodProducts = rootView.findViewById(R.id.foodProducts);
        foodAction = rootView.findViewById(R.id.food_action);
        foodActionPercent = rootView.findViewById(R.id.food_action_percent);

        if (!AppSettings.getInstance().clickedFood.getSale()) {
            foodAction.setVisibility(View.GONE);
            foodActionPercent.setText("");
        }

        Glide.with(foodImage).load(AppSettings.getInstance().clickedFood.getImageUrl()).into(foodImage);
        foodName.setText(AppSettings.getInstance().clickedFood.getName());

        foodPrice.setText(AppSettings.getInstance().clickedFood.getPrice() + "\u20BD");
        foodProducts.setText(AppSettings.getInstance().clickedFood.getProducts());

        foodDescription.setText(AppSettings.getInstance().clickedFood.getDescription());

        final LinearLayout countControlContainer = rootView.findViewById(R.id.count_control_ll);
        final TextView tvCount = rootView.findViewById(R.id.cartFoodCount);
        ImageButton bAdd = rootView.findViewById(R.id.button_add);
        ImageButton bRemove = rootView.findViewById(R.id.button_remove);

        if (AppSettings.getInstance().findCollectable(AppSettings.getInstance().clickedFood) != null
                && AppSettings.getInstance().findCollectable(AppSettings.getInstance().clickedFood).getFoodCount() >= 1) {
            addButton.setVisibility(View.GONE);
            countControlContainer.setVisibility(View.VISIBLE);
            tvCount.setText(AppSettings.getInstance().findCollectable(AppSettings.getInstance().clickedFood).getFoodCount().toString());
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;
                Integer clickedFood_id = clickedFood.getId();

                AppSettings.getInstance().foodCache.add(clickedFood);
                AppSettings.getInstance().foodCount++;

                if (AppSettings.getInstance().findFood(clickedFood_id) == null) {
                    AppSettings.getInstance().foodCart.add(
                            new FoodCollectable(clickedFood, 1));
                } else {
                    AppSettings.getInstance().findFood(clickedFood_id)
                            .setFoodCount(AppSettings.getInstance().findFood(clickedFood_id).getFoodCount() + 1);
                }

                AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                AppSettings.getInstance().fullNumPrice += clickedFood.getPrice();
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                AppSettings.getInstance().tvNum.startAnimation(anim);
                AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");

                addButton.setVisibility(View.GONE);
                countControlContainer.setVisibility(View.VISIBLE);
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;

                AppSettings.getInstance().foodCache.add(clickedFood);
                AppSettings.getInstance().foodCount++;

                if (AppSettings.getInstance().findCollectable(clickedFood) == null) {
                    AppSettings.getInstance().foodCart.add(
                            new FoodCollectable(clickedFood, 1));
                } else {
                    AppSettings.getInstance().findCollectable(clickedFood).incCount();
                    tvCount.setText(AppSettings.getInstance().findCollectable(clickedFood).getFoodCount().toString());
                }

                AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                AppSettings.getInstance().tvNum.startAnimation(anim);
                AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");
            }
        });

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food clickedFood = AppSettings.getInstance().clickedFood;

                if (AppSettings.getInstance().findCollectable(clickedFood) != null) {
                    if (AppSettings.getInstance().findCollectable(clickedFood).getFoodCount() > 1) {
                        AppSettings.getInstance().findCollectable(clickedFood)
                                .decCount();

                        AppSettings.getInstance().foodCount--;

                        AppSettings.getInstance().tvNum.setVisibility(View.VISIBLE);
                        AppSettings.getInstance().ivCircle.setVisibility(View.VISIBLE);

                        AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");
                        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                        AppSettings.getInstance().tvNum.startAnimation(anim);

                        tvCount.setText(AppSettings.getInstance().findCollectable(clickedFood).getFoodCount().toString());
                    }
                    else {
                        AppSettings.getInstance().findCollectable(clickedFood).decCount();
                        addButton.setVisibility(View.VISIBLE);
                        countControlContainer.setVisibility(View.GONE);

                        AppSettings.getInstance().tvNum.setText(AppSettings.getInstance().fullNumPrice + " \u20BD");

                        if (AppSettings.getInstance().foodCart.isEmpty()) {
                            AppSettings.getInstance().tvNum.setText("");
                            AppSettings.getInstance().ivCircle.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                else {
                    AppSettings.getInstance().findCollectable(clickedFood).decCount();
                }
            }
        });

        return rootView;
    }
}
