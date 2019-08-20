package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.joanne.selfinsns_.retrofit.model.UserInfo;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class KnowHowWrite_Step2_ImageDescription extends AppCompatActivity implements View.OnClickListener{

    Button btnBack, btnSave;//뒤로가기,저장하기
    ImageView setImage,btnClose;//이미지,닫기버튼
    EditText setDescription;//이미지설명입력란

    Integer get_img_position,put_image_position;
    String description;//사용자가 입력하는 이미지 설명
    Integer index;//이미지의 index값

    SharedPreferences sp;//이미지 설명 저장
    ArrayList<String> array_description;
    Gson gson;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowhow_image_descrirption_activity);

        btnClose = findViewById(R.id.image_description_btnclose);
        btnBack = findViewById(R.id.image_description_btnback);
        btnSave =findViewById(R.id.image_description_save);
        setImage = findViewById(R.id.image_description_image);
        setDescription = findViewById(R.id.image_description_description);

        btnClose.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        //1. step2로부터 이미지와, 이미지의 index값을 가져온다.
        Intent fromImageDescription = getIntent();
        String image_uri = fromImageDescription.getStringExtra("image");
        index = fromImageDescription.getIntExtra("index",0);
        Log.e("이미지uri",image_uri);
        Log.e("이미지index", String.valueOf(index));
        //2. 이미지를 셋팅시킨다.
        setImage.setImageURI(Uri.parse(image_uri));//가져온 이미지 셋팅

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_description_btnclose://닫기
                onBackPressed();
                break;
            case R.id.image_description_btnback://뒤로가기
                onBackPressed();
                break;
            case R.id.image_description_save://저장 후 닫기
                //셰어드저장씬 추가

                description = setDescription.getText().toString().trim();//사용자가 작성한 설명

                Log.e("index", String.valueOf(index));
                Log.e("description",description);
                Log.e("여기","여기여기1");
                //3. knowhow셰어드를 불러온다.
                sp = getSharedPreferences("knowhow",Activity.MODE_PRIVATE);
                gson = new Gson();
                String json = sp.getString("description", null);
                Type type = new TypeToken<ArrayList>() {
                }.getType();
                array_description = gson.fromJson(json, type);
                if(array_description==null){
                    array_description = new ArrayList<>();
                    Log.e("여기","여기여기2");
                }else {
                    //4. index=position값과 같게 하여,이미지 설명을 ArrayList에 저장한다.
                    array_description.add(index, description);
                    Log.e("kh_이미지설명", array_description.get(index));
                }
                String img_description = gson.toJson(array_description);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("description",img_description);
                editor.commit();
                Log.e("shared_knowhow",img_description);

                onBackPressed();
                break;
        }
    }



}
