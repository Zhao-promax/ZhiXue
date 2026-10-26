package com.zixue.progress.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zixue.progress.entity.LearnProgress;
import com.zixue.progress.mapper.LearnDurationMapper;
import com.zixue.progress.mapper.LearnProgressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 进度消息消费者：批量消费 + 手动 ack，批量 upsert 落库，失败进死信队列。
 */
@Component
public class ProgressConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProgressConsumer.class);

    private final ObjectMapper objectMapper;
    private final LearnProgressMapper progressMapper;
    private final LearnDurationMapper durationMapper;

    public ProgressConsumer(ObjectMapper objectMapper,
                            LearnProgressMapper progressMapper,
                            LearnDurationMapper durationMapper) {
        this.objectMapper = objectMapper;
        this.progressMapper = progressMapper;
        this.durationMapper = durationMapper;
    }

    @RabbitListener(queues = RabbitConfig.PROGRESS_QUEUE, containerFactory = "batchContainerFactory")
    public void onBatch(List<Message> messages, Channel channel) throws IOException {
        long lastDeliveryTag = messages.get(messages.size() - 1).getMessageProperties().getDeliveryTag();
        try {
            List<ProgressMessage> batch = new ArrayList<>(messages.size());
            for (Message message : messages) {
                batch.add(objectMapper.readValue(message.getBody(), ProgressMessage.class));
            }
            persistBatch(batch);
            // 批量确认：ack 到本批最后一条
            channel.basicAck(lastDeliveryTag, true);
        } catch (Exception e) {
            log.error("批量落库失败，消息进入死信队列", e);
            // 拒绝且不重入 → 走死信队列
            channel.basicNack(lastDeliveryTag, true, false);
        }
    }

    private void persistBatch(List<ProgressMessage> batch) {
        List<LearnProgress> progressList = new ArrayList<>(batch.size());
        for (ProgressMessage msg : batch) {
            LearnProgress p = new LearnProgress();
            p.setUserId(msg.getUserId());
            p.setVideoId(msg.getVideoId());
            p.setPosition(msg.getPosition());
            p.setDuration(msg.getDuration());
            p.setSpeed(msg.getSpeed());
            p.setLastReportTime(msg.getReportTime());
            p.setDeviceId(msg.getDeviceId());
            progressList.add(p);
        }
        // 批量 upsert 进度（幂等）
        progressMapper.batchUpsert(progressList);

        // 累加有效时长（按 reportTime 幂等）
        for (ProgressMessage msg : batch) {
            if (msg.getValidSeconds() != null && msg.getValidSeconds() > 0) {
                durationMapper.accumulate(msg.getUserId(), msg.getVideoId(), msg.getValidSeconds(), msg.getReportTime());
            }
        }
    }
}
