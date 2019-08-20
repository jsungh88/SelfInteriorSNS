package com.example.joanne.selfinsns_;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.joanne.selfinsns_.retrofit.model.ChatItem;
import com.example.joanne.selfinsns_.retrofit.model.KnowHowItem;
import com.example.joanne.selfinsns_.retrofit.model.StyleBookItem;
import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.example.joanne.selfinsns_.retrofit.remote.APIService;
import com.example.joanne.selfinsns_.retrofit.remote.API_Result;
import com.example.joanne.selfinsns_.retrofit.remote.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    클래스명: 채팅창
    설명: 메시지를 주고받을 수 있는 창
    상세설명:
            1. 방입장과 동시에 방번호, 보낸사람id를 서버에 전달한다.
            2. 입장한 사람id를 채팅참여DB에 저장한다.
            3. 서버로부터 '입장했다'는 메시지를 받은 것을 리스트뷰에 표시한다.
            4. 메세지 수신을 대기한다.
             4-1. 메세지를 수신받으면, 아이템객체에 저장후 리스트에 추가하여 갱신시킨다.
              4-1-1. 만약 보낸사람id와 유저id가 일치할 경우, who="sender"로 저장 후 담는다.
              4-1-2. 만약 보낸사람id와 유저id가 일치하지 않을 경우, who="receiver"로 저장 후 담는다.
            5. 메세지를 송신한다.
             5-1. 방id, 보낸사람id, 메세지, 보낸시간을 서버에 전달한다.
              => chatItem객체에 저장 후, 리스트에 추가 및 갱신한다.
             5-2. (서버) 방id로, 방에 참여한 사람을 불러낸다.(id,이름,이미지)
             5-3. (서버) 방에 참여한 사람 중 현재 접속자에게 메세지를 전달한다.
             5-4. (서버) 방에 참여한 사람 중 현재 접속하지않은 사람에게는 메세지를 보내지 않는다.
             5-5. (서버) 받은 메세지를 JDBC를 이용하여 메세지DB에 저장한다.
            6. 메세지를 수신받는다.
             6-1. 보낸사람id, 보낸사람이름, 보낸사람이미지, 메세지, 보낸시간을 전달 받는다.
             6-2. 보낸사람id가 유저id와 일치할 경우, who="sender"로 저장후 chatItem객체에 담는다. 그리고 리스트에 추가 및 갱신한다.
             6-3. 보낸사람id가 유저id와 일치하지않을 경우, who="receiver"로 저장 후 chatItem객체에 담는다. 그리고 리스트에추가 및 갱신한다.

            7.기타
             - 모든 내용은 Gson 또는 json으로 묶어 온다. 때문에 구분자를 이용하여 구분자를 풀어주는 클래스로 풀어주던지, gson을 사용하여 풀던지 해야한다.
    */
    Context context;

    LinearLayout select_photos;
    RecyclerView chatRecyclerview;
    ChatAdapter chatAdapter;
    EditText editMessage; //메시지 입력창
    Button btnSend,btnImage,btnViewCall; //보내기 버튼
    ImageView btnBack, btnOut;
    TextView chatTitle;
    Integer roomId;//방id
    String roomName, leaderName, first;//방name,방장이름,첫입장

    ChatItem item; //수신,발신메시지
    List<ChatItem> items; //저장메시지 불러오기

    Boolean fromMakeRoom = false;//방만들기에서 바로 왔는지?

    //서버 접속 여부를 판별하기 위한 변수
    boolean isConnect = false;
    LinearLayout container; //채팅 대화 오가는 레이아웃
    ScrollView scroll; //채팅대화 감싸는 스크롤뷰
    ProgressDialog dialog;
    //어플 종료시 스레드 중지를 위한 변수
    boolean isRunning = false;
    //이미지전송 or 메세지전송
    boolean isImage = false;
    //서버와 연결되어있는 소켓 객체
    Socket member_socket;
    //사용자 닉네임(내 닉네임과 일치하면 내가 보낸 말풍선으로 설정 아니면 반대설정)
    String user_nickname;

    SharedPreferences sp;//로그인유저정보 가져오기위한 셰어드
    APIService service;
    UserInfo loginUser;
    Integer userNo;//유저id
    String s_userNo, s_userName, s_roomId;//소켓서버로보낼 유저no,유저name,룸id
    String who, message;//서버로부터 받아오는 정보

    JSONObject connJsonData; //접속정보(방id,로그인유저id)를 담은 jsonObject
    JSONObject messageJson; //보낼메세지정보(방id,보낸사람정보(id,이름,이미지))를 담은 jsonObject

    String sendMessage;//서버로 보내는 메세지

    String image_uri;
    String img;
    JSONArray imageJson;//보낼이미지저장

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final String TAG = "TedPicker";
    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    ArrayList<Uri> image_uris2 = new ArrayList<Uri>();
    private ViewGroup mSelectedImagesContainer;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_messagelist);
        service = ApiUtils.getAPIService();

        select_photos = findViewById(R.id.select_photos);
        select_photos.setVisibility(View.GONE);

        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);//선택된 사진 표시영역
        editMessage = findViewById(R.id.edittext_chatbox); //채팅입력란
        btnImage = findViewById(R.id.button_image);//이미지첨부버튼
        btnSend = findViewById(R.id.button_chatbox_send); //채팅보내기버튼
//        btnViewCall = findViewById(R.id.button_viewCall);//화상통화걸기(방참여자 모두에게) 가능할지??//////////////////////////
        btnBack = findViewById(R.id.chat_btnBack); //뒤로가기버튼
        btnOut = findViewById(R.id.chat_out); //방나가기
        chatTitle = findViewById(R.id.chat_title); //채팅방이름
        container = findViewById(R.id.chat_container); //채팅 오가는 레이아웃
        scroll = findViewById(R.id.chat_scroll); //채팅대화 감싸는 스크롤뷰
        btnSend.setOnClickListener(this);
//        btnViewCall.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnOut.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        scroll.fullScroll(View.FOCUS_DOWN);
        Intent fromMR = getIntent();
        fromMakeRoom = fromMR.getBooleanExtra("fromMakeRoom", false);

        //1.방번호, 방이름 가져오기*******************************************************************************//
        if (fromMakeRoom) {
            roomId = fromMR.getIntExtra("roomId", 0);
            roomName = fromMR.getStringExtra("roomName");
            leaderName = fromMR.getStringExtra("leaderName"); //방장이름 - 아직 어디에 쓸지는 모르겠음
        } else {
            Intent intent = getIntent();
            roomId = intent.getIntExtra("roomId", 0);
            roomName = intent.getStringExtra("roomName");
            first = intent.getStringExtra("first");
//            leaderName = intent.getStringExtra("leaderName");
            Log.e("ChattingActivity" + "roomId", String.valueOf(roomId));
            Log.e("ChattingActivity" + "roomName", roomName);
            Log.e("ChattingActivity" + "first", first);
//            Log.e("ChattingActivity" + "leaderName", leaderName); //방장이름
        }
        chatTitle.setText(roomName);//채팅방이름 셋팅

        //2.로그인유저 정보 불러오기*******************************************************************************//
        sp = getSharedPreferences("login_user", Activity.MODE_PRIVATE); //로그인한 유저 정보를 저장해놓은 셰어드
        Gson gson = new Gson();
        String json = sp.getString("login", null);
        Type type = new TypeToken<UserInfo>() {
        }.getType();
        loginUser = gson.fromJson(json, type);
        userNo = loginUser.getNo();
        Log.e("로그인유저:", String.valueOf(loginUser.getNo()));

        //3.채팅 메세지내역 보여주기*******************************************************************************//
        chatRecyclerview = findViewById(R.id.reyclerview_message_list); //메시지리스트
        chatRecyclerview.setHasFixedSize(true);
        items = new ArrayList<>();
        getChatMessage();//저장된 채팅메세지 가져오기
        //이미지불러와서 셰어드저장.

        chatRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        chatAdapter = new ChatAdapter(ChattingActivity.this, items);
        chatRecyclerview.setAdapter(chatAdapter);
        chatRecyclerview.post(new Runnable() { //맨 아래로 보내기
            @Override
            public void run() {
                chatRecyclerview.scrollToPosition(chatRecyclerview.getAdapter().getItemCount()-1);
            }
        });

        //4.채팅 접속하기****************************************************************************************//
        //4-1.유저id,룸id를 가져온다.
        s_userNo = String.valueOf(userNo);
        s_roomId = String.valueOf(roomId);
        connJsonData = new JSONObject();
        try {
            connJsonData.put("userNo", s_userNo);
            connJsonData.put("userName", loginUser.getName());
            connJsonData.put("roomId", s_roomId);
            connJsonData.put("first", first);
            Log.e("userNo", connJsonData.getString("userNo"));
            Log.e("userName", connJsonData.getString("userName"));
            Log.e("roomId", connJsonData.getString("roomId"));
            Log.e("first", connJsonData.getString("first"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //4-2.소켓서버에 접속한다.
        if (isConnect == false) { //접속전 :소켓접속 스레드 가동
//            dialog = ProgressDialog.show(this, null, "[" + roomName + "] 채팅방에 접속중입니다.");
            ConnectionThread thread = new ConnectionThread();
            thread.start();
            Log.e("접속중", "스레드가동");
        } else { //접속후 : 메세지 송수신 스레드 가동
            isConnect = true;
        }

        //6.이미지전송
        //카메라,앨범 퍼미션
        if (Build.VERSION.SDK_INT > 22) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    | ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        INTENT_REQUEST_GET_IMAGES);
            } else {
                nextStage();
            }
        } else {
            nextStage();
        }

        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();

        // get the first preview size supported by the device
        Camera.Size s = parameters.getSupportedPreviewSizes().get(0);

        parameters.setPreviewSize(s.width, s.height);
        camera.setParameters(parameters);

    }


    public void btnMethod() {


        //전송버튼과 연결된 메소드
        //접속 전일 경우, 사용자이름을 전송하고, 접속 스레드를 가동시킴
        //접속 후일 경우, 송신스레드를 가동시킴
        if (isConnect == false) {//접속 전
            ConnectionThread thread = new ConnectionThread();
            thread.start();
        } else {//접속 후
            if(isImage == false) {//메세지 전송일때
                //5.메세지 송수신하기
                sendMessage = editMessage.getText().toString();
                messageJson = new JSONObject();
                try {
                    messageJson.put("kindof", "message");
                    messageJson.put("roomId", s_roomId);
                    messageJson.put("userNo", s_userNo);
                    messageJson.put("userName", loginUser.getName());
                    messageJson.put("userImage", loginUser.getPicture());
                    messageJson.put("message", sendMessage);
                    Log.e("보낼메세지(종류):", messageJson.getString("kindof"));
                    Log.e("보낼메세지(방id):", messageJson.getString("roomId"));
                    Log.e("보낼메세지(유저no):", messageJson.getString("userNo"));
                    Log.e("보낼메세지(이름):", messageJson.getString("userName"));
                    Log.e("보낼메세지(유저이미지):", messageJson.getString("userImage"));
                    Log.e("보낼메세지(메세지):", messageJson.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SendToServerThread thread = new SendToServerThread(member_socket, messageJson.toString());
                thread.start();


            }else{//이미지 전송일 때

                ArrayList<String> arr_images = saveImages(image_uris2);
                /*
                1.이미지를 DB에 저장한다.
                2.이미지를 화면에 표시한다.
                3.소켓서버로 보낸다.(jsonArray로 묶어서)
                 */

                for(int i =0; i<arr_images.size(); i++){
                    imageJson = new JSONArray();
                    final int j =i;
                    String img_uri = arr_images.get(i);
                    Log.e("img_uri",img_uri);
                    File file = new File(img_uri);
                    Log.e("file", String.valueOf(file));

                    RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file); //이미지파일
                    MultipartBody.Part upload_image = MultipartBody.Part.createFormData("picture", file.getName(), requestfile);
                    Log.e("requestfile", "upload_image:" + file.getName());

                    RequestBody req_roomId = RequestBody.create(MediaType.parse("text/plane"), s_roomId);


//                    final ProgressDialog progressDialog = new ProgressDialog(this);
//                    progressDialog.setMessage("uploading...");
//                    progressDialog.show();
                    //3.이미지를 DB에 등록한다.
                    Call<API_Result> call = service.chat_imageUpload(req_roomId, upload_image);
                    call.enqueue(new Callback<API_Result>() {
                        @Override
                        public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                            Log.e("response.body", String.valueOf(response.body().getMessage()));
                            img = response.body().getMessage();
//                            progressDialog.dismiss();

//                            if(!img.isEmpty()){

//                            }
                            //현재 시간을 구하고, 메세지리스트에 추가한다.(일단 화면상)
                            SimpleDateFormat dayTime = new SimpleDateFormat("a h:mm");
                            String str = dayTime.format(new Date());
                            ChatItem item = new ChatItem("sender_image",userNo,loginUser.getPicture(),loginUser.getName(),img,str);
                            items.add(item);
                            chatAdapter.notifyDataSetChanged();
                            select_photos.setVisibility(View.GONE);
                            isImage = false;

                            imageJson.put(img);
                            try {
                                Log.e("이미지저장",imageJson.get(j).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<API_Result> call, Throwable t) {
                            Log.e("이미지등록", t.getMessage());
                        }
                    });
                }

                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    //5.메세지 송수신하기
                                    messageJson = new JSONObject();

                                    try {
                                        messageJson.put("kindof", "image");
                                        messageJson.put("roomId", s_roomId);
                                        messageJson.put("userNo", s_userNo);
                                        messageJson.put("userName", loginUser.getName());
                                        messageJson.put("userImage", loginUser.getPicture());
                                        messageJson.put("message", imageJson);
                                        Log.e("보낼메세지(종류):", messageJson.getString("kindof"));
                                        Log.e("보낼메세지(방id):", messageJson.getString("roomId"));
                                        Log.e("보낼메세지(유저no):", messageJson.getString("userNo"));
                                        Log.e("보낼메세지(이름):", messageJson.getString("userName"));
                                        Log.e("보낼메세지(유저이미지):", messageJson.getString("userImage"));
                                        Log.e("보낼메세지(메세지):", messageJson.getString("message"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    SendToServerThread thread = new SendToServerThread(member_socket, messageJson.toString());
                                    thread.start();
                                }
                            }, 1000);// 0.5초 정도 딜레이를 준 후 시작



            }
        }
    }

    private void getChatMessage() { //저장된 채팅 메시지 가져오기
        /*
        1) 유의할 점 : 유저의 접속상태에 따라 메시지를 가져오는 정도가 다르다.
        - 신규 접속일 경우, 저장메시지를 가져오지 않는다.
        - 이미 접속해 있었을 경우, 유저가 접속한 시점부터의 메시지를 모두 볼 수 있다.
        2) 가져오는 정보
        <사용자관점>
        - 수신메세지: 메세지,보낸사람(id,이름,이미지),보낸시간
        - 발신메세지: 메세지,보낸시간
        <서버관점>
        - 보낸사람구분(receiver, sender), 보낸사람(id,이름,이미지), 메세지, 보낸시간
        - 유저의 접속시간보다 더 이후에 있는 메세지만 불러온다.
         */

        Call<List<ChatItem>> call = service.chatMessage_view(roomId,userNo);
        call.enqueue(new Callback<List<ChatItem>>() {
            @Override
            public void onResponse(Call<List<ChatItem>> call, Response<List<ChatItem>> response) {
                List<ChatItem> chatMessages = response.body();
                    for (ChatItem s : chatMessages) {
                        if(s.getSender()==null){
                            s.setSender(0);
                        }else if(s.getWho().equals(null)){
                            s.setWho("null");
                        }else if(s.getUserId()==null){
                            s.setUserId(0);
                        }else if(s.getUserName().equals(null)){
                            s.setUserName("null");
                        }else if(s.getUserImage().equals(null)){
                            s.setUserImage("null");
                        }

                        if(s.getSender()==userNo&&s.getWho().equals("receiver_image")&&s.getWho().equals("sender_image")){
                            s.setWho("sender");
                        }else if(s.getSender()!=userNo&&s.getSender()!=0&&s.getWho().equals("receiver_image")&&s.getWho().equals("sender_image")){
                            s.setWho("receiver");
                        }else if(s.getSender()==userNo&&s.getWho().equals("receiver_image")){
                            s.setWho("sender_image");
                        }

                        SimpleDateFormat dt = new SimpleDateFormat(s.getTime());
                        try {
                            Date date = dt.parse(s.getTime());
                            SimpleDateFormat dt1 = new SimpleDateFormat("a h:mm");
                            s.setTime(dt1.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        items.add(s);
                    }
//                items.addAll(chatMessages);
//                Log.e("채팅저장메세지", String.valueOf(items));
                chatRecyclerview.setLayoutManager(new LinearLayoutManager(ChattingActivity.this, LinearLayout.VERTICAL, false));
                chatAdapter = new ChatAdapter(ChattingActivity.this, items);
                chatRecyclerview.setAdapter(chatAdapter);
            }

            @Override
            public void onFailure(Call<List<ChatItem>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_btnBack:
                onBackPressed();

                break;
            case R.id.chat_out:
                roomOutDialog();

                break;
            case R.id.button_image://이미지첨부
                select_photos.setVisibility(View.VISIBLE);
                getImages(new Config());

                break;

            case R.id.button_chatbox_send://보내기
                btnMethod();
                break;
        }
    }

    public void roomOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("방 나가기");
        builder.setMessage("[" + roomName + "]방을 정말 나가시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                        Call<API_Result> call = service.chat_out(roomId, userNo);
                        call.enqueue(new Callback<API_Result>() {
                            @Override
                            public void onResponse(Call<API_Result> call, Response<API_Result> response) {
                                Log.e("방나가기", "성공");
                                onBackPressed();

                                JSONObject outJson = new JSONObject();
                                try {
                                    outJson.put("kindof", "out");
                                    outJson.put("roomId", s_roomId);
                                    outJson.put("userNo", s_userNo);
                                    outJson.put("userName", loginUser.getName());
                                    Log.e("보낼메세지(종류):", outJson.getString("kindof"));
                                    Log.e("보낼메세지(방id):", outJson.getString("roomId"));
                                    Log.e("보낼메세지(유저no):", outJson.getString("userNo"));
                                    Log.e("보낼메세지(이름):", outJson.getString("userName"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SendToServerThread thread2 = new SendToServerThread(member_socket, outJson.toString());
                                thread2.start();


                            }

                            @Override
                            public void onFailure(Call<API_Result> call, Throwable t) {
                                Log.e("방나가기", "실패");
                            }
                        });


                    }
                });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //취소: 다이아로그 종료
//                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void nextStage() {
        Log.e("nextStage", "nextStage");
    }

    //앨범에서 이미지 가ㅈㅕ오기
    private void getImages(Config config) {
        ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(this, ImagePickerActivity.class);

        if (image_uris != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris);
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris2);
        }

        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                image_uris2 = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }
            }
        }
    }

    //다중이미지 불러온 것을 썸네일뷰에 표시
    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        if (image_uris.size() >= 1) {
            mSelectedImagesContainer.setVisibility(View.VISIBLE);
        }

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        int index =0;
        Log.e("index", String.valueOf(index));
        for (Uri uri : image_uris) {
            final int ind = index++;
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });
        }
        isImage = true; //이미지전송상태
    }

    //카메라,앨범 접근 허용 결과
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    nextStage();
                } else {
//                    finish();
                    Log.e("finish", "finish");
                }
                break;
            default:
                break;
        }
    }

    private ArrayList<String> saveImages(ArrayList<Uri> images) {

        //image_uries > String형으로 변환하여 저장.
        images = image_uris;
        Log.e("이미지가 있니?", String.valueOf(images));
        // Log.e( "image.get(0)", String.valueOf( images.get( 0 ) ) );
        ArrayList<String> image_string = null;

        for (int i = 0; i < images.size(); i++) {
            String str_image = String.valueOf(images.get(i));
            Log.e("str_image", str_image);

            if (image_string == null) {
                image_string = new ArrayList<String>();
                Log.e("이문제니?", "ㅇㅇ");
            }
            image_string.add(i, str_image);
            Log.e("image_string" + i, image_string.get(i));
        }

        return image_string;
    }


    //접속 스레드
    private class ConnectionThread extends Thread {

        @Override
        public void run() {
            try {
                final Socket socket = new Socket("13.209.108.67", 10502);
                member_socket = socket;

                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                Log.e("userNo", connJsonData.getString("userNo"));
                Log.e("userName", connJsonData.getString("userName"));
                Log.e("roomId", connJsonData.getString("roomId"));
                Log.e("first", connJsonData.getString("first"));

                dos.writeUTF(String.valueOf(connJsonData)); //접속정보 송신(방id,유저id)
//                dos.writeUTF(roomName); //접속정보 송신(방id,유저id) -test용도
                //progressDialog제거
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        dialog.dismiss();
                        editMessage.setText("");
                        editMessage.setHint("메세지를 입력해주세요");
                        isConnect = true; //접속상태 스레드 가동: true
                        isRunning = true; //메세지수신 스레드 가동: true
                        MessageThread thread = new MessageThread(socket);
                        thread.start();
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    //메세지 수신 스레드
    class MessageThread extends Thread {
        Socket socket;
        DataInputStream dis; //데이터받는 스트림

        public MessageThread(Socket socket) {
            try {
                this.socket = socket;
                InputStream is = socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            try {
                while (isRunning) { //메세지 수신 스레드 가동중일 경우
                    //서버로부터 데이터를 수신받는다.
                    final String msg = dis.readUTF(); //////////////받는메세지 json형태로 받아야함.
                    Log.e("메세지 도착", msg);

                    try {
                        JSONObject jsonMsg = new JSONObject(msg);
                        who = jsonMsg.getString("who");
                        if(who.equals("notice")){//종류=공지인경우
                            JSONObject jsonNotice = new JSONObject(msg);
                            who = jsonNotice.getString("who");
                            message = jsonNotice.getString("message");
                            Log.e("who", who);
                            Log.e("message", message);
                            item = new ChatItem(who, message);
                            items.add(item);
                            for (ChatItem k : items) {
                                Log.e("items_who", k.getWho());
                                Log.e("items_message", k.getMessage());
                            }
                        }else if(who.equals("sender")){//종류=보낸사람
                            JSONObject jsonMessage = new JSONObject(msg);
                            who = jsonMessage.getString("who");
                            Integer roomId = Integer.valueOf(jsonMessage.getString("roomId"));
                            Integer userId = Integer.valueOf(jsonMessage.getString("userId"));
                            String userName = jsonMessage.getString("userName");
                            String userImage = jsonMessage.getString("userImage");
                            String message = jsonMessage.getString("message");
                            String time =jsonMessage.getString("time");
                            Log.e("who",who);
                            Log.e("roomId", String.valueOf(roomId));
                            Log.e("userId", String.valueOf(userId));
                            Log.e("userName",userName);
                            Log.e("userImage",userImage);
                            Log.e("message",message);
                            Log.e("time",time);


                            item = new ChatItem(roomId,who,userId,userName,userImage,message,time);
                            items.add(item);
                            for(ChatItem i : items){
                                Log.e("who",who);
                                Log.e("roomId", String.valueOf(roomId));
                                Log.e("userId", String.valueOf(userId));
                                Log.e("userName",userName);
                                Log.e("userImage",userImage);
                                Log.e("message",message);
                                Log.e("time",time);
                            }
                        }else if(who.equals("receiver")){//종류=받는사람(receiver)
                            JSONObject jsonMessage = new JSONObject(msg);
                            who = jsonMessage.getString("who");
                            Integer roomId = Integer.valueOf(jsonMessage.getString("roomId"));
                            Integer userId = Integer.valueOf(jsonMessage.getString("userId"));
                            String userName = jsonMessage.getString("userName");
                            String userImage = jsonMessage.getString("userImage");
                            String message = jsonMessage.getString("message");
                            String time =jsonMessage.getString("time");
                            Log.e("who",who);
                            Log.e("roomId", String.valueOf(roomId));
                            Log.e("userId", String.valueOf(userId));
                            Log.e("userName",userName);
                            Log.e("userImage",userImage);
                            Log.e("message",message);
                            Log.e("time",time);
                            item = new ChatItem(roomId,who,userId,userName,userImage,message,time);
                            items.add(item);
                            for(ChatItem i : items){
                                Log.e("who",who);
                                Log.e("roomId", String.valueOf(roomId));
                                Log.e("userId", String.valueOf(userId));
                                Log.e("userName",userName);
                                Log.e("userImage",userImage);
                                Log.e("message",message);
                                Log.e("time",time);
                            }

                        }else if(who.equals("sender_image")){
                            JSONObject jsonMessage = new JSONObject(msg);

                            who = jsonMessage.getString("who");
                            Integer roomId = Integer.valueOf(jsonMessage.getString("roomId"));
                            Integer userId = Integer.valueOf(jsonMessage.getString("userId"));
                            String userName = jsonMessage.getString("userName");
                            String userImage = jsonMessage.getString("userImage");
                            String message = jsonMessage.getString("message");
                            String time =jsonMessage.getString("time");
                            Log.e("sender_image:who",who);
                            Log.e("sender_image:roomId", String.valueOf(roomId));
                            Log.e("sender_image:userId", String.valueOf(userId));
                            Log.e("sender_image:userName",userName);
                            Log.e("sender_image:userImage",userImage);
                            Log.e("sender_image:message",message);
                            Log.e("sender_image:time",time);
                            JSONArray jsonImage = new JSONArray(message);
                            for(int i=0; i<jsonImage.length(); i++){
                                String message1 = jsonImage.get(i).toString();
                                item = new ChatItem(roomId,who,userId,userName,userImage,message1,time);
                                items.add(item);
                                for(ChatItem d : items){
                                    Log.e("sender_image:who",who);
                                    Log.e("sender_image:roomId", String.valueOf(roomId));
                                    Log.e("sender_image:userId", String.valueOf(userId));
                                    Log.e("sender_image:userName",userName);
                                    Log.e("sender_image:userImage",userImage);
                                    Log.e("sender_image:message",message);
                                    Log.e("sender_image:time",time);
                                }
                            }

                        }else{
                            JSONObject jsonMessage = new JSONObject(msg);

                            who = jsonMessage.getString("who");
                            Integer roomId = Integer.valueOf(jsonMessage.getString("roomId"));
                            Integer userId = Integer.valueOf(jsonMessage.getString("userId"));
                            String userName = jsonMessage.getString("userName");
                            String userImage = jsonMessage.getString("userImage");
                            String message = jsonMessage.getString("message");
                            String time =jsonMessage.getString("time");
                            Log.e("receiver_image:who",who);
                            Log.e("receiver_image:roomId", String.valueOf(roomId));
                            Log.e("receiver_image:userId", String.valueOf(userId));
                            Log.e("receiver_image:userName",userName);
                            Log.e("receiver_image:userImage",userImage);
                            Log.e("receiver_image:message",message);
                            Log.e("receiver_image:time",time);
                            JSONArray jsonImage = new JSONArray(message);
                            for(int i=0; i<jsonImage.length(); i++){
                                String message1 = jsonImage.get(i).toString();
                                item = new ChatItem(roomId,who,userId,userName,userImage,message1,time);
                                items.add(item);
                                for(ChatItem d : items){
                                    Log.e("receiver_image:who",who);
                                    Log.e("receiver_image:roomId", String.valueOf(roomId));
                                    Log.e("receiver_image:userId", String.valueOf(userId));
                                    Log.e("receiver_image:userName",userName);
                                    Log.e("receiver_image:userImage",userImage);
                                    Log.e("receiver_image:message",message);
                                    Log.e("receiver_image:time",time);
                                }
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //여기는 이제... 리스트에 담는 작업.
                            chatAdapter.notifyDataSetChanged();
                            scroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    class SendToServerThread extends Thread {
        Socket socket;
        String msg; //json형태로 바뀌어야함
        DataOutputStream dos;

        public SendToServerThread(Socket socket, String msg) {
            try {
                this.socket = socket;
                this.msg = msg;
                OutputStream os = socket.getOutputStream();
                dos = new DataOutputStream(os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // 서버로 데이터를 보낸다.
                dos.writeUTF(msg); //데이터 전달. 방번호,보낸사람정보(id,이름,이미지),메세지
                Log.e("SendToServerThread","스레드가동");
                runOnUiThread(new Runnable() {
                    //runOnUiThread: ui자원에 mainthread와 subthread가 동시접근하여 동기화 이슈를 발생시키는 것을 방지시키기위해
                    //UI자원사용은 UI Thread에서만 가능하도록 만들었음. Handler.post()와 같은 스레드 간 메시지 전달을 통해 구현가능.
                    @Override
                    public void run() {
                        editMessage.setText(""); //UI, 빈칸으로 바뀐다
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            member_socket.close(); //소켓종료
            isRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
