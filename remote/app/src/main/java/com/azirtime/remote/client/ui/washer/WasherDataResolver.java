package com.azirtime.remote.client.ui.washer;

import android.util.Log;

import com.azirtime.remote.common.utils.ByteUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WasherDataResolver {

    //https://blog.csdn.net/Andyzhu_2005/article/details/78816142
    //基于内存的阻塞队列
    private ByteBuf recievedBuffer = Unpooled.buffer(1024 * 1000);
    //蓝牙数据包存在拆包，包丢失的可能，这个是保存经过整理合并后的完整数据包的队列
    public BlockingQueue<byte[]> completeRecieveDataList = new LinkedBlockingQueue<byte[]>(1000 * 10);

    private onRecieveNewCompleteDataDataListener mOnRecieveNewCompleteDataListener;

    /**
     * 解析返回的数据
     * 蓝牙数据包存在拆包，包丢失的可能，
     * 通过该方法，将被拆分整理合并后的完整数据包
     *
     * @param data 蓝牙设备返回的原装字节数据
     */
    public void parseAndMergeCompleteReciveData(byte[] data) {
        if (data != null && data.length > 0) {
            recievedBuffer.writeBytes(data);     //将蓝牙收到的byte数组放入bytebuf缓冲区中
            Log.d("reciveData", "netty->缓冲区大小" + recievedBuffer.readableBytes());
            //重新组帧
            while (recievedBuffer.readableBytes() >= WasherReciveData.RECIEVE_BYTE_LENGTH) {     //判断缓冲区大小大于等于一帧长度，就可以进行解帧
                ByteBuf bufTemp = recievedBuffer.readBytes(1);    //先取第一个字节，判断是不是帧头的第一个字节0x24
                byte[] bytesTemp = new byte[1];
                bufTemp.readBytes(bytesTemp);
                if (bytesTemp[0] == WasherReciveData.RECIEVE_BYTE_START_FLAG[0]) {        //判断第一个字节是不是0x24，如果不是，直接丢弃，如果是，则进入if判断
                    recievedBuffer.markReaderIndex();     //取后1个数时候，考虑如果第1个是0xbe，但是后面1个不是0x24，这个时候需要进行回滚操作
                    ByteBuf bufTemp1 = recievedBuffer.readBytes(WasherReciveData.RECIEVE_BYTE_START_FLAG.length - 1);   //如果第一个字节是0x24,就再取后1个字节，继续对帧头进行判断
                    byte[] bytesTemp1 = new byte[WasherReciveData.RECIEVE_BYTE_START_FLAG.length - 1];
                    bufTemp1.readBytes(bytesTemp1);

                    if (bytesTemp1[0] == (byte) WasherReciveData.RECIEVE_BYTE_START_FLAG[1]) {      //如果后三位是0xbebebe，说明找到了帧头,就需要取一帧的后续部分（帧长-帧头）=128-4=124个字节
                        ByteBuf bufTemp2 = recievedBuffer.readBytes(WasherReciveData.RECIEVE_BYTE_LENGTH - WasherReciveData.RECIEVE_BYTE_START_FLAG.length);
                        byte[] bytesTemp2 = new byte[WasherReciveData.RECIEVE_BYTE_LENGTH - WasherReciveData.RECIEVE_BYTE_START_FLAG.length];
                        bufTemp2.readBytes(bytesTemp2);

                        //取出帧的后续部分，还需要判断帧尾是不是0x23,0x23；如果不是，说明这个帧不完整，即需要重新进入第二个字节搜索帧头
                        //检查是否是帧尾
                        /*
                        if (!(bytesTemp2+[bytesTemp2.length - 2] == WasherReciveData.RECIEVE_BYTE_END_FLAG[0]
                                && bytesTemp2[bytesTemp2.length - 1] == WasherReciveData.RECIEVE_BYTE_END_FLAG[1])) {
                            recievedBuffer.resetReaderIndex();   //指针回滚，回滚到只是取出第一个数
                            continue;       //重新进入while循环
                        }*/
                        byte[] endByteTemp =  ByteUtils.copyBytes(bytesTemp2, bytesTemp2.length - WasherReciveData.RECIEVE_BYTE_END_FLAG.length, bytesTemp2.length -1);
                        boolean isMatch = isMatchEndBytes(endByteTemp);
                        if (!isMatch) {
                            recievedBuffer.resetReaderIndex();   //指针回滚，回滚到只是取出第一个数
                            continue;
                        }

                        //         查看帧计数
             /*           new_frame_index=bytesTemp2[0]&0xff;
                        if (new_frame_index!=(old_frame_index+1)||((new_frame_index==0)&&(old_frame_index!=255))){
                            error_Frame_count++;
                            log2Flie.log2FileFun("log","帧计数错误"+"error_Frame_count-->"+error_Frame_count+";old_frame-->"+old_frame_index+";new_frame-->"+new_frame_index);
                        }
                        old_frame_index=new_frame_index;*/

                        //重新组帧
                        byte[] bytesTemp3 = new byte[WasherReciveData.RECIEVE_BYTE_LENGTH];
                        for (int i = 0; i < WasherReciveData.RECIEVE_BYTE_START_FLAG.length; i++) {
                            bytesTemp3[i] = WasherReciveData.RECIEVE_BYTE_START_FLAG[i];
                        }

                        System.arraycopy(bytesTemp2, 0, bytesTemp3, WasherReciveData.RECIEVE_BYTE_START_FLAG.length, bytesTemp2.length);
                        //如果是，则放入list
                        synchronized (completeRecieveDataList) {
                            completeRecieveDataList.add(bytesTemp3);     //放入completeRecieveDataList链表的帧shiw
                            mOnRecieveNewCompleteDataListener.onRecieveNewCompleteDataData(bytesTemp3);

                        }
                        //http://vlambda.com/wz_x6pDpz7SlN.html
                        recievedBuffer.discardReadBytes();   //将取出来的这一帧数据在buffer的内存进行清除，释放内存

                    } else {       //第一个字节是0xbe，后三个字节不是0xbebebe情况
                        recievedBuffer.resetReaderIndex();   //指针回滚，回滚到只是取出第一个数
                        continue;
                    }
                }
            }
        }
    }

    private boolean isMatchEndBytes(byte[] srcBytes) {
        for (int i = 0; i < WasherReciveData.RECIEVE_BYTE_END_FLAG.length; i++) {
            if (srcBytes[srcBytes.length - WasherReciveData.RECIEVE_BYTE_END_FLAG.length + i] != WasherReciveData.RECIEVE_BYTE_END_FLAG[i]) {
                return false;
            }
        }

        return true;
    }

    public void setOnReciveNewCompleteDataistener(onRecieveNewCompleteDataDataListener listener) {
        if (listener != null) {
            mOnRecieveNewCompleteDataListener = listener;
        }
    }

    public interface onRecieveNewCompleteDataDataListener {
        void onRecieveNewCompleteDataData(byte[] data);
    }
}
