package com.zixue.progress.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zixue.common.enums.ErrorCode;
import com.zixue.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 进度消息生产者：开启 publisher confirm + return 回调，保证消息不丢。
 */
@Component
public class ProgressProducer {

    private static final Logger log = LoggerFactory.getLogger(ProgressProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ProgressProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setConfirmCallback(this::onConfirm);
        this.rabbitTemplate.setReturnsCallback(returned ->
                log.error("消息路由失败: exchange={}, routingKey={}, reply={}",
                        returned.getExchange(), returned.getRoutingKey(), returned.getReplyText()));
    }

    public void send(ProgressMessage message) {
        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new BizException(ErrorCode.SYSTEM_ERROR.getCode(), "消息序列化失败");
        }

        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message amqpMessage = new Message(body, props);

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.send(RabbitConfig.PROGRESS_EXCHANGE, RabbitConfig.PROGRESS_ROUTING_KEY, amqpMessage, correlationData);
    }

    private void onConfirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.debug("消息确认成功: {}", correlationData);
        } else {
            log.error("消息确认失败: {}, cause: {}", correlationData, cause);
        }
    }
}
