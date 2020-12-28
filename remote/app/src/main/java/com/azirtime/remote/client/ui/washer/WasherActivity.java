package com.azirtime.remote.client.ui.washer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azirtime.remote.R;
import com.azirtime.remote.client.common.ActivityRequestCode;
import com.azirtime.remote.client.common.IntentDataKey;
import com.azirtime.remote.client.common.bluetooth.BluetoothConstant;
import com.azirtime.remote.client.common.bluetooth.BluetoothService;
import com.azirtime.remote.client.ui.activity.BaseActivity;
import com.azirtime.remote.client.ui.washer.washmode.TestFragment;
import com.azirtime.remote.client.view.ConnectStatusTipView;
import com.azirtime.remote.client.view.ConnectStatusView;
import com.azirtime.remote.common.ViewUtils;
import com.azirtime.remote.common.utils.TimeUtils;
import com.azirtime.remote.databinding.ActivityWasherBinding;
import com.azirtime.remote.dto.DeviceDto;
import com.azirtime.support.view.WaveView;
import com.azirtime.support.view.WaveView3;
import com.azirtime.support.view.ext.ScaleTransitionPagerTitleView;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.jaredrummler.materialspinner.MaterialSpinner;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.cesgroup.numpickerview.NumberPickerView;

import static com.azirtime.remote.client.common.ActivityRequestCode.PERMISSION_REQUEST_COARSE_LOCATION;

public class WasherActivity extends BaseActivity {
    private static String TAG = "WasherActivity";
    private WasherViewModel viewModel;
    private ActivityWasherBinding binding;

    private Toolbar toolbar;
    private boolean debugViewIsShow;
    private WaveView mWaveView;

    private static final String[] CHANNELS = new String[]{"混合洗", "臻柔洗", "快速洗15'"
            , "速洗30'", "漂洗+脱水", "单脱水", "单烘干", "羽绒服", "羊毛", "洁桶洗"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();


    /*    public TextView mTv_messageView;
        public TextView mOutEditText;
        public Button btn_connect;
        public Button mSendButton;*/

    private ConnectStatusTipView connectStatusView;

    private int index = 0;
    private int recieveIndex = 0;
    private int cmdIndex = 0;
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstant.MESSAGE_STATE_CHANGE:
                    viewModel.connectStatus.setValue(msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //TODO:连接成功
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //TODO:正在失败
                            break;

                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                        case BluetoothService.STATE_CONNECTE_FAILSE:
                            //TODO:连接失败
                            break;
                    }
                    break;
                case BluetoothConstant.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mTv_messageView.append("\nMe:  " + writeMessage);
                    break;
                case BluetoothConstant.MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    int readBufLength = msg.arg1;

                    if (debugViewIsShow) {
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, readBufLength);
                        // mTv_messageView.append("\n" + viewModel.mDevice.name + ":  " + readMessage);
                        String debugMsg = (++index) + ".(" + readBufLength + "):" + readMessage;
                        String oldTvText = binding.debugTvReciveData.getText().toString();
                        debugMsg = debugMsg + "\n" + oldTvText;
                        binding.debugTvReciveData.setText(debugMsg);
                    }
                    viewModel.washerDataResolver.parseAndMergeCompleteReciveData(Arrays.copyOf(readBuf, readBufLength));
                    break;

                case BluetoothConstant.MESSAGE_DEVICE_NAME:
                    Toast.makeText(WasherActivity.this, "连接成功：" + viewModel.mDevice.name, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothConstant.MESSAGE_TOAST:
                    Toast.makeText(WasherActivity.this, msg.getData().getString(BluetoothConstant.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_washer);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_washer);
        viewModel = new ViewModelProvider(this).get(WasherViewModel.class);
        viewModel.mDevice = (DeviceDto) getIntent().getSerializableExtra(IntentDataKey.DATAKEY_OPENDEVICEVIEW);
        binding.setData(viewModel);
        binding.setLifecycleOwner(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销蓝牙广播
        unregisterReceiver(viewModel.bluetoothReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToPreView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_washer_toolbar, menu); //创建系统菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                backToPreView();
                break;
            case R.id.washer_toolbar_debug:
                if (!debugViewIsShow) {
                    binding.washerDebugView.setVisibility(View.VISIBLE);
                    debugViewIsShow = true;
                } else {
                    binding.washerDebugView.setVisibility(View.GONE);
                    debugViewIsShow = false;
                }
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityRequestCode.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {  //用户授权开启蓝牙
                    findBluetoothDevice();
                } else {
                    Toast.makeText(this, "蓝牙没有打开,无法搜索蓝牙设备", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, R.string.bluetooth_refuse_authorization, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void init() {
        initToolbar();
        initView();
        intBluetoothReceiver();
        initDeviceOberser();
        searchDevice();
        initReciveDataEvent();

        WasherReciveData wsRevData =  WasherReciveData.empety();
        viewModel.washerReciveData.postValue(wsRevData);
        //initWashView(WasherReciveData.empety());
    }

    private void initView() {
        binding.connectStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.connectStatusView.currStatus != connectStatusView.STATE_CONNECTED) {
                    connectBluetoothDevice(viewModel.mLiveDataBluetoothDevice.getValue());
                }
            }
        });


        //波浪
        //ImageView imageView = (ImageView) findViewById(R.id.image);
        WaveView3 waveView3 = (WaveView3) findViewById(R.id.washer_wave_view);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        waveView3.setOnWaveAnimationListener(new WaveView3.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0, 0, 0, (int) y + 2);
                //imageView.setLayoutParams(lp);
            }
        });
    }

    private void initView_ttt() {
 /*       washerConnectView = findViewById(R.id.washer_connect_view);
        washerConnectProgress = findViewById(R.id.washer_connect_progress);*/


        //washerConnectView.setVisibility(View.VISIBLE);

        //washerConnectProgress.start();
        //washerConnectProgress.setOnClickListener(new View.OnClickListener() {
/*            @Override
            public void onClick(View v) {
                connectBluetoothDevice(viewModel.mLiveDataBluetoothDevice.getValue());
            }
        });*/



/*        mTv_messageView = findViewById(R.id.tv_message);
        mOutEditText = findViewById(R.id.edit_text_out);
        btn_connect = findViewById(R.id.btn_connect);
        mSendButton = findViewById(R.id.button_send);
        btn_connect.setVisibility(View.GONE);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBluetoothDevice(viewModel.mLiveDataBluetoothDevice.getValue());
            }
        });


        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView textView = findViewById(R.id.edit_text_out);
                String message = textView.getText().toString();
                sendMessage(message);
            }
        });*/
    }

    //初始化状态栏
    private void initToolbar() {
        //使用Toolbar替换系统的ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewModel.mDevice.name);
            actionBar.setDisplayHomeAsUpEnabled(true); //启用HomeAsUp按钮，其id永远为：android.R.id.home
        }

        setStatusBarFullTransparent();
        setFitSystemWindow(true);
    }

    //初始化广播接收器
    private void intBluetoothReceiver() {
        viewModel.bluetoothReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (viewModel.mDevice.mac.equals(device.getAddress())) {
                        viewModel.mLiveDataBluetoothDevice.setValue(device);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                }
            }
        };

        //注册蓝牙广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //搜索开始
        filter.addAction(BluetoothDevice.ACTION_FOUND); //找到新设备
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //搜索完成
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); //扫描模式？
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //绑定状态改变

        registerReceiver(viewModel.bluetoothReceiver, filter);
    }

    //初始化找到设备的观察者
    private void initDeviceOberser() {

        //蓝牙设备搜索
        viewModel.mLiveDataBluetoothDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice != null) {
                    connectBluetoothDevice(bluetoothDevice);
                }
            }
        });

        //蓝牙设备连接状态
        viewModel.connectStatus.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer connectStatus) {
                doWhenConnectStatusChanged(connectStatus);
            }
        });

        //收到消息
        viewModel.washerReciveData.observe(this, new Observer<WasherReciveData>() {
            @Override
            public void onChanged(WasherReciveData washerReciveData) {
                doWhenRecieveData(washerReciveData);
            }
        });
    }

    //连接设备
    private void searchDevice() {
        if (!isSupportBluetooth()) {
            return;
        } else if (!isEnabledBluetooth()) {
            viewModel.opentBluetooth(this);
        } else {
            findBluetoothDevice();
        }
    }

    //查找指定的蓝牙设备
    private void findBluetoothDevice() {
        if (!findBluetoothDeviceInBonded()) {
            findBluetoothDeviceInUnbonded();
        }
    }

    //现在已经配对的设备中找
    private boolean findBluetoothDeviceInBonded() {
        boolean hasFound = false;
        List<BluetoothDevice> bondedBluetoothDevices = viewModel.bluetoohManager.getBondedDevices();
        for (BluetoothDevice device : bondedBluetoothDevices) {
            if (viewModel.mDevice.mac.equals(device.getAddress())) {
                viewModel.mLiveDataBluetoothDevice.setValue(device);
                hasFound = true;
                break;
            }
        }

        return hasFound;
    }

    //在未配对的设备中找
    private void findBluetoothDeviceInUnbonded() {
        viewModel.startDiscovery();
    }

    //连接蓝牙设备
    private void connectBluetoothDevice(BluetoothDevice bluetoothDevice) {

        if (viewModel.bluetoothServer == null) {
            viewModel.bluetoothServer = new BluetoothService(this, mHandler);
        }
        viewModel.mOutStringBuffer = new StringBuffer();

        if (viewModel.bluetoothServer != null) {
            boolean secure = true;
            BluetoothDevice device = viewModel.bluetoohManager.bluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());
            viewModel.bluetoothServer.connect(device, secure);

            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (viewModel.bluetoothServer.getState() == BluetoothService.STATE_NONE
                    || viewModel.bluetoothServer.getState() == BluetoothService.STATE_CONNECTE_FAILSE) {
                viewModel.bluetoothServer.startListen();
            }
        }
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (viewModel.bluetoothServer.getState() != viewModel.bluetoothServer.STATE_CONNECTED) {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            viewModel.bluetoothServer.write(send);

            // Reset out string buffer to zero and clear the edit text field
            viewModel.mOutStringBuffer.setLength(0);
            //mOutEditText.setText(viewModel.mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    //连接状态变化时
    private void doWhenConnectStatusChanged(int connectStatus) {
        toggleWasherView(connectStatus);
    }

    private void toggleWasherView(int connectStatus) {

        switch (connectStatus) {
            case BluetoothService.STATE_NONE:
                binding.connectStatusView.setStatus(ConnectStatusView.STATE_NONE);
                break;

            case BluetoothService.STATE_LISTEN:
                binding.connectStatusView.setStatus(ConnectStatusView.STATE_NONE);
                break;

            case BluetoothService.STATE_CONNECTING:
                binding.connectStatusView.setStatus(ConnectStatusView.STATE_CONNECTING);
                break;

            case BluetoothService.STATE_CONNECTED:
                binding.connectStatusView.setAutoHideAfterConnected(true);
                binding.connectStatusView.setStatus(ConnectStatusView.STATE_CONNECTED);
                break;

            case BluetoothService.STATE_CONNECTE_FAILSE:
                binding.connectStatusView.setStatus(ConnectStatusView.STATE_CONNECTE_FAILSE);
                ViewTooltip
                        .on(binding.connectStatusView)
                        .autoHide(true, 1000)
                        //.autoHide(false, 0)
                        //.clickToHide(true)
                        .corner(30)
                        .color(getResources().getColor(R.color.colorWarming))
                        .position(ViewTooltip.Position.LEFT)
                        .text(getResources().getString(R.string.status_connect_failse))
                        .show();
                break;
        }
    }

    private void doWhenRecieveData(WasherReciveData washerReciveData) {
        if (!washerReciveData.isValid()) {
            return;
        }
        initWashView(washerReciveData);
    }

    private void initReciveDataEvent() {
        viewModel.washerDataResolver.setOnReciveNewCompleteDataistener(new WasherDataResolver.onRecieveNewCompleteDataDataListener() {
            @Override
            public void onRecieveNewCompleteDataData(byte[] data) {
                reciveDataAndUpdateUI();
            }
        });
    }

    private void reciveDataAndUpdateUI() {
        if (viewModel.washerDataResolver.completeRecieveDataList != null) {
            while (viewModel.washerDataResolver.completeRecieveDataList.size() > 0) {
                try {
                    byte[] reciveBytes = viewModel.washerDataResolver.completeRecieveDataList.take();

                    if (debugViewIsShow) {
                        String readMessage = new String(reciveBytes, 0, reciveBytes.length);
                        String debugMsg = (++recieveIndex) + "<(" + reciveBytes.length + "):" + readMessage;
                        String oldTvText = binding.debugTvReciveData.getText().toString();
                        debugMsg = debugMsg + "\n" + oldTvText;
                        binding.debugTvReciveData.setText(debugMsg);
                    }

                    WasherReciveData wshReciveData = new WasherReciveData(Arrays.copyOf(reciveBytes, WasherReciveData.RECIEVE_BYTE_LENGTH));
                    if (wshReciveData.isValid()) {
                        viewModel.washerReciveData.setValue(wshReciveData);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initWashView(WasherReciveData washerReciveData) {
        viewModel.washerCommandData = WasherCommandData.createFromReciveData(washerReciveData);

/*        WaveView waveView = findViewById(R.id.wave);
        waveView.setBorderWidth(mBorderWidth);
        waveView.setBorderColor(mBorderColor);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        waveView.setFrontColor(Color.parseColor("#28f16d7a"));
        waveView.setBehindColor(Color.parseColor("#3cf16d7a"));
        waveView.setHeightRatio(ratio);
        waveView.setFrequencyr(i);
        waveView.setAmplitudeRatio(ratio);
        waveView.setShiftTime(seekBar.getProgress());*/

        TextView tv_washer_remain_time = findViewById(R.id.tv_washer_remain_time);
        int minute = Integer.valueOf(washerReciveData.washingRemainTime);
        tv_washer_remain_time.setText(TimeUtils.stringForTimeHHMM(minute * 60));

        //运行状态
       doWhenWasherWorkStatusChaged(washerReciveData);

        //模式选择
        initWashingModeView();

        //控制开关
        initClick();

        //洗衣机开机状态
        MaterialSpinner washerCfgRunstatus = findViewById(R.id.washer_cfg_runstatus);
        washerCfgRunstatus.setItems(
                getString(R.string.washer_run_status_stop),
                getString(R.string.washer_run_status_runing),
                getString(R.string.washer_run_status_suspend));
        washerCfgRunstatus.setSelectedIndex(Integer.valueOf(washerReciveData.runStatus));
        washerCfgRunstatus.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                viewModel.washerCommandData.setRunStatus(String.valueOf(position));
            }
        });

        //已用时
        TextView washer_cfg_used_time = findViewById(R.id.washer_cfg_used_time);
        washer_cfg_used_time.setText(String.valueOf(Integer.parseInt(washerReciveData.washingTotalTime)));

        //工作状态
        TextView tv_washer_work_status = findViewById(R.id.tv_washer_work_status);
        TextView washer_cfg_work_status = findViewById(R.id.washer_cfg_work_status);
        switch (washerReciveData.workStatus) {
            case WasherDataConstant.Work_Status_Stop:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_stop));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_stop));
                break;
            case WasherDataConstant.Work_Status_Jinshui_1:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_jinshui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_jinshui));
                break;
            case WasherDataConstant.Work_Status_Washing:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_washing));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_washing));
                break;
            case WasherDataConstant.Work_Status_Paishui_1:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_paishui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_paishui));
                break;
            case WasherDataConstant.Work_Status_Jinshui_2:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_jinshui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_jinshui));
                break;
            case WasherDataConstant.Work_Status_Rinsing_1:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_rinsing));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_rinsing));
                break;
            case WasherDataConstant.Work_Status_Paishui_2:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_paishui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_paishui));
                break;
            case WasherDataConstant.Work_Status_Jinshui_3:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_jinshui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_jinshui));
                break;
            case WasherDataConstant.Work_Status_Rinsing_2:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_rinsing));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_rinsing));
                break;
            case WasherDataConstant.Work_Status_Paishui_3:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_paishui));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_paishui));
                break;
            case WasherDataConstant.Work_Status_Spin:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_spin));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_spin));
                break;
            case WasherDataConstant.Work_Status_Dry:
                tv_washer_work_status.setText(getString(R.string.washer_work_status_dry));
                washer_cfg_work_status.setText(getString(R.string.washer_work_status_dry));
                break;
        }

        //模式
        MaterialSpinner washerCfgMode = findViewById(R.id.washer_cfg_mode);
        washerCfgMode.setItems(
                getString(R.string.washer_mode_standard), getString(R.string.washer_mode_strong), getString(R.string.washer_mode_week),
                getString(R.string.washer_mode_single_wash), getString(R.string.washer_mode_rinse), getString(R.string.washer_mode_spin),
                getString(R.string.washer_mode_dry));
        washerCfgRunstatus.setSelectedIndex(Integer.valueOf(washerReciveData.updateMode));
        washerCfgMode.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                viewModel.washerCommandData.setMode(String.valueOf(position));
            }
        });

        //水位
        MaterialSpinner washerCfgWaterLevel = findViewById(R.id.washer_cfg_water_level);
        washerCfgWaterLevel.setItems(
                getString(R.string.washer_waterlevel_low),
                getString(R.string.washer_waterlevel_mid),
                getString(R.string.washer_waterlevel_hight));
        washerCfgRunstatus.setSelectedIndex(Integer.valueOf(washerReciveData.updateWaterLevel));
        washerCfgMode.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                viewModel.washerCommandData.setWaterLevel(String.valueOf(position));
            }
        });

        //温度
        NumberPickerView washerCfgTemperatrue = findViewById(R.id.washer_cfg_temperatrue);
        washerCfgTemperatrue
                .setCurrentInventory(1000) // 当前的库存
                .setMaxValue(60)           //最大输入值，也就是限量，默认无限大
                .setMinDefaultNum(0)       // 最小限定量
                .setCurrentNum(Integer.valueOf(washerReciveData.temperature))
                .setmOnClickInputListener(new NumberPickerView.OnClickInputListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        //超过最大库存
                    }

                    @Override
                    public void onWarningMinInput(int minValue) {
                        Toast.makeText(WasherActivity.this, "最低温度为：" + minValue + " ℃", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWarningMaxInput(int maxValue) {
                        Toast.makeText(WasherActivity.this, "最低温度为：" + maxValue + " ℃", Toast.LENGTH_SHORT).show();
                    }
                });
        washerCfgTemperatrue.setOnInputNumberListener(new NumberPickerView.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    int inputNum = Integer.parseInt(inputText);
                    viewModel.washerCommandData.setTemperature(String.valueOf(inputNum));
                }
            }
        });

        //洗涤时间
        NumberPickerView washerCfgWashingTime = findViewById(R.id.washer_cfg_washing_time);
        washerCfgWashingTime
                .setCurrentInventory(1000) // 当前的库存
                .setMaxValue(120)           //最大输入值，也就是限量，默认无限大
                .setMinDefaultNum(0)       // 最小限定量
                .setCurrentNum(Integer.valueOf(washerReciveData.updateWashingTime))
                .setmOnClickInputListener(new NumberPickerView.OnClickInputListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        //超过最大库存
                    }

                    @Override
                    public void onWarningMinInput(int minValue) {
                        Toast.makeText(WasherActivity.this, "最短洗涤时间为：" + minValue + " ℃", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWarningMaxInput(int maxValue) {
                        Toast.makeText(WasherActivity.this, "最长洗涤时间为：" + maxValue + " ℃", Toast.LENGTH_SHORT).show();
                    }
                });
        washerCfgWashingTime.setOnInputNumberListener(new NumberPickerView.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    int inputNum = Integer.parseInt(inputText);
                    viewModel.washerCommandData.setWashingTime(String.valueOf(inputNum));
                }
            }
        });


        //漂洗时间
        NumberPickerView washerCfgRinsingTime = findViewById(R.id.washer_cfg_rinsing_time);
        washerCfgRinsingTime
                .setCurrentInventory(1000) // 当前的库存
                .setMaxValue(120)           //最大输入值，也就是限量，默认无限大
                .setMinDefaultNum(0)       // 最小限定量
                .setCurrentNum(Integer.valueOf(washerReciveData.updateRinsingTime))
                .setmOnClickInputListener(new NumberPickerView.OnClickInputListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        //超过最大库存
                    }

                    @Override
                    public void onWarningMinInput(int minValue) {
                        Toast.makeText(WasherActivity.this, "最长漂洗时间为：" + minValue + " ℃", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWarningMaxInput(int maxValue) {
                        Toast.makeText(WasherActivity.this, "最短漂洗时间为：" + maxValue + " ℃", Toast.LENGTH_SHORT).show();
                    }
                });
        washerCfgRinsingTime.setOnInputNumberListener(new NumberPickerView.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    int inputNum = Integer.parseInt(inputText);
                    viewModel.washerCommandData.setRinsingTime(String.valueOf(inputNum));
                }
            }
        });

        //甩干时间
        NumberPickerView washerCfgSpinTime = findViewById(R.id.washer_cfg_spin_time);
        washerCfgSpinTime
                .setCurrentInventory(1000) // 当前的库存
                .setMaxValue(120)           //最大输入值，也就是限量，默认无限大
                .setMinDefaultNum(0)       // 最小限定量
                .setCurrentNum(Integer.valueOf(washerReciveData.updateSpinTime))
                .setmOnClickInputListener(new NumberPickerView.OnClickInputListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        //超过最大库存
                    }

                    @Override
                    public void onWarningMinInput(int minValue) {
                        Toast.makeText(WasherActivity.this, "最短甩干时间为：" + minValue + " ℃", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWarningMaxInput(int maxValue) {
                        Toast.makeText(WasherActivity.this, "最长甩干时间为：" + maxValue + " ℃", Toast.LENGTH_SHORT).show();
                    }
                });
        washerCfgSpinTime.setOnInputNumberListener(new NumberPickerView.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    int inputNum = Integer.parseInt(inputText);
                    viewModel.washerCommandData.setSpinTime(String.valueOf(inputNum));
                }
            }
        });

        //烘干时间
        NumberPickerView washerCfgDryTime = findViewById(R.id.washer_cfg_dry_time);
        washerCfgDryTime
                .setCurrentInventory(1000) // 当前的库存
                .setMaxValue(120)           //最大输入值，也就是限量，默认无限大
                .setMinDefaultNum(0)       // 最小限定量
                .setCurrentNum(Integer.valueOf(washerReciveData.updateDryTime))
                .setmOnClickInputListener(new NumberPickerView.OnClickInputListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        //超过最大库存
                    }

                    @Override
                    public void onWarningMinInput(int minValue) {
                        Toast.makeText(WasherActivity.this, "最短烘干时间为：" + minValue + " ℃", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWarningMaxInput(int maxValue) {
                        Toast.makeText(WasherActivity.this, "最长烘干时间为：" + maxValue + " ℃", Toast.LENGTH_SHORT).show();
                    }
                });
        washerCfgDryTime.setOnInputNumberListener(new NumberPickerView.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    int inputNum = Integer.parseInt(inputText);
                    viewModel.washerCommandData.setDryTime(String.valueOf(inputNum));
                }
            }
        });

    }

    private void initWashingModeView(){
        for (int i = 0; i < CHANNELS.length; i++) {
            TestFragment testFragment = new TestFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TestFragment.EXTRA_TEXT, CHANNELS[i]);
            testFragment.setArguments(bundle);
            mFragments.add(testFragment);
        }

        MagicIndicator magicIndicator = findViewById(R.id.washer_ctr_mode_magic_indicator);
        //magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.colorPrimary_washer));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.5f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.color_washer_mode_normal));
                simplePagerTitleView.setSelectedColor(Color.WHITE);

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragmentContainerHelper.handlePageSelected(index);
                        switchWashingModePages(index);
                    }
                });


                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(0.5f));
                indicator.setYOffset(UIUtil.dip2px(context, 39));
                indicator.setLineHeight(UIUtil.dip2px(context, 1));
                indicator.setColors(Color.WHITE);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);

                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
    }

    private void switchWashingModePages(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        for (int i = 0, j = mFragments.size(); i < j; i++) {
            if (i == index) {
                continue;
            }
            fragment = mFragments.get(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }
        fragment = mFragments.get(index);
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void initClick() {
        binding.washerCtlViewRunSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 啓動");
                viewModel.washerReciveData.getValue().runStatus = WasherDataConstant.RunningStatus_PowerOn;
                viewModel.washerReciveData.postValue(viewModel.washerReciveData.getValue());

            }
        });
    }

    private void doWhenWasherWorkStatusChaged(WasherReciveData washerReciveData){

        switch (washerReciveData.runStatus) {
            case WasherDataConstant.RunningStatus_PowerOff:
                binding.washerCtlViewPowerImg.setImageResource(R.drawable.ic_washer_switch_off);
                binding.washerCtlViewRunSwitchImg.setImageResource(R.drawable.ic_washer_washing_start_2);
                binding.washerCtlViewRunSwitchText.setText(R.string.washer_run_status_runing);
                break;

            case WasherDataConstant.RunningStatus_PowerOn:
                binding.washerCtlViewPowerImg.setImageResource(R.drawable.ic_washer_switch_on);
                binding.washerCtlViewRunSwitchImg.setImageResource(R.drawable.ic_washer_washing_stop_3);
                binding.washerCtlViewRunSwitchText.setText(R.string.washer_run_status_suspend);
                break;

            case WasherDataConstant.RunningStatus_Suspend:
                binding.washerCtlViewPowerImg.setImageResource(R.drawable.ic_washer_switch_on);
                binding.washerCtlViewRunSwitchImg.setImageResource(R.drawable.ic_washer_washing_start_2);
                binding.washerCtlViewRunSwitchText.setText(R.string.washer_run_status_runing);
                break;
        }

        if (washerIsRunning(washerReciveData)){
            binding.washerWaveViewRoot.setVisibility(View.VISIBLE);
            //TODO：禁用控件
            //binding.washerCtrModeMagicIndicatorCover.setVisibility(View.GONE);
            //ViewUtils.setEnableSubControls( binding.washerCtrModeMagicIndicator, false);

        }else {
            binding.washerWaveViewRoot.setVisibility(View.GONE);
            //TODO：禁用控件
            //ViewUtils.setEnableSubControls( binding.washerCtrModeMagicIndicator, true);
            //binding.washerCtrModeMagicIndicatorCover.setVisibility(View.VISIBLE);
        }
    }

    private boolean washerIsRunning(WasherReciveData washerReciveData){
        return WasherDataConstant.RunningStatus_PowerOn.equals( washerReciveData.runStatus);
    }

    //判定设备是否支持蓝牙设备
    private boolean isSupportBluetooth() {
        if (!viewModel.isSupportBluetooth()) {
            Toast.makeText(this, "您的设备不支持蓝牙,无法添加蓝牙设备", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //判定设备是否已经打开蓝牙
    private boolean isEnabledBluetooth() {
        return viewModel.isEnabledBluetooth();
    }

    private void backToPreView() {
        if (viewModel.bluetoothServer != null) {
            viewModel.bluetoothServer.stop();
        }

        finish();
    }
}
