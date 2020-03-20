package com.nbw.blueapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.nbw.blueapp.R;

public class UserInfoEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void onClick_update(View view) {
    }

    public void onClick_back(View view) {
        finish();
    }
}
