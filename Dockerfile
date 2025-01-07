FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM maven:3.8.4-openjdk-17-slim
COPY --from=build /target/SpringSeHibernate-0.0.1-SNAPSHOT.jar SpringSeHibernate.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","SpringSeHibernate.jar"]