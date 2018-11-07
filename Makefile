all: compile 

# compile the application
compile:  
	mvn clean build

# package the application into an uber-jar
package:  
	mvn clean package
