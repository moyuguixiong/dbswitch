package me.jin.dsswitch.transaction;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.springframework.util.Assert.notNull;

public class MultiDBSpringManagedTransaction implements Transaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDBSpringManagedTransaction.class);

    private static final ThreadLocal<Map<Object, Object>> MAIN_DATABASE_HOLDER = new ThreadLocal<>();

    private final DataSource dataSource;

    private Connection connection;

    private boolean isConnectionTransactional;

    private boolean autoCommit;

    public MultiDBSpringManagedTransaction(DataSource dataSource) {
        notNull(dataSource, "No DataSource specified");
        this.dataSource = dataSource;
    }

    /**
     * get connection by datasource
     */
    @Override
    public Connection getConnection() throws SQLException {
//        Connection connection = DataSourceUtils.getConnection(this.dataSource);
//        Map<Object, Object> resourceMap = TransactionSynchronizationManager.getResourceMap();
//        if (!CollectionUtils.isEmpty(resourceMap)) {
//            DynamicDataSource keyDynamicDataSource = null;
//            for (Object key : resourceMap.keySet()) {
//                if (key instanceof DynamicDataSource) {
//                    keyDynamicDataSource = (DynamicDataSource) key;
//                    if (this.connection == null) {
//                        HashMap<Object, Object> map = new HashMap<>();
//                        map.put(key, resourceMap.get(key));
//                        MAIN_DATABASE_HOLDER.set(map);
//                    }
//                    break;
//                }
//            }
//            if (keyDynamicDataSource != null) {
//                final DynamicDataSource newKey = keyDynamicDataSource;
//                LOGGER.error(() -> "手动 clear DynamicDataSource:" + newKey + "," + Thread.currentThread()
//                        .getName());
//                TransactionSynchronizationManager.unbindResource(keyDynamicDataSource);
//            }
//        }
        Connection finalConnection = DataSourceUtils.getConnection(this.dataSource);
        if (this.connection == null) {
            this.connection = finalConnection;
            this.autoCommit = this.connection.getAutoCommit();
            this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.connection, this
                    .dataSource);
            LOGGER.debug("JDBC Connection [" + this.connection + "] will"
                    + (this.isConnectionTransactional ? " " : " not ") + "be managed by Spring");
        } else {
            Connection currentConnection = dataSource.getConnection();
            if (!isSameDB(finalConnection, currentConnection)) {
                finalConnection = currentConnection;
                // TODO: 2020-08-30  重点在这里，是否开启事务，关键在于是否设置connection的autocommit为true
                // 这里已经完成了功能，用todo做关键步骤标记
                finalConnection.setAutoCommit(true);
            }

        }
        String catalog = finalConnection.getCatalog();
        boolean autoCommit = finalConnection.getAutoCommit();
        LOGGER.debug("database:" + catalog + ",autocommit:" + autoCommit);
        return finalConnection;
    }

    private boolean isSameDB(Connection c1, Connection c2) throws SQLException {
        if (c1.getCatalog().equals(c2.getCatalog()) && c1.getMetaData().getUserName().equals(c2.getMetaData()
                .getUserName())) {
            return true;
        }
        return false;
    }

    /**
     * Gets a connection from Spring transaction manager and discovers if this {@code Transaction} should manage
     * connection or let it to Spring.
     * <p>
     * It also reads autocommit setting because when using Spring Transaction MyBatis thinks that autocommit is always
     * false and will always call commit/rollback so we need to no-op that calls.
     */
//    private Connection openConnection() throws SQLException {
//        Connection connection = DataSourceUtils.getConnection(this.dataSource);
//        if (this.connection == null) {
//            this.connection = connection;
//        }
//        this.autoCommit = this.connection.getAutoCommit();
//        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.connection, this.dataSource);
//
//        LOGGER.debug(() -> "JDBC Connection [" + this.connection + "] will"
//                + (this.isConnectionTransactional ? " " : " not ") + "be managed by Spring");
//        return connection;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            LOGGER.debug("Committing JDBC Connection [" + this.connection + "]");
            this.connection.commit();
            Map<Object, Object> map = MAIN_DATABASE_HOLDER.get();
            if (!CollectionUtils.isEmpty(map)) {
                for (Object key : map.keySet()) {
                    TransactionSynchronizationManager.bindResource(key, map.get(key));
                    LOGGER.error("reput DynamicDataSource:" + key + "," + Thread.currentThread().getName());
                }
                MAIN_DATABASE_HOLDER.set(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            LOGGER.debug("Rolling back JDBC Connection [" + this.connection + "]");
            this.connection.rollback();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        DataSourceUtils.releaseConnection(this.connection, this.dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTimeout() throws SQLException {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        if (holder != null && holder.hasTimeout()) {
            return holder.getTimeToLiveInSeconds();
        }
        return null;
    }
}
