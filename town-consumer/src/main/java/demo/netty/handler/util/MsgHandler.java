package demo.netty.handler.util;

import io.netty.channel.ChannelHandlerContext;
import po.RequestMsg;
import po.ResponseMsg;

/**
 * @author Administrator
 * @Package : demo.netty.handler.util
 * @Create on : 2026/1/7 15:15
 **/


@FunctionalInterface
public interface MsgHandler {
    ResponseMsg handle(RequestMsg msg, ChannelHandlerContext ctx) throws Exception;
}

