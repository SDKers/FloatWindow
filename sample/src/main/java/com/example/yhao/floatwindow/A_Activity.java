package com.example.yhao.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.example.yhao.fixedfloatwindow.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.utils.LogUtil;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.utils.T;

public class A_Activity extends AppCompatActivity {


    private ImageView mImageView = null;
    private ImageView mImageView2 = null;
    private FloatWindow.Builder mBuilderA = null;
    private FloatWindow.Builder mBuilderB = null;
    private IFloatWindow mFirstWindow = null;
    private IFloatWindow mSecondWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("A");
        setContentView(R.layout.activity_a);
        initUI();
    }

    private void initUI() {
        mImageView = new ImageView(getApplicationContext());
        mImageView2 = new ImageView(getApplicationContext());
        mBuilderA = FloatWindow
                .with(getApplicationContext())
                .setView(mImageView)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopShow(true).setTag("mFirstWindow");

        mBuilderB = FloatWindow
                .with(getApplicationContext())
                .setView(mImageView2)
                .setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.7f)
                .setY(Screen.height, 0.02f)
                .setTag("Two")
                .setMoveType(MoveType.inactive)
                .setDesktopShow(true);
    }


    public void onClick(View view) {
        mImageView.setImageResource(R.drawable.icon);
        mImageView2.setImageResource(R.mipmap.ic_launcher_round);
        switch (view.getId()) {
            case R.id.btnOpenActivityB:
                startActivity(new Intent(this, B_Activity.class));
                break;
            case R.id.btnReqPermission:
                FloatWindow.prepare(this);
                break;
            case R.id.btnOnlyBuild:
                mBuilderA.build();
                break;
            case R.id.btnInitAndShowA:

                mFirstWindow = FloatWindow.get("mFirstWindow");
                //效果图1
                if (mFirstWindow != null) {
                    FloatWindow.get("mFirstWindow").show();
                } else {
                    mBuilderA.build();
                    FloatWindow.get("mFirstWindow").show();
                }
                break;

            case R.id.btnHideA:
                mFirstWindow = FloatWindow.get("mFirstWindow");
                if (mFirstWindow != null) {
                    mFirstWindow.hide();
                } else {
                    alert("悬浮窗展示状态还没创建~");
                }
                break;
            case R.id.btnDissmissA:
                FloatWindow.destroy("mFirstWindow");
                break;
            case R.id.btnShow2:

                mSecondWindow = FloatWindow.get("Two");
                if (mSecondWindow == null) {
                    mBuilderB.build();
                }
                FloatWindow.get("Two").show();
                break;
            case R.id.btnDismiss2:
                FloatWindow.destroy("Two");
                break;
            case R.id.btnIsVisable1:

                IFloatWindow f = FloatWindow.get("mFirstWindow");
                if (f != null) {
                    boolean isv = f.isViewVisible();
                    alert("悬浮窗展示状态:" + isv);
                } else {
                    alert("窗口一未创建");
                }
                break;
            default:
                break;
        }
    }

    private void alert(String status) {
        T.show(this, status);
        LogUtil.i(status);
    }
}
