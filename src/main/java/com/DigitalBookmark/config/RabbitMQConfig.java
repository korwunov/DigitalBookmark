package com.DigitalBookmark.config;

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
    //Получение значения из файла application.propetries
    @Value("${rabbitmq.file.queue.name}")
    private String fileInfoQueueName;   //Очередь для информации о файлах

    @Value("${rabbitmq.file.permission.queue.name}")
    private String filePermissionQueueName;     //Очередь для информации о доступе к файлам

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;    //Имя exchange pool

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKeyName;

    @Value("${rabbitmq.login.routing_key.name}")
    private String loginRoutingKeyName;

    @Value("${rabbitmq.file.routing_key.name}")
    private String fileInfoRoutingKeyName;  //Routing key для очереди

    @Value("${rabbitmq.file.permission.routing_key.name}")
    private String filePermissionRoutingKeyName;

    @Bean
    public Queue authQueue() { return new Queue(authQueueName); }

    @Bean
    public Queue loginQueue() { return new Queue(loginQueueName); }
    //Инициализация новой очереди
    @Bean
    public Queue fileInfoQueue() { return new Queue(fileInfoQueueName); }

    @Bean
    public Queue filePermissionQueue() { return new Queue(filePermissionQueueName); }
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
    //Маппинг очереди, exchange и routing key
    @Bean
    public Binding fileInfoBinding() {
        return BindingBuilder.bind(fileInfoQueue())
                .to(exchange()).with(fileInfoRoutingKeyName);
    }

    @Bean
    public Binding filePermissionBinding() {
        return BindingBuilder.bind(filePermissionQueue()).
                to(exchange()).with(filePermissionRoutingKeyName);
    }

//    @Bean
//    public MessageConverter converter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(converter());
//        return template;
//    }
}
