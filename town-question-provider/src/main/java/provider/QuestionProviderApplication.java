package provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @Package : PACKAGE_NAME
 * @Create on : 2026/1/12 20:17
 **/


@SpringBootApplication
@EnableDubbo
public class QuestionProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionProviderApplication.class, args);
    }
}
