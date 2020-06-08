# codapay
Utility for CSV file management

Listens input folder for CSV files 
Generates XML / JSON contents from the CSV files for every record
Needed ActiveMQ with pre-created queue named file.rec.queue

## Build
mvn clean install -DskipTests

## Deploy
mvn spring-boot:run -Dspring-boot.run.arguments="INPUT_FOLDER_PATH"

mvn spring-boot:run -Dspring-boot.run.arguments="/tmp/codapay/csv"

## Config

XML/JSON output location (application.properties):
/tmp/codapay/contents
