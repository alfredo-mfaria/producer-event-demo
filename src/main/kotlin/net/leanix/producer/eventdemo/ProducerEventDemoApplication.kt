package net.leanix.producer.eventdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProducerEventDemoApplication

fun main(args: Array<String>) {
	runApplication<ProducerEventDemoApplication>(*args)
}
