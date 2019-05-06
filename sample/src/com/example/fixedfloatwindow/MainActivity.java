package com.example.fixedfloatwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.enums.MoveType;
import com.yhao.floatwindow.enums.Screen;
import com.yhao.floatwindow.interfaces.BaseFloatWindow;

public class MainActivity extends Activity {

    private ImageView mImageView = null;
    private ImageView mImageView2 = null;
    private FloatWindow.Builder mBuilderA = null;
    private BaseFloatWindow mFirstWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("A");
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        mImageView = new ImageView(getApplicationContext());
        mImageView2 = new ImageView(getApplicationContext());
        mBuilderA = FloatWindow.with(getApplicationContext()).setView(mImageView).setWidth(Screen.WIDTH, 0.2f)
                .setHeight(Screen.WIDTH, 0.2f).setX(Screen.WIDTH, 0.8f).setY(Screen.HEIGHT, 0.3f)
                .setMoveType(MoveType.SLIDE).setMoveStyle(500, new BounceInterpolator()).setDesktopShow(true)
                .setTag("mFirstWindow");

    }

    public void onClick(View view) {
        mImageView.setImageResource(R.drawable.c_outline_add_circle_outline_black_48dp);
        mImageView2.setImageResource(R.drawable.c_outline_settings_black_48dp);
        switch (view.getId()) {
            case R.id.btnOpenActivityB:
                // 打开 B 界面
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.btnReqPermission:
                // 申请权限且不构建. 完善中
                // FloatWindow.prepare(this);
                break;
            case R.id.btnOnlyBuild:
                // 构建不加载
                // if (mBuilderA != null) {
                // mBuilderA.build();
                // }
                break;
            case R.id.btnInitAndShowA:
                // 初始化展示
                mFirstWindow = FloatWindow.get("mFirstWindow");
                // 效果图1
                if (mFirstWindow != null) {
                    FloatWindow.get("mFirstWindow").show();
                } else {
                    mBuilderA.build();
                    FloatWindow.get("mFirstWindow").show();
                }
                break;

            case R.id.btnHideA:
                // 隐藏悬浮窗
                mFirstWindow = FloatWindow.get("mFirstWindow");
                if (mFirstWindow != null) {
                    mFirstWindow.hide();
                } else {
                    alert("悬浮窗展示状态还没创建~");
                }
                break;
            case R.id.btnDissmissA:
                // 销毁悬浮窗
                FloatWindow.destroy("mFirstWindow");
                break;
            case R.id.btnIsVisable1:
                // 判断是否可见
                // BaseFloatWindow f = FloatWindow.get("mFirstWindow");
                // if (f != null) {
                // boolean isv = f.isViewVisible();
                // alert("悬浮窗展示状态:" + isv);
                // } else {
                // alert("窗口一未创建");
                // }
                break;
            default:
                break;
        }
    }

    private void alert(String status) {
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
        Log.i("FloatWindow", status);
    }

}
