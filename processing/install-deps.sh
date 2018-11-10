#!/bin/bash

mvn install:install-file -Dfile=./libs/sound.jar -DgroupId=org.processing -DartifactId=sound -Dversion=3.3.7 -Dpackaging=jar
mvn install:install-file -Dfile=./libs/jsyn-20171016.jar -DgroupId=jsyn -DartifactId=jsyn -Dversion=20171016 -Dpackaging=jar
mvn install:install-file -Dfile=./libs/javamp3-1.0.3.jar -DgroupId=javamp3 -DartifactId=javamp3 -Dversion=1.0.3 -Dpackaging=jar