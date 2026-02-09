# Build stage
FROM eclipse-temurin:17-jdk as builder

# Install sbt
RUN apt-get update && apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add && \
    apt-get update && apt-get install -y sbt && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy build files first for caching
COPY build.sbt .
COPY project/ project/

# Download dependencies (cached layer)
RUN sbt update

# Copy source code
COPY modules/ modules/

# Build frontend (ScalaJS) with full optimization
RUN sbt frontend/fullLinkJS

# Build backend fat JAR
RUN sbt backend/assembly

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the backend JAR
COPY --from=builder /app/modules/backend/target/scala-3.*/brice-backend.jar ./app.jar

# Copy frontend static files
COPY --from=builder /app/modules/frontend/dist/ ./static/dist/
COPY --from=builder /app/modules/frontend/index.html ./static/
COPY --from=builder /app/modules/frontend/styles.css ./static/
COPY --from=builder /app/modules/frontend/public/ ./static/public/

# Expose port (Railway will set PORT env var)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
