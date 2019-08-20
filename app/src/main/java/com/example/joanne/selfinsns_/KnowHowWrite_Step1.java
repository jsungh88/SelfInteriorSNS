package com.example.joanne.selfinsns_;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.badoualy.stepperindicator.StepperIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joanne on 2018-05-17.
 */

public class KnowHowWrite_Step1 extends Fragment implements View.OnClickListener{


    Button section, style, space;
    Button nextstep;
    String section_str, style_str, space_str;
    SaveContents1 SC;


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View step1 = inflater.inflate( R.layout.knowhow_write_step1, container,false );

//        Toast.makeText( getActivity(),"onCreateView1",Toast.LENGTH_SHORT ).show();

        section = step1.findViewById( R.id.btn_section );
        style = step1.findViewById( R.id.btn_style);
        space = step1.findViewById( R.id.btn_space);
        nextstep = step1.findViewById( R.id.btn_nextstep );
        section.setOnClickListener( this );
        style.setOnClickListener( this );
        space.setOnClickListener( this );



        /**
         * 1. 공간구분 선택
         * 2. 컨셉스타일 선택
         * 3. 평수 선택
         */


        nextstep.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("nextstep:","come?");
////                getFragmentManager().beginTransaction().replace( R.id.step2,new KnowHowWrite_Step2() ).commit();
//                FragmentTransaction trans = getFragmentManager().beginTransaction();
//                trans.replace( R.id.rootframe, new KnowHowWrite_Step2() );
//
//                trans.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
//                trans.addToBackStack( null );
//
//                trans.commit();


                //입력내용 번들에 저장.:섹션,스타일,평수
//                saveContents( section_str,style_str,space_str );
                Log.e("step1 저장데이터",section_str+style_str+space_str);
                SC.sendData( section_str,style_str,space_str );

                ((KnowHowWriteActivity )getActivity()).switchFragment(1);
            }
        } );



        return step1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //공간구분
            case R.id.btn_section:
                section_DialogRadio();
                break;

            //스타일 선택
            case R.id.btn_style:
                style_DialogRadio();
                break;

            //스타일 선택
            case R.id.btn_space:
                space_DialogRadio();
                break;

            //다음단계로 이동
//            case R.id.nextstep:
////                adapter.getItem( 1 );
//
//                break;

        }
    }

    private void section_DialogRadio() {
        final CharSequence[] arr = {"원룸", "거실", "침실", "아이방", "서재", "주방","욕실","발코니","현관","소품","DIY/가구","주거전체","기타"};
        AlertDialog.Builder alert = new AlertDialog.Builder( getContext() );
        alert.setTitle( "공간을 선택하세요." );
        alert.setSingleChoiceItems( arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( getActivity(), arr[which], Toast.LENGTH_SHORT ).show();

                section.setText( arr[which] );
                section_str = (String) arr[which];
                Log.e( "공간 선택", section_str );
                dialog.cancel();
            }
        } );
        AlertDialog aalert = alert.create();
        aalert.show();
    }

    private void style_DialogRadio() {
        final CharSequence[] arr = {"모던", "북유럽", "빈티지", "내츄럴", "프로방스", "로맨틱","클래식","엔틱","그린테리어","기타"};
        AlertDialog.Builder alert = new AlertDialog.Builder( getContext() );
        alert.setTitle( "컨셉&스타일을 선택하세요." );
        alert.setSingleChoiceItems( arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( getActivity(), arr[which], Toast.LENGTH_SHORT ).show();

                style.setText( arr[which] );
                style_str = (String) arr[which];
                Log.e( "컨셉&스타일 선택", style_str );
                dialog.cancel();
            }
        } );
        AlertDialog aalert = alert.create();
        aalert.show();
    }

    private void space_DialogRadio() {
        final CharSequence[] arr = {"10평이상","20평이상","30평이상","40평이상","50평이상"};
        AlertDialog.Builder alert = new AlertDialog.Builder( getContext() );
        alert.setTitle( "평수를 선택해주세요." );
        alert.setSingleChoiceItems( arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText( getActivity(), arr[which], Toast.LENGTH_SHORT ).show();

                space.setText( arr[which] );
                space_str = (String) arr[which];
                Log.e( "평수 선택", space_str );
                dialog.cancel();
            }
        } );
        AlertDialog aalert = alert.create();
        aalert.show();
    }


//    private void saveContents(String section, String style, String space){
//        Bundle bundle = new Bundle(  );
//        bundle.putString( "section",section );
//        bundle.putString( "style", style);
//        bundle.putString( "space", space);
//        Fragment f = new KnowHowWrite_Step3();
////        FragmentManager fm = getFragmentManager();
////        FragmentTransaction fmt = fm.beginTransaction();
//        f.setArguments( bundle ); //데이터 전달
////        fmt.replace( R.id.rootframe,f ).addToBackStack( null ).commit();
//        Log.e( "section",section );
//        Log.e( "style",style );
//        Log.e( "space",space );
//    }

    interface SaveContents1 {
        void sendData(String se1, String st1, String sp1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
//        Toast.makeText( getActivity(),"onAttach1",Toast.LENGTH_SHORT ).show();

        Log.e( "step1","onAttatch" );
        try {
            SC = (SaveContents1) getActivity();
        }catch(ClassCastException e){
            throw new ClassCastException( "에러 에러, 다시해보아" );
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//        Toast.makeText( getActivity(),"onCreate1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
//        Toast.makeText( getActivity(),"onActivityCreated1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText( getActivity(),"onStart1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText( getActivity(),"onResume1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText( getActivity(),"onPause1",Toast.LENGTH_SHORT ).show();

    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText( getActivity(),"onStop1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText( getActivity(),"onDestroyView1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText( getActivity(),"onDestroy1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Toast.makeText( getActivity(),"onDetach1",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onSaveInstanceState( Bundle outState) {
        super.onSaveInstanceState( outState );
//        Toast.makeText( getActivity(),"onSaveInstanceState1",Toast.LENGTH_SHORT ).show();
    }


}
