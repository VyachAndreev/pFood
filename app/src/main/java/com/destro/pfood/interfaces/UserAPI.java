package com.destro.pfood.interfaces;

import com.destro.pfood.response_models.PointModel;
import com.destro.pfood.response_models.ResponseModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("add_user.php?key=gDjRtEYOTwqW0w@ezsVn")
    Call<ResponseModel> addUser(
            @Query("id") String id
    );

    @GET("getUserInfo.php?key=gDjRtEYOTwqW0w@ezsVn")
    Call<PointModel> getUserInfo(
            @Query("id") String id
    );

    @GET("add_point_user.php?key=gDjRtEYOTwqW0w@ezsVn")
    Call<ResponseModel> addPoints(
            @Query("id") String id,
            @Query("point") String point
    );


    class Factory {
        public static UserAPI create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://foodappbackend.000webhostapp.com/")
                    .build();

            return retrofit.create(UserAPI.class);
        }
    }
}
