package com.rising.netty.mq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * create by zy 2019/11/28 16:09
 * TODO
 */
public class RabbitMQConsumer {
    private static String url = "127.0.0.1";
    private static Integer port = 5672;
    private static String username = "admin";
    private static String password = "123456";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(url);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //声明关注的队列
        AMQP.Queue.DeclareOk test = channel.queueDeclare("edr_mq_storage", false, false, false, null);
        System.out.println("========consumer wait received message================");
//        //接收message方式一：
//        //DefaultConsumer类实现了Consumer接口，传入一个通道，如果通道中有消息，就会执行回调函数handleDelivery  delivery：传送，投递
//        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                System.out.println(new String(body,"UTF-8"));
//            }
//        };
//        //自动回复队列应答 -- RabbitMQ中的消息确认机制
//        channel.basicConsume("test", true, defaultConsumer);

        //接收message方式二：
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);//QueueingConsumer extends DefaultConsumer
        channel.basicConsume("edr_mq_storage", true, queueingConsumer);
        while (true) {//一直执行，获取Delivery传递对象
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            System.out.println(new String(delivery.getBody(), "UTF-8"));
        }

    }
}
