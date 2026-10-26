# 网关服务镜像（构建上下文为仓库根目录）
# 构建：docker build -f zixue-gateway/Dockerfile -t zixue-gateway .
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY zixue-common ./zixue-common
COPY zixue-progress ./zixue-progress
COPY zixue-learning ./zixue-learning
COPY zixue-gateway ./zixue-gateway
RUN mvn -pl zixue-gateway -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/zixue-gateway/target/zixue-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
