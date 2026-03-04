# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (cached layer if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="magic-boot"
LABEL description="Magic-API rapid development platform"

# Install necessary tools
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# Create non-root user for security
RUN addgroup -S magicboot && adduser -S magicboot -G magicboot

WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /build/target/magic-boot.jar app.jar

# Copy magic-api resources (if needed at build time)
# Note: magic-api resources are typically loaded from data/magic-api at runtime
# If you want to include them in the image, uncomment the following line:
# COPY data/magic-api ./data/magic-api

# Change ownership to non-root user
RUN chown -R magicboot:magicboot /app

# Switch to non-root user
USER magicboot

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=online"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
