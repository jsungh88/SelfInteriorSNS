package com.example.joanne.selfinsns_.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

    public UserInfo(Integer no, String name, String email, String pw, String picture, String gender, String ageRange, Integer level, String regdate, String type) {
        this.no = no;
        this.name = name;
        this.email = email;
        this.pw = pw;
        this.picture = picture;
        this.gender = gender;
        this.ageRange = ageRange;
        this.level = level;
        this.regdate = regdate;
        this.type = type;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pw='" + pw + '\'' +
                ", picture='" + picture + '\'' +
                ", gender='" + gender + '\'' +
                ", ageRange='" + ageRange + '\'' +
                ", level=" + level +
                ", regdate='" + regdate + '\'' +
                ", type='" + type + '\'' +
                '}';
    }


    @SerializedName("no")
    @Expose
    private Integer no;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("pw")
    @Expose
    private String pw;

    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("agerange")
    @Expose
    private String ageRange;

    @SerializedName("level")
    @Expose
    private Integer level;

    @SerializedName("regdate")
    @Expose
    private String regdate;

    @SerializedName("join_type")
    @Expose
    private String type;


    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }


}