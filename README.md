# WitsmlApi-Server

[![Build Status](https://img.shields.io/travis/hashmapinc/WitsmlApi-Server.svg?logo=travis)](https://travis-ci.org/hashmapinc/WitsmlApi-Server)
[![Documentation Status](https://readthedocs.org/projects/witsml-server-api/badge/?version=latest)](https://witsml-server-api.readthedocs.io/en/latest/?badge=latest)
[![Waffle.io - Columns and their card count](https://badge.waffle.io/hashmapinc/WitsmlApi-Server.svg?columns=Backlog,Done,In%20progress)](https://waffle.io/hashmapinc/WitsmlApi-Server)

A dockerized WITSML API Server that is agnostic of the backend.

# Building

Execute `mvn clean install`

# Running

Execute `java -jar target/server-0.0.1-SNAPSHOT.jar`

# Building the Docker image

Navigate to the docker directory

Execute `docker build . -t hashmapinc/witsmlserver:latest` to build the image

Once completed execute `docker run -p 7070:7070 hashmapinc/witsmlserver:latest` 
