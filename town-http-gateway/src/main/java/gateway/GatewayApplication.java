package gateway;

/**
 * @author Administrator
 * @Package : gateway
 * @Create on : 2026/2/3 15:21
 **/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication()
@ConfigurationPropertiesScan(basePackages = "gateway.config")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

