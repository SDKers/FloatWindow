package com.example.fixedfloatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityB extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        setTitle("Builder");
    }

    public void change(View view) {
        startActivity(new Intent(this, ActivityC.class));
    }

    public void back(View view) {
        finish();
    }
}
