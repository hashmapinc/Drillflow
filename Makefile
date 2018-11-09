all: compile 

# compile the application
compile:  
	mvn clean compile

# package the application into an uber-jar
package:  
	mvn clean package

# run
run:  
	java -jar target/server-0.0.1-SNAPSHOT.jar
