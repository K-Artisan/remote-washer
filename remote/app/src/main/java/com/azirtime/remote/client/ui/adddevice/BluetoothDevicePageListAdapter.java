package com.azirtime.remote.client.ui.adddevice;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.azirtime.remote.R;

public class BluetoothDevicePageListAdapter extends PagedListAdapter<BluetoothDevice, BluetoothDevicePageListAdapter.BluetoothDeviceViewHolder> {
    protected Context mContext;

    protected BluetoothDevicePageListAdapter() {
        super(new DiffUtil.ItemCallback<BluetoothDevice>() {
            @Override
            public boolean areItemsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                return oldItem.getUuids() == newItem.getUuids();
            }

            @Override
            public boolean areContentsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_add_bluetooth_device_item, parent, false);
        return new BluetoothDevicePageListAdapter.BluetoothDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder holder, int position) {
        BluetoothDevice device = getItem(position);
        if (device != null){
            holder.bindData(device);
        }
    }

    public class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private ImageView ig_deviceImg;
        private TextView tv_deviceName ;

        public BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ig_deviceImg = itemView.findViewById(R.id.device_item_img);
            tv_deviceName = itemView.findViewById(R.id.device_item_name);
        }

        public void bindData(BluetoothDevice device){
            tv_deviceName.setText(device.getName());

/*            Glide.with(mContext).load(device.imgResId)
                    .placeholder(R.drawable.ic_washer) //默认图片
                    .centerCrop()
                    .into(ig_deviceImg);*/
        }
    }
}
