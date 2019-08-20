package com.example.joanne.selfinsns_.retrofit.remote;

import com.google.gson.annotations.SerializedName;

public class API_Result {// retrofit2의 gsonconvert를 이용하기 위한 클래스.. JSON으로 전달하는 response 값을 Gson으로 필드에 매핑하기 위해서 필요하다.

    //@SerializedName는 Gson이 JSON 키를 필드에 매핑하기 위해서 필요

    @SerializedName("error") // DB업로드가 실패했을 때 php페이지로부터 에러메시지를 받기 위한 어노테이션
    private Boolean error;

    @SerializedName("message") // 성공했을 때 php페이지로부터 내가 지정한 메시지를 받기 위한 어노테이션
    private String message;



    public API_Result(Boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public Boolean getError() {
        return error;
    } // 에러를 받기위한 메소드

    public String getMessage() {
        return message;
    } // 지정 메시지를 받기 위한 메소드

}
