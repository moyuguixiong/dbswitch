package me.jin.dsswitch.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

    private static ThreadLocal<String> CURRENT_DATA_SOURCE_NAME = new ThreadLocal<>();
    private static ThreadLocal<String> DEFAULT_DATA_SOURCE_NAME = new ThreadLocal<>();

    public static void setDataSoureName(String dataSourceName) {
        String defaultDataSourceName = DEFAULT_DATA_SOURCE_NAME.get();
        if (defaultDataSourceName == null) {
            LOGGER.info("set default datasource:" + dataSourceName);
            DEFAULT_DATA_SOURCE_NAME.set(dataSourceName);
        }
        CURRENT_DATA_SOURCE_NAME.set(dataSourceName);
        LOGGER.info("set current datasource:" + dataSourceName);
    }

    public static void clearDataSourceName() {
        String currentDataSourceName = CURRENT_DATA_SOURCE_NAME.get();
        String defaultDataSourceName = DEFAULT_DATA_SOURCE_NAME.get();
        CURRENT_DATA_SOURCE_NAME.remove();
        LOGGER.info("clear current datasource");
        if (currentDataSourceName.equals(defaultDataSourceName)) {
            LOGGER.info("clear default datasource");
            DEFAULT_DATA_SOURCE_NAME.remove();
        } else {
            LOGGER.info("current datasource automatically switch to default datasource");
            CURRENT_DATA_SOURCE_NAME.set(defaultDataSourceName);
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CURRENT_DATA_SOURCE_NAME.get();
    }
}
