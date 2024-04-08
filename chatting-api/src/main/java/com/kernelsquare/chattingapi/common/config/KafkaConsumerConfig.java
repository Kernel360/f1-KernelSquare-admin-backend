package com.kernelsquare.chattingapi.common.config;

import com.kernelsquare.chattingapi.domain.chatting.dto.ChatMessageRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	@Value("${kafka.url}")
	private String url;
	public ConsumerFactory<String, ChatMessageRequest> consumerFactory(String groupId) {
		Map<String, Object> config = new HashMap<>();

		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
		config.put(JsonDeserializer.TYPE_MAPPINGS,
			"ChatMessage:com.kernelsquare.chattingapi.domain.chatting.dto.ChatMessageRequest");
		config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		return new DefaultKafkaConsumerFactory<>(config);
	}

	public ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> kafkaListenerContainerFactory(String groupId) {
		ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory(groupId));
		return factory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> chattingKafkaListenerContainerFactory() {
		return kafkaListenerContainerFactory("chatting");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> mongoKafkaListenerContainerFactory() {
		return kafkaListenerContainerFactory("mongo");
	}
}
