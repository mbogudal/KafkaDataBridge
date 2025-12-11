package pl.mdmb.KafkaDataBridge.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log
@RequiredArgsConstructor
@Service
@Profile({"preprod","prod"})
public class KafkaService {
    @Value("kafka.topic")
    private String kafkaTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void notifyNewData(String data) {
        kafkaTemplate.send("kafkaTopic", data);
        log.info("Kafka event has been send.");
    }
}
