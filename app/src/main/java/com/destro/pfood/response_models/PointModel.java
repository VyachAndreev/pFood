package com.destro.pfood.response_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PointModel {
    @SerializedName("point")
    @Expose
    public String point = null;

    @SerializedName("point_m")
    @Expose
    public String point_m = null;

    @SerializedName("point_inteam")
    @Expose
    public String point_inteam = null;

    @SerializedName("place")
    @Expose
    public String place = null;

    @SerializedName("place_m")
    @Expose
    public String place_m = null;
}
