package com.ennew.annation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lilong on 16/1/12.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@BaseAnnation(listenerType = View.OnClickListener.class,listenerSetter = "setOnclickListener",methodName ="onclick")
public @interface OnClickAnnation {
    int[] value() default {};
}
