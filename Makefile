.PHONY: build run deploy

build:
	./gradlew build

run:
	./gradlew runClient

deploy:
	cp build/libs/hammerpickaxe-1.0.jar ~/Library/Application\ Support/minecraft/mods/