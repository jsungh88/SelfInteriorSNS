package com.example.joanne.selfinsns_.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StyleBookItem {

    @SerializedName("no")
    @Expose
    private Integer no;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("location_lat")
    @Expose
    private String location_lat;

    @SerializedName("location_lng")
    @Expose
    private String location_lng;

    @SerializedName("regdate") //작성일자
    @Expose
    private String regdate;

    @SerializedName("writer_no")//작성자번호
    @Expose
    private Integer writer_no;

    @SerializedName("writer_name")//작성자이름
    @Expose
    private String writer_name;

    @SerializedName("writer_image")//작성자이미지
    @Expose
    private String writer_image;

    @SerializedName("like") //좋아요수
    @Expose
    private Integer like;

    @SerializedName("comment") //댓글수
    @Expose
    private Integer comment;

    @SerializedName("like_shape")
    @Expose
    private String like_shape;

    //스타일북 저장할때 생성자.
//    public StyleBookItem(Integer no, String image, String content, String location, String regdate, Integer writer_no) {
//        this.no = no;
//        this.image = image;
//        this.content = content;
//        this.location = location;
//        this.regdate = regdate;
//        this.writer_no = writer_no;
//    }

    public StyleBookItem(Integer no, String image, String content, String location, String location_lat, String location_lng, String regdate, Integer writer_no) {
        this.no = no;
        this.image = image;
        this.content = content;
        this.location = location;
        this.location_lat = location_lat;
        this.location_lng = location_lng;
        this.regdate = regdate;
        this.writer_no = writer_no;
    }

    //스타일북 리사이클러뷰 불러올 때 생성자
    public StyleBookItem(Integer no, String image, String content, String location, String location_lat, String location_lng,String regdate, String writer_name, String writer_image, Integer like, Integer comment) {
        this.no = no;
        this.image = image;
        this.content = content;
        this.location = location;
        this.location_lat = location_lat;
        this.location_lng = location_lng;
        this.regdate = regdate;
        this.writer_name = writer_name;
        this.writer_image = writer_image;
        this.like = like;
        this.comment = comment;

    }


    //스타일북 셰어드프리퍼런스에 저장할 때, , ,
    public StyleBookItem(Integer sb_no, String image, String content, String location, String location_lat, String location_lng, String regdate, String writer_name, String writer_image, Integer writer_no, Integer like_count, Integer comment_count, String like_shape) {
        this.no = sb_no;
        this.image = image;
        this.content = content;
        this.location = location;
        this.location_lat = location_lat;
        this.location_lng = location_lng;
        this.regdate = regdate;
        this.writer_no = writer_no;
        this.writer_name = writer_name;
        this.writer_image = writer_image;
        this.like = like_count;
        this.comment = comment_count;
        this.like_shape = like_shape;
    }


    public String getLike_shape() {
        return like_shape;
    }

    public void setLike_shape(String like_shape) {
        this.like_shape = like_shape;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Integer getWriter_no() {
        return writer_no;
    }

    public void setWriter_no(Integer writer_no) {
        this.writer_no = writer_no;
    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public void setWriter_image(String writer_image) {
        this.writer_image = writer_image;
        }

    public String getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public String getLocation_lng() {
        return location_lng;
    }

    public void setLocation_lng(String location_lng) {
        this.location_lng = location_lng;
    }
}
