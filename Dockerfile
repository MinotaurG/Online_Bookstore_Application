# ============================================
# STAGE 1: Build the application
# ============================================
# Use Maven image with Java 21 to build our app
# "AS builder" names this stage so we can reference it later
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set working directory inside container
# All subsequent commands run from here
WORKDIR /app

# Copy pom.xml first (dependency caching optimization)
# Docker caches layers - if pom.xml hasn't changed, 
# dependencies won't be re-downloaded on next build
COPY pom.xml .

# Download all dependencies (cached if pom.xml unchanged)
# -B = batch mode (less verbose output)
# go-offline = download everything needed for offline build
RUN mvn dependency:go-offline -B

# Now copy source code
# This layer changes frequently, but dependencies are cached above
COPY src ./src

# Build the JAR file
# -Dmaven.test.skip=true = faster build, we test separately
# -B = batch mode
RUN mvn package -Dmaven.test.skip=true -B

# ============================================
# STAGE 2: Create lightweight runtime image
# ============================================
# Use smaller JRE-only image (not full JDK)
# Alpine = minimal Linux (~5MB base)
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user for security
# Running as root inside container is a security risk
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from builder stage
# --from=builder references the first stage
# We only copy the final JAR, not Maven/source code (smaller image)
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Document which port the app uses
# This is informational - you still need to map it in docker-compose
EXPOSE 8080

# Health check - Docker will monitor if app is healthy
# Checks /actuator/health endpoint every 30 seconds
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Command to run when container starts
# Using exec form (JSON array) - preferred over shell form
ENTRYPOINT ["java", "-jar", "app.jar"]
