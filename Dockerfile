# === Stage 1: Frontend build ===
FROM node:18-alpine AS frontend
WORKDIR /build
COPY justthree-review-my-webtoon-front/package.json justthree-review-my-webtoon-front/package-lock.json* ./
RUN npm ci
COPY justthree-review-my-webtoon-front/ ./
# Override vite outDir so output stays inside this stage
RUN npx vite build --outDir=./dist --emptyOutDir

# === Stage 2: Backend build ===
FROM gradle:8.5-jdk17 AS backend
WORKDIR /build
COPY justthree-review-my-webtoon-back/build.gradle justthree-review-my-webtoon-back/settings.gradle ./
RUN gradle --no-daemon dependencies > /dev/null 2>&1 || true
COPY justthree-review-my-webtoon-back/src ./src
COPY --from=frontend /build/dist ./src/main/resources/static
RUN gradle --no-daemon clean bootJar -x test

# === Stage 3: Runtime ===
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=backend /build/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8089
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
