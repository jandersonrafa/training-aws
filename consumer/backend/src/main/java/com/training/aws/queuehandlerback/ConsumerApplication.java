package com.training.aws.queuehandlerback;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.annotation.PostConstruct;
import javax.jms.Session;

@SpringBootApplication
@EnableJms
public class ConsumerApplication {

	private SQSConnectionFactory connectionFactory;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@PostConstruct
	public void init() {
		connectionFactory = createSQSConnectionFactory();
	}

	private SQSConnectionFactory createSQSConnectionFactory() {
		final AmazonSQS sqs = AmazonSQSClient.builder()
				.withRegion(Regions.US_EAST_2)
				.withCredentials(new ProfileCredentialsProvider())
				.build();
		return new SQSConnectionFactory(new ProviderConfiguration(), sqs);
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		factory.setSessionTransacted(true);

		return factory;
	}

	@Bean
	public JmsTemplate defaultJmsTemplate() {
		return new JmsTemplate(connectionFactory);
	}
}
