package com.azirtime.remote.dto;

public class ResponseResult {
    public boolean success = true;
    public String responseCode = "000000";
    public Object data;
    public String errorMsg;     //错消息
    public String message;      //普通的消息
    public String friendlyMsg;  //给用户的友好界面提示
}
