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

# Copy built jar from local build
COPY magic-boot-master/target/magic-boot.jar app.jar

# Change ownership to non-root user
RUN chown -R magicboot:magicboot /app

# Switch to non-root user
USER magicboot

# Expose port
EXPOSE 8089

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8089/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof \
    -Djava.security.egd=file:/dev/./urandom"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
