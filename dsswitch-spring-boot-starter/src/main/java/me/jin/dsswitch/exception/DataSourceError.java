package me.jin.dsswitch.exception;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class DataSourceError extends RuntimeException {
    public DataSourceError() {
    }

    public DataSourceError(String message) {
        super(message);
    }
}
