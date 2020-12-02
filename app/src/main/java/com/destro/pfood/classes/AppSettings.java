package com.destro.pfood.classes;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.destro.pfood.R;
import com.destro.pfood.model.UserItem;

import java.util.ArrayList;
import java.util.HashMap;

public class AppSettings {
    private static AppSettings instance;
    private AppSettings() {}

    private static final String TAG = "AppSettings";

    public static synchronized AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }

        return instance;
    }

    public FoodCategory clickedCategory;
    public Food clickedFood;
    public ArrayList<Food> foodCache = new ArrayList<>();
    public ArrayList<FoodCollectable> foodCart = new ArrayList<>();
    public TextView tvNum;
    public ImageView ivCircle;
    public Integer fullNumPrice = 0;
    public Integer foodCount = 0;
    public TextView fullPrice;
    public ListView foodListView;
    public FoodAdapter foodAdapter;
    public ArrayList<Order> orderList = new ArrayList<>();
    public ArrayList<String> orderKeyList = new ArrayList<>();
    public Order clickedOrder;
    public HashMap<String, UserItem> users = new HashMap<>();
    public ArrayList<String> chef_ids = new ArrayList<>();
    public ArrayList<String> courier_ids = new ArrayList<>();

    public OrderAdapter orderAdapter;

    public Integer max;
    public String date;

    public MenuItem menuItem;

    public String latestTime;
    public String earliestTime;


    public ArrayList<FoodCollectable> orderCart = new ArrayList<>();

    public Integer deliveryCost;
    public Integer freeDeliveryCost;


    public TextView deliveryPrice;

    public FoodCollectable findFood(Integer id) {
        for (FoodCollectable food : foodCart) {
            if (food.getName().equals(id)) {
                return food;
            }
        }
        return null;
    }

    public Integer countOf(Food food) {
        Integer foodCount = 0;
        if (!AppSettings.getInstance().foodCart.isEmpty()) {
            for (FoodCollectable f : AppSettings.getInstance().foodCart) {
                if (food.getName().equals(f.getName())) {
                    foodCount = f.getFoodCount();
                    break;
                }
            }
        }

        return foodCount;
    }

    public FoodCollectable findCollectable(Food food) {
        FoodCollectable foodCollectable = null;
        if (!AppSettings.getInstance().foodCart.isEmpty()) {
            for (FoodCollectable f : AppSettings.getInstance().foodCart) {
                if (food.getName().equals(f.getName())) {
                    foodCollectable = f;
                    break;
                }
            }
        }

        return foodCollectable;
    }

    public void deleteCollectable(Food food) {
        for (FoodCollectable f : AppSettings.getInstance().foodCart) {
            if (food.getName().equals(f.getName())) {
                AppSettings.getInstance().foodCart.remove(f);
                break;
            }
        }
    }

    public Food findSingleFood(String name) {
        Food result = new Food();

        for (Food f : AppSettings.getInstance().foodCache) {
            if (name.contains(f.getName())) result = f;
        }

        return result;
    }

    public Boolean isOrderContains(Food food) {
        Boolean flag = false;

        for (String s : AppSettings.getInstance().clickedOrder.getFoodCart()) {
            if (s.contains(food.getName())) flag = true;
        }

        return flag;
    }

    public String statusColor(String status){
        switch (status) {
            case "Получен":
            case "Получен Курьером":
                return "#AAAAAA";
            case "В работе":
            case "Доставлен":
                return "#FF9E51";
            case "Отменен":
                return "#CC0000";
            case "Выполнен":
                return "#00AA00";
            default:
                return null;
        }
    }
}
