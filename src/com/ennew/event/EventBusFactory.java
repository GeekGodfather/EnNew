package com.ennew.event;

import android.util.SparseArray;

import de.greenrobot.event.EventBus;

/**
 * Created by lilong on 16/1/11.
 */
public class EventBusFactory {
    private static int defult = 1;

    private static SparseArray<EventBus> sparseArray = new SparseArray<EventBus>();

    public static EventBus getInstace() {
        return get(defult);
    }

    public static EventBus get(int key) {
        EventBus eventBus=sparseArray.get(key);
        if ( eventBus== null) {
             eventBus = EventBus.getDefault();
            sparseArray.put(key, eventBus);
        }
        return eventBus;
    }

    public static void setDefult(int key) {
        sparseArray.put(key,EventBus.getDefault());
    }
}
