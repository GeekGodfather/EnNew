package com.ennew.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lilong on 16/1/13.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
    int value() default -1;
    public String click() default "";
    public String longClick() default "";
    public String itemClick() default "";
    public String itemLongClick() default "";
}
