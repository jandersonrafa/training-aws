package com.training.aws.queuehandlerback;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableJms
public class QueueHandlerApplication {

	private SQSConnectionFactory connectionFactory;

	public static void main(String[] args) {
		SpringApplication.run(QueueHandlerApplication.class, args);
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
		SQSConnectionFactory sqsConnectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqs);
		return sqsConnectionFactory;
	}

	@Bean(name = "sqsAsyncTaskExecutor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
		asyncTaskExecutor.setCorePoolSize(5);
		asyncTaskExecutor.setMaxPoolSize(20);
		asyncTaskExecutor.setQueueCapacity(10);
		asyncTaskExecutor.setThreadNamePrefix("threadPoolExecutor-SimpleMessageListenerContainer-");
		asyncTaskExecutor.initialize();
		return asyncTaskExecutor;
	}

	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQS, @Qualifier("sqsAsyncTaskExecutor") AsyncTaskExecutor asyncTaskExecutor) {
		SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory = new SimpleMessageListenerContainerFactory();
		simpleMessageListenerContainerFactory.setTaskExecutor(asyncTaskExecutor);
		return simpleMessageListenerContainerFactory;
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
			}
		};
	}
}
