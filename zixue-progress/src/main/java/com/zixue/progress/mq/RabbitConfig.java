package com.zixue.progress.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：交换机 / 队列 / 死信队列 / 批量消费容器工厂。
 */
@Configuration
public class RabbitConfig {

    public static final String PROGRESS_EXCHANGE = "progress.exchange";
    public static final String PROGRESS_QUEUE = "progress.queue";
    public static final String PROGRESS_ROUTING_KEY = "progress.report";

    public static final String PROGRESS_DLX = "progress.dlx";
    public static final String PROGRESS_DLQ = "progress.dlq";
    public static final String PROGRESS_DLQ_ROUTING_KEY = "progress.dlq";

    @Bean
    public DirectExchange progressExchange() {
        return new DirectExchange(PROGRESS_EXCHANGE, true, false);
    }

    /** 业务队列：绑定死信交换机，消费失败的消息进入死信队列 */
    @Bean
    public Queue progressQueue() {
        return QueueBuilder.durable(PROGRESS_QUEUE)
                .deadLetterExchange(PROGRESS_DLX)
                .deadLetterRoutingKey(PROGRESS_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding progressBinding() {
        return BindingBuilder.bind(progressQueue()).to(progressExchange()).with(PROGRESS_ROUTING_KEY);
    }

    @Bean
    public DirectExchange progressDlx() {
        return new DirectExchange(PROGRESS_DLX, true, false);
    }

    @Bean
    public Queue progressDlq() {
        return QueueBuilder.durable(PROGRESS_DLQ).build();
    }

    @Bean
    public Binding progressDlqBinding() {
        return BindingBuilder.bind(progressDlq()).to(progressDlx()).with(PROGRESS_DLQ_ROUTING_KEY);
    }

    /** 批量消费 + 手动 ack 的监听容器工厂 */
    @Bean("batchContainerFactory")
    public SimpleRabbitListenerContainerFactory batchContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setBatchListener(true);
        factory.setBatchSize(100);
        factory.setPrefetchCount(200);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
