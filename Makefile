all: compile 

# compile the application
compile:  
	mvn clean compile

# package the application into an uber-jar
package:  
	mvn clean package
