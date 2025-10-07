# CI Setup (Day 9 - No Docker)

## Jenkins
- Pipeline is defined in `Jenkinsfile`.
- Stages:
  - Checkout
  - Run `mvn -DskipITs=true clean verify` (unit tests + JaCoCo report)
  - Package JAR
- Integration tests (`@Tag("integration")`) are **skipped** in Jenkins.
- To run integration tests locally:
  1. Start DynamoDB Local JAR:
     ```bash
     java "-Djava.library.path=./DynamoDBLocal_lib" -jar DynamoDBLocal.jar -inMemory -port 8000
     ```
  2. Run:
     ```bash
     mvn clean test
     ```

## Artifacts
- `target/*.jar` packaged and archived.
- `target/site/jacoco/index.html` contains coverage report.
