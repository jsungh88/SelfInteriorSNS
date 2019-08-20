package com.example.joanne.selfinsns_.retrofit.remote;

import com.google.gson.annotations.SerializedName;

public class Like_Result {// retrofit2의 gsonconvert를 이용하기 위한 클래스.. JSON으로 전달하는 response 값을 Gson으로 필드에 매핑하기 위해서 필요하다.

    //@SerializedName는 Gson이 JSON 키를 필드에 매핑하기 위해서 필요

    @SerializedName("sb_id")
    private Integer sb_id;

    @SerializedName("heart")
    private String heart;

    public Integer getSb_id() {
        return sb_id;
    }

    public void setSb_id(Integer sb_id) {
        this.sb_id = sb_id;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }
}
