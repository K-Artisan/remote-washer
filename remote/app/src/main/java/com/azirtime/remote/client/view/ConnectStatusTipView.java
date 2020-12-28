package com.azirtime.remote.client.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.azirtime.remote.R;

public class ConnectStatusTipView extends FrameLayout {

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTE_FAILSE = 3;

    private View container;
    private ImageView imageView;
    private ProgressBar progressBar;

    public int currStatus;
    public boolean autoHideAfterConnected;

    public ConnectStatusTipView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_tip_connect_status, this);
        init();
    }

    private void init(){
        container = findViewById(R.id.view_cntsts_container);
        imageView = findViewById(R.id.view_cntsts_img);
        progressBar = findViewById(R.id.view_cntsts_pgBar);
    }

    public void setOnClickListener(OnClickListener listener){
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

                break;

            case STATE_CONNECTING:
                progressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ic_connecting);

                break;

            case STATE_CONNECTED:
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_connected);

                if (autoHideAfterConnected){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(View.GONE);
                        }
                    }, 2000);
                }

                break;

            case STATE_CONNECTE_FAILSE:
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_disconnect);

                break;
        }
    }

    public void setAutoHideAfterConnected(boolean autoHide){
        autoHideAfterConnected = autoHide;
    }
}
