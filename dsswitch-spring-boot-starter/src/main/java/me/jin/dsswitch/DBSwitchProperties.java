package me.jin.dsswitch;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/08/20
 */
@ConfigurationProperties(prefix = "dbswitch")
public class DBSwitchProperties {

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
