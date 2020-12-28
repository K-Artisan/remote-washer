package com.azirtime.remote.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.azirtime.remote.R;
import com.azirtime.remote.client.ui.activity.BaseActivity;
import com.azirtime.remote.client.ui.device.DeviceActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         startActivity(new Intent(this, DeviceActivity.class));
         finish();



    }
}
