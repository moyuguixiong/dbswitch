package me.jin.dsswitch.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/09/01
 */
public class AopBeanUtils {

    public static Object getOriginalBean(Object proxyBean) throws Exception {
        //递归获取代理前原始对象(需要兼容jdkproxy和cglib两种动态代理方式)
        if (!AopUtils.isAopProxy(proxyBean)) {
            return proxyBean;
        } else {
            Object tempTarget = null;
            if (AopUtils.isCglibProxy(proxyBean)) {
                Field h = proxyBean.getClass().getDeclaredField("CGLIB$CALLBACK_0");
                h.setAccessible(true);
                Object dynamicAdvisedInterceptor = h.get(proxyBean);
                Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
                advised.setAccessible(true);
                tempTarget = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
                if (AopUtils.isCglibProxy(tempTarget)) {
                    return getOriginalBean(tempTarget);
                }
            }
            if (AopUtils.isJdkDynamicProxy(proxyBean)) {
                //Field h = proxyBean.getClass().getDeclaredField("h");
                Field h = proxyBean.getClass().getSuperclass().getDeclaredField("h");
                h.setAccessible(true);
                AopProxy aopProxy = (AopProxy) h.get(proxyBean);
                Field advised = aopProxy.getClass().getDeclaredField("advised");
                advised.setAccessible(true);
                tempTarget = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
                if (AopUtils.isJdkDynamicProxy(tempTarget)) {
                    return getOriginalBean(tempTarget);
                }
            }
            return tempTarget;
        }
    }
}
