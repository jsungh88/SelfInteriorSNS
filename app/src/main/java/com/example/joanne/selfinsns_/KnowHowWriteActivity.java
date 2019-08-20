package com.example.joanne.selfinsns_;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.badoualy.stepperindicator.StepperIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joanne on 2018-05-11.
 */

public class KnowHowWriteActivity extends AppCompatActivity implements KnowHowWrite_Step1.SaveContents1, KnowHowWrite_Step2.saveContents2{


    StepperIndicator indicator;
    TextView text;
    ImageView close;
    ViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView( R.layout.knowhow_write_activity);
//        Toast.makeText( this,"onCreateActivity",Toast.LENGTH_SHORT ).show();

        pager = findViewById(R.id.khwrite_pager);
        assert pager != null;

        pager.setAdapter(new com.example.joanne.selfinsns_.PagerAdapter(getSupportFragmentManager()));

        indicator = findViewById(R.id.stepper_indicator);
        indicator.setViewPager(pager, true);

        setUpViewPager( pager );


        indicator.addOnStepClickListener(new StepperIndicator.OnStepClickListener() {
            @Override
            public void onStepClicked(int step) {
                pager.setCurrentItem(step, true);
            }
        });


        close = findViewById( R.id.khwrite_close );
        close.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KnowHowWriteActivity.this,MainActivity.class);
                intent.setAction( Intent.ACTION_VIEW );
                startActivity( intent );
                finish();
            }
        } );


    }//oncreate끝


    //탭뷰페이저
    public void setUpViewPager (ViewPager viewPage){

        List<Fragment> knowHowWriteFragment = new ArrayList<>(  );

        KnowHowWriteAdapter adapter = new KnowHowWriteAdapter(getSupportFragmentManager(),knowHowWriteFragment);

        adapter.AddFragmentPage( new KnowHowWrite_Step1(), "1단계" );
        adapter.AddFragmentPage( new KnowHowWrite_Step2(), "2단계" );
        adapter.AddFragmentPage( new KnowHowWrite_Step3(), "3단계" );

        viewPage.setAdapter( adapter );

    }


    //메인뷰페이저
    public static class KnowHowWriteAdapter extends FragmentPagerAdapter {
        private List<Fragment> knowHowWrite = new ArrayList<>(  );
        private List knowHowWriteTitle = new ArrayList<>(  );

        public KnowHowWriteAdapter(FragmentManager manager, List<Fragment> knowHowWrite) {
            super(manager);
            this.knowHowWrite = knowHowWrite;
        }


        public void AddFragmentPage(Fragment frag, String title){
            knowHowWrite.add( frag );
            knowHowWriteTitle.add( title );
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0) {
                return new KnowHoWrite_Root();
            }

            return (Fragment) knowHowWrite.get( position );
        }


        @Override
        public int getCount() {
            return knowHowWrite.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) knowHowWriteTitle.get(position);
        }
    }

    //fragment에서 버튼 클릭시 page전환
    void switchFragment(int target){
        pager.setCurrentItem( target );
    }


    @Override
    public void sendData(String section,String style,String space){ //섹션, 스타일, 공간
        String tag = "android:switcher:" + R.id.khwrite_pager + ":" + 1;
        KnowHowWrite_Step2 f = (KnowHowWrite_Step2) getSupportFragmentManager().findFragmentByTag(tag);
        f.receivedData(section,style,space);

        Log.e( "knowhowWrite","sendData1" );
    }


    @Override
    public void sendData(String section, String style, String space, String subject, String desc, String tags, ArrayList<String> images,ArrayList<String> img_description) { //섹션, 스타일, 공간, 설명, 태그, 이미지들, 이미지설명 저장.
        String tag = "android:switcher:" + R.id.khwrite_pager + ":" + 2;
        KnowHowWrite_Step3 f = (KnowHowWrite_Step3) getSupportFragmentManager().findFragmentByTag(tag);
        f.displayRecievedData(section,style,space, subject,desc,tags,images, img_description );

        Log.e( "knowhowWrite","sendData2" );
    }

//    void switchFragment2(int target, String subject, String desc, String tags, ArrayList<String> images){
//        pager.setCurrentItem( target );
//
//        Bundle bundle = new Bundle();
//        bundle.putString( "subject",subject );
//        bundle.putString( "desc", desc);
//        bundle.putString( "tags", tags);
//        bundle.putStringArrayList( "images",images);
//
//        Fragment f = new KnowHowWrite_Step3();
//        f.setArguments( bundle );
//        Log.e( "subject",subject );
//        Log.e( "desc",desc );
//        Log.e( "tags",tags );
//        Log.e( "images55", String.valueOf( images ) );
//    }




    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText( this,"onStartActivity",Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText( this,"onResumeActivity",Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText( this,"onPauseActivity",Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText( this,"onStopActivity",Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText( this,"onDestroyActivity",Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText( this,"onRestarActivity",Toast.LENGTH_SHORT ).show();
    }
}
