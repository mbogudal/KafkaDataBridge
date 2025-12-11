package pl.mdmb.KafkaDataBridge.integration.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseInsertEntity;
import pl.mdmb.KafkaDataBridge.integration.entity.MockDatabaseReadEntity;

public interface MockDatabaseReadRepository extends JpaRepository<MockDatabaseReadEntity, Long> {
}
