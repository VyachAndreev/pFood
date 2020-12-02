package com.destro.pfood.response_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseModel {
    @SerializedName("success")
    @Expose
    public Integer count = null;

    @SerializedName("message")
    @Expose
    public String message = null;
}
