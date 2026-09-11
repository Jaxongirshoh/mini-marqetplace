FROM bellsoft/liberica-openjdk-debian:25 AS builder
WORKDIR /build

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY . .

RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-debian:25 AS extractor
WORKDIR /builder
COPY --from=builder /build/target/*.jar marqetplace.jar
RUN java -Djarmode=tools -jar marqetplace.jar extract --layers --destination extracted

FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /marqetplace

COPY --from=extractor /builder/extracted/dependencies/ ./
COPY --from=extractor /builder/extracted/spring-boot-loader/ ./
COPY --from=extractor /builder/extracted/snapshot-dependencies/ ./
COPY --from=extractor /builder/extracted/application/ ./

ENTRYPOINT ["java", "-jar", "marqetplace.jar"]

