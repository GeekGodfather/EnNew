package com.ennew.event;

/**
 * Created by lilong on 16/1/11.
 */
public   abstract class BaseEvent<T> {

    private int type;
    private T data;

    public BaseEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public BaseEvent<T> setData(T data) {
        this.data = data;
        return this;
    }
}
