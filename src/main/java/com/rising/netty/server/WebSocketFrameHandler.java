package com.rising.netty.server;

import com.alibaba.fastjson.JSON;
import com.rising.netty.model.RequestModel;
import com.rising.netty.model.ResultModel;
import com.rising.netty.util.Base64;
import com.rising.netty.util.MQUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * create by zy 2019/11/28 14:57
 * TODO
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            //logger.info("接收消息:" + request);
            //验证数据并写入 mq
            String msg = DataToMq(request);

            //返回信息
            ctx.channel().writeAndFlush(new TextWebSocketFrame(msg));
            //ChannelSupervise.send2All(new TextWebSocketFrame(msg));
        } else if (frame instanceof BinaryWebSocketFrame) {
            //二进制
            ByteBuf content = frame.content();
            byte[] reg = new byte[content.readableBytes()];
            content.readBytes(reg);
            String request = new String(reg, "UTF-8");
            //logger.info("BinaryWebSocketFrame接收消息:" + request);

            //验证数据并写入 mq
            String msg = DataToMq(request);

            //返回信息
            ByteBuf respByteBuf = Unpooled.copiedBuffer(msg.getBytes());
            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(respByteBuf));
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //添加连接
        logger.info("客户端加入连接：" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //断开连接
        logger.info("客户端断开连接：" + ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    /**
     * 将数据解析 并写入 mq
     */
    private String DataToMq(String content) {
        boolean flag = true;

        //解析 json
        //RequestModel reqModel = null;
        ResultModel result = new ResultModel();
        TreeMap<String, String> map = new TreeMap<String, String>();
        try {
            map = JSON.parseObject(content, TreeMap.class);
            //reqModel = JSON.parseObject(content, RequestModel.class);
            logger.info("--------数据解析成功--------");
        } catch (Exception e) {
            flag = false;
            logger.error("--------json解析错误--------" + e.toString());
            result.setCode(1);
            result.setMsg("json解析错误");
        }

        //如果解析 json 成功, 验证是否合法
        if (flag) {
            //验证
            try {
                String str = "";
                //循环 map 的 key, 将对应的 value 拼接
                for (String key : map.keySet()) {
                    if (!key.equals("check_info")) {
                        str += map.get(key);
                    }
                }
                str += "Rising123";
                // md5 加密
                String md5List = DigestUtils.md5DigestAsHex(str.getBytes());
                //System.out.println("MD5:" + md5List);

                /**
                 System.out.println("=============================");
                 String blaze = reqModel.getList() + reqModel.getTimestamp() + "Rising123";
                 System.out.println(blaze);
                 String md5List2 = DigestUtils.md5DigestAsHex(blaze.getBytes());
                 System.out.println("MD5:" + md5List2);
                 */

                //验证是否一致
                if (!md5List.equals(map.get("check_info"))) {
                    logger.info("--------数据验证不合法--------");
                    //flag = false;
                    //result.setCode(2);
                    //result.setMsg("数据验证不合法");
                } else {
                    logger.info("--------数据验证成功--------");
                }
            } catch (Exception e) {
                logger.error("--------数据验证失败--------" + e.toString());
                flag = false;
                result.setCode(3);
                result.setMsg("数据验证出错");
            }
        }


        //验证通过, 继续处理数据
        if (flag) {
            //处理数据
            try {
                //处理数据
                String data = "logType:10;logTime:" + System.currentTimeMillis() + "<rsEdr>|" + map.get("list");
                //System.out.println(data);

                boolean isSend = MQUtil.sendMsg("edr_mq_storage", data);
                if (!isSend) {
                    logger.info("--------数据处理失败--------");
                    result.setCode(4);
                    result.setMsg("数据处理失败");
                } else {
                    logger.info("--------数据处理完成--------");
                    //返回结果
                    result.setCode(0);
                    result.setMsg("ok");
                }
            } catch (Exception e) {
                logger.error("--------数据处理错误--------" + e.toString());
                result.setCode(5);
                result.setMsg("数据处理错误");
            }
        }

        return JSON.toJSONString(result);
    }
}
