package com.example.fixedfloatwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.intdef.MoveType;
import com.yhao.floatwindow.intdef.Screen;
import com.yhao.floatwindow.interfaces.IFloatWindow;
import com.yhao.floatwindow.utils.LogUtil;
import com.yhao.floatwindow.utils.T;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private ImageView mImageView = null;
    private ImageView mImageView2 = null;
    private FloatWindow.Builder mBuilderA = null;
    private IFloatWindow mFirstWindow = null;

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
        mBuilderA = FloatWindow.with(getApplicationContext()).setView(mImageView).setWidth(Screen.width, 0.2f)
                .setHeight(Screen.width, 0.2f).setX(Screen.width, 0.8f).setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide).setMoveStyle(500, new BounceInterpolator()).setDesktopShow(true)
                .setTag("mFirstWindow");

    }

    public void onClick(View view) {
        mImageView.setImageResource(R.drawable.c_outline_add_circle_outline_black_48dp);
        mImageView2.setImageResource(R.drawable.c_outline_settings_black_48dp);
        switch (view.getId()) {
            case R.id.btnOpenActivityB:
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.btnReqPermission:
                FloatWindow.prepare(this);
                break;
            case R.id.btnOnlyBuild:
                mBuilderA.build();
                break;
            case R.id.btnInitAndShowA:

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconEnable(menu, true);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setIconEnable(Menu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            // 下面传入参数
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
