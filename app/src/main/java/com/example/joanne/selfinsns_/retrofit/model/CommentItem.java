package com.example.joanne.selfinsns_.retrofit.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentItem {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sb_id")
    @Expose
    private Integer sbId;
    @SerializedName("writer_no")
    @Expose
    private Integer writerNo;
    @SerializedName("depth")
    @Expose
    private Integer depth;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("regdate")
    @Expose
    private String regdate;
    @SerializedName("group")
    @Expose
    private Integer group;
    @SerializedName("writer_id")
    @Expose
    private String writerId;

    @SerializedName("writer_image")
    @Expose
    private String writer_image;

    public CommentItem() { }


    public CommentItem(String comment, String regdate, String writerId, String writer_image) {//댓글 등록
        this.comment = comment;
        this.regdate = regdate;
        this.writerId = writerId;
        this.writer_image = writer_image;
    }

    public CommentItem(String comment, String regdate, String writerId) {//임시
        this.comment = comment;
        this.regdate = regdate;
        this.writerId = writerId;
    }

    private int itemViewType;//댓글과 대댓글 구분하는 뷰타입

    public int getItemViewType() {
        return itemViewType;
    }

    public void setItemViewType(int itemViewType) {
        this.itemViewType = itemViewType;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
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

    public Integer getWriterNo() {
        return writerNo;
    }

    public void setWriterNo(Integer writerNo) {
        this.writerNo = writerNo;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public void setWriter_image(String writer_image) {
        this.writer_image = writer_image;
    }
}
