package com.AuthService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration      //Обозначение конфигурации для Spring
public class RabbitMQConfig {

    @Value("${rabbitmq.auth.queue.name}")
    private String authQueueName;

    @Value("${rabbitmq.login.queue.name}")
    private String loginQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;    //Имя exchange pool

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKeyName;

    @Value("${rabbitmq.login.routing_key.name}")
    private String loginRoutingKeyName;

    @Bean
    public Queue authQueue() { return new Queue(authQueueName); }

    @Bean
    public Queue loginQueue() { return new Queue(loginQueueName); }

    //Инициализация exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue())
                .to(exchange()).with(authRoutingKeyName);
    }

    @Bean
    public Binding loginBinding() {
        return BindingBuilder.bind(loginQueue())
                .to(exchange()).with(loginRoutingKeyName);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}
