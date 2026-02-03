package gateway.config;

/**
 * @author Administrator
 * @Package : gateway.config
 * @Create on : 2026/2/3 15:36
 **/

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "netty.client")
public class NettyClientConfig {

    /**
     * Netty TCP 服务器地址
     */
    private String host;

    /**
     * Netty TCP 服务器端口
     */
    private int port;

    /**
     * TCP 连接超时时间（毫秒）
     */
    private int connectTimeout = 3000;

    /**
     * TCP 读取超时时间（秒）
     */
    private int readTimeout = 5;

    // ===== getter / setter =====

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}

