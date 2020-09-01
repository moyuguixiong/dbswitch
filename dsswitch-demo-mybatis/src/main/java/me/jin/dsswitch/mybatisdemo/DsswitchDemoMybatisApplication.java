package me.jin.dsswitch.mybatisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DsswitchDemoMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsswitchDemoMybatisApplication.class, args);
    }

}
