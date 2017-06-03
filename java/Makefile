login:
	ssh -i eegStream.pem ubuntu@ec2-52-26-76-209.us-west-2.compute.amazonaws.com

build:
	mvn clean compile assembly:assembly

run:
	java -jar target/eeg-graph-1.0-jar-with-dependencies.jar --port 8080
