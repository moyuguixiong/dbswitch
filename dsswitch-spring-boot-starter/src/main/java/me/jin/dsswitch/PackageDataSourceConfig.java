package me.jin.dsswitch;

import me.jin.dsswitch.datasource.PackageDataSource;
import me.jin.dsswitch.exception.PackageError;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class PackageDataSourceConfig {

    private Map<String, PackageDataSource> packageDataSources = new HashMap<>();

    public void append(PackageDataSource packageDataSource) {
        if (packageDataSource != null) {
            String[] packages = packageDataSource.getPackages();
            if (packages != null && packages.length > 0)
                for (String p : packages) {
                    String trim = p.trim();
                    if (p != null && !"".equals(trim)) {
                        if (packageDataSources.containsKey(trim)) {
                            throw new PackageError("dbswitch repeated package path");
                        } else {
                            packageDataSources.put(trim, packageDataSource);
                        }
                    }
                }
        }
    }

    public Map<String, PackageDataSource> getPackageDataSourceMap() {
        return packageDataSources;
    }
}
