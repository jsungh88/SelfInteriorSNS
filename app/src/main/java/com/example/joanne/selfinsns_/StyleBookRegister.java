package com.example.joanne.selfinsns_;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.example.joanne.selfinsns_.retrofit.remote.FileUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
//import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

/*
1. 클래스명: 스타일북 등록
2. 설명: 스타일북 '글 등록' 기능 구현
3. 기능흐름:
 (1)스타일북 등록화면에서 유저가 글 정보(이미지등록, 제목, 내용(태그), 지도)를 작성한다.
 (2) 등록하기 버튼을 탭하면 서버DB에 저장된다.
  (2-1) 서버저장 성공시, 메인페이지의 '스타일북'탭으로 이동한다. => 글이 등록된 것을 확인할 수 있다.
  (2-2) 서버저장 실패시, '저장을 실패했다'는 메시지가 표시된다.
*/
public class StyleBookRegister extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mgGoogleApiClient;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_ALBUM = 200;
    private static final int REQUEST_IMAGE_CROP = 300;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CAMERA = 1;

    //화면셋팅요소
    private Button btnRegister, btnEdit, btnCamera, btnAlbum;
    private ImageView image,btnClose, btnLocation;
    private TextView setSubject, setTags, setLocation, setTitleRegister, setTitleEdit;
    private Dialog dialog;
    //저장변수들
    private String gSubject, gTags, gLocation, gLat, gLng; //제목,내용(태그), 위치,작성자no
    private Integer userNo;
    private String imageFilePath; //카메라이미지 저장경로
    private Uri photoUri, albumURI, imageUri = null; //카메라이미지 저장 URI

    //수정시 가져오는 요소
    private Integer sb_no, edit_writer_no;
    private String edit_image, edit_content, edit_location,edit_location_lat,edit_location_lng;

    boolean album = false;
    File file = null;

    private HashTagHelper mTextHashTagHelper;//해시태그 API
    private TextView mHashTagText;//

    private APIService service;
    private StyleBookItem sb;
    private SharedPreferences sp, sp1; //로그인한 사용자정보 불러오기(세션역할)
    private UserInfo user;

    private StyleBookAdapter_main styleBookAdapterMain;

    //구글맵관련
    private GoogleMap mMap;
    private Marker customMarker;
    private LatLng markerLatLng;

    /*
      fromEdit: 등록 or 수정화면 알려주는 intent 값.
      fromEdit = true 수정, fromEdit = false 등록
    */
    private Boolean fromEdit = false; //수정하려고 하니? 그렇다면 수정전 정보를 셋팅해주지.

    //whatScreen의 용도:
    //메인에서 왔니? 그렇다면 메인으로 보내주지.
    //마이페이지에서 왔니? 그러다면 마이페이지로 보내주지.
    private String whatScreen;

    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stylebook_register);

        service = ApiUtils.getAPIService(); // retrofit 빌드 생성
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //SharedPreference 객체 생성

        //google Api 연결 빌드 생성
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //읽기쓰기 권한 받기
        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(StyleBookRegister.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        } else {

        }

        //화면요소 셋팅
        btnCamera = (Button) findViewById(R.id.camera);//카메라버튼
        btnAlbum = (Button) findViewById(R.id.photoAlbum);//앨범버튼
        btnClose = findViewById(R.id.sr_btn_close);//닫기버튼
        btnLocation = findViewById(R.id.sr_btn_location);//위치추가버튼
        btnRegister = (Button) findViewById(R.id.sr_btn_register);//등록버튼
        btnEdit = (Button) findViewById(R.id.sr_btn_edit);//수정버튼
        setTitleRegister = findViewById(R.id.sr_title_register);//등록화면타이틀
        setTitleEdit = findViewById(R.id.sr_title_edit);//수정화면타이틀
        image = findViewById(R.id.sr_image);//이미지
        setTags = findViewById(R.id.sr_tags);//태그(또는내용)
        setLocation = findViewById(R.id.sr_txt_location);//위치
        image.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

        //수정시 - 수정전 내용을 셋팅한다.
        /************************************************************************************/
        Intent fromDetail = getIntent();
        fromEdit = fromDetail.getBooleanExtra("fromEdit", false);
        sb_no = fromDetail.getIntExtra("sb_no", 0);
        edit_image = fromDetail.getStringExtra("image");
        edit_content = fromDetail.getStringExtra("content");
        edit_location = fromDetail.getStringExtra("location");
        edit_location_lat = fromDetail.getStringExtra("location_lat");
        edit_location_lng = fromDetail.getStringExtra("location_lng");
        edit_writer_no = fromDetail.getIntExtra("writer_no", 0);
        try {
            Log.e("StyleBookRegister:", "fromEdit:" + String.valueOf(fromEdit));
            Log.e("StyleBookRegister:", "sb_no:" + String.valueOf(sb_no));
            Log.e("StyleBookRegister:", "image:" + edit_image);
            Log.e("StyleBookRegister:", "content:" + edit_content);
            Log.e("StyleBookRegister:", "location:" + edit_location);
            Log.e("StyleBookRegister:", "location_lat:" + edit_location_lat);
            Log.e("StyleBookRegister:", "location_lng:" + edit_location_lng);
            Log.e("StyleBookRegister:", "writer_no:" + String.valueOf(edit_writer_no));
        } catch (Exception e) {
            e.getMessage();
        }

        if (!fromEdit) {//등록일 경우, "스타일북 등록" 타이틀 셋팅.
            setTitleRegister.setVisibility(View.VISIBLE);
            setTitleEdit.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        } else if (fromEdit) {//수정일 경우, StyleBookDetailActivity에서 intent로 가져온 요소 셋팅.
            setTitleRegister.setVisibility(View.GONE);
            setTitleEdit.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            Log.e("test-file", String.valueOf(file));
            Log.e("test-albumUri", String.valueOf(albumURI));
            Log.e("test-photoUri", String.valueOf(photoUri));
            if (albumURI == null || photoUri == null) {
                Log.e("이미지", "여기?????????????????");
                Glide.with(this)//이미지
                        .load("http://13.209.108.67/uploads/" + edit_image)
                        .into(image);
            }
            setTags.setText(edit_content);//내용
            try {
                setLocation.setText(edit_location);//위치
            } catch (Exception e) {
                e.getMessage();
            }
        }
        /************************************************************************************/
        //어떤 화면에서 왔는가? 메인 또는 마이페이지
        sp1 = getSharedPreferences("what_screen", Activity.MODE_PRIVATE);
        whatScreen = sp1.getString("what", null);

        //해시태그 기능.
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorHashTag), null);
        mTextHashTagHelper.handle(setTags);

        //최근 device가 있었던 장소제공에대한 허가
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result =
                Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                for(PlaceLikelihood placeLikelihood : placeLikelihoods){
                    Log.i("가장 최근있었던 place정보",String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                placeLikelihoods.release();
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sr_image:
                /*
                 * 기능: 이미지등록하기
                 * 1. 이미지영역을 탭한다.
                 * 2. 카메라 or 앨범선택하기 다이아로그가 뜬다.
                 * 3. 선택한 사진이 이미지 영역에 set된다.
                 */

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View customLayout = View.inflate(this, R.layout.cameraoralbum_dialog, null);
                builder.setView(customLayout);
                customLayout.findViewById(R.id.camera).setOnClickListener(this);
                customLayout.findViewById(R.id.photoAlbum).setOnClickListener(this);
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.camera://카메라 : 카메라로 찍은 사진이 photoUri에 저장된다.
                dialog.dismiss();
                sendTakePhotoIntent();
                albumURI = null;
                break;

            case R.id.photoAlbum://앨범 : 앨범에서 불러온 사진이 albumUri에 저장된다.
                dialog.dismiss();
                photoAlbum();
                photoUri = null;
                break;
            case R.id.sr_btn_register://글 등록하기
                /**************************** 글 등록하기 ****************************
                 1. 등록하기 버튼을 탭한다.
                 2. 이미지(photo or album URI), 제목, 내용가 모두 입력되었는지 확인한다. ( 위치정보 필수 아님)
                 2-1. 이미지
                 - 'photoUri' 가 null 이 아닐 경우, photoUri 를 imageUri 변수에 저장한다.
                 - 'albumURI' 가 null 이 아닐 경우, albumURI 를 imageUri 변수에 저장한다.
                 - 'photoUri'와 'albumURI'가 모두 비어있을 경우, "이미지를 등록해주세요" 팝업 띄운다.
                 2-2. 제목
                 - 제목이 입력되지 않은 경우, "제목을 입력해주세요" 팝업 띄운다.
                 2-3. 내용
                 - 내용이 입력되지 않은 경우, "내용을 입력해주세요" 팝업 띄운다.
                 3. 로그인 회원정보를 불러와 회원no 를 변수에 저장한다.
                 4. 모두 입력되었으면 이미지, 제목, 내용, (+위치정보), 작성자no 를 requestbody로 변환한다.
                 5. requestbody로 변환된 입력정보를 서버로 전달한다.
                 6. 스타일북 DB테이블에 저장한다.
                 6-1. 성공시 메인화면으로 이동한다.
                 6-2. 실패시 '등록실패' 메시지가 뜬다.
                 ********************************************************************/


                Log.e("fromEdit", "궁금" + String.valueOf(fromEdit));
                gTags = setTags.getText().toString().trim();
                try {
                    gLocation = setLocation.getText().toString().trim();
                } catch (Exception e) {
                    Log.e("글등록", "위치추가" + e.getMessage());
                }

                if (gTags.isEmpty()) {
                    Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                if (gLocation.isEmpty()) {
                    gLocation = "null";
                }
                if (photoUri != null) { //카메라에서 찍은 사진
                    imageUri = photoUri;
                } else if (albumURI != null) { //앨범에서 가져온 사진
                    imageUri = albumURI;
                } else if (photoUri == null && albumURI == null) {
                    Toast.makeText(this, "이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                }

                //꼼수
//                gLocation = "카페창비";
//                gLat = "2332";
//                gLng = "3434";


                Log.e("imageUri", String.valueOf(imageUri));
                file = FileUtils.getFile(this, imageUri); //이미지파일

                Gson gson = new Gson();
                String json = sp.getString("login", null);
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                user = gson.fromJson(json, type);
                userNo = user.getNo();
                Log.e("셰어드 불러오기", "성공");
                Log.e("로그인유저:", String.valueOf(user.getNo()));

                Log.e("글등록", "작성자:" + userNo);
                Log.e("글등록", "내용:" + gTags);
                Log.e("글등록", "위치:" + gLocation);
                Log.e("글등록", "이미지uri:" + imageUri);
                Log.e("글등록", "이미지file:" + file);
                String no = String.valueOf(userNo);



                RequestBody req_writer = RequestBody.create(MediaType.parse("text/plane"), no);
                RequestBody req_tags = RequestBody.create(MediaType.parse("text/plane"), gTags);
                RequestBody req_location = RequestBody.create(MediaType.parse("text/plane"), gLocation);
                RequestBody req_lat = RequestBody.create(MediaType.parse("text/plane"), gLat);
                RequestBody req_lng = RequestBody.create(MediaType.parse("text/plane"), gLng);
                RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
                MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
                Log.e("requestfile", "upload_image:" + file.getName());
                Log.e("req_tags", String.valueOf(req_tags));
                Log.e("req_location", String.valueOf(req_location));
                Log.e("req_lat", String.valueOf(req_lat));
                Log.e("req_lng", String.valueOf(req_lng));
                Log.e("upload_image", String.valueOf(upload_image));

                Call<StyleBookItem> call = service.sb_register(req_writer, req_tags, req_location,req_lat,req_lng,upload_image);
                call.enqueue(new Callback<StyleBookItem>() {
                    @Override
                    public void onResponse(Call<StyleBookItem> call, Response<StyleBookItem> response) {
                        response.body();
                        Log.e("response.body", String.valueOf(response.body()));
//                        styleBookAdapterMain.notifyDataSetChanged();
                        onBackPressed();
                        Intent toMain = new Intent(StyleBookRegister.this,MainActivity.class);
                        toMain.setAction(Intent.ACTION_VIEW);
                        startActivity(toMain);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<StyleBookItem> call, Throwable t) {

                        Log.e("글등록", t.getMessage());
                        Toast.makeText(StyleBookRegister.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.sr_btn_edit://수정하기
                /**************************** 글 수정하기 ****************************
                 1. 수정하기 버튼을 탭한다.
                 2. 이미지(photo or album URI), 제목, 내용가 모두 입력되었는지 확인한다. ( 위치정보 필수 아님)
                 2-1. 이미지
                 - 'photoUri' 가 null 이 아닐 경우, photoUri 를 imageUri 변수에 저장한다.
                 - 'albumURI' 가 null 이 아닐 경우, albumURI 를 imageUri 변수에 저장한다.
                 - 'photoUri'와 'albumURI'가 모두 비어있을 경우, "이미지를 등록해주세요" 팝업 띄운다.
                 2-2. 제목
                 - 제목이 입력되지 않은 경우, "제목을 입력해주세요" 팝업 띄운다.
                 2-3. 내용
                 - 내용이 입력되지 않은 경우, "내용을 입력해주세요" 팝업 띄운다.
                 3. 로그인 회원정보를 불러와 회원no 를 변수에 저장한다.
                 4. 모두 입력되었으면 ★글no★, 이미지, 제목, 내용, (+위치정보), 작성자no 를 requestbody로 변환한다.
                 5. requestbody로 변환된 입력정보를 서버로 전달한다.
                 6. 스타일북 DB테이블에 저장한다.
                 6-1. 성공시 메인화면으로 이동한다.
                 6-2. 실패시 '수정실패' 메시지가 뜬다.
                 ********************************************************************/
                //꼼수
//                gLocation = "카페창비";
//                edit_location_lat = "2332";
//                edit_location_lng = "3434";

                Log.e("fromEdit", "궁금" + String.valueOf(fromEdit));
                gTags = setTags.getText().toString().trim();
                try {
                    gLocation = setLocation.getText().toString().trim();
                } catch (Exception e) {
                    Log.e("글등록", "위치추가" + e.getMessage());
                }

                if (gTags.isEmpty()) {
                    Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                if (gLocation.isEmpty()) {
                    gLocation = "null";
                }
                if (photoUri != null) { //카메라에서 찍은 사진
                    imageUri = photoUri;
                } else if (albumURI != null) { //앨범에서 가져온 사진
                    imageUri = albumURI;
                }



                file = FileUtils.getFile(this, imageUri); //이미지파일

                Gson gson_edit = new Gson();
                String json_edit = sp.getString("login", null);
                Type type_edit = new TypeToken<UserInfo>() {
                }.getType();
                user = gson_edit.fromJson(json_edit, type_edit);
                userNo = user.getNo();
                Log.e("셰어드 불러오기", "성공");
                Log.e("로그인유저:", String.valueOf(user.getNo()));

                Log.e("글등록", "작성자:" + userNo);
                Log.e("글등록", "내용:" + gTags);
                Log.e("글등록", "위치:" + gLocation);
                Log.e("글등록", "이미지uri:" + imageUri);
                Log.e("글등록", "이미지file:" + file);
                String no_edit = String.valueOf(userNo);

                //이미지를 새로 등록하지 않은 경우, 이미지를 빼고 보낸다.
                if (file == null) {
                    Call<API_Result> call_edit = service.sb_edit_noimage(sb_no, gTags,gLocation,edit_location_lat,edit_location_lng,no_edit);
                    call_edit.enqueue(new Callback<API_Result>() {
                        @Override
                        public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                            response.body();
                            Log.e("response.body", String.valueOf(response.body()));
                            onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<API_Result> call, Throwable t) {
                            Log.e("글수정", t.getMessage());
                            Toast.makeText(StyleBookRegister.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    RequestBody req_id_edit = RequestBody.create(MediaType.parse("text/plane"), String.valueOf(sb_no));
                    RequestBody req_writer_edit = RequestBody.create(MediaType.parse("text/plane"), no_edit);
                    RequestBody req_tags_edit = RequestBody.create(MediaType.parse("text/plane"), gTags);
                    RequestBody req_location_edit = RequestBody.create(MediaType.parse("text/plane"), gLocation);
                    RequestBody req_lat_edit = RequestBody.create(MediaType.parse("text/plane"), gLat);
                    RequestBody req_lng_edit = RequestBody.create(MediaType.parse("text/plane"), gLng);
                    RequestBody requestfile_edit = RequestBody.create(MediaType.parse("*/*"), file); //프로필 이미지파일
                    MultipartBody.Part upload_image_edit = MultipartBody.Part.createFormData("picture", file.getName(), requestfile_edit);
                    Log.e("upload_image", String.valueOf(upload_image_edit));
                    Log.e("requestfile", "upload_image:" + file.getName());
                    Log.e("req_tags", String.valueOf(req_tags_edit));
                    Log.e("req_location", String.valueOf(req_location_edit));
                    Call<API_Result> call_edit = service.sb_edit(req_id_edit, req_tags_edit, req_location_edit,req_lat_edit,req_lng_edit, req_writer_edit, upload_image_edit);
                    call_edit.enqueue(new Callback<API_Result>() {
                        @Override
                        public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                            response.body();
                            Log.e("response.body", String.valueOf(response.body()));
                            onBackPressed();
//                            Log.e("whatScreen",whatScreen);
//                            if (whatScreen.equals("main")) {
//                                Intent toMain = new Intent(StyleBookRegister.this, MainActivity.class);
//                                startActivity(toMain);
//                                finish();
//                            } else if (whatScreen.equals("mypage")) {
//                                Intent toMypage = new Intent(StyleBookRegister.this, MyPageActivity.class);
//                                startActivity(toMypage);
//                                finish();
//                            }
                        }
                        @Override
                        public void onFailure(Call<API_Result> call, Throwable t) {
                            Log.e("글수정", t.getMessage());
                            Toast.makeText(StyleBookRegister.this, "정보를 다시 한 번 확인해주세요.!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.sr_btn_location://위치정보 가져오기
                Log.e("PlacePicker","접근");
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder(); //검색 빌드
                Log.e("PlacePicker","검색필드");
                try {
                    Log.e("PlacePicker","인텐트빌더 접근");
                    startActivityForResult(intentBuilder.build(this), PLACE_PICKER_REQUEST);
                    Log.e("PlacePicker","인텐트빌더 빌드");
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("PlacePicker","예외1");
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("PlacePicker","예외2");
                    e.printStackTrace();
                }


                break;
            case R.id.sr_btn_close://닫기 버튼
                Intent toMain = new Intent(this,MainActivity.class);
                toMain.setAction(Intent.ACTION_VIEW);
                startActivity(toMain);
                finish();
                break;
        }
    }

    //앨범에서 가져오기 메소드
    private void photoAlbum() {
        //이미지 주소값 가져오기!!
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 200);
    }

    //사진찍기 메소드
    private void sendTakePhotoIntent() {

        int permissionCheck = ContextCompat.checkSelfPermission(StyleBookRegister.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한 없음
            ActivityCompat.requestPermissions(StyleBookRegister.this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            //권한 있음
            /*
            사진을 찍는다.
            사진을 찍었을 경우, photoUri에 사진이 저장된다.
            이 때 앨범은 선택하지 않았기 때문에 albumURI = null로 한다.
             */
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    Log.e("photoFile", String.valueOf(photoFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //이미지가 저장될 uri를 같이 넘긴다.
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                    albumURI = null;
                    Log.e("albumURI", String.valueOf(albumURI));
                    Log.e("photoUri", String.valueOf(photoUri));
                }
            }
        }
    }

    //찍은 사진 가져오는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("requestCode", String.valueOf(requestCode));
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            Log.e("이미지파일패스", imageFilePath);
            Log.e("이미지비트맵", String.valueOf(bitmap));

            try {
                exif = new ExifInterface(imageFilePath);
                Log.e("이미지exif", String.valueOf(exif));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                Log.e("null아님", "ㅇㅇ");
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = imageRotate(exifOrientation);

                Log.e("exifOrientation", String.valueOf(exifOrientation));
                Log.e("exifOrientation1", String.valueOf(exifDegree));
            } else {
                Log.e("null임", "ㅇㅇ");
                exifDegree = 0;
                Log.e("exifOrientation2", String.valueOf(exifDegree));
            }

            image.setImageBitmap(rotate(bitmap, exifDegree)); //가져온 이미지 셋팅

        } else if (requestCode == REQUEST_IMAGE_ALBUM) {
                albumURI = data.getData();
                file = FileUtils.getFile(getApplicationContext(), albumURI);
                image.setImageURI(albumURI);
                Log.e("albumURI"+"2222", String.valueOf(albumURI));
                Log.e("file"+"2222", String.valueOf(file));

        }else if (requestCode == PLACE_PICKER_REQUEST) {
            Log.e("PlacePicker","request");
            //피커가 찍힌 장소를 가져오는 코드
            if (resultCode == RESULT_OK) {
                Log.e("PlacePicker","result_ok1");
                Place place = PlacePicker.getPlace(data, this);
                Log.e("PlacePicker","result_ok2");
                String toastMsg = String.format("Place: %s", place.getName());
                Log.e("PlacePicker","result_ok3");
//                place.getLatLng();
//                PlaceAutocomplete.getPlace(this, data);
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//                double latitude = place.getLatLng().latitude; //위도
//                double longitude =place.getLatLng().longitude;  //경도
                gLocation = (String) place.getName();
                setLocation.setText(gLocation);
                gLat = String.valueOf(place.getLatLng().latitude);
                gLng = String.valueOf(place.getLatLng().longitude);

                Log.e("장소", (String) place.getName());
                Log.e("위도,경도",gLat+","+gLng);


//                //여기부터는 특정 영역을 먼저 반환한다...
//                LatLngBounds latLngBounds = new LatLngBounds(
//                        new LatLng(47.64299816, -122.14351988),
//                        new LatLng(50.64299816, -122.14351988)
//                );
//
//                try {
//                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .setBoundsBias(latLngBounds)
//                            .build(this);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }


            }
        }
    }

    //이미지 파일을 생성해주는 메소드 - 이미지가 저장"된" 파일이 아닌, 이미지가 저장"될"파일을 만드는 메소드.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults[0] == 0) {
                Toast.makeText(this, "카메라 권한이 승인됨", Toast.LENGTH_SHORT).show();
            } else {
                //권한이 거절된 경우
                Toast.makeText(this, "카메라 권한이 거절되었습니다. 카메라를 이용하려면 권한을 승낙해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                for(int i=0; i <permissions.length; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        if(grantResult == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"읽기/쓰기 권한이 승인되었습니다. ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,"읽기/쓰기 권한이 승인이 거절되었습니다. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_ALBUM:
                for(int i=0; i <permissions.length; i++){
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        if(grantResult == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"읽기/쓰기 권한이 승인되었습니다. ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,"읽기/쓰기 권한이 승인이 거절되었습니다. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }

    }

    //이미지 방향 정의 메소드
    private int imageRotate(int orientation) {
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }


    //이미지 회전 메소드
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //이미지 크롭
    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("scale", true);

        if (album == false) {
            cropIntent.putExtra("output", photoUri); // 크롭 이미지를 해당 경로에 저장
        } else if (album == true) {
            cropIntent.putExtra("output", albumURI);
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }

    @Override //얜 누구?
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }


    /**
     * <해시태그 메소드>
     * #문자를 가진 단어를 가져와서 해당 단어의 start와 end를 각각 저장한다.
     * 저장된 tagStart와 tagEnd를 가지고 SpannableString 객체를 만든 후
     * 그 결과 콘텐츠를 textView에 뿌려준다.
     */

//    //구글 마커
//    private void marker(String 장소명,double lat, double lug){
//        Marker marker = new Marker(lat,lug);
//        LatLng latLug = new LatLng(lat,lug);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLug);
//        markerOptions.title("선택된 장소");
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp));
//        markerOptions.getPosition();
//        marker = map.addMarker(markerOptions);
//
//    }
}
