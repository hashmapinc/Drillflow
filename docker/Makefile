all: build 

# build the image
build:  
	docker build . -t hashmapinc/witsmlapi-server:latest

# run a container
run:  
	docker run -p 7070:7070 hashmapinc/witsmlapi-server:latest
