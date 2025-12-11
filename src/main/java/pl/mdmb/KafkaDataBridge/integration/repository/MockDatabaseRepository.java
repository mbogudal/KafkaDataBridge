package pl.mdmb.KafkaDataBridge.integration.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseEntity;

public interface MockDatabaseRepository extends JpaRepository<MockDatabaseEntity, Long> {
}
