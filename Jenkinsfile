pipeline {
  agent any

  environment {
    MAVEN_OPTS = '-Xmx1024m'
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '30'))
    timestamps()
    ansiColor('xterm')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test (Unit Only)') {
      steps {
        // Skip ITs since DynamoDB Local isn't available in CI
        sh 'mvn -B -DskipITs=true clean verify'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/*.jar, target/site/jacoco/**', allowEmptyArchive: true
        }
      }
    }

    stage('Package') {
      steps {
        sh 'mvn -B -DskipTests=true package'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
  }

  post {
    success {
      echo "Build and unit tests successful"
    }
    failure {
      echo "Build failed — check logs."
    }
  }
}
