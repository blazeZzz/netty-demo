package com.rising.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create by zy 2019/11/28 14:50
 * TODO
 */
public final class WebSocketServer {

    //static final boolean SSL = System.getProperty("ssl") != null;
    //static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "9898"));
    static final boolean SSL = false;
    static final int PORT = 9898;

    public static void main(String[] args) throws Exception {
        init();
    }

    public static void init() throws Exception {
        // Configure SSL. 配置 SSL
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        /**
         * interface EventLoopGroup extends EventExecutorGroup extends ScheduledExecutorService extends ExecutorService
         * 配置服务端的 NIO 线程池,用于网络事件处理，实质上他们就是 Reactor 线程组
         * bossGroup 用于服务端接受客户端连接，workerGroup 用于进行 SocketChannel 网络读写
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 是 Netty 用于启动 NIO 服务端的辅助启动类，用于降低开发难度
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketServerInitializer(sslCtx));

            //服务器启动辅助类配置完成后，调用 bind 方法绑定监听端口，调用 sync 方法同步等待绑定操作完成，服务开启
            Channel ch = b.bind(PORT).sync().channel();
            System.out.println("服务已开启,等待客户端连接......");

            //服务端开启后,定时给服务端推送消息
            //timer();

            //下面会进行阻塞，等待服务器连接关闭之后 main 方法退出，程序结束
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //退出 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 定时器 定时给所有客户端主动推送消息
     */
    public static void timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer run:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                ChannelSupervise.send2All(new TextWebSocketFrame("abcde"));
            }
        }, 1000, 10000);//延时1s，之后每隔10s运行一次
    }

}
