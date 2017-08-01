package edu.nudt.das.sansiro.login.interceptor;

import java.lang.annotation.*;

/**
 * Created by yony on 17-6-13.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
}