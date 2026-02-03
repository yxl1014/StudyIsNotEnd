package gateway.service;

import gateway.tcp.NettyTcpClient;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @Package : gateway.service
 * @Create on : 2026/2/3 15:22
 **/

@Service
public class BinaryForwardService {

    private final NettyTcpClient tcpClient;

    public BinaryForwardService(NettyTcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    public byte[] forward(byte[] data) {
        return tcpClient.sendAndReceive(data);
    }
}

