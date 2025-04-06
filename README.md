# Prerequisities
JDK 11
Docker

# How bring the mockservice up
cd mockserver  
docker compose up  
the mocke server will start at port 1080

# How bring the service up
./mvnw clean install -DskipTests  
java -jar target/simple-springboot-app-0.0.1-SNAPSHOT.jar  
The server will start at port 9001

# How to run the tests
./mvnw test  


Test Cases link  --
 https://docs.google.com/spreadsheets/d/1P8QRqTn6F89P4zj3uXZJDus6NEhjOLVbkitnoZWujy4/edit?gid=24137911#gid=24137911
