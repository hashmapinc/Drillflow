all: compile 

# compile the application
compile:  
	maven clean build

# package the application into an uber-jar
package:  
	maven clean package
