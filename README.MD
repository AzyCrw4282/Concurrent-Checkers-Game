# Concurrent Checkers Game for FYP@RHUL 19/20
[![Build Status](https://api.travis-ci.com/AzyCrw4282/private_project.svg?token=e3aJwvFiyAMqAUsK6B24&branch=master)](https://travis-ci.com/AzyCrw4282/private_project)

## Overview

A concurrent game that is played through WebSockets. The game is first hosted by
a user who creates a room object and joining users enter the unique id to join
the game. When the game reaches 2 players the first game starts and when 4 players
are present then the second game starts. All played concurrently. The image below
illustrates 4 games in actions.

![](/git_images/game_3.png)

![](/git_images/first.png) ![](/git_images/first.png)


### Requirements
```
-Java 1.8

-MVN

-Spring Boot

-Lombok plugin
```

Then Build the app with:
```shell script
Mvn clean install 
```
Run it with
```shell script
Mvn spring-boot:run
```


#### Design/Development Implementations

- Application with Spring Boot, MVN, JAVA 8 & WebSockets
- Illustrates concurrency by using multi threads, locking, semaphore, sessions, concurrent Data-structures
- Scheduled threads handle back-end operations when a request is made by the user
- It's a stateful app, i.e. uses browser session id to allocate users after page load
- Developed TDD test cases to check all functionalities in the game


#### Game Images
UI to handle users creating and joining the room

![](/git_images/game_new_image_1.png)


![](/git_images/game_new_image_2.png)


![](/git_images/game_new_image_3.png)

Two games in action

![](/git_images//game_2.PNG)
