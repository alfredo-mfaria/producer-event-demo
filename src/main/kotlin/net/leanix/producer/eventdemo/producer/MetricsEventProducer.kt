package net.leanix.producer.eventdemo.producer

import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MetricsEventProducer(
    private val streamBridge: StreamBridge
) {
    fun send(key: String?, metric: String) {
        val message = MessageBuilder.withPayload(metric)
            .setHeader(KafkaHeaders.MESSAGE_KEY, key)
            .build()
        streamBridge.send("metrics-out-0", message)
    }
}