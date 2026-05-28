# ==========================================
# Tahap 1: Build
# ==========================================
# Gunakan image yang mendukung multi-platform
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Tahap 2: Runner
# ==========================================
# Gunakan image JRE yang stabil dan mendukung ARM64
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV MIDTRANS_IS_PROD=false
ENV TZ=Asia/Jakarta

CMD ["java", "-jar", "app.jar"]