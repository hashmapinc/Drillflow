###############
Getting Started
###############

************
Introduction
************

**NOTE: These instructions are generic until you get to the running section in which they are specific to the DoT valve**

==================
Building Drillflow
==================

Let's talk about building Drillflow. It is optimized to build on Java 8+ (including 11 for the latest LTS release). It is
most thoroughly tested on OpenJDK but has been built and runs on the Oracle JDK as well. In order to build Drillflow you will
need the JDK 8+ (preferrably 11), Maven 3.3+ and git to pull the code (or you can download the zip from `the GitHub repo <https://github.com/hashmapinc/Drillflow>`_.
Additionally to build the docker image you will need docker.

****************
Pulling the Code
****************

First we will start with cloning the repo...this is easy enough

``git clone https://github.com/hashmapinc/Drillflow.git``

This will clone Drillflow to your local repository

*****************
Building the Code
*****************

Building the code is also relatively simple thanks to the magic of Maven.

Change directories into Drillflow

``cd Drillflow``

And build the project

``mvn clean package``

You should end up with something like this:

``[INFO] ------------------------------------------------------------------------``
``[INFO] Reactor Summary for DrillFlow 0.0.1-SNAPSHOT:``
``[INFO]``
``[INFO] DrillFlow .......................................... SUCCESS [  0.750 s]``
``[INFO] DrillFlow Valve Module ............................. SUCCESS [ 46.487 s]``
``[INFO] DrillFlow Application Module ....................... SUCCESS [ 12.460 s]``
``[INFO] ------------------------------------------------------------------------``
``[INFO] BUILD SUCCESS``
``[INFO] ------------------------------------------------------------------------``
``[INFO] Total time:  01:00 min``
``[INFO] Finished at: 2018-12-21T12:35:52-06:00``
``[INFO] ------------------------------------------------------------------------``

*****************
Running Drillflow
*****************

Assuming no errors have occurred then you will have a ready to run application.

Now before you can get started you need to have some system behind Drillflow providing the data as described in the
:ref:`solution architecture <solution_arch>`

so you need to set 2 environment variables first:
**VALVE_BASE_URL**: this sets the base URL of the REST queries to make on the backend
**VALVE_API_KEY**: this sets the API key to use against the REST API on the back end

Change directories into the df-server/target directory:

``cd df-server/target``

And you can now execute:

``java -jar df-server-0.0.1-SNAPSHOT.jar``

At this point you will have a WITSML serving getCap, getVersion, getBaseMsg at the following url:

``http://localhost:7070/Service/WMLS``

The WSDL is available at:

``http://localhost:7070/Service/WMLS?wsdl``

****************************
Running the Docker Container
****************************

In the case where you don't want to build the code and just want to run the container it is as simple as:

Pulling the container:

``docker pull hashmapinc/Drillflow:latest``

Running the container:

``docker run -p 7070:7070 -e VALVE_API_KEY='<api key>' -e VALVE_BASE_URL='<put in your base url here>' hashmapinc/drillflow:latest``

Replacing **<api key>** with the actual API key and **<put in your base url here>** with the actual base url.

Ideally this would be injected with a configuration management tool such as Consul.