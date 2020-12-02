package com.destro.pfood.network_classes;

import android.util.Log;

import com.destro.pfood.interfaces.UserAPI;
import com.destro.pfood.response_models.PointModel;
import com.destro.pfood.response_models.ResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUsers {

    private static UserAPI serviceAPI = UserAPI.Factory.create();

    static AddUserCallback addUserCallback;
    static GetUserInfoCallback getUserInfoCallback;

    public static void addUser(String id) {
        Call call = serviceAPI.addUser(id);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                switch (response.code()) {
                    case 200: {
                        addUserCallback.onResult(response.body());
                        addUserCallback.onResultCode(response.code());
                        Log.i("CHECK RESPONSE", response.code() + "");
                    }
                    default: {
                        addUserCallback.onResultCode(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                addUserCallback.onResultCode(500);

                Log.i("ERROR", t.getMessage());
            }
        });
    }

    public static void getUserInfo(String id) {
        Call call = serviceAPI.getUserInfo(id);

        call.enqueue(new Callback<PointModel>() {
            @Override
            public void onResponse(Call<PointModel> call, Response<PointModel> response) {
                switch (response.code()) {
                    case 200: {
                        getUserInfoCallback.onResult(response.body());
                        getUserInfoCallback.onResultCode(response.code());
                    }
                    default: {
                        getUserInfoCallback.onResultCode(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                getUserInfoCallback.onResultCode(500);

                Log.i("ERROR", t.getMessage());
            }
        });
    }

    public static void onAddUserCallback(AddUserCallback callback) {
        addUserCallback = callback;
    }

    public interface AddUserCallback {
        void onResultCode(Integer resultCode);
        void onResult(ResponseModel result);
    }

    public interface GetUserInfoCallback {
        void onResultCode(Integer resultCode);
        void onResult(PointModel result);
    }

    public static void onGetUserInfoCallback(GetUserInfoCallback callback) {
        getUserInfoCallback = callback;
    }
}
