package org.ppke.itk.cnctoolpathsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"org.ppke.itk.cnctoolpathsharing.controller",
                "org.ppke.itk.cnctoolpathsharing.configuration",
                "org.ppke.itk.cnctoolpathsharing.exceptions"})
public class CncToolPathSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CncToolPathSharingApplication.class, args);
    }
}