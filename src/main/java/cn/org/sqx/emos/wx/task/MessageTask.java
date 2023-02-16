package cn.org.sqx.emos.wx.task;

import cn.org.sqx.emos.wx.domain.MessageEntity;
import cn.org.sqx.emos.wx.domain.MessageRefEntity;
import cn.org.sqx.emos.wx.exception.EmosException;
import cn.org.sqx.emos.wx.service.impl.MessageServiceImpl;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther: sqx
 * @Date: 2023-02-09
 */

@Component
@Slf4j
public class MessageTask {
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageServiceImpl messageService;

    /**
     * 同步发送消息
     * @param topic     主题
     * @param entity    消息对象
     */
    public void send(String topic, MessageEntity entity) {
        String id = messageService.insertMessage(entity);  //获得写入MQ的ID
        try (Connection connection = factory.newConnection();   //连接
             Channel channel = connection.createChannel();    //通道
        ){
            //连接队列
            channel.queueDeclare(topic, true, false, false, null);

            //附属其他消息
            HashMap map = new HashMap();
            map.put("messageId", id);

            //附属到AMQP协议的请求头中
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();

            //发送消息
            channel.basicPublish("",topic,properties,entity.getMsg().getBytes());

            log.debug("消息发送成功");

        }catch (Exception e){
            log.error("执行异常：",e);
            throw new EmosException("向MQ发送消息失败");
        }

    }

    @Async
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic,entity);
    }

    /**
     * 接收消息
     * @param topic 主题
     * @return  数量
     */
    public int receive(String topic) {
        int i=0;

        try (Connection connection = factory.newConnection();   //连接
             Channel channel = connection.createChannel();    //通道
        ){
            //连接队列
            channel.queueDeclare(topic, true, false, false, null);

            while (true) {
                GetResponse response = channel.basicGet(topic, false);
                if(response != null){
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> map = properties.getHeaders();
                    String messageId = map.get("messageId").toString();
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("从RabbitMQ接收消息："+message);

                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);

                    messageService.insertMessageRef(entity);
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag,false);

                    i++;
                }else {
                    break;
                }
            }

        }catch (Exception e){
            log.error("接收消息失败：",e);
            throw new EmosException("接收消息失败");
        }

        return i;
    }

    @Async
    public int receiveAsync(String topic){
        return receive(topic);
    }

    /**
     * 删除消息队列
     * @param topic 主题
     */
    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();   //连接
             Channel channel = connection.createChannel();    //通道
        ){
            channel.queueDelete(topic);
            log.debug("消息队列成功删除");
        }catch (Exception e){
            log.error("删除队列失败",e);
            throw new EmosException("删除队列失败");
        }
    }

    @Async
    public void deleteQueueAsync(String topic){
        deleteQueue(topic);
    }
}
