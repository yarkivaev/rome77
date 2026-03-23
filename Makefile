.PHONY: build test run clean

build:
	mvn compile

test:
	mvn test

run: build
	mvn exec:java -Dexec.mainClass=web.Main

clean:
	mvn clean
