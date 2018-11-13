all: compile 

# compile the application
compile:  
	mvn -q clean compile

# package the application into an uber-jar with tests
package:  
	mvn -q clean package

# package the application into an uber-jar without tests
fastPackage:  
	mvn -q clean package -DskipTests=true

# run
run: fastPackage
	java -jar target/server-0.0.1-SNAPSHOT.jar

# test
test: compile
	mvn test
