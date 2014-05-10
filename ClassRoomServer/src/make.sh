#!/bin/bash
javac -cp ./:../JavaPNS_2.2.jar:../bcprov-jdk15on-150.jar:../log4j-1.2.17.jar:../sqlite-jdbc-3.7.2.jar ./server/*.java
javac -cp ./:../JavaPNS_2.2.jar:../bcprov-jdk15on-150.jar:../log4j-1.2.17.jar:../sqlite-jdbc-3.7.2.jar ./message/*.java
