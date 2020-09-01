package me.jin.dsswitch.utils;

import me.jin.dsswitch.datasource.DataSourceType;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class DBSwitchUtils {

    public static String getDataSourceName(String packageName, DataSourceType dataSourceType) {
        if (dataSourceType.equals(DataSourceType.READ)) {
            return packageName + "_r";
        }
        return packageName + "_w";
    }
}
