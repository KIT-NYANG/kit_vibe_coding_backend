# ================================
# 1. Build stage
# ================================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew

# 의존성 캐시
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사
COPY src ./src

# 빌드
RUN ./gradlew clean bootJar --no-daemon

# ================================
# 2. Runtime stage
# ================================
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# ffmpeg / ffprobe 설치
RUN apt-get update && apt-get install -y ffmpeg \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENV TZ=Asia/Seoul

# Spring profile 기본값을 prod로 지정
ENV SPRING_PROFILES_ACTIVE=prod

# 업로드 폴더 생성
RUN mkdir -p /app/uploads

# jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]