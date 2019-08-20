package com.example.joanne.selfinsns_.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationItem {

    @SerializedName("id")//노티 id
    @Expose
    private Integer id;
    @SerializedName("group") //그룹(노하우,스타일북,qna)
    @Expose
    private String group;
    @SerializedName("category")//(좋아요,팔로우,댓글)
    @Expose
    private String category;
    @SerializedName("noti_desc")//노티메시지
    @Expose
    private String noti_desc;
    @SerializedName("sender_no")//보낸사람 no
    @Expose
    private Integer sender_no;
    @SerializedName("receiver_no")//받는사람no
    @Expose
    private Integer receiver_no;
    @SerializedName("sb_id")//글id
    @Expose
    private Integer sb_id;
    @SerializedName("regdate")//등록일자
    @Expose
    private String regdate;
    @SerializedName("image") //글이미지
    @Expose
    private String image;
    @SerializedName("user_image") //보낸사람 이미지
    @Expose
    private String uImage;
    @SerializedName("user_name") //보낸사람 이름
    @Expose
    private String uName;
    @SerializedName("follow") //팔로우,팔로잉
    @Expose
    private String follow;

    public NotificationItem(Integer id, String group, String category, String noti_desc, Integer sender_no, Integer receiver_no, Integer sb_id, String regdate, String image, String uImage, String uName, String follow) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.noti_desc = noti_desc;
        this.sender_no = sender_no;
        this.receiver_no = receiver_no;
        this.sb_id = sb_id;
        this.regdate = regdate;
        this.image = image;
        this.uImage = uImage;
        this.uName = uName;
        this.follow = follow;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getuImage() {
        return uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNoti_desc() {
        return noti_desc;
    }

    public void setNoti_desc(String noti_desc) {
        this.noti_desc = noti_desc;
    }

    public Integer getSender_no() {
        return sender_no;
    }

    public void setSender_no(Integer sender_no) {
        this.sender_no = sender_no;
    }

    public Integer getReceiver_no() {
        return receiver_no;
    }

    public void setReceiver_no(Integer receiver_no) {
        this.receiver_no = receiver_no;
    }

    public Integer getSb_id() {
        return sb_id;
    }

    public void setSb_id(Integer sb_id) {
        this.sb_id = sb_id;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
