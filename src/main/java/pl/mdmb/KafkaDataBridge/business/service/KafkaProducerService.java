package pl.mdmb.KafkaDataBridge.business.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log
@Service
@Profile({"preprod", "prod", "dev"})
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application-producer.properties")
})
public class KafkaProducerService {
    private final String kafkaTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(@Value("${insert.kafka.topic}") String kafkaTopic,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTopic = kafkaTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void notifyNewData(String data) {
        kafkaTemplate.send(kafkaTopic, data);
        log.info("Kafka event has been send to the topic: " + kafkaTopic);
    }

}
