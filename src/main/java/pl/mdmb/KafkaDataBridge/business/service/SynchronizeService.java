package pl.mdmb.KafkaDataBridge.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mdmb.KafkaDataBridge.integration.repository.DynamicTableRepository;
import pl.mdmb.KafkaDataBridge.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;

@Log
@RequiredArgsConstructor
@Service
@Profile({"preprod","prod", "dev"})
public class SynchronizeService {
    @Getter
    private List<HashMap<String, String>> cash;
    private final DynamicTableRepository dynamicTableRepository;
    private final KafkaProducerService kafkaProducerService;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        synchronize();
    }

    @Scheduled(cron = "${cron.synchronize}")
    public void cron(){
        synchronize();
    }

    public void synchronize(){
        log.info("Sync started.");
        List<HashMap<String, String>> data = dynamicTableRepository.findAll();
        if (!data.equals(cash)) {
            cash = data;
            try {
                kafkaProducerService.notifyNewData(JsonUtils.serialize(cash));
            } catch (JsonProcessingException e) {
                log.severe(e.getMessage());
            }
        } else {
            log.info("Nothing to proceed.");
        }
    }

}
