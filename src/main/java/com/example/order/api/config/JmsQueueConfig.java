package com.example.order.api.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

/**
 * Configuration classr.
 *
 * @author Tharaka Weheragoda
 */
@Configuration
public class JmsQueueConfig {

    @Bean
    public Queue queue(){
        return new ActiveMQQueue("order-queue");
    }
}
