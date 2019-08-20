package com.example.joanne.selfinsns_;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyPage_knowhow extends Fragment{

    public MyPage_knowhow(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View mypage_knowhow = inflater.inflate(R.layout.mypage_knowhow, container, false);

        return mypage_knowhow;
    }
}
