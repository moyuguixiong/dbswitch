package me.jin.dsswitch.beanlifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/09/01
 */
public class ApplicationContextListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationContextListener.class);

    private DBSwitchBeanPostProcessor dbSwitchBeanPostProcessor;

    public ApplicationContextListener(DBSwitchBeanPostProcessor dbSwitchBeanPostProcessor) {
        this.dbSwitchBeanPostProcessor = dbSwitchBeanPostProcessor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            Map<String, String> dbSwitchPackageToDataSource = dbSwitchBeanPostProcessor.getDbSwitchPackageToDataSource();
            if (dbSwitchPackageToDataSource != null && dbSwitchPackageToDataSource.size() > 0) {
                for (Map.Entry<String, String> kv : dbSwitchPackageToDataSource.entrySet()) {
                    LOGGER.info("dbswitch -> auto switch datasource packages:" + kv.getKey() + "," + kv.getValue());
                }
            }
        }
    }
}
