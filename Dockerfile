FROM openjdk:21

WORKDIR /app

COPY ./target/app_2_crud-0.0.1-SNAPSHOT.jar app_2_crud.jar

EXPOSE 8080

CMD ["java", "-jar", "app_2_crud.jar"]
