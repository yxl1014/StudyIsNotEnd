package provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @Package : provider
 * @Create on : 2026/1/9 10:40
 **/


@SpringBootApplication
@EnableDubbo
public class UpdateProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(UpdateProviderApplication.class, args);
    }
}
