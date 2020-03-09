# Concurrent Checkers Game for FYP@RHUL 19/20
[![Build Status](https://api.travis-ci.com/AzyCrw4282/private_project.svg?token=e3aJwvFiyAMqAUsK6B24&branch=master)](https://travis-ci.com/AzyCrw4282/private_project)
<a href="https://trello.com/b/czSy3gLz/chess"><img src="https://img.shields.io/badge/Project-Trello-brightgreen.svg" /></a>
![version](https://img.shields.io/badge/version-2.0-blue)
<a href="https://github.com/RHUL-CS-Projects/FullUnit_1920_AzkyMubarack/pulls"><img src="https://img.shields.io/github/commits-since/badges/shields/gh-pages?label=commits%20to%20be%20deployed" alt="Commits to be deployed"></a>

## Overview

A concurrent game that is played through WebSockets. The game is first hosted by
a user who creates a room object and joining users enter the unique id to join
the game. When the game reaches 2 players the first game starts and when 4 players
are present then the second game starts, the same logic follows upto 4 games. All games are played concurrently. The image below illustrates 4 games in actions.
### Image Of Game Environment
Includes: 3 concurrently playable games handling 6 users. Chat system, and game status updater.
See Image gallery below.

Click on Image to watch the video
[![IMAGE ALT TEXT HERE](/git_images/new.png)]((https://youtu.be/9vP_O2__9DU)

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
- It's a stateful app, i.e. uses browser session id to allocate user after page load
- Developed TDD test cases to check all functionalities in the game


#### Game Images

##### UI Interface
![](/git_images/first.png) ![](/git_images/rtyr.png)


##### Environment In Action
![](/git_images/ttt.png) ![](/git_images/hth.png)

##### All Other Functionalities
![](/git_images/bgr.png) ![](/git_images/erw.png)


![](/git_images/kjy.png) ![](/git_images/yui.png)
