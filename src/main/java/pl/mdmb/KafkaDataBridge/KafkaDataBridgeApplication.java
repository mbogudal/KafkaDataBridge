package pl.mdmb.KafkaDataBridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KafkaDataBridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDataBridgeApplication.class, args);
	}

}
