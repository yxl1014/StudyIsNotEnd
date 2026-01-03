package demo.netty;

import demo.manager.ServiceManager;
import demo.transfer.RequestTransfer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class NettyConfig {

    private final ServiceManager serviceManager;

    private final RequestTransfer requestTransfer;

    final NettyProperties nettyProperties;

    public NettyConfig(NettyProperties nettyProperties, ServiceManager serviceManager, RequestTransfer requestTransfer) {
        this.nettyProperties = nettyProperties;
        this.serviceManager = serviceManager;
        this.requestTransfer = requestTransfer;
    }

    /**
     * boss线程池-进行客户端连接
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyProperties.getBoss());
    }

    /**
     * worker线程池-进行业务处理
     *
     * @return
     */
    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorker());
    }

    /**
     * 服务端启动器，监听客户端连接
     *
     * @return
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        return new ServerBootstrap()
                // 指定使用的线程组
                .group(boosGroup(), workerGroup())
                // 指定使用的通道
                .channel(NioServerSocketChannel.class)
                // 指定连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyProperties.getTimeout())
                // 指定worker处理器
                .childHandler(new NettyServerHandler(serviceManager, requestTransfer));
    }
}
