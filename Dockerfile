FROM gradle:latest

WORKDIR /app

COPY . /app

RUN ./gradlew build --no-daemon

EXPOSE 8080

CMD ["./gradlew", "run"]