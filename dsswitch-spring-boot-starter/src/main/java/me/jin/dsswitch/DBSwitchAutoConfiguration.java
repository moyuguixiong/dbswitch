package me.jin.dsswitch;

import me.jin.dsswitch.beanlifecycle.ApplicationContextListener;
import me.jin.dsswitch.beanlifecycle.DBSwitchBeanPostProcessor;
import me.jin.dsswitch.datasource.DataSourceType;
import me.jin.dsswitch.datasource.PackageDataSource;
import me.jin.dsswitch.datasource.RoutingDataSource;
import me.jin.dsswitch.exception.DataSourceError;
import me.jin.dsswitch.utils.DBSwitchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
@Configuration
@ConditionalOnBean(PackageDataSourceConfig.class)
@EnableConfigurationProperties(DBSwitchProperties.class)
@ConditionalOnProperty(prefix = "dbswitch", name = "enable", havingValue = "true")
public class DBSwitchAutoConfiguration {

    @Autowired
    private DBSwitchProperties dbSwitchProperties;

    @Autowired
    private PackageDataSourceConfig packageDataSourceConfig;

    @Bean
    public DBSwitchBeanPostProcessor getDBSwitchBeanPostProcessor() {
        DBSwitchBeanPostProcessor beanPostProcessor = new DBSwitchBeanPostProcessor(packageDataSourceConfig);
        return beanPostProcessor;
    }

    /**
     * DataSourceTransactionManager、SqlSessionFactoryBean or MybatisSqlSessionFactoryBean should set this
     * datasource to their datasource parameter.
     *
     * @return
     */
    @Bean
    public RoutingDataSource getRoutingDataSource() {
        if (packageDataSourceConfig != null) {
            Map<String, PackageDataSource> map = packageDataSourceConfig.getPackageDataSourceMap();
            if (map != null && map.size() > 0) {
                RoutingDataSource routingDataSource = new RoutingDataSource();
                Map<Object, Object> targetDataSource = new HashMap<>();
                for (String packageName : map.keySet()) {
                    PackageDataSource packageDataSource = map.get(packageName);
                    targetDataSource.put(DBSwitchUtils.getDataSourceName(packageName, DataSourceType.READ), packageDataSource.getReadDataSource());
                    targetDataSource.put(DBSwitchUtils.getDataSourceName(packageName, DataSourceType.WRITE), packageDataSource.getWriteDataSource());
                }
                routingDataSource.setTargetDataSources(targetDataSource);
                return routingDataSource;
            }
        }
        throw new DataSourceError("dbswitch package datasource can't be null");
    }

    @Bean
    public ApplicationContextListener getListener(DBSwitchBeanPostProcessor dbSwitchBeanPostProcessor) {
        ApplicationContextListener listener = new ApplicationContextListener(dbSwitchBeanPostProcessor);
        return listener;
    }
}
