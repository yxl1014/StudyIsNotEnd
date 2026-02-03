package gateway.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @Package : gateway.tcp
 * @Create on : 2026/2/3 15:50
 **/


public class NettyTcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final CountDownLatch latch = new CountDownLatch(1);
    private byte[] response;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        response = new byte[msg.readableBytes()];
        msg.readBytes(response);
        latch.countDown();
    }

    public byte[] getResponse(int timeoutSeconds) throws InterruptedException {
        if (!latch.await(timeoutSeconds, TimeUnit.SECONDS)) {
            throw new RuntimeException("TCP response timeout");
        }
        return response;
    }
}
