package pl.mdmb.KafkaDataBridge.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.mdmb.KafkaDataBridge.integration.repository.DynamicTableRepository;
import pl.mdmb.KafkaDataBridge.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;

@Log
@RequiredArgsConstructor
@Service
@Profile({"preprod","prod"})
@ConfigurationProperties(prefix = "consumer")
@PropertySource("classpath:application-consumer.properties")
public class KafkaConsumerService {

    private final DynamicTableRepository dynamicTableRepository;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void recieveNewData(String data) {
        List<HashMap<String, String>> rows = null;
        try {
            rows = JsonUtils.deserialize(data, new TypeReference<List<HashMap<String, String>>>() {
            });
        } catch (JsonProcessingException e) {
            log.severe(e.getMessage());
            return;
        }
        dynamicTableRepository.insertData(rows);
    }
}
