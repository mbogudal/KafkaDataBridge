# 1. Wybieramy obraz bazowy z Javą
FROM eclipse-temurin:21-jdk-jammy

# 2. Ustawiamy katalog roboczy w kontenerze
WORKDIR /app

# 3. Kopiujemy jar do kontenera
COPY target/KafkaDataBridge-0.0.1-SNAPSHOT.jar app.jar

# 4. Uruchamiamy aplikację
ENTRYPOINT ["java","-jar","app.jar"]

#budowanie
# docker build -t kafkadatabridge:latest .
#sprawdzanie obrazu
# docker images
#uruchamianiae
# docker run -e "SPRING_PROFILES_ACTIVE=dev" kafkadatabridge:latest