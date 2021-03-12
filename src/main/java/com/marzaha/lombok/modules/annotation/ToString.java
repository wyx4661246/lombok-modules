package com.marzaha.lombok.modules.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ToString {
    boolean callSuper() default false;

    boolean override() default false;

    boolean toJson() default true;

    boolean formatDate() default true;
}

