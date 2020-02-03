mvn clean
mvn compile assembly:single
clear
java -jar target/router.jar conf/router1.conf