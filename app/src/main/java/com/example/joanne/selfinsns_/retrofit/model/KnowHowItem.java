package com.example.joanne.selfinsns_.retrofit.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Joanne on 2018-05-11.
 */

public class KnowHowItem {



    /**
     * 노하우 글 정보
     *  - 글 등록
     *  - 불러오기
     */

    @SerializedName("id")//글id
    @Expose
    private Integer id;

    @SerializedName("subject")//제목
    @Expose
    private String subject;

    @SerializedName("section")//공간구분
    @Expose
    private String section;
    @SerializedName("style")//스타일
    @Expose
    private String style;
    @SerializedName("space")//평수
    @Expose
    private String space;
    @SerializedName("desc")//설명
    @Expose
    private String desc;
    @SerializedName("tag")//태그
    @Expose
    private String tag;
    @SerializedName("writer")//작성자
    @Expose
    private String writer;
    @SerializedName("writer_image")//작성자
    @Expose
    private String writer_image;
    @SerializedName("writer_no")//작성자 번호
    @Expose
    private Integer writerNo;

//    @SerializedName("picture")//이미지들
//    @Expose
//    private ArrayList<String> pictures;
//    @SerializedName("picture_desc")//이미지설명
//    @Expose
//    private ArrayList<String> picture_desc;
//    @SerializedName("picture")//이미지
//    @Expose
//    private String picture;

    @SerializedName("regdate")//등록일자
    @Expose
    private String regdate;
    @SerializedName("image")//이미지
    @Expose
    private String image;
    @SerializedName("image_desc")//이미지 설명
    @Expose
    private String image_desc;
    @SerializedName("like_count")//좋아요 수
    @Expose
    private Integer like_count;
    @SerializedName("comment_count")//댓글 수
    @Expose
    private Integer comment_count;
    @SerializedName("share_count")//공유 수
    @Expose
    private Integer share_count;
    @SerializedName("like_shape")
    @Expose
    private String like_shape;


    //글 등록
    public KnowHowItem(String subject, String section, String style, String space, String desc, String tag, String writer, String regdate) {
        this.subject = subject;
        this.section = section;
        this.style = style;
        this.space = space;
        this.desc = desc;
        this.tag = tag;
        this.writer = writer;
        this.regdate = regdate;
    }

    //메인리스트에 뿌려지는 내용
    public KnowHowItem(Integer id, String subject, String section, String style, String space, String desc, String tag, String writer_image, Integer writer_no, String writer, String regdate, String picture, Integer like_count, Integer comment_count) {
        this.id = id;//글id
        this.subject = subject;
        this.section = section;
        this.style = style;
        this.space = space;
        this.desc = desc;
        this.tag = tag;
        this.writer_image = writer_image;
        this.writerNo = writer_no;
        this.writer = writer;//작성자이름
        this.regdate = regdate;
        this.image = picture;
//        this.picture_desc = picture_desc;
        this.like_count = like_count;
        this.comment_count = comment_count;
//        this.share_count = share_count;
    }



    //서버에 보내는 내용
    public KnowHowItem(String subject, String section, String style, String space, String desc, String tag, String writer, Integer writer_no) {
        this.subject = subject;
        this.section = section;
        this.style = style;
        this.space = space;
        this.desc = desc;
        this.tag = tag;
        this.writer = writer;
        this.writerNo = writer_no;
    }

    //서버에서 가져오는 내용2
    public KnowHowItem(String image, String image_desc) {
        this.image = image;
        this.image_desc = image_desc;
    }

    public KnowHowItem(String image) {
        this.image = image;
    }


    public String getWriter_image() {
        return writer_image;
    }

    public void setWriter_image(String writer_image) {
        this.writer_image = writer_image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_desc() {
        return image_desc;
    }

    public void setImage_desc(String image_desc) {
        this.image_desc = image_desc;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public Integer getShare_count() {
        return share_count;
    }

    public void setShare_count(Integer share_count) {
        this.share_count = share_count;
    }

    public String getLike_shape() {
        return like_shape;
    }

    public void setLike_shape(String like_shape) {
        this.like_shape = like_shape;
    }
}


