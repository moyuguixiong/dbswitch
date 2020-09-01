package me.jin.dsswitch.annotation;

import java.lang.annotation.*;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForceDBWrite {
}
