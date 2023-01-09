package com.firex.pubg_wallpaper_2.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.Utils;


public class WelFragment1 extends Fragment {
    private FragmentActivity fragmentactivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome1, container, false);

        Button btnContinue = view.findViewById(R.id.continue_btn);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.clickEffect(view);
                Utils.changeFragment(fragmentactivity,new WelFragment2(),R.id.fragContainerWelcome);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentactivity= (FragmentActivity) context;
    }
}