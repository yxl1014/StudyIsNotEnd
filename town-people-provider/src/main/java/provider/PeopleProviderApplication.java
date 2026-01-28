package provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @Package : provider
 * @Create on : 2026/1/28 14:34
 **/


@SpringBootApplication
@EnableDubbo
public class PeopleProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeopleProviderApplication.class, args);
    }
}
