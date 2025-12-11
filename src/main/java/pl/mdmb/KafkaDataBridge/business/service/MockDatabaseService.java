package pl.mdmb.KafkaDataBridge.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseInsertEntity;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseReadEntity;
import pl.mdmb.KafkaDataBridge.integration.repository.MockDatabaseInsertRepository;
import pl.mdmb.KafkaDataBridge.integration.repository.MockDatabaseReadRepository;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class MockDatabaseService {

    private final MockDatabaseReadRepository mockDatabaseReadRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(){
        mockDatabaseReadRepository.save(MockDatabaseReadEntity.builder().String("test 1").build());
        mockDatabaseReadRepository.save(MockDatabaseReadEntity.builder().String("test 2").build());
    }
}
