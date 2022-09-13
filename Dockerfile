FROM openjdk:17-alpine
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml pom.xml
COPY .mvn .mvn
COPY mvnw mvnw
COPY src src
#Download all required dependencies into one layer
RUN ./mvnw -B dependency:resolve dependency:resolve-plugins
RUN ./mvnw clean package -Dmaven.test.skip=true \
  && cp target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080
