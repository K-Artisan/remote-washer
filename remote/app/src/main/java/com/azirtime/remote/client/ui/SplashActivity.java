package com.azirtime.remote.client.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.azirtime.remote.R;
import com.azirtime.remote.client.ui.adddevice.AddBluetoothDeviceActivity;
import com.bumptech.glide.Glide;

/**
 * 启动页:
 * 一般用于展示公司的LOGO，企业文化等
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init(){
        ImageView img_splash_logo = findViewById(R.id.splash_logo);
        Glide.with(this).load(R.drawable.ic_splash_logo)
                .centerCrop()
                .into(img_splash_logo);

        autoGotoNextActivity();
    }

    private void autoGotoNextActivity(){
        //https://blog.csdn.net/Maiduoudo/article/details/82454864

/*        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();//启动线程*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
