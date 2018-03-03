package com.example.yhao.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.example.yhao.fixedfloatwindow.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

public class A_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        setTitle("A");
        //        testAmethod();
        testa();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatWindow.get("first").show();
        FloatWindow.get("Two").show();
    }

    /**
     * 展示两个初始化参数的悬浮窗，不build,直接可以使用。目的是构建可控展示的悬浮窗
     */
    private void testa() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.icon);


        //效果图1
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopShow(true).setTag("first");


        ImageView imageView2 = new ImageView(getApplicationContext());
        imageView2.setImageResource(R.mipmap.ic_launcher_round);

//      效果图2
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView2)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.7f)
                .setY(Screen.height, 0.02f)
                .setTag("Two")
                .setMoveType(MoveType.inactive);

    }


    /**
     * 展示两个不同声明域的悬浮窗
     */
    private void testAmethod() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.icon);


        //效果图1
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopShow(true)
                .build();

        ImageView imageView2 = new ImageView(getApplicationContext());
        imageView2.setImageResource(R.mipmap.ic_launcher_round);

//      效果图2
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView2)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.7f)
                .setY(Screen.height, 0.02f)
                .setTag("second")
                .setMoveType(MoveType.inactive)
                .setFilter(true, B_Activity.class, C_Activity.class)
                .build();
    }

    public void change(View view) {
        startActivity(new Intent(this, B_Activity.class));
    }

}
