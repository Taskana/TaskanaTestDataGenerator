# TaskanaTestDataGenerator
Generator for test data structure. Can for example be used to create test data for performance tests.

To run it,
- build it via **mvn clean install**
- provide file **taskanaPerformanceTest.properties** in the user-home directory. This file needs to define the properties needed by taskana to connect to its database. The required properties are jdbcDriver, jdbcUrl, dbUserName, dbPassword and schemaName. A sample file looks as follows:

>jdbcDriver=org.postgresql.Driver\
>jdbcUrl=jdbc:postgresql://localhost:50102/postgres\
>dbUserName=postgres\
>dbPassword=postgres\
>schemaName=taskana

- open a command prompt in the target directory of the TaskanaTestDataGenerator project and issue the command 

<project_home>\\performance-test\\target>java -jar performance-test-0.0.1-SNAPSHOT-jar-with-dependencies.jar -o <output_directory>

The generator will then fill the database with test data and create .csv files in the output directory that can be used to control JMeter performance tests.

