package com.jasonlat.knowledeg;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@Configurable
@SpringBootApplication
@SpringBootConfiguration
@EnableEncryptableProperties
@ComponentScan(basePackages = {"com.jasonlat", "cc.jq1024.middleware"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("ai mcp knowledge 启动成功......");
    }

}
