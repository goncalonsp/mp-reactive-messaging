# mp-reactive-messaging project

This project aims to implement an example application capable of consuming and producing events reactively.
It uses [Quarkus](https://quarkus.io/) and [MicroProfile reactive messaging](https://github.com/eclipse/microprofile-reactive-messaging) (with the [SmallRye](https://smallrye.io/smallrye-reactive-messaging) implementation).

This project was bootstraped using https://code.quarkus.io/

In this project you can find multiple experimental implementations of the same app. While running the commands below, take into account which implementation you are running. 

## Packaging and running the application

The application can be packaged using:
```shell script
mvn package
```
It produces a `reactive-messaging-*-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/reactive-messaging-1.0.0-SNAPSHOT-runner.jar`.

## Running containerized

In each implementation, you can find in the folder `src/main/docker/` a set of prepared Dockerfiles. 
Also on the root you can find a prepared `docker-compose.yml` setup to build and start a containerized version of the application as well as run all its external dependencies (such as Kafka).

To run this setup, and having the application packaged already, run the following:

```shell script
docker-compose up --build
```

If you are interested in selecting a different implementation of the app do:

 ```shell script
APP=implementation1 docker-compose up --build
 ```

To send an event and receive the result, run the following **in two separate terminal sessions**:

 ```shell script
docker run -it --network=host edenhill/kafkacat:1.5.0 -b localhost:9092 -t utterances -P
docker run -it --network=host edenhill/kafkacat:1.5.0 -b localhost:9092 -t recommendations -C
 ```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

## Running the tests

You can run all tests using:

```shell script
mvn verify
```

Or just the unit tests:

```shell script
mvn test
```

## Creating a native executable

You can create a native executable using: 
```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/reactive-messaging-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# Command Mode

Guide: https://quarkus.io/guides/command-mode-reference
