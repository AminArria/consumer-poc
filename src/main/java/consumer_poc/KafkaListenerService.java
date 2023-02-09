package consumer_poc;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import static consumer_poc.ConsumerConfig.LISTENER_CONTAINER_FACTORY;

@Service
public class KafkaListenerService {

    public KafkaListenerService() {};

    @KafkaListener(id = "listener-destination",
                   topics = "destination",
                   autoStartup = "true",
                   containerFactory = LISTENER_CONTAINER_FACTORY)
    public void onDestination(final ConsumerRecord<byte[], byte[]> record,
                              final Acknowledgment acknowledgment) {
        accept(record, acknowledgment);
    }

    private void accept(final ConsumerRecord<byte[], byte[]> record,
                        final Acknowledgment acknowledgment) {
        if (record.offset() > 0 && record.offset() % 10 == 0) {
            // no ack
        } else {
            System.out.println("ACK " + record.offset());
            acknowledgment.acknowledge();
        }
    }
}
