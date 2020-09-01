package me.jin.dsswitch.datasource;

import me.jin.dsswitch.exception.DataSourceError;
import me.jin.dsswitch.exception.PackageError;

import javax.sql.DataSource;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class PackageDataSource {

    /**
     * package name,support batch
     */
    private String[] packages;
    /**
     * read datasource
     */
    private DataSource readDataSource;

    /**
     * write datasource
     */
    private DataSource writeDataSource;

    private String readDataSourceName;

    private String writeDataSourceName;

//    public PackageDataSource(String[] packages, DataSource readDataSource, DataSource writeDataSource) {
//        this(packages, null, readDataSource, null, writeDataSource);
//    }

    public PackageDataSource(String[] packages, String readDataSourceName, DataSource readDataSource, String writeDataSourceName, DataSource writeDataSource) {
        if (packages == null) {
            throw new PackageError("dbswitch packages can't be null");
        }
        for (String p : packages) {
            if (p == null || "".equals(p.trim())) {
                throw new PackageError("dbswitch packages can't be empty");
            }
        }
        if (readDataSource == null) {
            throw new DataSourceError("dbswitch read datasource can't be null");
        }
        if (writeDataSource == null) {
            throw new DataSourceError("dbswitch write datasource can't be null");
        }
        this.packages = packages;
        this.readDataSource = readDataSource;
        this.writeDataSource = writeDataSource;
        this.readDataSourceName = readDataSourceName;
        this.writeDataSourceName = writeDataSourceName;
    }

    public String[] getPackages() {
        return packages;
    }

    public DataSource getReadDataSource() {
        return readDataSource;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    public String getReadDataSourceName() {
        return readDataSourceName;
    }

    public void setReadDataSourceName(String readDataSourceName) {
        this.readDataSourceName = readDataSourceName;
    }

    public String getWriteDataSourceName() {
        return writeDataSourceName;
    }

    public void setWriteDataSourceName(String writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }
}
