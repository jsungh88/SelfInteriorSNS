package com.example.joanne.selfinsns_;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Expert extends Fragment {

    public Expert(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View QnA = inflater.inflate(R.layout.qna_activity, container, false);


        return QnA;
    }
}
