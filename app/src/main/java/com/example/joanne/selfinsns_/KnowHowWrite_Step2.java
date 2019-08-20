package com.example.joanne.selfinsns_;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Joanne on 2018-05-17.
 */

public class KnowHowWrite_Step2 extends Fragment implements View.OnClickListener {

    Button laststep, btnpic;//단계이동 버튼, 이미지 선택버튼
    View step2;

    EditText subject, desc, tags;
    String str_subject, str_desc, str_tags;
    String recived_section, recived_style, recived_space;
    String image_uri;

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final String TAG = "TedPicker";
    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    ArrayList<Uri> image_uris2 = new ArrayList<Uri>();
    private ViewGroup mSelectedImagesContainer;

    saveContents2 SC;

    //이미지설명 셰어드 불러오기
    SharedPreferences sp;//이미지 설명 저장
    static ArrayList<String> array_description = null;

    private HashTagHelper mTextHashTagHelper;//해시태그 API

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        step2 = inflater.inflate(R.layout.knowhow_write_step2, container, false);
//        Toast.makeText(getActivity(), "onCreateView2", Toast.LENGTH_SHORT).show();


        mSelectedImagesContainer = (ViewGroup) step2.findViewById(R.id.selected_photos_container);//선택된 사진 표시 영역
        btnpic = step2.findViewById(R.id.btn_pic);
        btnpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getImages(new Config(), step2);

            }
        });


        //입력정보 저장
        subject = step2.findViewById(R.id.subject);
        desc = step2.findViewById(R.id.desc);
        tags = step2.findViewById(R.id.tags);

        //해시태그 기능.
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorHashTag), null);
        mTextHashTagHelper.handle(tags);

        laststep = step2.findViewById(R.id.laststep);
        laststep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("nextstep:", "come?");

                sp = getActivity().getSharedPreferences("knowhow", Activity.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sp.getString("description", null);
                Type type = new TypeToken<ArrayList>() {
                }.getType();
                array_description = gson.fromJson(json, type);
                Log.e("array_description", String.valueOf(array_description));

                //불러온 image_uris의 size만큼 돌려보고,
                // image_uris의 position = array_description의 index값을 일치시킨 후에
                // 만약 해당 index 값이 비어있으면, 그 index에 'null'값을 추가한다.
                int j = 0;
                for (int i = 0; i < image_uris2.size(); i++) {
                    if (array_description.get(j).equals(image_uris2.get(i))) {
                        array_description.set(j, "null");
                        Log.e("array_description"+(j),array_description.get(j));
                    }
                    j++;
                    Log.e("j:", String.valueOf(j));
                }

                //입력내용 번들에 저장.
                str_subject = subject.getText().toString().trim();
                str_desc = desc.getText().toString().trim();
                str_tags = tags.getText().toString().trim();
                SC.sendData(recived_section, recived_style, recived_space, str_subject, str_desc, str_tags, saveImages(image_uris2),array_description);
                Log.e("step2저장", recived_section + recived_style + recived_space + str_subject + str_desc + str_tags + saveImages(image_uris2)+array_description);
                ((KnowHowWriteActivity) getActivity()).switchFragment(2);
            }
        });

        //카메라,앨범 퍼미션
        if (Build.VERSION.SDK_INT > 22) {
            int permissionCheck = ContextCompat.checkSelfPermission(step2.getContext(), Manifest.permission.CAMERA)
                    | ContextCompat.checkSelfPermission(step2.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) step2.getContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        INTENT_REQUEST_GET_IMAGES);
            } else {
                nextStage();
            }
        } else {
            nextStage();
        }

        return step2;
    }

    @Override
    public void onClick(View v) {

    }


    public void nextStage() {
        Log.e("nextStage", "nextStage");
    }

    //앨범에서 이미지 가ㅈㅕ오기
    private void getImages(Config config, View view) {
        ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(view.getContext(), ImagePickerActivity.class);

        if (image_uris != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris);
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris2);
        }

        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

    }


    //
    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                image_uris2 = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);


                if (image_uris != null) {
                    showMedia();
                    array_description = new ArrayList<>();
                    for (Uri uri : image_uris) {
                        String img = uri.toString();
                        array_description.add(img);//이미지와 이미지설명 array의 size를 같게하기 위함.
                    }




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
            View imageHolder = LayoutInflater.from(step2.getContext()).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            Glide.with(step2.getContext())
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

//            image_uri = uri.toString();//이미지설명 액티비티에 보낼 이미지uri값
//            Log.e("image_uri1", String.valueOf(image_uri));
            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*
                    이미지, 해당인덱스값을 '이미지설명액티비티'로 전달한다.
                    인덱스값, 이미지설명을 sharedpreference 에 저장한다.(key:인덱스, value:이미지설명)
                    uris의 position과 이미지인덱스값 을 비교하여 arrayList에 저장한다.
                    (포지션값이 없으면 이미지설명을 비게 저장한다.)
                    지금까지 작성한 모든 정보를 step3으로 보낸다.
                    (step3에서 저장버튼을 누르면 서버로 전달하여 저장한다.)
                     */


                    image_uri = String.valueOf(image_uris.get(ind));





                    sp = getActivity().getSharedPreferences("knowhow",Activity.MODE_PRIVATE);
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = sp.edit();
                    String json = sp.getString("description", null);
                    Type type = new TypeToken<ArrayList>() {
                    }.getType();
                    array_description = gson.fromJson(json, type);
                    if(array_description==null){
                        array_description = new ArrayList<>();
                        Log.e("여기","여기여기2");
                    }



                    String img_description = gson.toJson(array_description);
                    editor.putString("description",img_description);
                    editor.commit();
                    Log.e("shared_knowhow",img_description);


                    Log.e("step2", "포지션:" + ind);
                    Log.e("step2", "이미지:" + image_uri);

                    Intent i = new Intent(step2.getContext(), KnowHowWrite_Step2_ImageDescription.class);
                    i.putExtra("index", ind);//이미지포지션값
                    i.putExtra("image", image_uri);
                    startActivity(i);


                }
            });
        }
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

    //Bundle 저장: 제목,설명,이미지,태그
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

    protected void receivedData(String section, String style, String space) {
        recived_section = section;
        recived_style = style;
        recived_space = space;
        Log.e("공간1:", recived_section);
        Log.e("스타일1:", recived_style);
        Log.e("평수1:", recived_space);

    }

    //Step3 프레그먼트로 데이터를 보내기위한 인터페이스
    interface saveContents2 {
        void sendData(String section, String style, String space, String subject, String desc, String tags, ArrayList<String> images, ArrayList<String> img_description);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Toast.makeText(getActivity(), "onAttach2", Toast.LENGTH_SHORT).show();

        Log.e("step2", "onAttatch");
        try {
            SC = (saveContents2) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("에러 에러, 다시해보아");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText( getActivity(),"onCreate2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Toast.makeText( getActivity(),"onActivityCreated2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText( getActivity(),"onStart2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText( getActivity(),"onResume2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText( getActivity(),"onPause2",Toast.LENGTH_SHORT ).show();

    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText( getActivity(),"onStop2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText( getActivity(),"onDestroyView2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText( getActivity(),"onDestroy2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Toast.makeText( getActivity(),"onDetach2",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Toast.makeText( getActivity(),"onSaveInstanceState2",Toast.LENGTH_SHORT ).show();
    }


}
