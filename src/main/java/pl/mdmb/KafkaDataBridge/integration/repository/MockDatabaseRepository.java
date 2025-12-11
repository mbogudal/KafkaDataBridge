package pl.mdmb.KafkaDataBridge.integration.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseInsertEntity;

public interface MockDatabaseRepository extends JpaRepository<MockDatabaseInsertEntity, Long> {
}
