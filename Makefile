all: compile 

# compile the application
compile:  
	mvn -q clean compile

# package the application into an uber-jar with tests
package:  
	mvn -q clean 
	mvn package

# package the application into an uber-jar without tests
fastPackage:  
	mvn -q clean package -DskipTests=true

# clean
clean:
	mvn -q clean

# test
test: clean
	mvn test