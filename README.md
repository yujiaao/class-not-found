# method-not-found to help you solve ClassNotFoundException & MethodNotFoundException problem

Goal:
 Every time when a `ClassNotFoundException` or a `MethodNotFoundException`  is thrown, your awesome application refuses to continue running.
To find out what is the right version of jar need to use, is a time-consuming and exhausting job. 
This project is to help you solve this problem by searching your local maven repository.

## How to build
```shell
$ ./mvnw clean package
```
Will produce a jar file and native-image in the `target` directory.

## How to run
```shell
$ java -jar target/method-not-found-0.0.1-SNAPSHOT.jar
```
Will show usage information if all is successful.

## How to use
```shell
$ java -jar target/method-not-found-0.0.1-SNAPSHOT.jar java.lang.StringBuilder append
```

## How to use with GraalVM
```shell
$ target/method-not-found java.lang.StringBuilder append
```
## Arguments
```shell
$ method-not-found <className> [methodName] [groupId1,groupId2...]
```
Where `className` is the fully qualified class name your need to find in jars,
and `methodName` is the method name, if also need to find, it is optional,
and `groupId1,groupId2...` is the groupId of the jars you want to search, it is optional, if not provided, will search every jar in your local repository.

