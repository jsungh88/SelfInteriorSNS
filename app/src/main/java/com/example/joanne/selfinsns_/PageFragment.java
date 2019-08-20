package com.example.joanne.selfinsns_;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Joanne on 2018-05-17.
 */

public class PageFragment extends Fragment {

    private TextView lblPage;

    public static PageFragment newInstance(int page, boolean isLast) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        if (isLast)
            args.putBoolean("isLast", true);
        final PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.knowhow_write_step1, container, false);
//        lblPage = (TextView) view.findViewById(R.id.lbl_page);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int page = getArguments().getInt("page", 0);
//        if (getArguments().containsKey("isLast"))
//            lblPage.setText("You're done!");
//        else
//            lblPage.setText(Integer.toString(page));
    }
}