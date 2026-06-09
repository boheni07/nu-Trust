package com.nubiz.nutrust.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${nutrust.rabbitmq.notification.exchange:topic.notification}")
    private String notificationExchange;

    @Value("${nutrust.rabbitmq.notification.queue:nutrust.notification}")
    private String notificationQueue;

    @Value("${nutrust.rabbitmq.notification.routing.key:notification.#}")
    private String notificationRoutingKey;

    @Value("${nutrust.rabbitmq.chat.exchange:topic.chat}")
    private String chatExchange;

    @Value("${nutrust.rabbitmq.chat.queue:nutrust.chat}")
    private String chatQueue;

    @Value("${nutrust.rabbitmq.chat.routing.key:chat.#}")
    private String chatRoutingKey;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueue)
            .withArgument("x-dead-letter-exchange", "dlx.notification")
            .withArgument("x-dead-letter-routing-key", "notification.deadletter")
            .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
            .to(notificationExchange)
            .with(notificationRoutingKey);
    }

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(chatExchange);
    }

    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(chatQueue)
            .withArgument("x-dead-letter-exchange", "dlx.chat")
            .withArgument("x-dead-letter-routing-key", "chat.deadletter")
            .build();
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue)
            .to(chatExchange)
            .with(chatRoutingKey);
    }

    @Bean
    public DirectExchange dlxNotificationExchange() {
        return new DirectExchange("dlx.notification");
    }

    @Bean
    public Queue dlxNotificationQueue() {
        return QueueBuilder.durable("dlx.notification.queue").build();
    }

    @Bean
    public Binding dlxNotificationBinding(Queue dlxNotificationQueue, DirectExchange dlxNotificationExchange) {
        return BindingBuilder.bind(dlxNotificationQueue)
            .to(dlxNotificationExchange)
            .with("notification.deadletter");
    }

    @Bean
    public DirectExchange dlxChatExchange() {
        return new DirectExchange("dlx.chat");
    }

    @Bean
    public Queue dlxChatQueue() {
        return QueueBuilder.durable("dlx.chat.queue").build();
    }

    @Bean
    public Binding dlxChatBinding(Queue dlxChatQueue, DirectExchange dlxChatExchange) {
        return BindingBuilder.bind(dlxChatQueue)
            .to(dlxChatExchange)
            .with("chat.deadletter");
    }
}
