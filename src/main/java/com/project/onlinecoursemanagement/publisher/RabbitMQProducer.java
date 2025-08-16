package com.project.onlinecoursemanagement.publisher;

import com.project.onlinecoursemanagement.dto.EmailRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String EXCHANGE;

    @Value("${rabbitmq.routing.key}")
    private String ROUTING_KEY ;

    private static final Logger LOGGER=  LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification(EmailRequestDto emailNotification) {
        LOGGER.info(String.format("Message sent -> %s",emailNotification));
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, emailNotification);
        System.out.println("Sent message to RabbitMQ: " + emailNotification);
    }
}
