package com.example.joanne.selfinsns_.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatRoomItem {


    @SerializedName("first") //첫입장여부
    @Expose
    private String first;

    @SerializedName("kindof")
    @Expose
    private String kindof;

    @SerializedName("room_id")
    @Expose
    private Integer group_id;

    @SerializedName("chatroom_image")
    @Expose
    private String chatroom_groupimage;

    @SerializedName("room_name")
    @Expose
    private String group_name;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("leaderId")//방장이름
    @Expose
    private Integer leaderId;

    @SerializedName("leaderName")//방장이름
    @Expose
    private String leaderName;

    @SerializedName("leaderImage")//방장이미지
    @Expose
    private String leaderImage;

    @SerializedName("count")//방참여수
    @Expose
    private Integer count;

    //방리스트가져올 때 사용 - 초안
    public ChatRoomItem(Integer group_id, String group_name) {
        this.group_id = group_id;
        this.group_name = group_name;
    }

    //방리스트 가져올 때 사용 - 완성안 - 채팅에 참여한 방
    public ChatRoomItem(String kindof,Integer group_id, String chatroom_groupimage, String group_name, String message, String time, Integer count, String first) {
        this.kindof = kindof;
        this.group_id = group_id;
        this.chatroom_groupimage = chatroom_groupimage;
        this.group_name = group_name;
        this.message = message;
        this.time = time;
        this.count = count;
        this.first = first;
    }

    //방리스트 가져올 때 사용 - 완성안. - 채팅에 참여하지 않은 방
    public ChatRoomItem(String kindof,Integer group_id, String chatroom_groupimage, String group_name, Integer  leaderId, String leaderName, String leaderImage, Integer count, String first) {
        this.kindof = kindof;
        this.group_id = group_id;
        this.chatroom_groupimage = chatroom_groupimage;
        this.group_name = group_name;
        this.leaderId = leaderId;
        this.leaderImage = leaderImage;
        this.leaderName = leaderName;
        this.count = count;
        this.first = first;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getKindof() {
        return kindof;
    }

    public void setKindof(String kindof) {
        this.kindof = kindof;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderImage() {
        return leaderImage;
    }

    public void setLeaderImage(String leaderImage) {
        this.leaderImage = leaderImage;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getChatroom_groupimage() {
        return chatroom_groupimage;
    }

    public void setChatroom_groupimage(String chatroom_groupimage) {
        this.chatroom_groupimage = chatroom_groupimage;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
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
