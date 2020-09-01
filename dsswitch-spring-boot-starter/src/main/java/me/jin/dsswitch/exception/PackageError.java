package me.jin.dsswitch.exception;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
public class PackageError extends RuntimeException {

    public PackageError() {
    }

    public PackageError(String message) {
        super(message);
    }
}
