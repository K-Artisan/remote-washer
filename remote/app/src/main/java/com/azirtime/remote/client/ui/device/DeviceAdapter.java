package com.azirtime.remote.client.ui.device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.azirtime.remote.R;
import com.azirtime.remote.dto.DeviceDto;
import com.bumptech.glide.Glide;

public class DeviceAdapter extends PagedListAdapter<DeviceDto, DeviceAdapter.DeviceViewHolder> {
    protected Context mContext;
    private DeviceActivity activity;

    public DeviceAdapter() {
        super(new DiffUtil.ItemCallback<DeviceDto>() {
            @Override
            public boolean areItemsTheSame(@NonNull DeviceDto oldItem, @NonNull DeviceDto newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull DeviceDto oldItem, @NonNull DeviceDto newItem) {
                return oldItem.equals(newItem);
            }
        });

    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            activity = (DeviceActivity) mContext;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_device_item, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "TODO: 设备详情页", Toast.LENGTH_SHORT).show();
            }
        });


        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceDto device = getItem(position);
        if (device != null){
            holder.bindData(device);
        }
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private ImageView ig_deviceImg;
        private TextView tv_deviceName ;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ig_deviceImg = itemView.findViewById(R.id.device_item_img);
            tv_deviceName = itemView.findViewById(R.id.device_item_name);
        }

        public void bindData(DeviceDto device){
            tv_deviceName.setText(device.name);

            Glide.with(mContext).load(device.imgResId)
                    .placeholder(R.drawable.ic_washer_02_64) //默认图片
                    .centerCrop()
                    .into(ig_deviceImg);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.goToDevcieView(device);
                }
            });
        }
    }

}
