package pl.mdmb.KafkaDataBridge.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import pl.mdmb.KafkaDataBridge.integration.repository.DynamicTableRepository;
import pl.mdmb.KafkaDataBridge.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;

@Log
@Service
@Profile({"preprod","prod", "dev"})
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application-consumer.properties")
})
@ConditionalOnProperty(
        name = "enable.consumer",
        havingValue = "true"
)
public class KafkaConsumerService {

    private final DynamicTableRepository dynamicTableRepository;
    private final String topic;

    public KafkaConsumerService(DynamicTableRepository dynamicTableRepository,
                                @Value("${read.kafka.topic}") String topic) {
        this.dynamicTableRepository = dynamicTableRepository;
        this.topic = topic;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Listening for data from topic: "+topic);
    }

    @KafkaListener(topics = "${read.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void recieveNewData(String data) {
        log.info("Received data");
        List<HashMap<String, String>> rows = null;
        try {
            rows = JsonUtils.deserialize(data, new TypeReference<List<HashMap<String, String>>>() {
            });
        } catch (JsonProcessingException e) {
            log.severe(e.getMessage());
            return;
        }
        log.info("Parsed data");
        dynamicTableRepository.insertData(rows);
        log.info("Persisted data");
    }
}
