package com.ennew.event;

/**
 * Created by lilong on 16/1/11.
 */
public class SmsCodeVerif extends BaseEvent{

    public static final  int mobile=0;

    public SmsCodeVerif(int type) {
        super(type);
    }
}
