package consumer_poc;

import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.LogIfLevelEnabled;

import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class ConsumerConfig {
    public static final String LISTENER_CONTAINER_FACTORY = "listenerContainerFactory";

    private final KafkaProperties kafkaProperties;

    public ConsumerConfig(final KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean(LISTENER_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> ListenerContainerFactory(ConsumerFactory<byte[], byte[]> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<byte[], byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAsyncAcks(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.TRACE);
        return factory;
    }

    @Bean
    public ConsumerFactory<byte[], byte[]> consumerFactory() {
        final Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, List.of(RoundRobinAssignor.class));
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, List.of("localhost:19092"));
        return new DefaultKafkaConsumerFactory<>(configs);
    }
}
