package com.ennew.annation;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;

import java.lang.reflect.Field;

/**
 * Created by lilong on 16/1/13.
 */
public class InitInjectedView {

    public  InitInjectedView(Activity activity){
         InjectedView(activity, activity.getWindow().getDecorView());
    }

    private void  InjectedView(Object activity,View sourceView){
        Field[] fields = activity.getClass().getDeclaredFields();   //获取字段
        if(fields!=null && fields.length>0){
            for(Field field : fields){
                try {
                    field.setAccessible(true);   //设为可访问

                    if(field.get(activity)!= null )
                        continue;

                    ViewInject viewInject = field.getAnnotation(ViewInject.class);
                    if(viewInject!=null){

                        int viewId = viewInject.value();
                        //关键,注解初始化，相当于 backBtn = (TextView) findViewById(R.id.back_btn);
                        field.set(activity,sourceView.findViewById(viewId));
                        //事件
                        setListener(activity,field,viewInject.click(),Method.Click);
                        setListener(activity,field,viewInject.longClick(),Method.LongClick);
                        setListener(activity,field,viewInject.itemClick(),Method.ItemClick);
                        setListener(activity,field,viewInject.itemLongClick(),Method.itemLongClick);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setListener(Object activity,Field field,String methodName,Method method)throws Exception{
        if(methodName == null || methodName.trim().length() == 0)
            return;

        Object obj = field.get(activity);

        switch (method) {
            case Click:
                if(obj instanceof View){
                    ((View)obj).setOnClickListener(new EventListener(activity).click(methodName));
                }
                break;
            case ItemClick:
                if(obj instanceof AbsListView){
                    ((AbsListView)obj).setOnItemClickListener(new EventListener(activity).itemClick(methodName));
                }
                break;
            case LongClick:
                if(obj instanceof View){
                    ((View)obj).setOnLongClickListener(new EventListener(activity).longClick(methodName));
                }
                break;
            case itemLongClick:
                if(obj instanceof AbsListView){
                    ((AbsListView)obj).setOnItemLongClickListener(new EventListener(activity).itemLongClick(methodName));
                }
                break;
            default:
                break;
        }
    }




}
