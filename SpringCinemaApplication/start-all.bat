@echo off
start cmd /k "cd /d C:\Users\andre\Documents\SourceTree\SpringCinemaApplication\SpringCinemaApplication\film-api && mvn spring-boot:run -Dspring-boot.run.profiles=docker"
start cmd /k "cd /d C:\Users\andre\Documents\SourceTree\SpringCinemaApplication\SpringCinemaApplication\film-web && mvn spring-boot:run -Dspring-boot.run.profiles=docker"
start cmd /k "cd /d C:\Users\andre\Documents\SourceTree\SpringCinemaApplication\SpringCinemaApplication\store-api && mvn spring-boot:run -Dspring-boot.run.profiles=docker"
start cmd /k "cd /d C:\Users\andre\Documents\SourceTree\SpringCinemaApplication\SpringCinemaApplication\rating-api && mvn spring-boot:run -Dspring-boot.run.profiles=docker"
