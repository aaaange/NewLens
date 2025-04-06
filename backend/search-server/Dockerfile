# 1단계: 빌드 단계
FROM gradle:8.13-jdk17 AS builder
WORKDIR /app

# Gradle Wrapper 및 설정 파일들을 먼저 복사해서 의존성 캐시 활용 (옵션)
COPY gradle gradle
COPY gradlew .
RUN chmod +x gradlew
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# 의존성 미리 다운로드 (옵션)
RUN ./gradlew --no-daemon help

# 전체 소스코드 복사
COPY . .
RUN chmod +x gradlew

# 실제 빌드 (테스트 스킵: -x test)
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 단계
FROM openjdk:17-slim
WORKDIR /app

# 빌드 단계에서 생성된 실행 가능한 jar 파일 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 9002

ENTRYPOINT ["java", "-jar", "app.jar"]
