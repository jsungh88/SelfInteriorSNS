package com.example.joanne.selfinsns_.retrofit.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatItem {

    //구분(보낸사람?받는사람?)
    //보낼때: 방번호, 보낸메시지, 보낸사람(id), 받는사람(id), 보낸날짜
    //받을떄: 방번호, 받는메시지, 보낸사람(id), 받는사람(id), 보낸사람이미지, 보낸사람이름, 받은날짜
    //방생성때 : 방번호, 방이름

    @SerializedName("who")//구분 receiver or sender
    @Expose
    private String who;
    @SerializedName("userId")//유저 id
    @Expose
    private Integer userId;
    @SerializedName("sender")//보낸사람
    @Expose
    private Integer sender;
    @SerializedName("roomId")//방번호
    @Expose
    private Integer roomId;
    @SerializedName("roomName")//방이름
    @Expose
    private String roomName;
    @SerializedName("userImage")//보낸사람메시지
    @Expose
    private String userImage;
    private Drawable userImagei;
    @SerializedName("userName")//보낸사람이름
    @Expose
    private String userName;
    @SerializedName("message")//보낸메시지
    @Expose
    private String message;
    @SerializedName("time")//보낸날짜
    @Expose
    private String time;
    @SerializedName("leaderName")//방장이름
    @Expose
    private String leaderName;

    public ChatItem(String who,Integer roomId, String message, String time) {
        this.who = who;
        this.roomId = roomId;
        this.message = message;
        this.time = time;
    }

    //채팅방 만들기 생성자
    public ChatItem(Integer roomId, String roomName, String leader) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.leaderName = leader; //방장이름
    }

    //공지메세지받기
    public ChatItem(String who, String Message){
        this.who = who;
        this.message = Message;
    }

    //메시지 받기 생성자
    public ChatItem(Integer roomId, String who, Integer userId, String userName, String userImage, String message, String time) {
        this.roomId = roomId;
        this.who = who;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.message = message;
        this.time = time;
    }

    //메시지 받기 생성자
    public ChatItem(String who, Integer userId, String userImage, String userName, String message, String time) {
        this.who = who;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.message = message;
        this.time = time;
    }



    //메시지 받기 생성자
    public ChatItem(String who, Integer userId, Drawable userImage, String userName, String message, String time) {
        this.who = who;
        this.userId = userId;
        this.userImagei = userImage;
        this.userName = userName;
        this.message = message;
        this.time = time;
    }


    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public Drawable getUserImagei() {
        return userImagei;
    }

    public void setUserImagei(Drawable userImagei) {
        this.userImagei = userImagei;
    }

    public String getLeader() {
        return leaderName;
    }

    public void setLeader(String leader) {
        this.leaderName = leader;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
