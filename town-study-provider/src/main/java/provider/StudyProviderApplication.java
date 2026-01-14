package provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @Package : PACKAGE_NAME
 * @Create on : 2026/1/14 15:59
 **/


@SpringBootApplication
@EnableDubbo
public class StudyProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyProviderApplication.class, args);
    }
}
