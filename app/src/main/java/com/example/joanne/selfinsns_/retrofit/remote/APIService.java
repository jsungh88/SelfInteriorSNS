package com.example.joanne.selfinsns_.retrofit.remote;

import com.example.joanne.selfinsns_.retrofit.model.ChatItem;
import com.example.joanne.selfinsns_.retrofit.model.ChatRoomItem;
import com.example.joanne.selfinsns_.retrofit.model.CommentItem;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.LikeItem;
import com.example.joanne.selfinsns_.retrofit.model.LikerItem;
import com.example.joanne.selfinsns_.retrofit.model.NotificationItem;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {
//    @Headers({"Accept: application/json"})

    //스타일북 글 삭제
    @FormUrlEncoded
    @POST("chatMessage_view.php")
    Call<List<ChatItem>> chatMessage_view(
            @Field("roomId") Integer roomId, //로그인유저no
            @Field("no") Integer user_no //로그인유저no
    );


    //토큰 저장
    @FormUrlEncoded
    @POST("saveTOKEN.php")
    Call<String> updateToken(
            @Field("Token") String Token, //토큰값
            @Field("no") Integer user_no //로그인유저no
    );


    //로그인
    @FormUrlEncoded
    @POST("login.php")
    Call<UserInfo> login(@Field("email") String email,
                         @Field("pwd") String pwd);

    //회원가입
    @Multipart
    @POST("join.php")
    Call<UserInfo> join(
            @Part("name") RequestBody name, //이름 또는 닉네임
            @Part("email") RequestBody email, //이메일
            @Part("pwd") RequestBody pwd, //비밀번호
            @Part("gender") RequestBody gender, //성별
            @Part("agerange") RequestBody age_range, //연령대
            @Part MultipartBody.Part picture //프로필이미지
    );

    //노하우글쓰기
    @Multipart
    @POST("kh_register.php")
    Call<KnowHowItem> knowhow_write(
            @Part("section") RequestBody section, //공간
            @Part("style") RequestBody style, //스타일
            @Part("space") RequestBody space, //평수
            @Part("subject") RequestBody subject, //제목
            @Part("desc") RequestBody desc, //설명
            @Part("tags") RequestBody tags, //태그
            @Part("writer") RequestBody writer, //작성자
            @Part("writer_no") RequestBody writer_no, //작성자번호
            @Part List<MultipartBody.Part> picture, //이미지들
            @Part("picture_desc[]") ArrayList<String> picture_desc //설명들
    );

    //노하우글쓰기
    @FormUrlEncoded
    @POST("kh_register2.php")
    Call<API_Result> knowhow_write(
            @Field("section") String section, //공간
            @Field("style") String style, //스타일
            @Field("space") String space, //평수
            @Field("subject") String subject, //제목
            @Field("desc") String desc, //설명
            @Field("tags") String tags, //태그
            @Field("writer") String writer, //작성자
            @Field("writer_no") Integer writer_no //작성자번호
    );

    //노하우글쓰기
    @Multipart
    @POST("kh_register_image.php")
    Call<API_Result> knowhow_image_write(
            @Part("id") RequestBody writer_no, //글id
            @Part MultipartBody.Part picture, //이미지들
            @Part("picture_desc") RequestBody picture_desc //이미지설명들
    );

    //노하우 글 보기
    @FormUrlEncoded
    @POST("kh_view.php")
    Call<List<KnowHowItem>> kh_view_main(//로그인한 사용자 no 값.
                                         @Field("no") Integer userNo);

//    //노하우 글 보기 - 이미지개수 불러오기
//    @FormUrlEncoded
//    @POST("kh_image_view.php")
//    Call<API_Result> kh_view_main_image(//로그인한 사용자 no 값.
//                                              @Field("no") Integer userNo);
//
//    //노하우 글 보기 - 이미지 불러오기
//    @FormUrlEncoded
//    @POST("kh_image_view.php")
//    Call<API_Result> kh_view_main_image(//글id
//                                               @Field("id") Integer kh_id,
//                                               @Field("order") Integer order);
//
//    //노하우 글 보기 - 이미지 설명 개수 불러오기
//    @FormUrlEncoded
//    @POST("kh_image_desc_view.php")
//    Call<API_Result> kh_view_main_desc(//로그인한 사용자 no 값.
//                                             @Field("no") Integer userNo);

    //노하우 글 보기 - 이미지개수 가져오기.
    @FormUrlEncoded
    @POST("kh_view_images.php")
    Call<API_Result> kh_view_image_count(
                                           @Field("kh_id") Integer kh_id);

    //노하우 글 보기 - 이미지,이미지설명 불러오기
    @FormUrlEncoded
    @POST("kh_view_images.php")
    Call<KnowHowItem> kh_view_images(//로그인한 사용자 no 값.
                                              @Field("kh_id") Integer kh_id,
                                           @Field("order") Integer order);



//    @FormUrlEncoded
//    @POST("knowhow_write.php")
//    Call<String> knowhow_write(
//            @Field("section") String section, //공간
//            @Field("style") String style, //스타일
//            @Field("space") String space, //평수
//            @Field("subject") String subject, //제목
//            @Field("desc") String desc, //설명
//            @Field("tags") String tags, //태그
//            @Field("writer") String writer, //작성자
//            @Field("writer_no") String writer_no //작성자번호
//    );


    //스타일북 글 등록
    @Multipart
    @POST("sb_register.php")
    Call<StyleBookItem> sb_register(
            @Part("writer") RequestBody writer, //작성자
            @Part("tags") RequestBody tags, //태그
            @Part("location") RequestBody location, //태그
            @Part("location_lat") RequestBody lat, //태그
            @Part("location_lng") RequestBody lng, //태그
            @Part MultipartBody.Part picture //이미지
    );

    //스타일북 글 수정 - 이미지 같이 수정하는 경우
    @Multipart
    @POST("sb_edit.php")
    Call<API_Result> sb_edit(
            @Part("id") RequestBody id, //글id
            @Part("tags") RequestBody tags, //태그
            @Part("location") RequestBody location, //태그
            @Part("location_lat") RequestBody lat, //태그
            @Part("location_lng") RequestBody lng, //태그
            @Part("writer") RequestBody writer, //작성자no
            @Part MultipartBody.Part picture //이미지
    );


    //스타일북 글 수정 - 이미지 없이 수정하는 경우
    @FormUrlEncoded
    @POST("sb_edit.php")
    Call<API_Result> sb_edit_noimage(
            @Field("id") Integer id, //글id
            @Field("tags") String tags, //태그
            @Field("location") String location, //태그
            @Field("location_lat") String lat, //태그
            @Field("location_lng") String lng, //태그
            @Field("writer") String writer //작성자no
    );

    //스타일북 글 삭제
    @FormUrlEncoded
    @POST("sb_delete.php")
    Call<API_Result> sb_delete(
            @Field("id") String id//글id
    );

    //스타일북 글 보기1(메인)
    @FormUrlEncoded
    @POST("sb_view_main.php")
    Call<List<StyleBookItem>> sb_view_main(//로그인한 사용자 no 값.
                                           @Field("no") Integer userNo);

    //스타일북 글 보기2(마이페이지)
    @FormUrlEncoded
    @POST("sb_view.php")
    Call<List<StyleBookItem>> sb_view_mypage( //로그인한 사용자 no 값.
                                              @Field("no") Integer userNo
    );

    //스타일북 글 보기3(상세)
    @FormUrlEncoded
    @POST("sb_view_main.php")
    Call<StyleBookItem> sb_view_sub(
            @Field("sb_no") Integer sb_no,//글번호
            @Field("no") Integer userNo//로그인한 사용자no값
    );

    //해시태그글 불러오기
    @FormUrlEncoded
    @POST("sb_tagsview.php")
    Call<List<StyleBookItem>> sb_tagsview(
            @Field("tags") String tags, //태그 문자열 값 전달.
            @Field("no") Integer userNo//로그인한 사용자no값
    );

    //댓글 보기
    @FormUrlEncoded
    @POST("comment_view.php")
    Call<List<CommentItem>> comments_view(
            @Field("sb_id") Integer sb_id, //글id(sb:스타일북)
            @Field("writer_no") Integer writer_no //작성자id
    );

    //댓글 등록
    @FormUrlEncoded
    @POST("comment_register.php")
    Call<List<CommentItem>> comments_register(
            @Field("sb_id") Integer sb_id,
            @Field("writer_no") Integer writer_no,
            @Field("depth") Integer depth,
            @Field("comment") String comments
    );

    //대댓글 등록
    @FormUrlEncoded
    @POST("comment_register.php")
    Call<List<CommentItem>> cofc_register(
            @Field("sb_id") Integer sb_id,
            @Field("writer_no") Integer writer_no,//댓글작성자
            @Field("depth") Integer depth,
            @Field("group") Integer group,
            @Field("comment") String comments
    );

    //댓글,대댓글 삭제
    @FormUrlEncoded
    @POST("comment_delete.php")
    Call<List<CommentItem>> comment_delete(
            @Field("sb_id") Integer sb_id,//글id
            @Field("id") Integer id//댓글id
    );

    //좋아요 등록
    @FormUrlEncoded
    @POST("sb_like.php")
    Call<Integer> sb_like(
            @Field("sb_id") Integer sb_id,
            @Field("liker_id") Integer liker_id,
            @Field("writer_no") Integer writer_no
    );

    //좋아요 취소
    @FormUrlEncoded
    @POST("sb_like.php")
    Call<Integer> sb_unlike(
            @Field("sb_id") Integer sb_id,
            @Field("liker_id") Integer liker_id
    );

    //좋아요 채움여부
    @FormUrlEncoded
    @POST("sb_like_shape.php")
    Call<List<Like_Result>> sb_like_shape(
            @Field("sb_id") Integer sb_id,
            @Field("liker_id") Integer liker_id
    );


    //좋아요한 사람 보기
    @FormUrlEncoded
    @POST("sb_liker_view.php")
    Call<List<LikerItem>> sb_liker_view( //글id, 로그인유저no
                                         @Field("sb_id") Integer sb_id,
                                         @Field("no") Integer no
    );

    //좋아요개수 불러오기
    @FormUrlEncoded
    @POST("sb_liker_count.php")
    Call<Integer> sb_liker_count( //글id, 로그인유저no
                                  @Field("sb_id") Integer sb_id
    );


    //알림내역 불러오기
    @FormUrlEncoded
    @POST("notification_view.php")
    Call<List<NotificationItem>> notification_view(
            @Field("no") Integer no //로그인유저 아이디
    );

    //회원정보 가져오기
    @FormUrlEncoded
    @POST("member_info.php")
    Call<UserInfo> member_info(
            @Field("no") Integer userNo//정보보고싶은 유저의 no값
    );

    //팔로우관계 확인하기
    @FormUrlEncoded
    @POST("follow_view.php")
    Call<API_Result> follow_view( //"팔로우"해야하는지, "팔로잉"한상태인지 불러옴
                                  @Field("login_no") Integer userNo,
                                  @Field("writer_no") Integer writerNo
    );

    //팔로우하기
    @FormUrlEncoded
    @POST("follow_or_unfollow.php")
    Call<API_Result> follow_regit( //follow_no팔로우 하는사람, following_no 팔로잉당하는사람
                                   @Field("follow_no") Integer follow_no,
                                   @Field("following_no") Integer following_no,
                                   @Field("regit") String regit
    );

    //팔로우하기
    @FormUrlEncoded
    @POST("follow_or_unfollow.php")
    Call<API_Result> follow_unfollow( //follow_no팔로우 취소하는사람, following_no 팔로잉취소당하는사람
                                      @Field("follow_no") Integer follow_no,
                                      @Field("following_no") Integer following_no,
                                      @Field("unfollow") String unfollow
    );

    //채팅방만들기
    @FormUrlEncoded
    @POST("chat_makeRoom.php")
    Call<ChatItem> make_chatroom( //채팅방 만들기
                                  @Field("roomName") String room_name,
                                  @Field("no") Integer user_id
    );

    @FormUrlEncoded
    @POST("chatRoom_view2.php") //참여 or 참여하지않은 채팅방리스트
    Call<List<ChatRoomItem>> chatroom_view(
            @Field("no") Integer user_id,
            @Field("inorout") String inorout
    );

    @FormUrlEncoded
    @POST("chatRoom_in.php")//방참여자DB 등록
    Call<API_Result> chatroom_in(
            @Field("no") Integer user_id,
            @Field("roomId") Integer room_id
    );

    @FormUrlEncoded
    @POST("chatRoom_out.php")//방참여자DB 등록
    Call<API_Result> chat_out(
            @Field("roomId") Integer roomId,
            @Field("no") Integer userNo);

    @Multipart
    @POST("chat_imageUpload.php")
    Call<API_Result> chat_imageUpload(
            @Part("roomId") RequestBody req_roomId,
            @Part MultipartBody.Part picture); //이미지

    @Multipart
    @POST("profile_edit.php")
    Call<UserInfo> profile_edit(
            @Part("no") RequestBody req_no,
            @Part MultipartBody.Part upload_image);
}


