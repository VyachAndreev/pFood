package com.destro.pfood.interfaces;

import com.destro.pfood.response_models.ResponseModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderAPI {
    @POST("add_in_order_history.php")
    Call<ResponseModel> addOrderToHistory(
            @Body RequestBody body
    );


    class Factory {
        public static OrderAPI create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://foodappbackend.000webhostapp.com/")
                    .build();

            return retrofit.create(OrderAPI.class);
        }
    }
}
