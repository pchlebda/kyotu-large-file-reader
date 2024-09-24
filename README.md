# Large file reader

This application reads csv file content, process it and expose REST API to fetch average city temperature along with years.

## Requirements
* Java 21
* Maven 3.6.3
* Git

## Project set up
* Build `mvn -U clean package`
* Run test `mvn -U clean test`
* Run application `mvn spring-boot:run`


## Custom file read
To set up custom csv file please set up `KYOTU_FILE` as a full path to your csv file.
Default csv files is [min_file.csv](src/resources/min_file.csv) inside resources.

## API usage
* Run `curl --location 'http://localhost:8080/api/v1/temperature/Warszawa'`