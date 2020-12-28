package com.azirtime.remote.client.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.azirtime.remote.R;
import com.azirtime.remote.client.ui.MainActivity;

public class ConnectStatusView extends ConstraintLayout {

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTE_FAILSE = 3;

    private View container;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView tvMessage;

    public int currStatus;

    public ConnectStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_connect_status, this);
        init();
    }

    private void init(){
        container = findViewById(R.id.view_cntsts_container);
        imageView = findViewById(R.id.view_cntsts_img);
        progressBar = findViewById(R.id.view_cntsts_pgBar);
        tvMessage = findViewById(R.id.view_cntsts_msg);
    }

    public void setOnClickListener(View.OnClickListener listener){
        container.setOnClickListener(listener);
    }

    public void setStatus(int status){
        currStatus = status;
        setVisibility(View.VISIBLE);
        switch (status){
            case STATE_NONE:
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_disconnect);
                tvMessage.setText(R.string.status_connect_failse);
                break;

            case STATE_CONNECTING:
                progressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ic_connecting);
                tvMessage.setText(R.string.status_connecting);
                break;

            case STATE_CONNECTED:
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_connected);
                tvMessage.setText(R.string.status_conndected_device);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(View.GONE);
                    }
                }, 1000);

                break;
        }
    }
}
