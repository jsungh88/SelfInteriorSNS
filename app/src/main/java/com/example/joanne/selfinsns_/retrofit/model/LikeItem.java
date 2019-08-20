package com.example.joanne.selfinsns_.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeItem {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sb_id")
    @Expose
    private Integer sbId;
    @SerializedName("liker_id")
    @Expose
    private Integer likerId;
    @SerializedName("writer_no")//글작성자no
    @Expose
    private Integer writerNo;
    @SerializedName("regdate")
    @Expose
    private String regdate;
    @SerializedName("writer_name")//글작성자이름
    @Expose
    private String writerName;

    @SerializedName("writer_image")
    @Expose
    private String writer_image;

    @SerializedName("like_count")
    @Expose
    private Integer like_count;



    public LikeItem(Integer id, Integer sbId, Integer likerId, Integer writerNo, String regdate, String writerName, String writer_image) {
        this.id = id;
        this.sbId = sbId;
        this.likerId = likerId;
        this.writerNo = writerNo;
        this.regdate = regdate;
        this.writerName = writerName;
        this.writer_image = writer_image;
    }


    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSbId() {
        return sbId;
    }

    public void setSbId(Integer sbId) {
        this.sbId = sbId;
    }

    public Integer getLikerId() {
        return likerId;
    }

    public void setLikerId(Integer likerId) {
        this.likerId = likerId;
    }

    public Integer getWriterNo() {
        return writerNo;
    }

    public void setWriterNo(Integer writerNo) {
        this.writerNo = writerNo;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public void setWriter_image(String writer_image) {
        this.writer_image = writer_image;
    }
}
