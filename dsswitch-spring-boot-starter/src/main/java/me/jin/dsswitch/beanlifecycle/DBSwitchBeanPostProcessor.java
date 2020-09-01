package me.jin.dsswitch.beanlifecycle;

import me.jin.dsswitch.PackageDataSourceConfig;
import me.jin.dsswitch.annotation.ForceDBWrite;
import me.jin.dsswitch.datasource.DataSourceType;
import me.jin.dsswitch.datasource.PackageDataSource;
import me.jin.dsswitch.datasource.RoutingDataSource;
import me.jin.dsswitch.utils.AopBeanUtils;
import me.jin.dsswitch.utils.DBSwitchUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class DBSwitchBeanPostProcessor implements BeanPostProcessor {

    private PackageDataSourceConfig packageDataSourceConfig;

    private Map<String, String> dbSwitchPackageToDataSource = new HashMap<>();

    public Map<String, String> getDbSwitchPackageToDataSource() {
        return dbSwitchPackageToDataSource;
    }

    public DBSwitchBeanPostProcessor(PackageDataSourceConfig packageDataSourceConfig) {
        this.packageDataSourceConfig = packageDataSourceConfig;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Map<String, PackageDataSource> packageDataSourceMap = packageDataSourceConfig.getPackageDataSourceMap();
//        if (beanName.equals("helloService2")) {
//            System.out.println(bean.getClass().getName());
//        }
        if (packageDataSourceMap != null && packageDataSourceMap.size() > 0) {
            for (String configPackageName : packageDataSourceMap.keySet()) {
                String classPath = null;
                try {
                    classPath = getOriginalBeanClassName(bean);
                } catch (Exception e) {
                }
                if (classPath.contains(configPackageName)) {
                    //spring的代理工厂ProxyFactory，会根据配置(spring.aop.proxy-target-class)获取JDKProxy或CGLIBProxy，生成代理对象
                    //spring的代理工厂ProxyFactory,使用了代理链模式，支持多重代理
                    PackageDataSource packageDataSource = packageDataSourceMap.get(configPackageName);
                    String ds = "[readDataSource:" + (packageDataSource.getReadDataSourceName() == null ? packageDataSource.getReadDataSource().toString() : packageDataSource.getReadDataSourceName()
                    ) + ",writeDataSource:" + (packageDataSource.getWriteDataSourceName() == null ? packageDataSource.getWriteDataSource().toString() : packageDataSource
                            .getWriteDataSourceName()) +
                            "]";
                    dbSwitchPackageToDataSource.put(configPackageName, ds);
                    ProxyFactory proxyFactory = new ProxyFactory();
                    proxyFactory.setTarget(bean);
//                    proxyFactory.setAdvisorChainFactory();
                    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
                    advisor.setAdvice(new DBSwitchProxy(bean, configPackageName));
                    proxyFactory.addAdvisor(advisor);
                    Object targetBean = proxyFactory.getProxy();
                    return targetBean;
                }
            }
        }
        return bean;
    }

    private String getOriginalBeanClassName(Object bean) throws Exception {
        Object originalBean = AopBeanUtils.getOriginalBean(bean);
        return originalBean.getClass().getName();
    }

    private class DBSwitchProxy implements MethodInterceptor {
        private Object target;
        private String packageName;

        public DBSwitchProxy(Object target, String packageName) {
            this.target = target;
            this.packageName = packageName;
        }

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String methodName = methodInvocation.getMethod().getName();
            Method originalClassMethod = getOriginalClassMethod(target, methodName, methodInvocation.getArguments());
//            System.out.println("DBWrite:" + originalClassMethod.isAnnotationPresent(DBWrite.class));
//            System.out.println("Transactional:" + originalClassMethod.isAnnotationPresent(Transactional.class));
            //set default datasource
            if (methodName.startsWith("add") || methodName.startsWith("insert") || methodName.startsWith("update") || methodName.startsWith("alter") || methodName.startsWith("delete") || methodName
                    .startsWith("remove") || originalClassMethod.isAnnotationPresent(ForceDBWrite.class) || originalClassMethod.isAnnotationPresent(Transactional.class)) {
                String writeDataSourceName = DBSwitchUtils.getDataSourceName(packageName, DataSourceType.WRITE);
                RoutingDataSource.setDataSoureName(writeDataSourceName);
            } else {
                String readDataSourceName = DBSwitchUtils.getDataSourceName(packageName, DataSourceType.READ);
                RoutingDataSource.setDataSoureName(readDataSourceName);
            }
            try {
                return methodInvocation.proceed();
            } catch (Throwable throwable) {
                throw throwable;
            } finally {
                RoutingDataSource.clearDataSourceName();
            }
        }

        private Method getOriginalClassMethod(Object target, String methodName, Object[] args) throws Exception {
            Object originalBean = AopBeanUtils.getOriginalBean(target);
            Class[] parameterTypes = null;
            if (args != null && args.length > 0) {
                parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
            return originalBean.getClass().getDeclaredMethod(methodName, parameterTypes);
        }
    }
}
