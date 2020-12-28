package com.azirtime.remote.client.ui.adddevice;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azirtime.remote.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothDeviceViewHolder> {
    private final static  int VIEW_TYPE_EMPTY = -1;  //作为空数据的布局类型
    private Context mContext;
    private AddBluetoothDeviceActivity activity;
    private List<BluetoothDevice> dataList;
    private int mViewType;

    public BluetoothDeviceAdapter(List<BluetoothDevice> bluetoothDevices) {
        super();
        dataList = bluetoothDevices;
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
            activity = (AddBluetoothDeviceActivity)mContext;
        }
        mViewType = viewType;

        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_add_bluetooth_device_empty_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_add_bluetooth_device_item, parent, false);

        }
        return new BluetoothDeviceAdapter.BluetoothDeviceViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder holder, int position) {

        if (mViewType == VIEW_TYPE_EMPTY){
        }else {
            BluetoothDevice device = dataList.get(position);
            if (device != null) {
                holder.bindData(device);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() > 0 ? dataList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.size() <= 0) {
            return VIEW_TYPE_EMPTY;
        }

        return super.getItemViewType(position);
    }

    public class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private int viewType;
        private ImageView ig_deviceImg;
        private TextView tv_deviceName;

        public BluetoothDeviceViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            view = itemView;
            ig_deviceImg = itemView.findViewById(R.id.device_item_img);
            tv_deviceName = itemView.findViewById(R.id.device_item_name);
        }

        public void bindData(BluetoothDevice device) {

            if (viewType >= 0){
                if (!TextUtils.isEmpty(device.getName())){
                    tv_deviceName.setText(device.getName());
                }else {
                    tv_deviceName.setText(device.getAddress());
                }

                Glide.with(mContext).load(R.drawable.ic_bluetooth_item)
                        .placeholder(R.drawable.ic_bluetooth_item) //默认图片
                        .circleCrop()
                        .into(ig_deviceImg);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.callWhenHasSelectedBluetooth(device);
                    }
                });
            }

        }
    }

}
