package pl.mdmb.KafkaDataBridge.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseInsertEntity;
import pl.mdmb.KafkaDataBridge.integration.repository.MockDatabaseRepository;

@Service
@RequiredArgsConstructor
public class MockDatabaseService {

    private final MockDatabaseRepository mockDatabaseRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(){
        mockDatabaseRepository.save(MockDatabaseInsertEntity.builder().String("test 1").build());
        mockDatabaseRepository.save(MockDatabaseInsertEntity.builder().String("test 2").build());
    }
}
