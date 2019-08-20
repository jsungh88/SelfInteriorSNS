package com.example.joanne.selfinsns_;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Joanne on 2018-04-26.
 */

public class SelectingItem_section implements AdapterView.OnItemSelectedListener{

    //회원가입 - 연령대 Spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //"연령대 선택"인 경우에는 Toast를 출력하지 마시오.
        String a = parent.getItemAtPosition(position).toString();
        if(!a.equals("연령대를 선택하세요.")) {
            Toast.makeText( parent.getContext(), parent.getItemAtPosition( position ).toString(), Toast.LENGTH_SHORT ).show();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
