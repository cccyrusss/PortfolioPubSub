## Simple realtime portfolio publisher and listener system

### Requirements
- JDK 1.8
- Gradle version 7.1
- SQLite database with securities info, which can be created by running SecuritiesInfoWriter.java

### Third party libraries used 
- org.xerial:sqlite-jdbc:3.45.2.0
- org.apache.commons:commons-math3:3.6.1
- org.junit.jupiter:junit-jupiter-api:5.7.0

### Running the application
1. Build project with Gradle
2. Run SecuritiesInfoWriter.java (if SQLite db doesn't exist)
3. Run PortfolioPubSub.java


### Running the tests
1. Build project with Gradle
2. Run SecuritiesInfoWriter.java (if SQLite db doesn't exist)
3. Run tests

### Future Work / Improvement
- Make SecuritiesInfoWriter.java executes SQL from an SQL file
- Write more tests
- Use MessageQueue or proper Publisher and Subscriber design pattern (depends on usage)

