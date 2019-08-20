package com.example.joanne.selfinsns_;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.example.joanne.selfinsns_.retrofit.remote.FileUtils;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Joanne on 2018-05-17.
 */

public class KnowHowWrite_Step3 extends Fragment implements View.OnClickListener {

    Button complete;
    ArrayList<Uri> image_uris;//이미지
    ArrayList<String> str_images, img_descriptions, send_img_descriptions;
    String str_section, str_style, str_space, str_subject, str_desc, str_tags;//공간,스타일,평수,제목,설명,태그
    TextView set_section, set_style, set_space, set_subject, set_tags, set_desc;//공간,스타일,평수,제목,태그,설명
    TextView set_likecount, set_comment_count, set_share_count;

    RecyclerView knowHowRecycler_image;
    KnowHowImageAdapter knowHowImageAdapter;
    List<KnowHowItem> items;
    List<MultipartBody.Part> part_images;

    private SharedPreferences sp,sp1;//로그인한 사용자정보 불러오기(세션역할)
    private UserInfo user;

    //APIService 인터페이스
    public static APIService service;
    private File file = null;

    private Integer kh_id; //글id


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        str_images = new ArrayList<String>();
        items = new ArrayList<>();

        final View step3 = inflater.inflate(R.layout.knowhow_write_step3, container, false);
//        Toast.makeText( getActivity(), "onCreateView3", Toast.LENGTH_SHORT ).show();

        set_section = step3.findViewById(R.id.step3_section);
        set_style = step3.findViewById(R.id.step3_style);
        set_space = step3.findViewById(R.id.step3_space);
        set_subject = step3.findViewById(R.id.step3_subject);
        set_tags = step3.findViewById(R.id.step3_tags);
        set_desc = step3.findViewById(R.id.step3_desc);

        set_likecount = step3.findViewById(R.id.step3_like_count);
        set_comment_count = step3.findViewById(R.id.step3_comment_count);
        set_share_count = step3.findViewById(R.id.step3_share_count);

        knowHowRecycler_image = step3.findViewById(R.id.write_knowhow_body);
        knowHowRecycler_image.setHasFixedSize(true);

        //retrofit빌드 생성.
        service = ApiUtils.getAPIService();
        //SharedPreference 객체 생성
        sp = step3.getContext().getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        sp1 = step3.getContext().getSharedPreferences("knowhow", Activity.MODE_PRIVATE);
        /**
         * 로그인한 유저의 이름과 no를 불러와 writer, writer_no에 저장.
         */
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        user = gson.fromJson(json, type);
        //test log
        Log.e("로그인유저:", String.valueOf(user.getNo()));
        Log.e("로그인유저:", user.getName());

        complete = step3.findViewById(R.id.step3_complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Log.e("nextstep:", "come?");

                //이미지와 이미지설명을 제외한 모든 정보를 일단 DB에 등록한다.
                Call<API_Result> call = service.knowhow_write(str_section, str_style, str_space, str_subject, str_desc, str_tags, user.getName(), user.getNo());
                call.enqueue(new Callback<API_Result>() {
                    @Override
                    public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                        kh_id = Integer.valueOf(response.body().getMessage());
                        Log.e("response.body", String.valueOf(response.body().getMessage()));


                        //이미지를 불러와서 List<MultipartBody.Part> picture 에 담는다.
                        for (int i = 0; i < items.size(); i++) {

                            Log.e("들어왔니","for문안에");
                            //1. 이미지
                            String img_uri = items.get(i).getImage();
                            Log.e("image_uri", img_uri);
                            File file = new File(items.get(i).getImage());
                            Log.e("file", String.valueOf(file));
                            RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
                            MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
                            Log.e("requestfile", "upload_image:" + file.getName());

                            //2. 이미지설명
                            String img_description = items.get(i).getImage_desc();
                            RequestBody req_img_description = RequestBody.create(MediaType.parse("text/plane"), img_description);

                            //3. 글id가져오기
                            RequestBody req_kh_id = RequestBody.create(MediaType.parse("text/plane"), String.valueOf(kh_id));

                            //3.이미지와 이미지설명을 DB에 등록한다.
                            Call<API_Result> call2 = service.knowhow_image_write(req_kh_id, upload_image, req_img_description);
                            call2.enqueue(new Callback<API_Result>() {
                                @Override
                                public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                    Log.e("response.body", String.valueOf(response.body().getMessage()));

                                    SharedPreferences.Editor editor = sp1.edit();
                                    editor.remove("description");
                                    editor.commit();


                                }

                                @Override
                                public void onFailure(Call<API_Result> call, Throwable t) {
                                    Log.e("이미지등록", t.getMessage());

                                }
                            });


                        }

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        startActivity(intent);



                    }

                    @Override
                    public void onFailure(Call<API_Result> call, Throwable t) {
                        Log.e("글등록", t.getMessage());
                    }
                });


            }
        });


        return step3;
    }


    @Override
    public void onClick(View v) {

    }

    public void Data() {


        //이미지,이미지설명 데이터.
        Log.e("Step3", "DATA()접근");
        if (str_images.size() > 0) {
            Log.e("Step3", "str_images > 0");
            for (int i = 0; i < str_images.size(); i++) {
                KnowHowItem item = new KnowHowItem(str_images.get(i), img_descriptions.get(i));
                items.add(i, item);
                Log.e("items추가", String.valueOf(items.get(i)));
            }
        }


    }

    protected void displayRecievedData(String section, String style, String space, String subject, String desc, String tags, ArrayList<String> images, ArrayList<String> img_description) {
        //화면에 셋팅하기
        set_section.setText(section);
        set_style.setText(style);
        set_space.setText(space);
        set_subject.setText(subject);
        set_tags.setText(tags);
        set_desc.setText(desc);


        //서버전달위해 전역변수에 저장.
        str_section = section;
        str_style = style;
        str_space = space;
        str_subject = subject;
        str_desc = desc;
        str_tags = tags;
        str_images = images;
        img_descriptions = img_description;

        Log.e("공간3_공간구분", section);
        Log.e("공간3_스타일", style);
        Log.e("공간3_평수", space);
        Log.e("공간3_제목", subject);
        Log.e("공간3_태그", tags);
        Log.e("공간3_설명", desc);
        Log.e("공간3_이미지", String.valueOf(images));
        Log.e("공간3_이미지설명", String.valueOf(img_description));


        //이미지 데이터 List<KnowHowItem> items 에 추가.
        Data();

        //items > 0 일 경우, 리사이클러뷰에 보내기
        if (items.size() > 0) {//이미지,이미지설명임.
            Log.e("아이템사이즈", "0아님");
//            GridLayoutManager gridLayoutManager = new GridLayoutManager( getContext(), items.size(), GridLayoutManager.HORIZONTAL, false );
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(items.size(), StaggeredGridLayoutManager.HORIZONTAL);
            knowHowRecycler_image.setLayoutManager(staggeredGridLayoutManager);
            knowHowImageAdapter = new KnowHowImageAdapter(this, items);
            Log.e("이미지item", items.get(0).getImage());
            Log.e("이미지item", items.get(1).getImage());
            knowHowRecycler_image.setAdapter(knowHowImageAdapter);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Toast.makeText( getActivity(), "onAttach3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText( getActivity(), "onCreate3", Toast.LENGTH_SHORT ).show();

        Log.e("데이터불러오기", "3");
//        Data();
        //Bundle에 저장된 내용 가져오기
//        getContents();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Toast.makeText( getActivity(), "onActivityCreated3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText( getActivity(), "onStart3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText( getActivity(), "onResume3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText( getActivity(), "onPause3", Toast.LENGTH_SHORT ).show();

    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText( getActivity(), "onStop3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText( getActivity(), "onDestroyView3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText( getActivity(), "onDestroy3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Toast.makeText( getActivity(), "onDetach3", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Toast.makeText( getActivity(), "onSaveInstanceState3", Toast.LENGTH_SHORT ).show();
    }

//    public void connect(RequestBody section, RequestBody style, RequestBody space, RequestBody subject, RequestBody desc, RequestBody tags, RequestBody writer, RequestBody writer_no){
//        //서버에 노하우글 내용 저장.
//        Call<KnowHowItem> call = mAPIService.knowhow_write_activity(section,style,space,subject,desc,tags,writer,writer_no);
//        Log.e( "서버연결?","왜안되노" );
//        call.enqueue( new Callback<KnowHowItem>() {
//            @Override
//            public void onResponse(Call<KnowHowItem> call, Response<KnowHowItem> response) {
//                response.body();
//                //저장까지만.
//                Log.e( "서버전송","완료" );
//
//
//            }
//
//            @Override
//            public void onFailure(Call<KnowHowItem> call, Throwable t) {
//                t.getMessage();
//                Toast.makeText( getContext().getApplicationContext(),"다시!!",Toast.LENGTH_SHORT ).show();
//                Log.e( "서버전송","실패" );
//            }
//        } );
//
//
//    }

    //TEST(17:01)
//    public void Connect(String Section){
//        mAPIService.getTest( Section ).enqueue( new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("실패실패",t.getMessage());
//            }
//        } );
//    }
//    public void connect(String section, String style, String space, String subject, String desc, String tags, String writer, String writer_no){
//        //서버에 노하우글 내용 저장.
//        mAPIService.knowhow_write_activity( section,style,space,subject,desc,tags,writer,writer_no).enqueue( new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                response.body();
//                //저장까지만.
//                Log.e( "서버전송","완료" );
//                Intent intent = new Intent( getContext(), MainActivity.class );
//                intent.setAction( Intent.ACTION_VIEW );
//                startActivity( intent );
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                t.getMessage();
//                Toast.makeText( getContext().getApplicationContext(),"다시!!",Toast.LENGTH_SHORT ).show();
//                Log.e( "서버전송","실패" );
//            }
//        } );
//        Call<KnowHowItem> call = mAPIService.knowhow_write_activity(section,style,space,subject,desc,tags,writer,writer_no);
//        Log.e( "서버연결?","왜안되노" );
//        call.enqueue( new Callback<KnowHowItem>() {
//            @Override
//            public void onResponse(Call<KnowHowItem> call, Response<KnowHowItem> response) {
//                response.body();
//                //저장까지만.
//                Log.e( "서버전송","완료" );
//
//
//            }
//
//            @Override
//            public void onFailure(Call<KnowHowItem> call, Throwable t) {
//                t.getMessage();
//                Toast.makeText( getContext().getApplicationContext(),"다시!!",Toast.LENGTH_SHORT ).show();
//                Log.e( "서버전송","실패" );
//            }
//        } );


}


//    public String getRealPathFromURI(Uri contentUri) {
//
//        // can post image
//        String [] proj={MediaStore.Images.Media.DATA};
//        CursorLoader cursorLoader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
//        Cursor cursor = cursorLoader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//}
