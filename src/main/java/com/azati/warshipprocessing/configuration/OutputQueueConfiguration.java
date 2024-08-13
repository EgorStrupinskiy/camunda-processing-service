package com.azati.warshipprocessing.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutputQueueConfiguration {

	@Value("${actionQueue.name}")
	private String queueName;

	@Value("${actionQueue.exchangeName}")
	private String exchangeName;

	@Value("${actionQueue.key}")
	private String key;

	@Bean
	public Queue actionQueue() {
		return new Queue(queueName, false);
	}
	@Bean
	Exchange actionExchange() {
		return new DirectExchange(exchangeName, false, false);
	}
	@Bean
	Binding actionBinding(@Qualifier("actionQueue")Queue queue, @Qualifier("actionExchange") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(key).noargs();
	}
}
