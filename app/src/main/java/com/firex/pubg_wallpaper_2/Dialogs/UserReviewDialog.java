package com.firex.pubg_wallpaper_2.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firex.pubg_wallpaper_2.BuildConfig;
import com.firex.pubg_wallpaper_2.R;
import com.firex.pubg_wallpaper_2.Utilities.Utils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserReviewDialog extends Dialog {
    private EditText edittext;
    private TextView canceltxt;
    private TextView sendtxt;
    private final Context mContext;
    private String deviceid = "Unknown";

    public UserReviewDialog(@NonNull Context context) {
        super(context);

        this.mContext = context;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);

        setTitle(null);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_dialog, null);

        edittext = view.findViewById(R.id.edtTxt);
        canceltxt = view.findViewById(R.id.textViewCancel);
        sendtxt = view.findViewById(R.id.textViewSend);

        Utils.setOnTouchListener(canceltxt);
        Utils.setOnTouchListener(sendtxt);

        canceltxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                Utils.clickEffect(view);
            }
        });

        sendtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                Utils.clickEffect(view);
                sendFeedback();
            }
        });

        setContentView(view);
    }

    public void setDeviceid(String id) {
        deviceid = id;
    }

    private void sendFeedback() {
        String msg = edittext.getText().toString();
        if (msg.length() < 1) {
            Toast.makeText(mContext, "Feedback sent!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy / HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("Date", currentDateAndTime);
        data.put("App Version", BuildConfig.VERSION_NAME);
        data.put("Android Version", Build.VERSION.SDK_INT);
        data.put("Msg", msg);

        db.collection("User_Feedbacks")
                .document(Build.MANUFACTURER + "-" + Build.MODEL)
                .collection(deviceid)
                .document("Report:" + System.currentTimeMillis() + "")
                .set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Feedback sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}