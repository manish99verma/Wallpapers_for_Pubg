package com.firex.pubg_wallpaper_2.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firex.pubg_wallpaper_2.R;

public class MyProgressDialog extends Dialog {
    TextView mTextView;

    public void setMsg(String msg) {
        mTextView.setText(msg);
    }

    public MyProgressDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);

        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        mTextView = view.findViewById(R.id.textView);
        setContentView(view);
    }



}