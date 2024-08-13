package com.azati.warshipprocessing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseQueueConfiguration {

    @Bean
    public Queue responseQueue() {
        String queueName = "response-queue";
        return new Queue(queueName, false);
    }

    @Bean
    Exchange responseExchange() {
        String exchangeName = "test";
        return new DirectExchange(exchangeName, false, false);
    }

    @Bean
    Binding responseBinding(@Qualifier("responseQueue") Queue queue,@Qualifier("responseExchange") Exchange exchange) {
        String key = "key";
        return BindingBuilder.bind(queue).to(exchange).with(key).noargs();
    }
}
