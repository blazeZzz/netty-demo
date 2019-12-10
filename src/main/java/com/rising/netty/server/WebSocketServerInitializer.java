package com.rising.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;

/**
 * create by zy 2019/11/28 14:53
 * TODO
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/";

    private final SslContext sslCtx;

    public WebSocketServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc())); // 设置 https 相关
        }
        pipeline.addLast(new HttpServerCodec()); // http 编码
        pipeline.addLast(new HttpObjectAggregator(65536 * 100)); // http 消息聚合器
        pipeline.addLast(new WebSocketServerCompressionHandler()); // 压缩 可以不设置
        // 这里设置 frame 的大小
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true, 65536 * 100)); // 协议
        pipeline.addLast(new WebSocketFrameHandler()); // 处理 WebSocketFrame
    }
}
