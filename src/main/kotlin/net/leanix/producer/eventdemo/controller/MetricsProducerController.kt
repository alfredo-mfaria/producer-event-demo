package net.leanix.producer.eventdemo.controller

import net.leanix.producer.eventdemo.producer.MetricsEventProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MetricsProducerController(
    private val metricsEventProducer:MetricsEventProducer
) {

    @PostMapping("/")
    fun produceMetric(@RequestBody metric: String) {
        println(metric)
        metricsEventProducer.send(null, metric)
    }
}